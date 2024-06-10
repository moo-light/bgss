package com.server.blockchainserver.server;

import com.server.blockchainserver.dto.user_dto.ApiResponse;
import com.server.blockchainserver.dto.user_dto.PersonInfo;
import com.server.blockchainserver.dto.user_dto.UserInfoData;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.models.user_model.CICardImage;
import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.repository.user_repository.CICardRepository;
import com.server.blockchainserver.repository.user_repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component
public class FPTAIVision {


    private final UserInfoRepository userInfoRepository;
    @Value("${fpt.ai.apikey}")
    private String apiKey;

    private final WebClient webClient;

    private static final Logger logger = LoggerFactory.getLogger(FPTAIVision.class);

    @Autowired
    private CICardRepository ciCardRepository;

    @Value("${path.cicard.image}")
    private String pathCICardImage;


    /**
     * Constructs a new instance of the FPTAIVision class.
     *
     * @param webClientBuilder   A builder for creating a WebClient instance.
     * @param userInfoRepository A repository for managing user information.
     */
    public FPTAIVision(WebClient.Builder webClientBuilder, UserInfoRepository userInfoRepository) {
        this.webClient = webClientBuilder.build();
        this.userInfoRepository = userInfoRepository;
    }


    /**
     * Analyzes an image using the FPT AI Vision API.
     *
     * @param image The MultipartFile containing the image to be analyzed.
     * @return A Mono of ApiResponse containing the analyzed data.
     * @throws IOException If there is an error reading the image bytes.
     */
    public Mono<ApiResponse> analyzeImage(MultipartFile image) throws IOException {
        String apiUrl = "https://api.fpt.ai/vision/idr/vnm";
        try {
            return webClient.post().uri(apiUrl).header("api-key", apiKey) // Sử dụng api-key của bạn
                    .contentType(MediaType.MULTIPART_FORM_DATA).body(BodyInserters.fromMultipartData("image", new ByteArrayResource(image.getBytes()) {
                        @Override
                        public String getFilename() {
                            return image.getOriginalFilename(); // Đảm bảo rằng tên file được gửi đi
                        }
                    })).retrieve().onStatus(httpStatus -> httpStatus.value() == 400, clientResponse -> Mono.error(new IllegalStateException("Bad Request to the vision API"))).onStatus(httpStatus -> HttpStatus.valueOf(httpStatus.value()).isError(), clientResponse -> Mono.error(new IllegalStateException("Error with the vision API"))).bodyToMono(ApiResponse.class);
        } catch (IOException e) {
            return Mono.error(new RuntimeException("Failed to read image bytes", e));
        }
    }


    /**
     * Analyzes an image using the FPT AI Vision API.
     *
     * @param userInfoId the user information identifier
     * @param frontImage The MultipartFile containing the image to be analyzed and save to resourcesFile.
     * @param backImage The MultipartFile containing the just image to be to resourcesFile
     * @return A Mono of ApiResponse containing the analyzed data.
     */
    public Mono<UserInfoData> getPersonalData(Long userInfoId, MultipartFile frontImage, MultipartFile backImage) {

        return Mono.just(userInfoId).flatMap(id -> Mono.justOrEmpty(userInfoRepository.findById(id)).switchIfEmpty(Mono.error(new NotFoundException("Not found user with id: " + userInfoId)))).flatMap(userInfo -> {
            List<CICardImage> ciCardImages = new ArrayList<>();
            try {
                if (frontImage != null && !frontImage.isEmpty()) {
                    ciCardImages.add(saveCICardImage(userInfoId, frontImage));
                }
                if (backImage != null && !backImage.isEmpty()) {
                    ciCardImages.add(saveCICardImage(userInfoId, backImage));
                }
            } catch (IOException e) {
                return Mono.error(new RuntimeException("Error processing images", e));
            }

            userInfo.setCiCardImage(ciCardImages);
            return Mono.fromCallable(() -> userInfoRepository.save(userInfo)).subscribeOn(Schedulers.boundedElastic()).then(Mono.justOrEmpty(frontImage)).flatMap(img -> {
                if (img == null || img.isEmpty()) {
                    return Mono.error(new IllegalArgumentException("Front image is null or empty"));
                }
                try {
                    return analyzeImage(img);
                } catch (IOException e) {
                    return Mono.error(new RuntimeException(e));
                }
            }).flatMap(apiResponse -> {
                if (!apiResponse.getData().isEmpty()) {
                    PersonInfo personInfo = apiResponse.getData().get(0);
                    UserInfoData userInfoData = new UserInfoData();
                    String fullName = personInfo.getName();
                    userInfoData.setCiCard(personInfo.getId());
                    int firstSpaceIndex = personInfo.getName().indexOf(" ");
                    if (firstSpaceIndex != -1) {
                        String firstName = fullName.substring(0, firstSpaceIndex);
                        String lastName = fullName.substring(firstSpaceIndex + 1);
                        userInfoData.setFirstName(firstName);
                        userInfoData.setLastName(lastName);
                    }
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String formatStr = personInfo.getDob();
                    try {
                        java.util.Date dobUtil = formatter.parse(formatStr);
                        java.sql.Date dobSql = new java.sql.Date(dobUtil.getTime());
                        userInfoData.setDob(dobSql);
                    } catch (ParseException e) {
                        logger.error("Error parsing date: {}", formatStr, e);
                    }
                    userInfoData.setAddress(personInfo.getAddress());

                    return Mono.just(userInfoData);
                } else {
                    return Mono.error(new IllegalStateException("No data found in API response"));
                }
            });
        });
    }


    /**
     * Saves a CICardImage to the specified path and associates it with the given userInfo.
     *
     * @param userInfoId the user information identifier
     * @param file  the MultipartFile containing the image to be saved and associated with the userInfo
     * @return the saved CICardImage
     * @throws IOException if there is an error reading the image bytes
     */
    public CICardImage saveCICardImage(Long userInfoId, MultipartFile file) throws IOException {

        UserInfo userInfo = userInfoRepository.findById(userInfoId)
                .orElseThrow(() -> new NotFoundException("Not found user with id: " + userInfoId));

        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        Path storagePath = Paths.get(pathCICardImage + uniqueFileName);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        Files.copy(file.getInputStream(), storagePath, StandardCopyOption.REPLACE_EXISTING);


        CICardImage ciCardImage = new CICardImage();
        ciCardImage.setImgUrl(storagePath.toString());
        ciCardImage.setUserInfo(userInfo);
        ciCardRepository.save(ciCardImage);
        return ciCardImage;

    }

}
