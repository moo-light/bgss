package com.server.blockchainserver.server;


import com.server.blockchainserver.dto.payment_dto.PaymentHistoryDTO;
import com.server.blockchainserver.dto.product_dto.ProductDTO;
import com.server.blockchainserver.dto.product_dto.ProductImagesDTO;
import com.server.blockchainserver.dto.shopping_dto.ReviewProductDTO;
import com.server.blockchainserver.dto.user_dto.UserDTO;
import com.server.blockchainserver.dto.user_dto.UserInfoDTO;
import com.server.blockchainserver.exeptions.*;
import com.server.blockchainserver.models.TransferUnitGold;
import com.server.blockchainserver.models.enums.*;
import com.server.blockchainserver.models.shopping_model.*;
import com.server.blockchainserver.models.user_model.Balance;
import com.server.blockchainserver.models.user_model.PaymentHistory;
import com.server.blockchainserver.models.user_model.User;
import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.payload.request.FromAndToRequest;
import com.server.blockchainserver.payload.request.TransferUnitGoldRequest;
import com.server.blockchainserver.payload.request.TypeGoldRequest;
import com.server.blockchainserver.payload.request.forum_request.PostRequest;
import com.server.blockchainserver.payload.request.shopping_request.*;
import com.server.blockchainserver.payload.response.*;
import com.server.blockchainserver.platform.entity.GoldTransaction;
import com.server.blockchainserver.platform.entity.WithdrawGold;
import com.server.blockchainserver.platform.entity.enums.TransactionStatus;
import com.server.blockchainserver.platform.entity.enums.TransactionType;
import com.server.blockchainserver.platform.entity.enums.TransactionVerification;
import com.server.blockchainserver.platform.entity.enums.WithdrawalStatus;
import com.server.blockchainserver.platform.repositories.*;
import com.server.blockchainserver.repository.ProductImageRepository;
import com.server.blockchainserver.repository.TransferUnitGoldRepository;
import com.server.blockchainserver.repository.discount_repository.DiscountCodeOfUserRepository;
import com.server.blockchainserver.repository.discount_repository.DiscountRepository;
import com.server.blockchainserver.repository.forum_repository.ForumRepository;
import com.server.blockchainserver.repository.forum_repository.PostRepository;
import com.server.blockchainserver.repository.forum_repository.RateRepository;
import com.server.blockchainserver.repository.shopping_repository.*;
import com.server.blockchainserver.repository.user_repository.RoleRepository;
import com.server.blockchainserver.repository.user_repository.UserInfoRepository;
import com.server.blockchainserver.repository.user_repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
//import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BlockchainServer {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    DiscountRepository discountRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    RateRepository rateRepository;
    @Autowired
    ProductCategoryRepository categoryRepository;
    @Autowired
    DiscountCodeOfUserRepository codeOfUserRepository;
    @Autowired
    ForumRepository forumRepository;
    @Autowired
    CategoryPostRepository categoryPostRepository;
    @Autowired
    ReviewProductRepository reviewProductRepository;
    @Autowired
    BalanceRepository balanceRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    GoldTransactionRepository goldTransactionRepository;
    @Autowired
    PaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    WithdrawGoldRepository withdrawGoldRepository;
    @Autowired
    UserGoldInventoryRepository userGoldInventoryRepository;
    @Autowired
    TypeGoldRepository typeGoldRepository;
    @Autowired
    ProductImageRepository productImageRepository;
    @Autowired
    TransferUnitGoldRepository transferUnitGoldRepository;
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Function handle logic for table Product

    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Not found product with id: " + productId));

        return mapToDTO(product);
    }

    public void checkValidationProduct(ProductRequest productRequest) {
        //        boolean containsCharacter = Long.toString(productRequest.getCategoryId()).chars().anyMatch(c -> c != '0');

        if ((productRequest.getProductName() == null || productRequest.getProductName().isEmpty())
                || (productRequest.getDescription() == null || productRequest.getDescription().isEmpty())
                || productRequest.getWeight() == null) {
            throw new ProductException("Please input in field is missing.");
        }
//
//        if(productRequest.getPrice() != null){
//            if (productRequest.getPrice().compareTo(BigDecimal.ZERO) < 0) {
//                throw new ProductException("The price cannot be negative");
//            }
//            if (!Pattern.matches("[0-9.]+", productRequest.getPrice().toString())) {
//                throw new ProductException("The price cannot contain character");
//            }
//        }

        if (productRequest.getTotalUnitOfStock() < 0) {
            throw new ProductException("The total unit of stock cannot be negative");
        }
        if (!Pattern.matches("[0-9.]+", productRequest.getTotalUnitOfStock().toString())) {
            throw new ProductException("The number cannot contain character");
        }
        if (!Pattern.matches("[0-9.]+", productRequest.getCategoryId().toString())) {
            throw new ProductException("Category Id is number cannot contain character");
        }
        if (productRequest.getWeight() < 0) {
            throw new ProductException("The weight of product cannot be negative");
        }
        if (!Pattern.matches("[0-9.]+", productRequest.getWeight().toString())) {
            throw new ProductException("The number cannot contain character");
        }
//        if(productRequest.getPercentageReduce() < 0){
//            throw new ProductException("The number percentage cannot be negative");
//        }
//        if (!Pattern.matches("[0-9.]+", productRequest.getPercentageReduce().toString())) {
//            throw new ProductException("The number cannot contain character");
//        }
    }

    // admin tạo sản phẩm và ảnh của sản phẩm
    public ProductDTO addNewProduct(ProductRequest productRequest) {
        checkValidationProduct(productRequest);

        ProductCategory category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(() -> new NotFoundException("Not found category with id: " + productRequest.getCategoryId()));
        TypeGold typeGold = typeGoldRepository.findById(productRequest.getTypeGoldId()).orElseThrow(() -> new NotFoundException("Not found type gold with id: " + productRequest.getTypeGoldId()));
        if (productRequest.getTypeOption().equals(EGoldOptionType.CRAFT)) {
            if (!typeGold.getTypeName().equals("24k gold")) {
                throw new ProductException("The craft gold can only be selected as 24k gold.");
            }
            if (productRequest.getProcessingCost() == null) {
                Product product = new Product(productRequest.getProductName(), productRequest.getDescription(), productRequest.getWeight(),
                        BigDecimal.ZERO, productRequest.getTotalUnitOfStock(), productRequest.getTotalUnitOfStock(),
                        0L, category, typeGold, productRequest.getTypeOption(), true);

                productRepository.save(product);
                return mapToDTO(product);
            }

        }
        Product product = new Product(productRequest.getProductName(), productRequest.getDescription(), productRequest.getWeight(),
                productRequest.getProcessingCost(), productRequest.getTotalUnitOfStock(), productRequest.getTotalUnitOfStock(),
                0L, category, typeGold, productRequest.getTypeOption(), true);
        productRepository.save(product);

        return mapToDTO(product);
    }

    // admin cập nhật lại thông tin của sản phẩm
    public ProductDTO updateProduct(Long productId, ProductRequest productRequest) {
        checkValidationProduct(productRequest);
        Product product = productRepository.findById(productId)
                        .orElseThrow(()-> new NotFoundException("Not found product with id: " + productId));

        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setWeight(productRequest.getWeight());
        product.setTotalUnitOfStock(product.getTotalUnitOfStock() + productRequest.getTotalUnitOfStock());
        product.setUnitOfStock(product.getUnitOfStock() + productRequest.getTotalUnitOfStock());

        if (!product.getTypeGoldOption().equals(EGoldOptionType.CRAFT)) {
            product.setProcessingCost(productRequest.getProcessingCost());
            product.setCategory(categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Not found category with id: " + productRequest.getCategoryId())));
            product.setTypeGold(typeGoldRepository.findById(productRequest.getTypeGoldId())
                    .orElseThrow(() -> new NotFoundException("Not found type gold with id: " + productRequest.getTypeGoldId())));
        }

        if (productRequest.getPercentageReduce() != null) {
            product.setPercentageReduce(productRequest.getPercentageReduce());
            BigDecimal priceProduct = product.getTypeGold().getPrice().multiply(BigDecimal.valueOf(product.getWeight())).add(product.getProcessingCost());
            BigDecimal priceReduce = priceProduct.multiply(BigDecimal.valueOf(productRequest.getPercentageReduce())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
            product.setSecondPrice(priceProduct.subtract(priceReduce));
        }

        productRepository.save(product);
        return mapToDTO(product);
    }

    public String defindTypeOfImg(MultipartFile file) {
        if (file == null) {
            return null;
        }
        String contentType = file.getContentType();
        assert contentType != null;
        String type = contentType.split("/")[1];
        if (type.equals("jpg")) type = "jpeg";
        if (type.contains("svg")) type = "svg";
        return type;
    }

    @Value("${upload.path}")
    private String uploadProduct;

    @Value("${upload.path.root}")
    private String uploadRoot;

    @Value("${save.path}")
    private String savePath;

    public void productImage(Product product, MultipartFile imgProduct, int index) throws IOException {
        if (imgProduct != null) {
            String type = defindTypeOfImg(imgProduct);
            String fileName = "PR000" + product.getId() + "_" + index + "." + type;

            saveImage(uploadProduct, imgProduct, fileName);
            saveImage(uploadRoot, imgProduct, fileName);

            // After saving the file, you can use imgUrl as required in your application
            String imgUrl = Paths.get(savePath, "Product", fileName).toString();
            ProductImage image = new ProductImage(imgUrl, product);
            productImageRepository.save(image);
        }
    }

    private void saveImage(String path, MultipartFile imgProduct, String fileName) throws IOException {
        Path directoryPath = Paths.get(path, "Product");
        Path filePath = Paths.get(path, "Product", fileName);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        try (InputStream input = imgProduct.getInputStream()) {
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Transactional
    public Product updateProductImgUrl(Long productId, ProductImageRequest imgProduct) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Not found product with id: " + productId));
        List<ProductImage> imgList = productImageRepository.findAllByProductId(product.getId());
        List<ProductImage> noKeepImages;
        List<ProductImage> keepImages;
        MultipartFile[] newImgs = imgProduct.getNewImgs();

//        if (!imgList.isEmpty()) {
            List<String> keepUrl = Arrays.stream(imgProduct.getOldImgs()).collect(Collectors.toList());

            keepImages = imgList.stream().filter(img -> keepUrl.contains(img.getImgUrl()))
                    .collect(Collectors.toList());
            noKeepImages = imgList.stream().filter(img -> !keepUrl.contains(img.getImgUrl()))
                    .collect(Collectors.toList());

            // delete first rename later approach
            productImageRepository.deleteAll(noKeepImages);
            for (ProductImage deleteImage : noKeepImages) {
                deleteImage.remove(uploadProduct);
                deleteImage.remove(uploadRoot);
            }

            // rename images by index
            EProductImageType[] indexes = imgProduct.getIndexes();
            for (int i = 0, j = 0, k = 0; i < indexes.length; i++) {
                switch (indexes[i]) {
                    case URL -> {
                        ProductImage keepImage = keepImages.get(j++);
                        // change index of url
                        String newImgUrl = ProductImage.replaceIndex(keepImage.getImgUrl(), i);
                        // rename the file
                        boolean check = keepImage.move(newImgUrl, uploadRoot);
                        if (check) {
                            keepImage.move(newImgUrl, uploadProduct);
                            productImageRepository.save(keepImage);
                        }
                    }
                    case FILE -> {
                        MultipartFile image = newImgs[k++];
                        productImage(product, image, i);
                    }
                }
            }
//        }

        List<ProductImage> imgNewList = productImageRepository.findAllByProductId(product.getId());
        product.setProductImages(imgNewList);
        return productRepository.save(product);
    }

    public boolean deleteProduct(Long productId) {
        Product product =  productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Not found product with id: " + productId));
        product.setActive(false);
        productRepository.save(product);
        return true;
    }

    public List<Product> searchProductByName(String search) {
        List<Product> listProduct = productRepository.findAll();
        return listProduct.stream().filter(product -> product.getProductName().toLowerCase().contains(search.toLowerCase().trim())).collect(Collectors.toList());
    }

    public List<Product> getAllProductByTypeName(String typeGoldName, String search) {
        if (typeGoldName == null || typeGoldName.isEmpty()) {
            throw new ProductException("Please input type gold name.");
        }

        TypeGold typeGold = typeGoldRepository.findByTypeName(typeGoldName.trim());
        List<Product> productList = new ArrayList<>();
        boolean check = true;

        if (typeGold != null) {
            List<Product> listActive = productRepository.findAllByTypeGoldId(typeGold.getId());
            productList = listActive.stream().filter(product -> product.isActive() == check).collect(Collectors.toList());
            productList = productList.stream().filter(product -> product.getTypeGoldOption().equals(EGoldOptionType.CRAFT)).toList();

            if (StringUtils.hasText(search)) {
                productList = productList.stream().filter(product -> product.getProductName().toLowerCase().contains(search.trim().toLowerCase())).collect(Collectors.toList());
            }
        }
        return productList;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Function xử lí logic cho table Cart và CartItem

    public List<CartItem> getListCartItemOfUser(Long userId) {
        Cart cart = cartRepository.findCartByUserId(userId).orElseThrow(() -> new NotFoundException("Not found cart with userId: " + userId));
        List<CartItem> itemList = cartItemRepository.findAllByCartId(cart.getId());

        if (!itemList.isEmpty()) {
            for (CartItem item : itemList) {
                Product product = productRepository.findById(item.getProduct().getId()).
                        orElseThrow(() -> new NotFoundException("Not found product"));
                if (product.getSecondPrice() != null) {
                    item.setPrice(product.getSecondPrice());
                    item.setAmount(product.getSecondPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }
            cartItemRepository.saveAll(itemList);
            Collections.reverse(itemList);
        }
        return itemList;
    }

//    public List<CartItem> getListCartItem(Long userId) {
//        Cart cart = cartRepository.findCartByUserId(userId).orElseThrow(() -> new NotFoundException("Not found cart with userId: " + userId));
//        List<CartItem> itemList = cartItemRepository.findAllByCartId(cart.getId());
//
//        Collections.reverse(itemList);
//        return itemList;
//    }

    // lấy giỏ hàng của 1 user
    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findCartByUserId(userId).orElseThrow(() -> new NotFoundException("Not found cart of user with userId: " + userId));
        if (cart != null) {
            return cart;
        } else {
            throw new CartException("Get cart by userId: " + userId + " fail");
        }
    }

    // tạo giỏ hàng, thêm sản phẩm vào giỏ hàng
    public CartItem addProductToCart(ProductToCartRequest productToCartRequest) {
        checkQuantityIsNegative(productToCartRequest.getQuantity());
        User user = userRepository.findById(productToCartRequest.getUserId()).orElseThrow(() -> new NotFoundException("Not found user with id: " + productToCartRequest.getUserId()));
        Cart existingCartOfUser = cartRepository.findCartByUserId(user.getId()).orElse(null);
        Product product = productRepository.findById(productToCartRequest.getProductId())
                .orElseThrow(() -> new NotFoundException("Not found product"));
        Long availableQuantity;
        BigDecimal priceGenerate;

        if (product != null) {
            availableQuantity = product.getUnitOfStock();

            if (existingCartOfUser == null) {
                existingCartOfUser = new Cart(userRepository.getById(productToCartRequest.getUserId()));
                cartRepository.save(existingCartOfUser);

                CartItem cartItem = new CartItem();
                cartItem.setCart(existingCartOfUser);
                cartItem.setProduct(product);
                cartItem.setQuantity(productToCartRequest.getQuantity());
                if (productToCartRequest.getQuantity() > availableQuantity) {
                    throw new CartException("You cannot buy exceed quantity of available product in stock. The available quantity of product is: " + availableQuantity);
                }

                if (product.getSecondPrice() != null) {
                    priceGenerate = product.getSecondPrice();
                    cartItem.setPrice(priceGenerate);
                } else {
                    priceGenerate = product.getTypeGold().getPrice().multiply(BigDecimal.valueOf(product.getWeight()))
                            .add(product.getProcessingCost());
                    cartItem.setPrice(priceGenerate);
                }
                cartItem.setAmount(priceGenerate.multiply(BigDecimal.valueOf(productToCartRequest.getQuantity())));
                return cartItemRepository.save(cartItem);

            } else {
                List<CartItem> cartItemList = cartItemRepository.findAllByCartId(existingCartOfUser.getId());
                CartItem existingItem;
                if (cartItemList.isEmpty()) {
                    CartItem cartItem = new CartItem();
                    cartItem.setCart(existingCartOfUser);
                    cartItem.setProduct(product);
                    cartItem.setQuantity(productToCartRequest.getQuantity());
                    if (productToCartRequest.getQuantity() > availableQuantity) {
                        throw new CartException("You cannot buy exceed quantity of available product in stock. The available quantity of product is: " + availableQuantity);
                    }

                    if (product.getSecondPrice() != null) {
                        priceGenerate = product.getSecondPrice();
                        cartItem.setPrice(priceGenerate);
                    } else {
                        priceGenerate = product.getTypeGold().getPrice().multiply(BigDecimal.valueOf(product.getWeight()))
                                .add(product.getProcessingCost());
                        cartItem.setPrice(priceGenerate);
                    }
                    cartItem.setAmount(priceGenerate.multiply(BigDecimal.valueOf(productToCartRequest.getQuantity())));
                    return cartItemRepository.save(cartItem);

                } else {
                    existingItem = cartItemList.stream().filter(item -> item.getProduct().getId().equals(productToCartRequest.getProductId())).findFirst().orElse(null);

                    if (existingItem == null) {
                        CartItem cartItem = new CartItem();
                        cartItem.setCart(existingCartOfUser);
                        cartItem.setProduct(product);
                        cartItem.setQuantity(productToCartRequest.getQuantity());
                        if (productToCartRequest.getQuantity() > availableQuantity) {
                            throw new CartException("You cannot buy exceed quantity of available product in stock. The available quantity of product is: " + availableQuantity);
                        }

                        if (product.getSecondPrice() != null) {
                            priceGenerate = product.getSecondPrice();
                            cartItem.setPrice(priceGenerate);
                        } else {
                            priceGenerate = product.getTypeGold().getPrice().multiply(BigDecimal.valueOf(product.getWeight()))
                                    .add(product.getProcessingCost());
                            cartItem.setPrice(priceGenerate);
                        }
                        cartItem.setAmount(priceGenerate.multiply(BigDecimal.valueOf(productToCartRequest.getQuantity())));
                        return cartItemRepository.save(cartItem);

                    } else {
                        long quantityUpdate = existingItem.getQuantity() + productToCartRequest.getQuantity();
                        if (quantityUpdate > availableQuantity) {
                            throw new CartException("You cannot buy exceed quantity of available product in stock. The available quantity of product is: " + availableQuantity);
                        }
                        existingItem.setQuantity(quantityUpdate);
                        if (product.getSecondPrice() == null) {
                            priceGenerate = product.getTypeGold().getPrice().multiply(BigDecimal.valueOf(product.getWeight()))
                                    .add(product.getProcessingCost());
                            existingItem.setPrice(priceGenerate);
                        } else {
                            priceGenerate = product.getSecondPrice();
                            existingItem.setPrice(priceGenerate);
                        }

                        existingItem.setAmount(priceGenerate.multiply(BigDecimal.valueOf(quantityUpdate)));

                        return cartItemRepository.save(existingItem);
                    }
                }
            }
        } else {
            throw new NotFoundException("Not found product with id: " + productToCartRequest.getProductId());
        }
    }

    // update lại số lượng mà user muốn trong giỏ hàng
    public CartItem updateQuantityProductInCartItem(Long cartItemId, Long quantity) {
        checkQuantityIsNegative(quantity);
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new NotFoundException("Product in cart not found with cartItemId: " + cartItemId));

        if (quantity <= 0) {
            quantity = 1L;
        }
        if (quantity > cartItem.getProduct().getUnitOfStock()) {
            quantity = cartItem.getProduct().getUnitOfStock();
        }
        cartItem.setQuantity(quantity);
        cartItem.setAmount(cartItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return cartItemRepository.save(cartItem);
    }

    // xóa luôn 1 product ra khỏi giỏ hàng
    public boolean removeProductFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new NotFoundException("Not found cart item with id: " + cartItemId));
        cartItemRepository.delete(cartItem);
        return true;
    }

    private void checkQuantityIsNegative(Long quantity) {
        if (quantity < 0) {
            throw new CartException("The quantity cannot be negative.");
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Function xử lí logic cho table Order và OrderDetail

    public Order createOrder(Long userId, CreateOrderRequest createOrderRequest) {
        List<CartItem> listCartItem = new ArrayList<>();

        for (Long cartItemId : createOrderRequest.getListCartId()) {
            CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new NotFoundException("Not found cart item with id: " + cartItemId));
            if (cartItem != null) {
                listCartItem.add(cartItem);
            }
        }
        BigDecimal originTotalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : listCartItem) {
            originTotalAmount = originTotalAmount.add(cartItem.getAmount());
        }

        Order order = new Order();
        order.setUser(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found this user ID" + userId)));
        // thời gian theo utc +0 chậm hơn 7h so với Việt Nam
        order.setCreateDate(Instant.now());

        BigDecimal discountPrice = BigDecimal.ZERO;
        boolean check = true;
        if (StringUtils.hasText(createOrderRequest.getDiscountCode())) {
            List<DiscountCodeOfUser> listDiscounts = codeOfUserRepository.findAllByUserId(userId);
            for (DiscountCodeOfUser discountUser : listDiscounts) {
                Discount discount = discountRepository.findById(discountUser.getDiscount().getId()).orElseThrow(() -> new NotFoundException("Not found discount code with id: " + discountUser.getDiscount().getId()));


                if (discount.getCode().equals(createOrderRequest.getDiscountCode())) {
                    checkConditionOrderByCartItem(originTotalAmount, listCartItem, discount);
                    if (discountUser.isAvailable() == check) { // kiểm tra xem mã giảm giá đã được sử dụng hay chưa
                        Instant dateTimeNow = Instant.now();
                        if (discount.isExpire() != check && discount.getDateExpire().isAfter(dateTimeNow)) {
                            // kiểm tra xem mã giảm giá có còn hạn sử dụng không
                            discountPrice = originTotalAmount.multiply(BigDecimal.valueOf(discount.getPercentage())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
                            // check số tiền được giảm có vượt quá quy định
                            if (discountPrice.compareTo(discount.getMaxReduce()) > 0) {
                                discountPrice = discount.getMaxReduce();
                            }
                            order.setDiscountCode(discount.getCode());
                            order.setPercentageDiscount(discount.getPercentage());
                            discountUser.setAvailable(false); // mã giảm giá đã được dùng sẽ đưa về trạng thái hết hạn
                            discountUser.setQuantity_default(0);
                            codeOfUserRepository.save(discountUser);
                        } else {
                            throw new OrderException("The discount code you use already expired.");
                        }
                    } else {
                        throw new OrderException("The discount code you use already used.");
                    }
                }
            }
        }

        order.setQrCode(randomCode(8));

        // đơn hàng mới tạo sẽ mang trạng thái là "chưa nhận", "đã nhận" sẽ được cập
        // nhật khi khách đến của hàng lấy
        order.setStatusReceived(EReceivedStatus.NOT_RECEIVED);
        order.setTotalAmount(originTotalAmount.subtract(discountPrice));
        order.setConsignment(createOrderRequest.getIsConsignment());

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        UserInfo userInfo = userInfoRepository.findByUserId(user.getId());

        order.setFirstName(user.getFirstName());
        order.setLastName(user.getLastName());
        order.setEmail(user.getEmail());
        order.setPhoneNumber(user.getPhoneNumber());
        order.setAddress(userInfo.getAddress());
        order.setUserConfirm(EUserConfirm.NOT_RECEIVED);

        Balance balance = balanceRepository.findBalanceByUserInfoId(userInfo.getId()).orElseThrow(() -> new NotFoundException("Not found balance with user info id: " + userInfo.getId()));
        if (balance.getBalance().compareTo(order.getTotalAmount()) < 0) {
            throw new OrderException("Wallet balance is insufficient for payment of this order");
        }


        // tạo ra ordertail, với "vàng kxi thuật số" thì sẽ có "lấy về" và "kí gửi", còn "vàng chế tác" thì chỉ có trạng thái "lấy về"
        List<OrderDetail> orderDetails = listCartItem.stream().map(cartItem -> new OrderDetail(order, cartItem, EProcessReceiveProduct.PENDING)).collect(Collectors.toList());
        unitOfStockOfProduct(orderDetails);

        if (orderDetails.isEmpty()) {
            throw new OrderException("Error when create order detail.");
        }

        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        order.setOrderDetails(orderDetails);

        cartItemRepository.deleteAllById(createOrderRequest.getListCartId());

        balance.setBalance(balance.getBalance().subtract(order.getTotalAmount()));
        balanceRepository.save(balance);

        return order;
    }

    public int getQuantityOrderDetail(List<CartItem> listCartItem) {
        int quantity = 0;
        for (CartItem item : listCartItem) {
            quantity = quantity + Integer.parseInt(item.getQuantity().toString());
        }

        return quantity;
    }

    public void checkConditionOrderByCartItem(BigDecimal originTotalAmount, List<CartItem> listCartItem, Discount discount) {
        int quantity = getQuantityOrderDetail(listCartItem);

        if (discount.getMinPrice().compareTo(originTotalAmount) > 0) {
            throw new OrderException("Let buy enough: " + discount.getMinPrice() + " to can use this discount code.");
        }
        if (discount.getQuantityMin() > quantity) {
            throw new OrderException("Order must contain " + discount.getQuantityMin() + " or more items");
        }
    }

//    public void checkConditionOrderNow(BigDecimal totalOriginPrice, Discount discount, CreateOrderNowRequest requestNow) {
//
//        if (discount.getMinPrice().compareTo(totalOriginPrice) < 0) {
//            throw new OrderException("Let buy enough: " + discount.getMinPrice() + " to can use this discount code.");
//        }
//        if (discount.getQuantityMin() > requestNow.getQuantity()) {
//            throw new OrderException("Order must contain " + discount.getQuantityMin() + " or more items");
//        }
//    }

//    public Order createOrderNow(Long userId, CreateOrderNowRequest request) {
//        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new NotFoundException("Not found product with id: " + request.getProductId()));
//
//        Order order = new Order();
//        order = useDiscountInOrder(userId, order, request, product);
//        order.setUser(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found this user ID" + userId)));
//        // thời gian theo utc +0 chậm hơn 7h so với Việt Nam
//        order.setCreateDate(Instant.now());
//        order.setStatusReceived(EReceivedStatus.NOT_RECEIVED);
//        order.setConsignment(request.getIsConsignment());
//
//        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
//        UserInfo userInfo = userInfoRepository.findByUserId(user.getId());
//
//        order.setFirstName(user.getFirstName());
//        order.setLastName(user.getLastName());
//        order.setEmail(user.getEmail());
//        order.setPhoneNumber(user.getPhoneNumber());
//        order.setAddress(userInfo.getAddress());
//        order.setUserConfirm(EUserConfirm.NOT_RECEIVED);
//
//        Balance balance = balanceRepository.findBalanceByUserInfoId(userInfo.getId()).orElseThrow(() -> new NotFoundException("Not found balance with user info id: " + userInfo.getId()));
//        if (balance.getBalance().compareTo(order.getTotalAmount()) < 0) {
//            throw new OrderException("Wallet balance is insufficient for payment of this order");
//        }
//        orderRepository.save(order);
//
//        // tạo ra ordertail, với "vàng kxi thuật số" thì sẽ có "lấy về" và "kí gửi", còn "vàng chế tác" thì chỉ có trạng thái "lấy về"
//        List<OrderDetail> orderDetails = new ArrayList<>();
//        OrderDetail orderDetail = new OrderDetail(order, product, request.getQuantity(), product.getPrice(), order.getTotalAmount(), EProcessReceiveProduct.PENDING);
//        orderDetails.add(orderDetail);
//
//        product.setUnitOfStock(product.getUnitOfStock() - request.getQuantity());
//        productRepository.save(product);
//        orderDetailRepository.save(orderDetail);
//        order.setOrderDetails(orderDetails);
//
//        if (orderDetails.isEmpty()) {
//            throw new OrderException("Error when create order detail.");
//        }
//        balance.setBalance(balance.getBalance().subtract(order.getTotalAmount()));
//        balanceRepository.save(balance);
//
//        return order;
//    }

//    public Order useDiscountInOrder(Long userId, Order order, CreateOrderNowRequest request, Product product) {
//        BigDecimal totalOriginPrice = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
//
//        BigDecimal discountPrice = BigDecimal.ZERO;
//        boolean check = true;
//        if (StringUtils.hasText(request.getDiscountCode())) {
//            List<DiscountCodeOfUser> listDiscounts = codeOfUserRepository.findAllByUserId(userId);
//            for (DiscountCodeOfUser discountUser : listDiscounts) {
//                Discount discount = discountRepository.findById(discountUser.getDiscount().getId()).orElseThrow(() -> new NotFoundException("Not found discount code with id: " + discountUser.getDiscount().getId()));
//                checkConditionOrderNow(totalOriginPrice, discount, request);
//
//                if (discount.getCode().equals(request.getDiscountCode())) {
//                    if (discountUser.isAvailable() == check) { // kiểm tra xem mã giảm giá đã được sử dụng hay chưa
//                        Instant dateTimeNow = Instant.now();
//                        if (discount.isExpire() != check && discount.getDateExpire().isAfter(dateTimeNow)) {
//                            // kiểm tra xem mã giảm giá có còn hạn sử dụng không
//                            discountPrice = totalOriginPrice.multiply(BigDecimal.valueOf(discount.getPercentage())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
//
//                            // check số tiền được giảm có vượt quá quy định
//                            if (discountPrice.compareTo(discount.getMaxReduce()) > 0) {
//                                discountPrice = discount.getMaxReduce();
//                            }
//                            order.setDiscountCode(discount.getCode());
//                            order.setPercentageDiscount(discount.getPercentage());
//                            discountUser.setAvailable(false); // mã giảm giá đã được dùng sẽ đưa về trạng thái hết hạn
//                            discountUser.setQuantity_default(0);
//                            codeOfUserRepository.save(discountUser);
//                        } else {
//                            throw new OrderException("The discount code you use already expired.");
//                        }
//                    } else {
//                        throw new OrderException("The discount code you use already used.");
//                    }
//                }
//            }
//        }
//
//        order.setTotalAmount(totalOriginPrice.subtract(discountPrice));
//        return order;
//    }

    // get order by id
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Not found order with id: " + orderId));
    }

    // show ra những sản phẩm mà user đã thêm vào giỏ hàng
    public List<Order> getOrderListFromUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<Order> orderList = orderRepository.findAllByUserId(userId);
            Collections.reverse(orderList);
            return orderList;
        } else {
            throw new NotFoundException("Not found user with id: " + userId);
        }
    }

    // trừ số đã mua sản phẩm đã mua đi trong kho
    public void unitOfStockOfProduct(List<OrderDetail> orderDetails) {
        if (orderDetails.isEmpty()) {
            throw new OrderException(("The list order detail when create order is empty"));
        }
        for (OrderDetail orderDetail : orderDetails) {
            Product product = productRepository.findById(orderDetail.getProduct().getId()).orElseThrow(() -> new NotFoundException("Not found product with id: " + orderDetail.getProduct().getId()));
            product.setUnitOfStock(product.getUnitOfStock() - orderDetail.getQuantity());
            product.setTotalProductSold(product.getTotalProductSold() + orderDetail.getQuantity());
            productRepository.save(product);
        }
    }

    // tất cả sản phầm trong 1 giỏ hàng
    public List<OrderDetail> getAllOrderDetailByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Not found order with id: " + orderId));
        return orderDetailRepository.findAllByOrderId(order.getId());
    }

    // tìm kiếm hóa đơn đã thanh toán bằng mã qrcode để khách nhận đồ đã mua
    public Order searchOrderByQrCode(String code) {
        Order order = orderRepository.findByQrCode(code.trim()).orElseThrow(() -> new NotFoundException("Order with this code is not found!"));

        if (order.getStatusReceived().equals(EReceivedStatus.UNVERIFIED)) {
            throw new OrderException("The order not yet verify OTP. Please verify OTP to search or scan QR code.");
        }

        if (!order.getStatusReceived().equals(EReceivedStatus.RECEIVED)) {
            updateOrderToConfirm(order);
        }

        return order;
    }

    private void updateOrderToConfirm(Order order) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderId(order.getId());
        orderDetails.forEach(orderDetail -> orderDetail.setProcessReceiveProduct(EProcessReceiveProduct.CONFIRM));
        orderDetailRepository.saveAll(orderDetails);
    }

    public Order updateStatusReceived(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Not found order with id: " + orderId));
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderId(order.getId());

        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getProcessReceiveProduct().equals(EProcessReceiveProduct.CONFIRM)) {
                order.setStatusReceived(EReceivedStatus.RECEIVED);
                orderDetail.setProcessReceiveProduct(EProcessReceiveProduct.COMPLETE);
            } else {
                throw new OrderException("Only Admin or Staff can update the order status to 'received' when confirmed QR code in-store.");
            }
        }
        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }

    public Order userConfirmReceived(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Not found order with id: " + orderId));
        if (order.getStatusReceived().equals(EReceivedStatus.RECEIVED) && order.getUserConfirm().equals(EUserConfirm.RECEIVED)) {
            throw new OrderException("Customers cannot confirm after the order is completed on both sides.");
        }

        if (order.getStatusReceived().equals(EReceivedStatus.RECEIVED)) {
            order.setUserConfirm(EUserConfirm.RECEIVED);
        } else {
            throw new OrderException("User only confirm received when admin or staff checked.");
        }

        return orderRepository.save(order);
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Function xử lí logic cho table Discount

    public String randomCode(int len) {
        Random rnd = new Random();
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }


    // admin tạo ra mã giảm giá, có ngày hết hạn
    public Discount createDiscountCode(DiscountCodeRequest discountCodeRequest) {
        validationDiscount(discountCodeRequest);

        Instant dateTimeCreate = Instant.now();
        Instant dateTimeExpire = discountCodeRequest.getDateExpire();

        if (dateTimeExpire.isBefore(dateTimeCreate)) {
            throw new DiscountCodeException("The expiration date cannot be before the current date. Please enter a valid expiration date.");
        }
        Discount discount = new Discount(randomCode(8), discountCodeRequest.getDiscountPercentage(), discountCodeRequest.getMinPrice(), discountCodeRequest.getMaxReduce(), discountCodeRequest.getQuantityMin(), discountCodeRequest.getDefaultQuantity(), dateTimeCreate, dateTimeExpire, false, EStatusDiscount.APPLY);
        return discountRepository.save(discount);
    }

    // khi load sẽ check xem mã giảm giá đã hết hạn chưa, nếu rồi sẽ cập nhật lại trạng thái mã giảm giá
    public List<Discount> getAllDiscountCode() {
        List<Discount> discountList = discountRepository.findAll();
        checkExpired(discountList);
        return discountList.stream().filter(discount -> discount.getStatusDiscount().equals(EStatusDiscount.APPLY)).toList();
    }

    public void checkExpired(List<Discount> discountList) {
        Instant dateTimeNow = Instant.now();
        boolean check = false;

        if (!discountList.isEmpty()) {
            for (Discount discount : discountList) {
                List<DiscountCodeOfUser> codeOfUserList = codeOfUserRepository.findAllByDiscountId(discount.getId());
                if (discount.isExpire() == check) {
                    if (discount.getDateExpire().isBefore(dateTimeNow)) {
                        discount.setExpire(true);
                        discount.setStatusDiscount(EStatusDiscount.NOT_APPLY);
                    }
                }
                if (discount.isExpire()) {
                    setExpired(discount, codeOfUserList);
                }
            }
        }
        discountRepository.saveAll(discountList);
    }

    // xem chi tiết 1 mã giảm giá, và khi load sẽ check xem mã giảm giá đã hết hạn chưa, nếu rồi sẽ cập nhật lại trạng thái mã giảm giá
    public Discount getDiscountCodeById(Long discountId) {
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new NotFoundException("Not found discount code with id: " + discountId));
        List<DiscountCodeOfUser> codeOfUserList = codeOfUserRepository.findAllByDiscountId(discount.getId());
        Instant dateTimeNow = Instant.now();
        boolean check = false;

        if (discount.isExpire() == check) {
            if (discount.getDateExpire().isBefore(dateTimeNow)) {
                discount.setExpire(true);
            }
        }
        if (discount.isExpire()) {
            setExpired(discount, codeOfUserList);
        }
        return discountRepository.save(discount);
    }

//    public Discount getDiscountCodeByCode(String code) {
//        Discount discount = discountRepository.findByCode(code).orElse(null);
//        if (discount == null || discount.isExpire()) return null;
//        return discount;
//    }

    public void setExpired(Discount discount, List<DiscountCodeOfUser> codeOfUserList) {
        boolean check = true;
        if (discount != null) {
            for (DiscountCodeOfUser codeOfUser : codeOfUserList) {
                if (codeOfUser.isAvailable() == check) {
                    if (codeOfUser.getDiscount().getId().equals(discount.getId())) {
                        codeOfUser.setAvailable(false);
                    }
                }
            }
            codeOfUserRepository.saveAll(codeOfUserList);
        } else {
            throw new DiscountCodeException("Discount is empty.");
        }
    }

    public boolean deleteDiscountCode(Long discountId) {
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new NotFoundException("Not found discount code with id: " + discountId));
        List<DiscountCodeOfUser> discountCodeOfUserList = codeOfUserRepository.findAllByDiscountId(discount.getId());

        codeOfUserRepository.deleteAll(discountCodeOfUserList);
        discountRepository.delete(discount);
        return true;
    }

    public void validationDiscount(DiscountCodeRequest discountCodeRequest) {
        if (discountCodeRequest.getDateExpire() == null) {
            throw new DiscountCodeException("Please input in field is missing.");
        }
        if (discountCodeRequest.getDiscountPercentage() < 0) {
            throw new DiscountCodeException("The percentage cannot be negative.");
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Function xử lí logic cho table Post và PostImage

    public List<Post> getAllPost() {
        List<Post> postList = postRepository.findAll();
        List<Post> list = new ArrayList<>();
        boolean check = false;

        if (!postList.isEmpty()) {
            for (Post post : postList) {
                if (post.isHide() == check) {
                    list.add(post);
                }
            }
        } else {
            throw new PostException("The list posts are empty.");
        }
        return list;
    }

    public List<Post> getFilterAndSortedPost(String search, Optional<Long> categoryId, Post.FilterHideEnum filterHideEnum, Instant fromDate, Instant toDate, boolean asc) {
        Sort.Direction direction = asc ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Post> postList = postRepository.findAll(Sort.by(direction, "id"));
        // Enum filter
        if (filterHideEnum.equals(Post.FilterHideEnum.DEFAULT)) {
            postList = postList.stream().filter(post -> !post.isHide()).collect(Collectors.toList());
        } else if (filterHideEnum.equals(Post.FilterHideEnum.HIDE)) {
            postList = postList.stream().filter(Post::isHide).collect(Collectors.toList());
        }
        // category filter
        if (categoryId.isPresent()) {
            // Validate category existence
            categoryRepository.findById(categoryId.get()).orElseThrow(() -> new BadRequestException("Category Not Exist"));

            // Filter posts by category ID
            postList = postList.stream().filter(post -> post.getCategoryPost().getId().equals(categoryId.get())).collect(Collectors.toList());
        }
        // date filter
        if (fromDate != null || toDate != null) {
            if (fromDate != null) {
                postList = postList.stream().filter(post -> post.getCreateDate().isAfter(fromDate)).collect(Collectors.toList());
            }
            if (toDate != null) {
                postList = postList.stream().filter(post -> post.getCreateDate().isBefore(toDate)).collect(Collectors.toList());
            }
        }
        // search
        if (StringUtils.hasText(search)) {
            // Filter posts by search keyword in title
            postList = postList.stream().filter(post -> post.getTitle().toLowerCase().contains(search.toLowerCase().trim())).collect(Collectors.toList());
        }
        return postList;
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Not found post with id: " + postId));
    }

    // admin hoặc staff tạo bài post, và tạo ra các ảnh trong bài post nếu có
    public Post createPost(Long userId, PostRequest postRequest, MultipartFile imgUrl) throws IOException {
        checkFieldPost(postRequest);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        CategoryPost categoryPost = categoryPostRepository.findById(postRequest.getCategoryPostId()).orElseThrow(() -> new NotFoundException("Not found category of post with id: " + postRequest.getCategoryPostId()));
        Instant dateTimeCreate = Instant.now();

        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), dateTimeCreate, postRequest.getIsPinned(), false, categoryPost, user);
        Post savePost = postRepository.save(post);

        if (imgUrl != null) {
            if (imgUrl.getOriginalFilename() != null && !imgUrl.isEmpty()) {
                createRPostImage(post, imgUrl);
            }
        }

        return savePost;
    }

    @Value("${upload.path}")
    private String uploadPost;

    public void createRPostImage(Post post, MultipartFile image) throws IOException {
        String type = defindTypeOfImg(image);
        String fileName = "P000" + post.getId() + "." + type;
        saveImagePost(uploadPost, image, fileName);
        saveImagePost(uploadRoot, image, fileName);

        // After saving the file, you can use imgUrl as required in your application
        String imgUrl = Paths.get(savePath, "Post", fileName).toString();

        post.setImgUrl(imgUrl);
        postRepository.save(post);
    }

    public void saveImagePost(String path, MultipartFile imgPost, String fileName) throws IOException {
        Path directoryPath = Paths.get(path, "Post");
        Path filePath = Paths.get(path, "Post", fileName);

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        try (InputStream input = imgPost.getInputStream()) {
            // Check if the file already exists
            if (Files.exists(filePath)) {
                // File exists, so we overwrite it
                Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // File does not exist, so we simply copy it
                Files.copy(input, filePath);
            }
        }
    }

    // cập nhật lại bài post và ảnh nếu cần
    public Post updatePost(Long postId, PostRequest postRequest, MultipartFile imgUrl) throws IOException {
        checkFieldPost(postRequest);
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Not found post with id: " + postId));
        CategoryPost categoryPost = categoryPostRepository.findById(postRequest.getCategoryPostId()).orElseThrow(() -> new NotFoundException("Not found category of post with id: " + postRequest.getCategoryPostId()));
        Instant dateTimeUpdate = Instant.now();

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setUpdateDate(dateTimeUpdate);
        post.setPinned(postRequest.getIsPinned());
        post.setCategoryPost(categoryPost);
        postRepository.save(post);

        if (imgUrl != null) {
            if (imgUrl.getOriginalFilename() != null && !imgUrl.isEmpty()) {
                createRPostImage(post, imgUrl);
            }
        } else {
            post.setImgUrl(null);
            postRepository.save(post);
        }
        return post;
    }

    // xóa bài post, là ẩn bài post đi chứ không xóa trong db
    public Post deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Not found post with id: " + postId));
        Instant dateTimeDelete = Instant.now();

        post.setHide(true);
        post.setDeleteDate(dateTimeDelete);

        List<Rate> rateList = rateRepository.findRatesByPostId(post.getId());
        rateRepository.saveAll(rateList.stream().peek(rate -> rate.setHide(true)).collect(Collectors.toList()));

        return postRepository.save(post);
    }

    // search những bài post dựa vào tiêu đề của bài post
    public List<Post> searchPostByTitle(String search) {
        List<Post> listPost = postRepository.findAll();
        List<Post> listSearch = new ArrayList<>();
        boolean check = false;

        if (!listPost.isEmpty()) {
            for (Post post : listPost) {
                if (post.getTitle().toLowerCase().contains(search.toLowerCase().trim())) {
                    if (post.isHide() == check) {
                        listSearch.add(post);
                    }
                }
            }

        }
        return listSearch;
    }

    public List<Post> getAllPostByCategoryPostId(Long categoryPostId, String search) {
        List<Post> postList = postRepository.findAllByCategoryPostId(categoryPostId);

        postList = postList.stream().filter(t -> t.isHide() == false).collect(Collectors.toList());
        if (StringUtils.hasText(search))
            postList = postList.stream().filter(t -> t.getTitle().toLowerCase().contains(search.toLowerCase().trim())).collect(Collectors.toList());

        return postList;
    }

    public List<Post> getAllPostWithIsPinned() {
        List<Post> postList = postRepository.findAll();
        List<Post> list = new ArrayList<>();

        if (!postList.isEmpty()) {
            for (Post post : postList) {
                if (post.isPinned()) {
                    list.add(post);
                }
            }
        }
        return list;
    }

    private void checkFieldPost(PostRequest postRequest) {
        if (postRequest.getTitle() == null || postRequest.getTitle().isEmpty() || postRequest.getCategoryPostId() == null) {
            throw new PostException("Please input in field is missing.");
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Function xử lí logic cho table Rate và RateImage

    // admin quản lí tất cả các rate trong tất cả post
    public List<Rate> getAllRate(String search) {
        List<Rate> rates = rateRepository.findAll();

        if (StringUtils.hasText(search)) {
            return searchRateByContent(search);
        }

        return rates;
    }

    public List<Rate> checkRatesIsHide(List<Rate> rateList) {
        List<Rate> list = new ArrayList<>();
        boolean check = false;

        for (Rate rate : rateList) {
            if (rate.isHide() == check) {
                list.add(rate);
            }
        }
        Collections.reverse(list);
        return list;
    }

    // show hết những bình luận của 1 người trong 1 bài post mà admin đã đăng
    public List<Rate> getAllRateByUserIdAndPostId(Long userId, Long postId) {
        List<Rate> rateList = rateRepository.findRatesByUserIdAndPostId(userId, postId);

        if (!rateList.isEmpty()) {
            return checkRatesIsHide(rateList);
        } else {
            throw new RateException("The list rates are empty.");
        }
    }

    // show ra hết những bình luận của 1 bài post
    public List<Rate> getAllRateOfPost(Long postId) {
        List<Rate> rateList = rateRepository.findRatesByPostId(postId);
        if (!rateList.isEmpty()) {
            return checkRatesIsHide(rateList);
        } else {
            throw new RateException("The list rates are empty.");
        }
    }

    // chi tiết 1 bình luận
    public Rate getRateById(Long rateId) {
        return rateRepository.findById(rateId).orElseThrow(() -> new NotFoundException("Not found rate with id: " + rateId));
    }

    // khi user bình luận trong 1 bài post thì comment đó sẽ được khỏi tạo bao gồm
    // cả ảnh nếu có và lưu vào db
    public Rate createRate(Long userId, Long postId, String content, MultipartFile imageRate) throws IOException {
        checkNullSame(content, imageRate);
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Not found post with id: " + postId));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        Instant dateTimeCreate = Instant.now();

        Rate rate = new Rate(post, content, dateTimeCreate, false, user);
        Rate saveRate = rateRepository.save(rate);

        if (imageRate != null) {
            if (imageRate.getOriginalFilename() != null && !imageRate.isEmpty()) {
                createRateImage(rate, imageRate);
            }
        }

        return saveRate;
    }

    @Value("${upload.path}")
    private String uploadRate;

    public void createRateImage(Rate rate, MultipartFile image) throws IOException {
        String type = defindTypeOfImg(image);
        String fileName = "R000" + rate.getId() + "." + type;

        saveImageRate(uploadRate, image, fileName);
        saveImageRate(uploadRoot, image, fileName);

        // After saving the file, you can use imgUrl as required in your application
        String imgUrl = Paths.get(savePath, "Rate", fileName).toString();

        rate.setImgUrl(imgUrl);
        rateRepository.save(rate);
    }

    public void saveImageRate(String path, MultipartFile imgRate, String fileName) throws IOException {
        Path directoryPath = Paths.get(path, "Rate");
        Path filePath = Paths.get(path, "Rate", fileName);

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        try (InputStream input = imgRate.getInputStream()) {
            // Check if the file already exists
            if (Files.exists(filePath)) {
                // File exists, so we overwrite it
                Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // File does not exist, so we simply copy it
                Files.copy(input, filePath);
            }
        }

    }

    // cập nhật lại bình luận của user trong 1 bài post
    public Rate updateRateOfUserInPost(Long rateId, Long userId, Long postId, String content, MultipartFile imageRate) throws IOException {
        checkNullSame(content, imageRate);
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Not found post with id: " + postId));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        Rate rate = rateRepository.findById(rateId).orElseThrow(() -> new NotFoundException("Not found rate with id : " + rateId));
        List<Rate> rateList = rateRepository.findRatesByUserIdAndPostId(user.getId(), post.getId());
        Instant dateTimeUpdate = Instant.now();

        if (!rateList.isEmpty()) {
            for (Rate rateInList : rateList) {
                if (rateInList.getId().equals(rate.getId())) {
                    rate.setContent(content);
                    rate.setUpdateDate(dateTimeUpdate);
                    rateRepository.save(rate);
                }
            }

            if (imageRate != null) {
                if (imageRate.getOriginalFilename() != null && !imageRate.isEmpty()) {
                    createRateImage(rate, imageRate);
                }
            } else {
                rate.setImgUrl(null);
                rateRepository.save(rate);
            }
        } else {
            throw new RateException("User with id: " + userId + " dont have any rate in post with id: " + postId);
        }
        return rate;
    }

    // admin hoặc staff xóa bình luận của user, hoặc user tự xóa bình luận của mình
    public boolean deleteRate(Long rateId, Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Not found post with id: " + postId));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        Rate rateDB = rateRepository.findById(rateId).orElseThrow(() -> new NotFoundException("Not found rate with id: " + rateId));
        List<Rate> rateList = rateRepository.findRatesByUserIdAndPostId(user.getId(), post.getId());

        for (Rate rate : rateList) {
            if (rate.equals(rateDB)) {
                rateRepository.delete(rateDB);
                return true;
            }
        }

        return false;
    }

    // tìm kiếm bình luận dựa vào nội dung của bình luận
    public List<Rate> searchRateByContent(String search) {
        List<Rate> rateList = rateRepository.findAll();
        List<Rate> searchList = new ArrayList<>();
        boolean check = false;

        if (!rateList.isEmpty()) {
            for (Rate rate : rateList) {
                if (rate.getContent().toLowerCase().contains(search.toLowerCase().trim())) {
                    if (rate.isHide() == check) {
                        searchList.add(rate);
                    }
                }
            }
        }
        return searchList;
    }

    public List<Rate> getFilteredRate(String search, Optional<Long> userId, Optional<Long> postId, Boolean showHiding) {
        List<Rate> rateList = new ArrayList<>();

        // If both userId and postId are absent, fetch all rates
        if (!userId.isPresent() && !postId.isPresent()) {
            rateList = rateRepository.findAll();
        }
        // filter by userId && postId
        else if (userId.isPresent() && postId.isPresent()) {
            rateList = rateRepository.findRatesByUserIdAndPostId(userId.get(), postId.get());
        }
        // filter by userId
        else if (userId.isPresent()) {
            rateList = rateRepository.findRatesByUserId(userId.get());
        }
        // filter by postId
        else if (postId.isPresent()) {
            rateList = rateRepository.findRatesByPostId(postId.get());
        }
        if (rateList.isEmpty()) {
            return rateList;
        }
        // filter by showHiding
        if (showHiding.equals(false)) {
            rateList = rateList.stream().filter(r -> r.isHide() == false).collect(Collectors.toList());
        }
        // filter by search
        if (!search.isEmpty()) {
            rateList = rateList.stream().filter(r -> r.getContent().toLowerCase().contains(search.toLowerCase().trim())).collect(Collectors.toList());
        }
        rateList.sort((o1, o2) -> o2.getId().compareTo(o1.getId()));
        return rateList;
    }

    private void checkNullSame(String content, MultipartFile img) {
        if ((content == null || content.isEmpty()) && img == null) {
            throw new RateException("The content and image in rate cannot same miss");
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Function xử lí logic cho table Category
    public List<ProductCategory> getAllCategory(String search) {
        List<ProductCategory> categoryList = categoryRepository.findAllByActive(true);
        List<ProductCategory> searchList;
        Collections.reverse(categoryList);

        if (!categoryList.isEmpty()) {
            if (StringUtils.hasText(search)) {
                searchList = searchCategoryName(search, categoryList);
                return searchList;
            }
        }
        return categoryList;
    }

    public ProductCategory getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Not found category with id: " + categoryId));
    }

    // search loại của product dựa vào tên loại
    public List<ProductCategory> searchCategoryName(String search, List<ProductCategory> categoryList) {
        List<ProductCategory> searchList;

        if (!categoryList.isEmpty()) {
            if (StringUtils.hasText(search)) {
                searchList = categoryList.stream().filter(category -> category.getCategoryName().toLowerCase().contains(search.toLowerCase().trim())).collect(Collectors.toList());

                return searchList;
            }
        }
        return categoryList;
    }

    // admin tạo ra một loại mới của product
    public ProductCategory creatProductCategory(String categoryName) {
        checkValidationCategory(categoryName);
        List<ProductCategory> categoryList = categoryRepository.findAll();

        for (ProductCategory category : categoryList) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                throw new ProductCategoryException("The category name already existed");
            }
        }
        ProductCategory category = new ProductCategory(categoryName, true);
        return categoryRepository.save(category);
    }

    // admin cập nhật
    public ProductCategory updateProductCategory(Long categoryId, String categoryName) {
        checkValidationCategory(categoryName);
        ProductCategory category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Not found category with id: " + categoryId));
        category.setCategoryName(categoryName);
        return categoryRepository.save(category);
    }

    //admin xóa
    public boolean deleteProductCategory(Long categoryId) {
        ProductCategory category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Not found category with id: " + categoryId));
        categoryRepository.delete(category);
        return true;
    }

    private void checkValidationCategory(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            throw new ProductCategoryException("Please input in field is missing.");
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Function xử lí logic phần filter

    // lọc ra tất cả sản phẩm dựa vào tên loại
    public List<Product> filterProductByCategoryName(List<Long> categorIdList) {
        List<Product> filterList = new ArrayList<>();

        for (Long categoryId : categorIdList) {
            List<Product> productList = productRepository.findProductByCategoryId(categoryId);
            filterList.addAll(productList);
        }

        return filterList;
    }

//    public List<Product> searchByPrice(Long firstPrice, Long secondPrice) throws Exception {
//        List<Product> productList = productRepository.findAll();
//        List<Product> searchList = new ArrayList<>();
//
//        if ((firstPrice != null && secondPrice == null) || (firstPrice == null && secondPrice != null)) {
//            throw new Exception("Must enter 2 cells at the same time to search");
//        }
//        if (firstPrice.compareTo(secondPrice) >= 0) {
//            throw new Exception("The first price cannot greater than the second price");
//        }
//        if (firstPrice.compareTo(0L) < 0 || secondPrice.compareTo(0L) <= 0) {
//            throw new Exception("The first price cannot negative and the second price cannot negative or equal 0");
//        }
//
//        for (Product product : productList) {
//            boolean equal1 = product.getPrice().compareTo(BigDecimal.valueOf(firstPrice)) == 0;
//            boolean greater = product.getPrice().compareTo(BigDecimal.valueOf(firstPrice)) > 0;
//
//            boolean equal2 = product.getPrice().compareTo(BigDecimal.valueOf(secondPrice)) == 0;
//            boolean less = product.getPrice().compareTo(BigDecimal.valueOf(secondPrice)) < 0;
//
//            if ((greater || equal1) && (less || equal2)) {
//                searchList.add(product);
//            }
//        }
//
//        return searchList;
//    }

    public List<ProductDTO> filterProductWithSort(String search, Optional<Boolean> asc, Optional<Long> minPrice, Optional<Long> maxPrice, Optional<Integer> avgReview, Optional<List<Long>> categoryIDs, Optional<List<Long>> typeGoldIds, String typeOptionName) {

//        List<Product> list = productRepository.findAll();
        List<Product> productList = productRepository.findAllByActive(true);
        Collections.reverse(productList);

        if (typeOptionName != null && !typeOptionName.isEmpty()) {
            productList = productList.stream().filter(product -> product.getTypeGoldOption().equals(EGoldOptionType.CRAFT)).toList();
        } else {
            productList = productList.stream().filter(product -> product.getTypeGoldOption().equals(EGoldOptionType.AVAILABLE)).toList();
        }

//        boolean check = true;
//        List<Product> productList = list.stream().filter(product -> product.isActive() == check).toList();


        // Show product with search
        if (!search.isEmpty()) {
            productList = searchProductByName(search);
        }
        // Show product with category IDs
        if (categoryIDs.isPresent()) {
            productList = productList.stream().filter(t -> categoryIDs.get().contains(t.getCategory().getId())).toList();
        }
        // Show product with type gold IDs
        if (typeGoldIds.isPresent()) {
            productList = productList.stream().filter(t -> typeGoldIds.get().contains(t.getTypeGold().getId())).toList();
        }
        if (avgReview.isPresent()) {
            assert (Long.valueOf(5).equals((long) Math.ceil(4.5)));
            productList = productList.stream().filter(t -> {
                int avg = (int) Math.ceil(t.getAvgReview());
                return avgReview.get().equals(avg);
            }).toList();
        }

        if (minPrice.isPresent()) {
            productList = searchMinPrice(productList, minPrice);
        }
        // filter by max Price
        if (maxPrice.isPresent()) {
            productList = searchMaxPrice(productList, maxPrice);
        }

        // Show product with Asc or Desc Price
        if (asc.isPresent()) {
            // make collection sortable
            productList = new ArrayList<>(productList);
            // ascending
            if (asc.get()) productList.sort(Comparator.naturalOrder());
                // descending
            else productList.sort(Comparator.reverseOrder());
        }

        return productList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private List<Product> searchMinPrice(List<Product> productList, Optional<Long> minPrice) {
        return productList.stream().filter(t -> t.getTypeGold().getPrice().multiply(BigDecimal.valueOf(t.getWeight()))
                .add(t.getProcessingCost()).compareTo(new BigDecimal(minPrice.get())) > -1).toList();
    }

    private List<Product> searchMaxPrice(List<Product> productList, Optional<Long> maxPrice) {
        return productList.stream().filter(t -> t.getTypeGold().getPrice().multiply(BigDecimal.valueOf(t.getWeight()))
                .add(t.getProcessingCost()).compareTo(new BigDecimal(maxPrice.get())) < 1).toList();
    }

    private ProductDTO mapToDTO(Product product) {
        List<ProductImage> imageList = productImageRepository.findAllByProductId(product.getId());
        List<ProductImagesDTO> imagesDTO= new ArrayList<>();
        List<ReviewProduct> reviewProductList = reviewProductRepository.findAllByProductId(product.getId());
        List<ReviewProductDTO> reviewsDTO = new ArrayList<>();

        if(!imageList.isEmpty()){
            imagesDTO = product.getProductImages().stream().map(image -> new ProductImagesDTO(image.getId(), image.getImgUrl(), image.getProduct().getId())).collect(Collectors.toList());

        }

        if(!reviewProductList.isEmpty()){
            reviewsDTO = product.getReviewProduct().stream().map(review -> new ReviewProductDTO(review.getId(), review.getNumOfReviews(), review.getContent(), review.getCreateDate(), review.getUpdateDate(), review.getImgUrl(), mapToUserDTO(review.getUser()))).collect(Collectors.toList());

        }

        BigDecimal priceProduct = BigDecimal.ZERO;
        if(product.getTypeGoldOption().equals(EGoldOptionType.AVAILABLE)){
            priceProduct = product.getTypeGold().getPrice()
                            .multiply(BigDecimal.valueOf(product.getWeight()))
                            .add(product.getProcessingCost());
        }

        // Tạo và trả về một instance mới của ProductDTO
        return new ProductDTO(
                product.getId(),
                product.getProductName(),
                product.getDescription(),
                product.getWeight(),
                product.getProcessingCost(),
                product.getTotalUnitOfStock(),
                product.getUnitOfStock(),
                product.getTotalProductSold(),
                product.getPercentageReduce(),
                imagesDTO,
                product.getSecondPrice(),
                product.getCategory(),
                product.getTypeGold(),
                product.getAvgReview(),
                product.getTypeGoldOption(),
                product.isActive(),
                reviewsDTO,
                priceProduct
        );
    }


    private UserDTO mapToUserDTO(User user) {
        return new UserDTO(mapToUserInfo(user.getUserInfo()), user.getId(), user.getRoles(), user.isActive(), user.isEmailVerified());
    }


    private UserInfoDTO mapToUserInfo(UserInfo userInfo) {
        return new UserInfoDTO(userInfo.getLastName(), userInfo.getId(), userInfo.getFirstName(), userInfo.getDoB(), userInfo.getAvatarData());
    }


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Function xử lí logic cho mã giảm giá user đã thu thập

    // show tất cả các mã giảm giá mà user đã thu thập, và check xem mã đó đã hết
    // hạn chưa, nếu rồi cập nhật lại trạng thái mã code
    // search by code
    public List<DiscountCodeOfUser> getAllDiscountCodeOfUser(Long userId, Optional<Boolean> expire, String search) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        List<DiscountCodeOfUser> listCode = codeOfUserRepository.findAllByUserId(user.getId());
        List<DiscountCodeOfUser> searchList;
        boolean checkCondition = true;

        if (!listCode.isEmpty()) {
            for (DiscountCodeOfUser discountUser : listCode) {
                if (discountUser.isAvailable() == checkCondition) {
                    getDiscountCodeById(discountUser.getDiscount().getId());
                }
            }
        }

        Collections.reverse(listCode);
        //filter when expire = false
        if (expire.isEmpty() || !expire.get()) {
            //take only expirer = false;
            listCode = listCode.stream().filter(d -> !d.getDiscount().isExpire()).collect(Collectors.toList());
        }

        if (StringUtils.hasText(search)) {
            searchList = listCode.stream().filter(discountCodeOfUser -> discountCodeOfUser.getDiscount().getCode().contains(search.trim())).collect(Collectors.toList());

            return searchList;
        } else {
            return listCode;
        }
    }

    // show mã giảm giá mà user đã thu thập, và check xem mã đó đã hết hạn chưa, nếu
    // rồi cập nhật lại trạng thái mã code
    public DiscountCodeOfUser getDiscountCode(Long userId, Long discountCodeId) {
        User user = userRepository.findById(userId).orElse(null);
        DiscountCodeOfUser discountCodeOfUser = codeOfUserRepository.findByIdAndUserId(discountCodeId, userId);
        boolean checkCondition = true;

        if (user != null) {
            if (discountCodeOfUser != null) {
                if (discountCodeOfUser.isAvailable() == checkCondition) {
                    getDiscountCodeById(discountCodeOfUser.getDiscount().getId());
                }
            } else {
                throw new NotFoundException("Not found code of user with code id: " + discountCodeId);
            }
        } else {
            throw new NotFoundException("Not found user with user id: " + userId);
        }

        return discountCodeOfUser;
    }

    // thu thập mã giảm giá mà admin đã đăng lên
    public DiscountCodeOfUser createDiscountCodeOfUser(Long discountId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new NotFoundException("Not found discount code with discount id: " + discountId));
        DiscountCodeOfUser checkExisted = codeOfUserRepository.findByUserIdAndDiscountId(userId, discountId);

        if (discount.getDefaultQuantity() <= 0) {
            throw new DiscountCodeOfUserException("The discount code does not remain. Unable to collect.");
        }

        if (checkExisted != null) {
            throw new DiscountCodeOfUserException("The discount code already existed.");
        } else {
            DiscountCodeOfUser discountCodeOfUser = new DiscountCodeOfUser(discount, 1, user);
            discount.setDefaultQuantity(discount.getDefaultQuantity() - 1);
            discountRepository.save(discount);

            return codeOfUserRepository.save(discountCodeOfUser);
        }
    }

    // xóa luôn mã giảm giá khỏi bảng
    public boolean deleteDiscountCodeExpired(Long userId, Long codeOfUserId) {
        User user = userRepository.findById(userId).orElse(null);
        DiscountCodeOfUser discountCodeOfUser = codeOfUserRepository.findByIdAndUserId(codeOfUserId, userId);

        if (user != null) {
            if (discountCodeOfUser != null) {
                discountCodeOfUser.setAvailable(false);
                codeOfUserRepository.save(discountCodeOfUser);
            } else {
                throw new NotFoundException("Not found code of user with code id: " + codeOfUserId);
            }
        } else {
            throw new NotFoundException("Not found user with user id: " + userId);
        }
        return true;
    }

    // get All Orders (Admin only)
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByIdDesc();
        return orders;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Function xử lí logic cho table forum

    // Forum chỉ có 1
    public List<Forum> showForum() {
        List<Forum> forums = forumRepository.findAll();

        if (!forums.isEmpty()) {
            return forums;
        } else {
            throw new ForumException("The forum is empty.");
        }
    }

    // Chỉ tạo forum 1 lần
    public Forum createForumTheFirst() {
        List<Forum> list = forumRepository.findAll();
        if (!list.isEmpty()) {
            throw new ForumException("Forum already existed. Just create forum once.");
        } else {
            Forum forum = new Forum(false);
            return forumRepository.save(forum);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Function xử lí logic cho table CategoryForum

    // show tất cả các loại của bài post
    public List<CategoryPost> showAllCategoryPost(String search) {
        List<CategoryPost> categoryPosts = categoryPostRepository.findAllByActive(true);
        List<CategoryPost> searchList;
        Collections.reverse(categoryPosts);

        if (!categoryPosts.isEmpty()) {
            if (StringUtils.hasText(search)) {
                searchList = categoryPosts.stream().filter(categoryPost -> categoryPost.getCategoryName().toLowerCase()
                        .contains(search.toLowerCase().trim())).collect(Collectors.toList());
                return searchList;
            }
        }
        return categoryPosts;
    }

    // show chi tiết loại của bài post dựa vào id
    public CategoryPost getCategoryPostById(Long id) {
        return categoryPostRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found category of post with id: " + id));
    }

    // admin tạo ra 1 loại của bài post cho forum
    public CategoryPost createCategoryPost(String categoryName) {
        checkEmpty(categoryName);
        List<Forum> forums = forumRepository.findAll();
        Forum forum = null;
        if (!forums.isEmpty()) {
            for (Forum forumExist : forums) {
                forum = forumExist;
            }
        } else {
            forum = createForumTheFirst();
        }

        List<CategoryPost> categoryPosts = categoryPostRepository.findAll();

        for (CategoryPost category : categoryPosts) {
            if (category.getCategoryName().equals(categoryName)) {
                throw new PostCategoryException("Category name already exists");
            }
        }
        CategoryPost categoryPost = new CategoryPost(categoryName, forum, true);
        return categoryPostRepository.save(categoryPost);
    }

    // admin cập nhật lại 1 loại của bài post cho forum
    public CategoryPost updateCategoryPost(Long categoryPostId, String categoryName) {
        checkEmpty(categoryName);
        CategoryPost categoryPost = categoryPostRepository.findById(categoryPostId).orElseThrow(() -> new NotFoundException("Not found category of post with id: " + categoryPostId));
        List<CategoryPost> categoryPosts = categoryPostRepository.findAll();

        for (CategoryPost category : categoryPosts) {
            if (category.getCategoryName().equals(categoryName)) {
                throw new PostCategoryException("Category name already exists");
            }
        }
        categoryPost.setCategoryName(categoryName);
        return categoryPostRepository.save(categoryPost);
    }

    // admin xóa 1 loại của bài post ra khỏi table
    public boolean deleteCategoryPost(Long categoryPostId) {
        CategoryPost categoryPost = categoryPostRepository.findById(categoryPostId).orElseThrow(() -> new NotFoundException("Not found category of post with id: " + categoryPostId));
        categoryPost.setActive(false);
        categoryPostRepository.save(categoryPost);
        return true;
    }

    private void checkEmpty(String categoryPostName) {
        if (categoryPostName == null || categoryPostName.isEmpty()) {
            throw new PostCategoryException("Please input in field is missing.");
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public List<Order> checkProductReceived(Long userId, Long productId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        List<Order> checkReceived = new ArrayList<>();

        if (!orders.isEmpty()) {
            for (Order order : orders) {
                if (order.getStatusReceived().equals(EReceivedStatus.RECEIVED)) {
                    List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrderId(order.getId());

                    for (OrderDetail orderDetail : orderDetailList) {
                        if (orderDetail.getProduct().getId().equals(productId)) {
                            checkReceived.add(order);
                        }
                    }
                }
            }
        } else {
            throw new ReviewProductException("The list orders are empty.");
        }
        return checkReceived;
    }

    public ReviewProduct checkReview(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Not found product with id: " + productId));
        return reviewProductRepository.findByUserIdAndProductId(user.getId(), product.getId());
    }

    public List<ReviewProduct> getAllReviewOfAllProduct(String search) {
        List<ReviewProduct> reviewProducts = reviewProductRepository.findAll();

        if (StringUtils.hasText(search)) {
            return reviewProducts.stream().filter(reviewProduct -> reviewProduct.getContent().toLowerCase().contains(search.toLowerCase().trim())).collect(Collectors.toList());
        }

        return reviewProducts;
    }

    public List<ReviewProduct> getAllReviewInProduct(Long productId) {
        List<ReviewProduct> list = reviewProductRepository.findAllByProductId(productId);

        if (!list.isEmpty()) {
            Collections.reverse(list);
            return list;
        } else {
            throw new ReviewProductException("The list reviews of product are empty.");
        }
    }


    @Value("${upload.path}")
    private String uploadReview;

    public ReviewProduct createReview(Long userId, Long productId, ReviewProductRequest reviewRequest, MultipartFile imgReview) throws IOException {
        checkValidationReview(reviewRequest, imgReview);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        List<Order> ordersReceived = checkProductReceived(userId, productId);
        if (ordersReceived.isEmpty()) {
            throw new ReviewProductException("User dont have received this product, cannot review this product");
        }

        ReviewProduct checkReviewed = checkReview(userId, productId);
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Not found product with id: " + productId));

        if (checkReviewed != null) {
            throw new ReviewProductException("Product already reviewed, user can only update the existing review");
        }

        ReviewProduct review = new ReviewProduct(reviewRequest.getNumOfReviews(), reviewRequest.getContent(), Instant.now(), product, user);

        if (imgReview != null) {
            if (imgReview.getOriginalFilename() != null && !imgReview.isEmpty()) {
                String type = defindTypeOfImg(imgReview);
                String fileName = "RV000" + review.getId() + "." + type;

                saveImageReview(uploadReview, imgReview, fileName);
                saveImageReview(uploadRoot, imgReview, fileName);

                // After saving the file, you can use imgUrl as required in your application
                String imgUrl = Paths.get(savePath, "Review", fileName).toString();

                review.setImgUrl(imgUrl);
            }
        }
        return reviewProductRepository.save(review);
    }

    public void saveImageReview(String path, MultipartFile imgReview, String fileName) throws IOException {
        Path directoryPath = Paths.get(path, "Review");
        Path filePath = Paths.get(path, "Review", fileName);

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        try (InputStream input = imgReview.getInputStream()) {
            // Check if the file already exists
            if (Files.exists(filePath)) {
                // File exists, so we overwrite it
                Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // File does not exist, so we simply copy it
                Files.copy(input, filePath);
            }
        }
    }

    public ReviewProduct updateReview(Long userId, Long productId, ReviewProductRequest reviewRequest, MultipartFile imgReview) throws IOException {
        checkValidationReview(reviewRequest, imgReview);
        ReviewProduct checkReviewed = checkReview(userId, productId);

        if (checkReviewed == null) {
            throw new ReviewProductException("User does not review this product, so user cannot update review");
        }

        checkReviewed.setNumOfReviews(reviewRequest.getNumOfReviews());
        checkReviewed.setContent(reviewRequest.getContent());
        checkReviewed.setUpdateDate(Instant.now());

        if (imgReview != null) {
            if (imgReview.getOriginalFilename() != null && !imgReview.isEmpty()) {
                String type = defindTypeOfImg(imgReview);
                String fileName = "RV000" + checkReviewed.getId() + "." + type;
                Path directoryPath = Paths.get(uploadReview, "Review");
                Path filePath = Paths.get(uploadReview, "Review", fileName);

                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }

                try (InputStream input = imgReview.getInputStream()) {
                    // Check if the file already exists
                    if (Files.exists(filePath)) {
                        // File exists, so we overwrite it
                        Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        // File does not exist, so we simply copy it
                        Files.copy(input, filePath);
                    }
                }

                // After saving the file, you can use imgUrl as required in your application
                String imgUrl = Paths.get(savePath, "Review", fileName).toString();

                checkReviewed.setImgUrl(imgUrl);
            }
        } else {
            checkReviewed.setImgUrl(null);
        }
        return reviewProductRepository.save(checkReviewed);
    }

    public boolean deleteReviewed(Long reviewProductId, Long productId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Not found product with id: " + productId));
        ReviewProduct review = reviewProductRepository.findById(reviewProductId).orElseThrow(() -> new NotFoundException("Not found review with id: " + reviewProductId));
        ReviewProduct reviewProduct = reviewProductRepository.findByUserIdAndProductId(user.getId(), product.getId());

        if (review.getId().equals(reviewProduct.getId())) {
            reviewProductRepository.delete(review);
            return true;
        } else {
            throw new ReviewProductException("This is not your review about this product. Can not delete this review.");
        }
    }

    private void checkValidationReview(ReviewProductRequest reviewRequest, MultipartFile imgReview) {
        if ((reviewRequest.getContent() == null || reviewRequest.getContent().isEmpty()) && (imgReview == null || imgReview.isEmpty())) {
            throw new ReviewProductException("Please input in content or upload image to submit your review.");
        }

        if (reviewRequest.getNumOfReviews() == null) {
            throw new ReviewProductException("Choose number of stars to submit your review.");
        }

        if (reviewRequest.getNumOfReviews() < 0 || reviewRequest.getNumOfReviews() > 5) {
            throw new ReviewProductException("You just rate 0 to 5 stars for the product.");
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    public DashboardOrderResponse calculateOrderSalesStatistics(String type, Long monthChoose, Long quarterChoose, Long yearChoose) {
        List<Order> orders = orderRepository.findAll();
        List<Order> orderHaveCondition = new ArrayList<>();

        if (type.equals("DAY")) {
            orderHaveCondition = compareDateInOrder(orders);
        }
        if (type.equals("WEEK")) {
            orderHaveCondition = compareDateInWeekOrder(orders);
        }
        if (type.equals("MONTH")) {
            orderHaveCondition = compareDateInMonthOrder(monthChoose, orders);
        }
        if (type.equals("QUARTER")) {
            orderHaveCondition = compareDateInQuarterOrder(quarterChoose, orders);
        }
        if (type.equals("YEAR")) {
            orderHaveCondition = compareDateInYearOrder(yearChoose, orders);
        }

        List<Order> orderUnverified = orderHaveCondition.stream().filter(order -> order.getStatusReceived().equals(EReceivedStatus.UNVERIFIED)).toList();
        List<Order> orderNot_received = orderHaveCondition.stream().filter(order -> order.getStatusReceived().equals(EReceivedStatus.NOT_RECEIVED)).toList();
        List<Order> orderReceived = orderHaveCondition.stream().filter(order -> order.getStatusReceived().equals(EReceivedStatus.RECEIVED)).toList();
        List<Order> orderPaid = orderHaveCondition.stream().filter(order -> order.getStatusReceived().equals(EReceivedStatus.NOT_RECEIVED) || order.getStatusReceived().equals(EReceivedStatus.RECEIVED)).toList();

        BigDecimal totalAmountOrder = totalAmountOder(orderPaid);

        return new DashboardOrderResponse(orderHaveCondition.size(), orderUnverified.size(), orderNot_received.size(), orderReceived.size(), totalAmountOrder);
    }

    public BigDecimal totalAmountOder(List<Order> orderPaid) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Order order : orderPaid) {
            totalAmount = totalAmount.add(order.getTotalAmount());
        }

        return totalAmount;
    }

    public List<Order> compareDateInOrder(List<Order> orders) {
        Instant instant = Instant.now();
        List<Order> orderInDate = new ArrayList<>();
        Instant dateNow = instant.truncatedTo(ChronoUnit.DAYS);

        for (Order order : orders) {
            Instant dateCreate = order.getCreateDate().truncatedTo(ChronoUnit.DAYS);
            if (!dateCreate.isBefore(dateNow) && !dateCreate.isAfter(dateNow)) {
                orderInDate.add(order);
            }
        }

        return orderInDate;
    }

    public List<Order> compareDateInWeekOrder(List<Order> orders) {
        List<Order> ordersInWeek = new ArrayList<>();
        Instant dateNow = Instant.now();
        Instant firstDate = dateNow.minus(7 * 24 * 60 * 60, ChronoUnit.SECONDS).truncatedTo(ChronoUnit.DAYS);

        for (Order order : orders) {
            Instant dateCreate = order.getCreateDate().truncatedTo(ChronoUnit.DAYS);
            boolean condition1 = dateCreate.isAfter(firstDate) && dateCreate.isBefore(dateNow);
            boolean condition2 = !dateCreate.isAfter(firstDate) && !dateCreate.isBefore(firstDate);
            boolean condition3 = !dateCreate.isAfter(dateNow) && !dateCreate.isBefore(dateNow);

            if (condition1 || condition2 || condition3) {
                ordersInWeek.add(order);
            }
        }

        return ordersInWeek;
    }

    public List<Order> compareDateInMonthOrder(Long monthChoose, List<Order> orders) {
        List<Order> ordersInMonth = new ArrayList<>();
        int yearCurrent = Instant.now().atZone(ZoneOffset.UTC).getYear();

        for (Order order : orders) {
            int year = order.getCreateDate().atZone(ZoneOffset.UTC).getYear();
            int month = order.getCreateDate().atZone(ZoneOffset.UTC).getMonthValue();

            if (year == yearCurrent) {
                if (month == monthChoose) {
                    ordersInMonth.add(order);
                }
            }
        }

        return ordersInMonth;
    }

    public List<Order> compareDateInQuarterOrder(Long quarterChoose, List<Order> orders) {
        List<Order> ordersInQuarter = new ArrayList<>();
        int yearCurrent = Instant.now().atZone(ZoneOffset.UTC).getYear();

        for (Order order : orders) {
            int year = order.getCreateDate().atZone(ZoneOffset.UTC).getYear();
            int month = order.getCreateDate().atZone(ZoneOffset.UTC).getMonthValue();

            if (year == yearCurrent) {
                if (quarterChoose == 1 && (month >= 1 && month <= 3)) {
                    ordersInQuarter.add(order);
                }
                if (quarterChoose == 2 && (month >= 4 && month <= 6)) {
                    ordersInQuarter.add(order);
                }
                if (quarterChoose == 3 && (month >= 7 && month <= 9)) {
                    ordersInQuarter.add(order);
                }
                if (quarterChoose == 4 && (month >= 10 && month <= 12)) {
                    ordersInQuarter.add(order);
                }
            }
        }

        return ordersInQuarter;
    }

    public List<Order> compareDateInYearOrder(Long yearChoose, List<Order> orders) {
        List<Order> ordersInYear = new ArrayList<>();

        for (Order order : orders) {
            int year = order.getCreateDate().atZone(ZoneOffset.UTC).getYear();

            if (year == yearChoose) {
                ordersInYear.add(order);
            }
        }

        return ordersInYear;
    }

    public DashboardOrderResponse calculateOrderSalesStatisticsFromAndTo(FromAndToRequest request) {
        checkValidationDate(request.getFrom(), request.getTo());
        List<Order> orders = orderRepository.findAll();
        List<Order> orderHaveCondition = filterFromAndToOrder(orders, request.getFrom(), request.getTo());

        List<Order> orderUnverified = orderHaveCondition.stream().filter(order -> order.getStatusReceived().equals(EReceivedStatus.UNVERIFIED)).toList();
        List<Order> orderNot_received = orderHaveCondition.stream().filter(order -> order.getStatusReceived().equals(EReceivedStatus.NOT_RECEIVED)).toList();
        List<Order> orderReceived = orderHaveCondition.stream().filter(order -> order.getStatusReceived().equals(EReceivedStatus.RECEIVED)).toList();
        List<Order> orderPaid = orderHaveCondition.stream().filter(order -> order.getStatusReceived().equals(EReceivedStatus.NOT_RECEIVED) || order.getStatusReceived().equals(EReceivedStatus.RECEIVED)).toList();

        BigDecimal totalAmountOrder = totalAmountOder(orderPaid);

        return new DashboardOrderResponse(orderHaveCondition.size(), orderUnverified.size(), orderNot_received.size(), orderReceived.size(), totalAmountOrder);
    }

    public List<Order> filterFromAndToOrder(List<Order> orderList, Instant from, Instant to) {
        List<Order> orderFilter = new ArrayList<>();
        from = from.truncatedTo(ChronoUnit.DAYS);
        to = to.truncatedTo(ChronoUnit.DAYS);

        for (Order order : orderList) {
            Instant dateCreate = order.getCreateDate().truncatedTo(ChronoUnit.DAYS);

            boolean condition1 = dateCreate.isAfter(from) && dateCreate.isBefore(to);
            boolean condition2 = !dateCreate.isAfter(from) && !dateCreate.isBefore(from);
            boolean condition3 = !dateCreate.isAfter(to) && !dateCreate.isBefore(to);

            if (condition1 || condition2 || condition3) {
                orderFilter.add(order);
            }
        }

        return orderFilter;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    public DashboardTransactionResponse calculateTransactionSellSales(String type, Long monthChoose, Long quarterChoose, Long yearChoose) {
        List<GoldTransaction> transactions = goldTransactionRepository.findAll();
        List<GoldTransaction> transactionHaveCondition = new ArrayList<>();

        if (type.equals("DAY")) {
            transactionHaveCondition = compareDateInTransaction(transactions);
        }
        if (type.equals("WEEK")) {
            transactionHaveCondition = compareDateInWeekTransaction(transactions);
        }
        if (type.equals("MONTH")) {
            transactionHaveCondition = compareDateInMonthTransaction(monthChoose, transactions);
        }
        if (type.equals("QUARTER")) {
            transactionHaveCondition = compareDateInQuarterTransaction(quarterChoose, transactions);
        }
        if (type.equals("YEAR")) {
            transactionHaveCondition = compareDateInYearTransaction(yearChoose, transactions);
        }

        List<GoldTransaction> transactionsSell = transactionHaveCondition.stream().filter(goldTransaction -> goldTransaction.getTransactionType().equals(TransactionType.SELL)).toList();

        DashboardTransactionResponse response = getFieldTransaction(transactionsSell, "sell");
        response.setTotalTransactions(transactionHaveCondition.size());

        return response;
    }

    public DashboardTransactionResponse calculateTransactionBuySales(String type, Long monthChoose, Long quarterChoose, Long yearChoose) {
        List<GoldTransaction> transactions = goldTransactionRepository.findAll();
        List<GoldTransaction> transactionHaveCondition = new ArrayList<>();

        if (type.equals("DAY")) {
            transactionHaveCondition = compareDateInTransaction(transactions);
        }
        if (type.equals("WEEK")) {
            transactionHaveCondition = compareDateInWeekTransaction(transactions);
        }
        if (type.equals("MONTH")) {
            transactionHaveCondition = compareDateInMonthTransaction(monthChoose, transactions);
        }
        if (type.equals("QUARTER")) {
            transactionHaveCondition = compareDateInQuarterTransaction(quarterChoose, transactions);
        }
        if (type.equals("YEAR")) {
            transactionHaveCondition = compareDateInYearTransaction(yearChoose, transactions);
        }

        List<GoldTransaction> transactionsBuy = transactionHaveCondition.stream().filter(goldTransaction -> goldTransaction.getTransactionType().equals(TransactionType.BUY)).toList();

        DashboardTransactionResponse response = getFieldTransaction(transactionsBuy, "buy");
        response.setTotalTransactions(transactionHaveCondition.size());

        return response;
    }

    public DashboardTransactionResponse getFieldTransaction(List<GoldTransaction> transactions, String subType) {
        List<GoldTransaction> transactionUnverified = transactions.stream().filter(goldTransaction -> goldTransaction.getTransactionVerification().equals(TransactionVerification.UNVERIFIED)).toList();
        List<GoldTransaction> transactionVerified = transactions.stream().filter(goldTransaction -> goldTransaction.getTransactionVerification().equals(TransactionVerification.VERIFIED)).toList();
        List<GoldTransaction> transactionPending = transactions.stream().filter(goldTransaction -> goldTransaction.getTransactionStatus().equals(TransactionStatus.PENDING)).toList();
        List<GoldTransaction> transactionConfirmed = transactions.stream().filter(goldTransaction -> goldTransaction.getTransactionStatus().equals(TransactionStatus.CONFIRMED)).toList();
        List<GoldTransaction> transactionRejected = transactions.stream().filter(goldTransaction -> goldTransaction.getTransactionStatus().equals(TransactionStatus.REJECTED)).toList();
        List<GoldTransaction> transactionCompleted = transactions.stream().filter(goldTransaction -> goldTransaction.getTransactionStatus().equals(TransactionStatus.COMPLETED)).toList();

        BigDecimal totalAmountTransaction = totalAmountTransaction(transactionVerified);

        if (subType.equals("buy")) {
            return new DashboardTransactionResponse(0, transactions.size(), 0, transactionUnverified.size(), transactionVerified.size(), transactionPending.size(), transactionConfirmed.size(), transactionRejected.size(), transactionCompleted.size(), totalAmountTransaction);
        } else {
            return new DashboardTransactionResponse(0, 0, transactions.size(), transactionUnverified.size(), transactionVerified.size(), transactionPending.size(), transactionConfirmed.size(), transactionRejected.size(), transactionCompleted.size(), totalAmountTransaction);
        }
    }

    public BigDecimal totalAmountTransaction(List<GoldTransaction> transactions) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (GoldTransaction transaction : transactions) {
            totalAmount = totalAmount.add(transaction.getPricePerOunce());
        }

        return totalAmount;
    }

    public List<GoldTransaction> compareDateInTransaction(List<GoldTransaction> transactions) {
        Instant instant = Instant.now();
        List<GoldTransaction> transactionsInDate = new ArrayList<>();
        Instant dateNow = instant.truncatedTo(ChronoUnit.DAYS);

        for (GoldTransaction transaction : transactions) {
            Instant dateCreate = transaction.getCreatedAt().truncatedTo(ChronoUnit.DAYS);
            if (!dateCreate.isBefore(dateNow) && !dateCreate.isAfter(dateNow)) {
                transactionsInDate.add(transaction);
            }
        }

        return transactionsInDate;
    }

    public List<GoldTransaction> compareDateInWeekTransaction(List<GoldTransaction> transactions) {
        List<GoldTransaction> transactionsInWeek = new ArrayList<>();
        Instant dateNow = Instant.now();
        Instant firstDate = dateNow.minus(7 * 24 * 60 * 60, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);

        for (GoldTransaction transaction : transactions) {
            Instant dateCreate = transaction.getCreatedAt().truncatedTo(ChronoUnit.DAYS);
            boolean condition1 = dateCreate.isAfter(firstDate) && dateCreate.isBefore(dateNow);
            boolean condition2 = !dateCreate.isAfter(firstDate) && !dateCreate.isBefore(firstDate);
            boolean condition3 = !dateCreate.isAfter(dateNow) && !dateCreate.isBefore(dateNow);

            if (condition1 || condition2 || condition3) {
                transactionsInWeek.add(transaction);
            }
        }

        return transactionsInWeek;
    }

    public List<GoldTransaction> compareDateInMonthTransaction(Long monthChoose, List<GoldTransaction> transactions) {
        List<GoldTransaction> transactionsInMonth = new ArrayList<>();
        int yearCurrent = Instant.now().atZone(ZoneOffset.UTC).getYear();

        for (GoldTransaction transaction : transactions) {
            int year = transaction.getCreatedAt().atZone(ZoneOffset.UTC).getYear();
            int month = transaction.getCreatedAt().atZone(ZoneOffset.UTC).getMonthValue();

            if (year == yearCurrent) {
                if (month == monthChoose) {
                    transactionsInMonth.add(transaction);
                }
            }
        }

        return transactionsInMonth;
    }

    public List<GoldTransaction> compareDateInQuarterTransaction(Long quarterChoose, List<GoldTransaction> transactions) {
        List<GoldTransaction> transactionsInQuarter = new ArrayList<>();
        int yearCurrent = Instant.now().atZone(ZoneOffset.UTC).getYear();

        for (GoldTransaction transaction : transactions) {
            int year = transaction.getCreatedAt().atZone(ZoneOffset.UTC).getYear();
            int month = transaction.getCreatedAt().atZone(ZoneOffset.UTC).getMonthValue();

            if (year == yearCurrent) {
                if (quarterChoose == 1 && (month >= 1 && month <= 3)) {
                    transactionsInQuarter.add(transaction);
                }
                if (quarterChoose == 2 && (month >= 4 && month <= 6)) {
                    transactionsInQuarter.add(transaction);
                }
                if (quarterChoose == 3 && (month >= 7 && month <= 9)) {
                    transactionsInQuarter.add(transaction);
                }
                if (quarterChoose == 4 && (month >= 10 && month <= 12)) {
                    transactionsInQuarter.add(transaction);
                }
            }
        }

        return transactionsInQuarter;
    }

    public List<GoldTransaction> compareDateInYearTransaction(Long yearChoose, List<GoldTransaction> transactions) {
        List<GoldTransaction> transactionsInYear = new ArrayList<>();

        for (GoldTransaction transaction : transactions) {
            int year = transaction.getCreatedAt().atZone(ZoneOffset.UTC).getYear();

            if (year == yearChoose) {
                transactionsInYear.add(transaction);
            }
        }

        return transactionsInYear;
    }

    public DashboardTransactionResponse calculateTransactionSellSalesByFromAndTo(FromAndToRequest request) {
        checkValidationDate(request.getFrom(), request.getTo());
        List<GoldTransaction> transactions = goldTransactionRepository.findAll();
        List<GoldTransaction> transactionHaveCondition = filterFromAndToTransaction(transactions, request.getFrom(), request.getTo());

        List<GoldTransaction> transactionsSell = transactionHaveCondition.stream().filter(goldTransaction -> goldTransaction.getTransactionType().equals(TransactionType.SELL)).toList();
        DashboardTransactionResponse response = getFieldTransaction(transactionsSell, "sell");
        response.setTotalTransactions(transactionHaveCondition.size());

        return response;
    }

    public DashboardTransactionResponse calculateTransactionBuySalesByFromAndTo(FromAndToRequest request) {
        checkValidationDate(request.getFrom(), request.getTo());
        List<GoldTransaction> transactions = goldTransactionRepository.findAll();
        List<GoldTransaction> transactionHaveCondition = filterFromAndToTransaction(transactions, request.getFrom(), request.getTo());

        List<GoldTransaction> transactionsBuy = transactionHaveCondition.stream().filter(goldTransaction -> goldTransaction.getTransactionType().equals(TransactionType.BUY)).toList();
        DashboardTransactionResponse response = getFieldTransaction(transactionsBuy, "buy");
        response.setTotalTransactions(transactionHaveCondition.size());

        return response;
    }

    public List<GoldTransaction> filterFromAndToTransaction(List<GoldTransaction> transactionList, Instant from, Instant to) {
        List<GoldTransaction> transactionFilter = new ArrayList<>();
        from = from.truncatedTo(ChronoUnit.DAYS);
        to = to.truncatedTo(ChronoUnit.DAYS);

        for (GoldTransaction transaction : transactionList) {
            Instant dateCreate = transaction.getCreatedAt().truncatedTo(ChronoUnit.DAYS);
            boolean condition1 = dateCreate.isAfter(from) && dateCreate.isBefore(to);
            boolean condition2 = !dateCreate.isAfter(from) && !dateCreate.isBefore(from);
            boolean condition3 = !dateCreate.isAfter(to) && !dateCreate.isBefore(to);

            if (condition1 || condition2 || condition3) {
                transactionFilter.add(transaction);
            }
        }

        return transactionFilter;
    }


    public QuantityStatisticResponse quantityStatistics() {
        List<User> users = userRepository.findAll();
        List<Post> posts = postRepository.findAll();
        List<Product> products = productRepository.findAll();

        QuantityStatisticResponse responseQuantityReviewStar = getQuantityReviewStar();
        QuantityStatisticResponse response = getQuantityUser(responseQuantityReviewStar);

        response.setQuantityUser(users.size());
        response.setQuantityPost(posts.size());
        response.setQuantityProduct(products.size());

        return response;
    }

    public QuantityStatisticResponse getQuantityReviewStar() {
        List<ReviewProduct> reviews = reviewProductRepository.findAll();

        List<ReviewProduct> listOneStar = reviews.stream().filter(reviewProduct -> reviewProduct.getNumOfReviews() == 1).toList();
        List<ReviewProduct> listTwoStar = reviews.stream().filter(reviewProduct -> reviewProduct.getNumOfReviews() == 2).toList();
        List<ReviewProduct> listThreeStar = reviews.stream().filter(reviewProduct -> reviewProduct.getNumOfReviews() == 3).toList();
        List<ReviewProduct> listFourStar = reviews.stream().filter(reviewProduct -> reviewProduct.getNumOfReviews() == 4).toList();
        List<ReviewProduct> listFiveStar = reviews.stream().filter(reviewProduct -> reviewProduct.getNumOfReviews() == 5).toList();

        return new QuantityStatisticResponse(0, 0, 0, 0, 0, 0, 0, listOneStar.size(), listTwoStar.size(), listThreeStar.size(), listFourStar.size(), listFiveStar.size());
    }

    public QuantityStatisticResponse getQuantityUser(QuantityStatisticResponse response) {
        List<User> users = userRepository.findAll();
        boolean check = true;
        List<User> listActiveUser = users.stream().filter(user -> user.isActive() == check).toList();
        List<User> listInactiveUser = users.stream().filter(user -> user.isActive() != check).toList();
        List<User> listVerifiedUser = users.stream().filter(user -> user.isEmailVerified() == check).toList();
        List<User> listUnverifiedUser = users.stream().filter(user -> user.isEmailVerified() != check).toList();

        return new QuantityStatisticResponse(0, listActiveUser.size(), listInactiveUser.size(), listVerifiedUser.size(), listUnverifiedUser.size(), 0, 0, response.getQuantityReviewOneStar(), response.getQuantityReviewTwoStar(), response.getQuantityReviewThreeStar(), response.getQuantityReviewFourStar(), response.getQuantityReviewFiveStar());
    }

    //-------------------------

    public DashboardWithdrawResponse calculateWithdrawGoldStatistics(String type, Long monthChoose, Long quarterChoose, Long yearChoose) {
        List<WithdrawGold> withdrawGolds = withdrawGoldRepository.findAll();
        List<WithdrawGold> withdrawHaveCondition = new ArrayList<>();

        if (type.equals("DAY")) {
            withdrawHaveCondition = compareDateInWithdraw(withdrawGolds);
        }
        if (type.equals("WEEK")) {
            withdrawHaveCondition = compareDateInWeekWithdraw(withdrawGolds);
        }
        if (type.equals("MONTH")) {
            withdrawHaveCondition = compareDateInMonthWithdraw(monthChoose, withdrawGolds);
        }
        if (type.equals("QUARTER")) {
            withdrawHaveCondition = compareDateInQuarterWithdraw(quarterChoose, withdrawGolds);
        }
        if (type.equals("YEAR")) {
            withdrawHaveCondition = compareDateInYearWithdraw(yearChoose, withdrawGolds);
        }

        List<WithdrawGold> withdrawUnverified = withdrawHaveCondition.stream().filter(withdraw -> withdraw.getStatus().equals(WithdrawalStatus.UNVERIFIED)).toList();
        List<WithdrawGold> withdrawPending = withdrawHaveCondition.stream().filter(withdraw -> withdraw.getStatus().equals(WithdrawalStatus.PENDING)).toList();
        List<WithdrawGold> withdrawConfirmed = withdrawHaveCondition.stream().filter(withdraw -> withdraw.getStatus().equals(WithdrawalStatus.CONFIRMED)).toList();
        List<WithdrawGold> withdrawCompleted = withdrawHaveCondition.stream().filter(withdraw -> withdraw.getStatus().equals(WithdrawalStatus.COMPLETED)).toList();
        List<WithdrawGold> withdrawCanceled = withdrawHaveCondition.stream().filter(withdraw -> withdraw.getStatus().equals(WithdrawalStatus.CANCELLED)).toList();

        List<WithdrawGold> totalWithdrawConfirmAndComplete = withdrawHaveCondition.stream()
                .filter(withdrawGold -> withdrawGold.getStatus().equals(WithdrawalStatus.CONFIRMED) || withdrawGold.getStatus().equals(WithdrawalStatus.COMPLETED)).toList();
        BigDecimal totalAmountWithdrawConfirm = BigDecimal.valueOf(totalAmountWithdrawConfirm(totalWithdrawConfirmAndComplete));

        BigDecimal totalWithdrawGold = BigDecimal.ZERO;
        for (WithdrawGold withdrawGold : withdrawHaveCondition) {
            totalWithdrawGold = totalWithdrawGold.add(BigDecimal.valueOf(withdrawGold.getAmount()));
        }

        return new DashboardWithdrawResponse(withdrawHaveCondition.size(), withdrawUnverified.size(), withdrawPending.size(),
                withdrawConfirmed.size(), withdrawCompleted.size(), withdrawCanceled.size(), totalAmountWithdrawConfirm, totalWithdrawGold);
    }


    public Double totalAmountWithdrawConfirm(List<WithdrawGold> withdrawConfirmedAndComplete) {
        return withdrawConfirmedAndComplete.stream()
                .map(WithdrawGold::getAmount)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }


    public List<WithdrawGold> compareDateInWithdraw(List<WithdrawGold> withdrawGolds) {
        Instant instant = Instant.now();
        List<WithdrawGold> withdrawInDate = new ArrayList<>();
        Instant dateNow = instant.truncatedTo(ChronoUnit.DAYS);

        for (WithdrawGold withdraw : withdrawGolds) {
            Instant dateCreate = withdraw.getTransactionDate().truncatedTo(ChronoUnit.DAYS);
            if (!dateCreate.isBefore(dateNow) && !dateCreate.isAfter(dateNow)) {
                withdrawInDate.add(withdraw);
            }
        }

        return withdrawInDate;
    }

    public List<WithdrawGold> compareDateInWeekWithdraw(List<WithdrawGold> withdrawGolds) {
        List<WithdrawGold> withdrawInWeek = new ArrayList<>();
        Instant dateNow = Instant.now();
        Instant firstDate = dateNow.minus(7 * 24 * 60 * 60, ChronoUnit.SECONDS).truncatedTo(ChronoUnit.DAYS);

        for (WithdrawGold withdraw : withdrawGolds) {
            Instant dateCreate = withdraw.getTransactionDate().truncatedTo(ChronoUnit.DAYS);
            boolean condition1 = dateCreate.isAfter(firstDate) && dateCreate.isBefore(dateNow);
            boolean condition2 = !dateCreate.isAfter(firstDate) && !dateCreate.isBefore(firstDate);
            boolean condition3 = !dateCreate.isAfter(dateNow) && !dateCreate.isBefore(dateNow);

            if (condition1 || condition2 || condition3) {
                withdrawInWeek.add(withdraw);
            }
        }

        return withdrawInWeek;
    }

    public List<WithdrawGold> compareDateInMonthWithdraw(Long monthChoose, List<WithdrawGold> withdrawGolds) {
        List<WithdrawGold> withdrawInMonth = new ArrayList<>();
        int yearCurrent = Instant.now().atZone(ZoneOffset.UTC).getYear();

        for (WithdrawGold withdraw : withdrawGolds) {
            int year = withdraw.getTransactionDate().atZone(ZoneOffset.UTC).getYear();
            int month = withdraw.getTransactionDate().atZone(ZoneOffset.UTC).getMonthValue();

            if (year == yearCurrent) {
                if (month == monthChoose) {
                    withdrawInMonth.add(withdraw);
                }
            }
        }

        return withdrawInMonth;
    }

    public List<WithdrawGold> compareDateInQuarterWithdraw(Long quarterChoose, List<WithdrawGold> withdrawGolds) {
        List<WithdrawGold> withdrawInQuarter = new ArrayList<>();
        int yearCurrent = Instant.now().atZone(ZoneOffset.UTC).getYear();

        for (WithdrawGold withdraw : withdrawGolds) {
            int year = withdraw.getTransactionDate().atZone(ZoneOffset.UTC).getYear();
            int month = withdraw.getTransactionDate().atZone(ZoneOffset.UTC).getMonthValue();

            if (year == yearCurrent) {
                if (quarterChoose == 1 && (month >= 1 && month <= 3)) {
                    withdrawInQuarter.add(withdraw);
                }
                if (quarterChoose == 2 && (month >= 4 && month <= 6)) {
                    withdrawInQuarter.add(withdraw);
                }
                if (quarterChoose == 3 && (month >= 7 && month <= 9)) {
                    withdrawInQuarter.add(withdraw);
                }
                if (quarterChoose == 4 && (month >= 10 && month <= 12)) {
                    withdrawInQuarter.add(withdraw);
                }
            }
        }

        return withdrawInQuarter;
    }

    public List<WithdrawGold> compareDateInYearWithdraw(Long yearChoose, List<WithdrawGold> withdrawGolds) {
        List<WithdrawGold> withdrawInYear = new ArrayList<>();

        for (WithdrawGold withdraw : withdrawGolds) {
            int year = withdraw.getTransactionDate().atZone(ZoneOffset.UTC).getYear();

            if (year == yearChoose) {
                withdrawInYear.add(withdraw);
            }
        }

        return withdrawInYear;
    }

    public DashboardWithdrawResponse calculateWithdrawGoldStatisticsFromAndTo(FromAndToRequest request) {
        checkValidationDate(request.getFrom(), request.getTo());
        List<WithdrawGold> withdrawGolds = withdrawGoldRepository.findAll();
        List<WithdrawGold> withdrawHaveCondition = filterFromAndToWithdraw(withdrawGolds, request.getFrom(), request.getTo());

        List<WithdrawGold> withdrawUnverified = withdrawHaveCondition.stream().filter(withdraw -> withdraw.getStatus().equals(WithdrawalStatus.UNVERIFIED)).toList();
        List<WithdrawGold> withdrawPending = withdrawHaveCondition.stream().filter(withdraw -> withdraw.getStatus().equals(WithdrawalStatus.PENDING)).toList();
        List<WithdrawGold> withdrawConfirmed = withdrawHaveCondition.stream().filter(withdraw -> withdraw.getStatus().equals(WithdrawalStatus.CONFIRMED)).toList();
        List<WithdrawGold> withdrawCompleted = withdrawHaveCondition.stream().filter(withdraw -> withdraw.getStatus().equals(WithdrawalStatus.COMPLETED)).toList();
        List<WithdrawGold> withdrawCanceled = withdrawHaveCondition.stream().filter(withdraw -> withdraw.getStatus().equals(WithdrawalStatus.CANCELLED)).toList();

        List<WithdrawGold> totalWithdrawConfirmAndComplete = withdrawHaveCondition.stream()
                .filter(withdrawGold -> withdrawGold.getStatus().equals(WithdrawalStatus.CONFIRMED) || withdrawGold.getStatus().equals(WithdrawalStatus.COMPLETED)).toList();
        BigDecimal totalAmountWithdrawConfirm = BigDecimal.valueOf(totalAmountWithdrawConfirm(totalWithdrawConfirmAndComplete));

        BigDecimal totalWithdrawGold = BigDecimal.ZERO;
        for (WithdrawGold withdrawGold : withdrawHaveCondition) {
            totalWithdrawGold = totalWithdrawGold.add(BigDecimal.valueOf(withdrawGold.getAmount()));
        }

        return new DashboardWithdrawResponse(withdrawHaveCondition.size(), withdrawUnverified.size(), withdrawPending.size(), withdrawConfirmed.size(), withdrawCompleted.size(), withdrawCanceled.size(), totalAmountWithdrawConfirm, totalWithdrawGold);
    }

    public List<WithdrawGold> filterFromAndToWithdraw(List<WithdrawGold> withdrawList, Instant from, Instant to) {
        List<WithdrawGold> withdrawFilter = new ArrayList<>();
        from = from.truncatedTo(ChronoUnit.DAYS);
        to = to.truncatedTo(ChronoUnit.DAYS);

        for (WithdrawGold withdraw : withdrawList) {
            Instant dateCreate = withdraw.getTransactionDate().truncatedTo(ChronoUnit.DAYS);

            boolean condition1 = dateCreate.isAfter(from) && dateCreate.isBefore(to);
            boolean condition2 = !dateCreate.isAfter(from) && !dateCreate.isBefore(from);
            boolean condition3 = !dateCreate.isAfter(to) && !dateCreate.isBefore(to);

            if (condition1 || condition2 || condition3) {
                withdrawFilter.add(withdraw);
            }
        }

        return withdrawFilter;
    }

    //-------------------------
    public List<PaymentHistoryDTO> historyDepositOfUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user by id: " + userId));
        List<PaymentHistory> paymentHistory = paymentHistoryRepository.findAllByUserInfoId(user.getUserInfo().getId());
        List<PaymentHistoryDTO> historyDTOS = new ArrayList<>();

        for (PaymentHistory payment : paymentHistory) {
            PaymentHistoryDTO paymentHistoryDTO = new PaymentHistoryDTO(payment.getId(), payment.getOrderCode(), payment.getAmount(), payment.getBankCode(), payment.getPaymentStatus(), payment.getPayDate(), payment.getReason(), payment.getUserInfo().getId());

            historyDTOS.add(paymentHistoryDTO);
        }

        return historyDTOS;
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    public List<TypeGold> getAllTypeGold() {
        return typeGoldRepository.findAllByActive(true);
    }

    public TypeGold getTypeGoldById(Long typeGoldId) {
        return typeGoldRepository.findById(typeGoldId).orElseThrow(() -> new NotFoundException("Not found type product with id: " + typeGoldId));
    }

    // admin tạo mới
    public TypeGold createTypeGold(TypeGoldRequest request) {
        checkValidationTypeGold(request.getTypeName());
        List<TypeGold> typeList = typeGoldRepository.findAll();

        for (TypeGold type : typeList) {
            if (request.getTypeName().equalsIgnoreCase(type.getTypeName())) {
                throw new TypeGoldException("The type gold name already existed");
            }
        }
        TypeGold type = new TypeGold(request.getTypeName(), request.getPrice(), request.getGoldUnit(), true);
        return typeGoldRepository.save(type);
    }

    // admin cập nhật
    public TypeGold updateTypeGold(Long typeGoldId, TypeGoldRequest request) {
        checkValidationTypeGold(request.getTypeName());
        TypeGold type = typeGoldRepository.findById(typeGoldId).orElseThrow(() -> new NotFoundException("Not found type product with id: " + typeGoldId));

        type.setTypeName(request.getTypeName());
        type.setPrice(request.getPrice());
        type.setGoldUnit(request.getGoldUnit());
        return typeGoldRepository.save(type);
    }

    //admin xóa
    public boolean deleteTypeGold(Long typeGoldId) {
        TypeGold type = typeGoldRepository.findById(typeGoldId)
                .orElseThrow(() -> new NotFoundException("Not found type product with id: " + typeGoldId));
        type.setActive(false);
        typeGoldRepository.save(type);
        return true;
    }

    private void checkValidationTypeGold(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            throw new TypeGoldException("Please input in field is missing.");
        }
    }

    public StatisticProductResponse filterByTypeGold(Long typeGoldId) {
        TypeGold typeGold = typeGoldRepository.findById(typeGoldId)
                .orElseThrow(() -> new NotFoundException("Not found type of gold with id: " + typeGoldId));
        List<Product> productList = productRepository.findAllByTypeGoldId(typeGold.getId());

        return new StatisticProductResponse(
                productList,
                totalWeightProduct(productList),
                totalQuantityProduct(productList),
                totalAmountProduct(productList)
        );
    }

    private Double totalWeightProduct(List<Product> productList) {
        double total = 0;
        for (Product product : productList) {
            total = total + (product.getWeight() * product.getUnitOfStock());
        }
        return total;
    }

    private Long totalQuantityProduct(List<Product> productList) {
        long total = 0;
        for (Product product : productList) {
            total = total + product.getUnitOfStock();
        }
        return total;
    }


    private BigDecimal totalAmountProduct(List<Product> productList) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal priceGen;
        for (Product product : productList) {
            if (product.getSecondPrice() != null) {
                priceGen = product.getSecondPrice();
            } else {
                priceGen = product.getTypeGold().getPrice();
            }

            total = total.add(priceGen.multiply(BigDecimal.valueOf(product.getUnitOfStock())));
        }
        return total;
    }


    private void checkValidationDate(Instant from, Instant to) {
        if (from == null && to == null) {
            throw new DashboardStatisticException("Please input start date and end date.");
        }

        if (from == null) {
            throw new DashboardStatisticException("Please input start date.");
        }

        if (to == null) {
            throw new DashboardStatisticException("Please input end date.");
        }

        if (to.isBefore(from)) {
            throw new DashboardStatisticException("Start date must be before end date.");
        }
    }

    public List<TransferUnitGold> getAllInformationTransfer() {
        return transferUnitGoldRepository.findAll();
    }

    public TransferUnitGold createInformationTransfer(TransferUnitGoldRequest request) {
        TransferUnitGold transferUnitGold = new TransferUnitGold(request.getFromUnit(), request.getToUnit(), request.getConversionFactor());
        return transferUnitGoldRepository.save(transferUnitGold);
    }

    public TransferUnitGold updateInformationTransfer(Long id, TransferUnitGoldRequest request) {
        TransferUnitGold transferUnitGold = transferUnitGoldRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found with id: " + id));

        transferUnitGold.setFromUnit(request.getFromUnit());
        transferUnitGold.setToUnit(request.getToUnit());
        transferUnitGold.setConversionFactor(request.getConversionFactor());

        return transferUnitGoldRepository.save(transferUnitGold);
    }

    public boolean deleteInformationTransfer(Long id) {
        TransferUnitGold transferUnitGold = transferUnitGoldRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found with id: " + id));
        transferUnitGoldRepository.delete(transferUnitGold);
        return true;
    }
}
