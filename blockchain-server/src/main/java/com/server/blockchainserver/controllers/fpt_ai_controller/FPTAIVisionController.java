package com.server.blockchainserver.controllers.fpt_ai_controller;

import com.server.blockchainserver.dto.user_dto.ApiResponse;
import com.server.blockchainserver.dto.user_dto.UserInfoData;
import com.server.blockchainserver.server.FPTAIVision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class FPTAIVisionController {


    @Autowired
    FPTAIVision fptAiVision;


    /**
     * This method is used to analyze an image using the FPTAIVision service.
     *
     * @param image The MultipartFile object containing the image to be analyzed.
     * @return A Mono of ResponseEntity containing an ApiResponse object if the image is successfully analyzed, otherwise, it returns a ResponseEntity with status NOT_FOUND.
     * @throws IOException If there is an error reading the image file.
     */
    @PostMapping("/analyze-image")
    public Mono<ResponseEntity<ApiResponse>> analyzeImage(@RequestParam("image") MultipartFile image) throws IOException {
        return fptAiVision.analyzeImage(image)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    /**
     * This method is used to verify the CI card images of a user using the FPTAIVision service.
     *
     * @param userInfoId The unique identifier of the user whose CI card images are to be verified.
     * @param frontImage The MultipartFile object containing the front image of the CI card.
     * @param backImage The MultipartFile object containing the back image of the CI card.
     * @return A Mono of ResponseEntity containing a UserInfoData object if the CI card images are successfully verified, otherwise, it returns a ResponseEntity with status NOT_FOUND.
     */
    @PostMapping("/verify/ciCard/image-verify/{userInfoId}")
    public Mono<ResponseEntity<UserInfoData>> verifyCiCard(@PathVariable Long userInfoId,
                                                           @RequestParam("frontImage") MultipartFile frontImage,
                                                           @RequestParam("backImage") MultipartFile backImage)  {
        return fptAiVision.getPersonalData(userInfoId, frontImage, backImage)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
