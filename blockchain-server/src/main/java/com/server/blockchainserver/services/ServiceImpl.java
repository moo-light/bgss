package com.server.blockchainserver.services;

import com.server.blockchainserver.dto.forum_dto.PostDTO;
import com.server.blockchainserver.dto.payment_dto.PaymentHistoryDTO;
import com.server.blockchainserver.dto.product_dto.ProductDTO;
import com.server.blockchainserver.dto.shopping_dto.RateDTO;
import com.server.blockchainserver.dto.shopping_dto.ReviewProductDTO;
import com.server.blockchainserver.dto.user_dto.UserDTO;
import com.server.blockchainserver.dto.user_dto.UserInfoDTO;
import com.server.blockchainserver.models.TransferUnitGold;
import com.server.blockchainserver.models.shopping_model.*;
import com.server.blockchainserver.models.shopping_model.Post.FilterHideEnum;
import com.server.blockchainserver.payload.request.FromAndToRequest;
import com.server.blockchainserver.payload.request.TransferUnitGoldRequest;
import com.server.blockchainserver.payload.request.TypeGoldRequest;
import com.server.blockchainserver.payload.request.forum_request.PostRequest;
import com.server.blockchainserver.payload.request.shopping_request.*;
import com.server.blockchainserver.payload.response.*;
import com.server.blockchainserver.server.BlockchainServer;
import com.server.blockchainserver.server.UserManagement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceImpl implements Services {

    @Autowired
    BlockchainServer blockchainServer;

    @Autowired
    UserManagement userManagement;

    @Autowired
    ModelMapper mapper;

    @Override
    public List<Product> filterProductByCategoryName(List<Long> categorIdList) {
        return blockchainServer.filterProductByCategoryName(categorIdList);
    }

    @Override
    public List<ProductDTO> filterProductWithSort(String search, Optional<Boolean> asc, Optional<Long> minPrice, Optional<Long> maxPrice, Optional<Integer> avgReview, Optional<List<Long>> categoryIDS, Optional<List<Long>> typeGoldIds, String typeOptionName) throws Exception {
//        List<Product> products = blockchainServer.filterProductWithSort(search, asc, minPrice, maxPrice,avgReview, categoryIDs);
//        return products.stream().map(p -> new ProductDTO(p,mapper)).collect(Collectors.toList());
        return  blockchainServer.filterProductWithSort(search, asc, minPrice, maxPrice, avgReview, categoryIDS,typeGoldIds, typeOptionName);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return blockchainServer.getProductById(id);
    }

    @Override
    public ProductDTO addNewProduct(ProductRequest productRequest) {
        return blockchainServer.addNewProduct(productRequest);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductRequest productRequest) {
        return blockchainServer.updateProduct(id, productRequest);
    }

    @Override
    public boolean deleteProduct(Long id) {
        return blockchainServer.deleteProduct(id);
    }

    @Override
    public ProductDTO updateProductImgUrl(Long productId, ProductImageRequest imgProduct) throws IOException {
        Product product = blockchainServer.updateProductImgUrl(productId, imgProduct);
        return mapper.map(product, ProductDTO.class);
    }

    @Override
    public List<Product> getAllProductByTypeName(String typeGoldName, String search) {
        return blockchainServer.getAllProductByTypeName(typeGoldName, search);
    }

    @Override
    public List<CartItem> getListCartItemOfUser(Long userId) {
        return blockchainServer.getListCartItemOfUser(userId);
    }

//    @Override
//    public List<CartItem> getListCartItem(Long userId) {
//        return blockchainServer.getListCartItem(userId);
//    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return blockchainServer.getCartByUserId(userId);
    }

    @Override
    public CartItem addProductToCart(ProductToCartRequest productToCartRequest) {
        return blockchainServer.addProductToCart(productToCartRequest);
    }

    @Override
    public CartItem updateQuantityProductInCartItem(Long cartItemId, Long quantity) {
        return blockchainServer.updateQuantityProductInCartItem(cartItemId, quantity);
    }

    @Override
    public boolean removeProductFromCart(Long cartItemId) {
        return blockchainServer.removeProductFromCart(cartItemId);
    }

    @Override
    public Order createOrder(Long userId, CreateOrderRequest createOrderRequest) {
        return blockchainServer.createOrder(userId, createOrderRequest);
    }

//    @Override
//    public Order createOrderNow(Long userId, CreateOrderNowRequest request) {
//        return blockchainServer.createOrderNow(userId, request);
//    }

    @Override
    public List<OrderDetail> getAllOrderDetailByOrderId(Long orderId) {
        return blockchainServer.getAllOrderDetailByOrderId(orderId);
    }

    @Override
    public Order searchOrderByQrCode(String code) {
        return blockchainServer.searchOrderByQrCode(code);
    }

    @Override
    public Order updateStatusReceived(Long orderId) {
        return blockchainServer.updateStatusReceived(orderId);
    }

    @Override
    public Order userConfirmReceived(Long orderId) {
        return blockchainServer.userConfirmReceived(orderId);
    }


    @Override
    public List<Order> getOrderList(Optional<Long> userId) {
        if (userId.isPresent()) return blockchainServer.getOrderListFromUserId(userId.get());
        else return blockchainServer.getAllOrders();
    }

    @Override
    public Order getOrderById(Long orderId) {
        return blockchainServer.getOrderById(orderId);
    }

    @Override
    public Discount createDiscountCode(DiscountCodeRequest discountCodeRequest) {
        return blockchainServer.createDiscountCode(discountCodeRequest);
    }

    @Override
    public List<Discount> getAllDiscountCode() {
        return blockchainServer.getAllDiscountCode();
    }

    @Override
    public Discount getDiscountCodeById(Long id) {
        return blockchainServer.getDiscountCodeById(id);
    }

//    @Override
//    public Discount getDiscountCodeByCode(String code, Long userId) {
//        Discount discount = blockchainServer.getDiscountCodeByCode(code);
//        if (discount != null && discount.getCodeOfUserList().stream().anyMatch(t -> t.getUser().getId().equals(userId)))
//            return null;
//        return discount;
//    }

    @Override
    public boolean deleteDiscountCode(Long id) {
        return blockchainServer.deleteDiscountCode(id);
    }

    @Override
    public List<DiscountCodeOfUser> getAllDiscountCodeOfUser(Long userId, Optional<Boolean> expire, String search) {
        return blockchainServer.getAllDiscountCodeOfUser(userId,expire, search);
    }

    @Override
    public DiscountCodeOfUser getDiscountCode(Long userId, Long discountCodeId) {
        return blockchainServer.getDiscountCode(userId, discountCodeId);
    }

    @Override
    public DiscountCodeOfUser createDiscountCodeOfUser(Long discountId, Long userId) {
        return blockchainServer.createDiscountCodeOfUser(discountId, userId);
    }

    @Override
    public boolean deleteDiscountCodeExpired(Long userId, Long discountCodeOfUserId) {
        return blockchainServer.deleteDiscountCodeExpired(userId, discountCodeOfUserId);
    }

    @Override
    public List<ProductCategory> getAllCategory(String search) {
        return blockchainServer.getAllCategory(search);
    }

    @Override
    public ProductCategory getCategoryById(Long categoryId) {
        return blockchainServer.getCategoryById(categoryId);
    }

    @Override
    public ProductCategory creatProductCategory(String categoryName) {
        return blockchainServer.creatProductCategory(categoryName);
    }

    @Override
    public ProductCategory updateProductCategory(Long categoryId, String categoryName) {
        return blockchainServer.updateProductCategory(categoryId, categoryName);
    }

    @Override
    public boolean deleteProductCategory(Long categoryId) {
        return blockchainServer.deleteProductCategory(categoryId);
    }


    @Override
    public List<PostDTO> getAllPost() {
        return blockchainServer.getAllPost().stream().map(post -> new PostDTO(post, new UserDTO(post.getUser(), new UserInfoDTO(post.getUser().getUserInfo())))).collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> getFilterAndSortedPost(String search, Optional<Long> categoryId, FilterHideEnum filterHideEnum, Instant fromDate, Instant toDate, boolean asc) {
        return blockchainServer.getFilterAndSortedPost(search, categoryId, filterHideEnum, fromDate, toDate, asc).stream().map(post -> new PostDTO(post, new UserDTO(post.getUser(), new UserInfoDTO(post.getUser().getUserInfo())))).collect(Collectors.toList());
    }

    @Override
    public PostDTO getPostById(Long postId) {
        Post post = blockchainServer.getPostById(postId);
        return new PostDTO(post, new UserDTO(post.getUser(), new UserInfoDTO(post.getUser().getUserInfo())));
    }

    @Override
    public PostDTO createPost(Long userId, PostRequest postRequest, MultipartFile imgUrl) throws IOException {
        Post createPost = blockchainServer.createPost(userId, postRequest, imgUrl);
        return new PostDTO(createPost, new UserDTO(createPost.getUser(), new UserInfoDTO(createPost.getUser().getUserInfo())));
    }

    @Override
    public PostDTO updatePost(Long postId, PostRequest postRequest, MultipartFile imgUrl) throws IOException {
        Post updatePost = blockchainServer.updatePost(postId, postRequest, imgUrl);
        return new PostDTO(updatePost, new UserDTO(updatePost.getUser(), new UserInfoDTO(updatePost.getUser().getUserInfo())));
    }

    @Override
    public PostDTO deletePost(Long postId) {
        Post deletePost = blockchainServer.deletePost(postId);
        return new PostDTO(deletePost, new UserDTO(deletePost.getUser(), new UserInfoDTO(deletePost.getUser().getUserInfo())));
    }

    @Override
    public List<PostDTO> searchPostByTitle(String search) {
        return blockchainServer.searchPostByTitle(search).stream().map(post -> new PostDTO(post, new UserDTO(post.getUser(), new UserInfoDTO(post.getUser().getUserInfo())))).collect(Collectors.toList());
    }

//    @Override
//    public List<PostDTO> getAllPostByCategoryPostId(Long categoryPostId, String search) {
//        return blockchainServer.getAllPostByCategoryPostId(categoryPostId, search).stream().map(post -> new PostDTO(post, new UserDTO(post.getUser(), new UserInfoDTO(post.getUser().getUserInfo())))).collect(Collectors.toList());
//    }

    @Override
    public List<PostDTO> getAllPostWithIsPinned() {
        return blockchainServer.getAllPostWithIsPinned().stream().map(post -> new PostDTO(post, new UserDTO(post.getUser(), new UserInfoDTO(post.getUser().getUserInfo())))).collect(Collectors.toList());
    }

    @Override
    public List<RateDTO> getAllRate(String search) {
        return blockchainServer.getAllRate(search).stream().map(rate -> new RateDTO(rate, new UserDTO(rate.getUser(), new UserInfoDTO(rate.getUser().getUserInfo())))).collect(Collectors.toList());
    }

//    @Override
//    public List<RateDTO> getAllRateByUserIdAndPostId(Long userId, Long postId) {
//        return blockchainServer.getAllRateByUserIdAndPostId(userId, postId).stream().map(rate -> new RateDTO(rate, new UserDTO(rate.getUser(), new UserInfoDTO(rate.getUser().getUserInfo())))).collect(Collectors.toList());
//    }

    @Override
    public List<RateDTO> getAllRateOfPost(Long postId) {
        return blockchainServer.getAllRateOfPost(postId).stream().map(rate -> new RateDTO(rate, new UserDTO(rate.getUser(), new UserInfoDTO(rate.getUser().getUserInfo())))).collect(Collectors.toList());
    }

    @Override
    public RateDTO getRateById(Long rateId) {
        Rate rate = blockchainServer.getRateById(rateId);
        return new RateDTO(rate, new UserDTO(rate.getUser(), new UserInfoDTO(rate.getUser().getUserInfo())));
    }

    @Override
    public RateDTO createRate(Long userId, Long postId, String content, MultipartFile imageRate) throws IOException {
        Rate createRate = blockchainServer.createRate(userId, postId, content, imageRate);
        return new RateDTO(createRate, new UserDTO(createRate.getUser(), new UserInfoDTO(createRate.getUser().getUserInfo())));
    }

    @Override
    public RateDTO updateRateOfUserInPost(Long rateId, Long userId, Long postId, String content, MultipartFile imageRate) throws IOException {
        Rate updateRate = blockchainServer.updateRateOfUserInPost(rateId, userId, postId, content, imageRate);
        return new RateDTO(updateRate, new UserDTO(updateRate.getUser(), new UserInfoDTO(updateRate.getUser().getUserInfo())));
    }

    @Override
    public boolean deleteRate(Long rateId, Long userId, Long postId) {
        return blockchainServer.deleteRate(rateId, userId, postId);
    }

    @Override
    public List<RateDTO> getFilteredRate(String search, Optional<Long> userId, Optional<Long> postId, Boolean showHiding) {
        return blockchainServer.getFilteredRate(search, userId, postId, showHiding)
                .stream()
                .map(rate -> new RateDTO(rate, new UserDTO(rate.getUser(), new UserInfoDTO(rate.getUser().getUserInfo()))))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryPost> showAllCategoryPost(String search) {
        return blockchainServer.showAllCategoryPost(search);
    }

    @Override
    public CategoryPost getCategoryPostById(Long id) {
        return blockchainServer.getCategoryPostById(id);
    }

    @Override
    public CategoryPost createCategoryPost(String categoryName) {
        return blockchainServer.createCategoryPost(categoryName);
    }

    @Override
    public CategoryPost updateCategoryPost(Long categoryPostId, String categoryName) {
        return blockchainServer.updateCategoryPost(categoryPostId, categoryName);
    }

    @Override
    public boolean deleteCategoryPost(Long categoryPostId) {
        return blockchainServer.deleteCategoryPost(categoryPostId);
    }

    @Override
    public List<Order> checkProductReceived(Long userId, Long productId) {
        return blockchainServer.checkProductReceived(userId, productId);
    }

    @Override
    public ReviewProduct checkReview(Long userId, Long productId) {
        return blockchainServer.checkReview(userId, productId);
    }

    @Override
    public List<ReviewProductDTO> getAllReviewOfAllProduct(String search) {
        return blockchainServer.getAllReviewOfAllProduct(search).stream().map(reviewProduct -> new ReviewProductDTO(reviewProduct, new UserDTO(reviewProduct.getUser(), new UserInfoDTO(reviewProduct.getUser().getUserInfo())))).collect(Collectors.toList());
    }

    @Override
    public List<ReviewProductDTO> getAllReviewInProduct(Long productId) {
        return blockchainServer.getAllReviewInProduct(productId).stream().map(reviewProduct -> new ReviewProductDTO(reviewProduct, new UserDTO(reviewProduct.getUser(), new UserInfoDTO(reviewProduct.getUser().getUserInfo())))).collect(Collectors.toList());
    }

    @Override
    public ReviewProductDTO createReview(Long userId, Long productId, ReviewProductRequest reviewRequest, MultipartFile imgReview) throws IOException {
        ReviewProduct reviewProduct = blockchainServer.createReview(userId, productId, reviewRequest, imgReview);
        return new ReviewProductDTO(reviewProduct, new UserDTO(reviewProduct.getUser(), new UserInfoDTO(reviewProduct.getUser().getUserInfo())));
    }

    @Override
    public ReviewProductDTO updateReview(Long userId, Long productId, ReviewProductRequest reviewRequest, MultipartFile imgReview) throws IOException {
        ReviewProduct reviewProduct = blockchainServer.updateReview(userId, productId, reviewRequest, imgReview);
        return new ReviewProductDTO(reviewProduct, new UserDTO(reviewProduct.getUser(), new UserInfoDTO(reviewProduct.getUser().getUserInfo())));
    }

    @Override
    public boolean deleteReviewed(Long reviewProductId, Long productId, Long userId) {
        return blockchainServer.deleteReviewed(reviewProductId, productId, userId);
    }

    @Override
    public DashboardOrderResponse calculateOrderSalesStatistics(String type, Long monthChoose, Long quarterChoose, Long yearChoose) {
        return blockchainServer.calculateOrderSalesStatistics(type, monthChoose, quarterChoose, yearChoose);
    }

    @Override
    public DashboardOrderResponse calculateOrderSalesStatisticsFromAndTo(FromAndToRequest request) {
        return blockchainServer.calculateOrderSalesStatisticsFromAndTo(request);
    }

    @Override
    public DashboardTransactionResponse calculateTransactionSellSales(String type, Long monthChoose, Long quarterChoose, Long yearChoose) {
        return blockchainServer.calculateTransactionSellSales(type, monthChoose, quarterChoose, yearChoose);
    }

    @Override
    public DashboardTransactionResponse calculateTransactionBuySales(String type, Long monthChoose, Long quarterChoose, Long yearChoose) {
        return blockchainServer.calculateTransactionBuySales(type, monthChoose, quarterChoose, yearChoose);
    }

    @Override
    public DashboardTransactionResponse calculateTransactionSellSalesByFromAndTo(FromAndToRequest request) {
        return blockchainServer.calculateTransactionSellSalesByFromAndTo(request);
    }

    @Override
    public DashboardTransactionResponse calculateTransactionBuySalesByFromAndTo(FromAndToRequest request) {
        return blockchainServer.calculateTransactionBuySalesByFromAndTo(request);
    }

    @Override
    public QuantityStatisticResponse quantityStatistics() {
        return blockchainServer.quantityStatistics();
    }

    @Override
    public DashboardWithdrawResponse calculateWithdrawGoldStatistics(String type, Long monthChoose, Long quarterChoose, Long yearChoose) {
        return blockchainServer.calculateWithdrawGoldStatistics(type, monthChoose, quarterChoose, yearChoose);
    }

    @Override
    public DashboardWithdrawResponse calculateWithdrawGoldStatisticsFromAndTo(FromAndToRequest request) {
        return blockchainServer.calculateWithdrawGoldStatisticsFromAndTo(request);
    }

    @Override
    public List<PaymentHistoryDTO> historyDepositOfUser(Long userId) {
        return blockchainServer.historyDepositOfUser(userId);
    }

    @Override
    public List<TypeGold> getAllTypeGold() {
        return blockchainServer.getAllTypeGold();
    }

    @Override
    public TypeGold getTypeGoldById(Long typeGoldId) {
        return blockchainServer.getTypeGoldById(typeGoldId);
    }

    @Override
    public TypeGold createTypeGold(TypeGoldRequest request) {
        return blockchainServer.createTypeGold(request);
    }

    @Override
    public TypeGold updateTypeGold(Long typeGoldId, TypeGoldRequest request) {
        return blockchainServer.updateTypeGold(typeGoldId, request);
    }

    @Override
    public boolean deleteTypeGold(Long typeGoldId) {
        return blockchainServer.deleteTypeGold(typeGoldId);
    }

    @Override
    public StatisticProductResponse filterByTypeGold(Long typeGoldId) {
        return blockchainServer.filterByTypeGold(typeGoldId);
    }

    @Override
    public List<TransferUnitGold> getAllInformationTransfer() {
        return blockchainServer.getAllInformationTransfer();
    }

    @Override
    public TransferUnitGold createInformationTransfer(TransferUnitGoldRequest request) {
        return blockchainServer.createInformationTransfer(request);
    }

    @Override
    public TransferUnitGold updateInformationTransfer(Long id, TransferUnitGoldRequest request) {
        return blockchainServer.updateInformationTransfer(id, request);
    }

    @Override
    public boolean deleteInformationTransfer(Long id) {
        return blockchainServer.deleteInformationTransfer(id);
    }


}
