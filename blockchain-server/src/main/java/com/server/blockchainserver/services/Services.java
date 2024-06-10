package com.server.blockchainserver.services;

import com.server.blockchainserver.dto.forum_dto.PostDTO;
import com.server.blockchainserver.dto.payment_dto.PaymentHistoryDTO;
import com.server.blockchainserver.dto.product_dto.ProductDTO;
import com.server.blockchainserver.dto.shopping_dto.RateDTO;
import com.server.blockchainserver.dto.shopping_dto.ReviewProductDTO;
import com.server.blockchainserver.models.TransferUnitGold;
import com.server.blockchainserver.models.shopping_model.*;
import com.server.blockchainserver.models.shopping_model.Post.FilterHideEnum;
import com.server.blockchainserver.payload.request.FromAndToRequest;
import com.server.blockchainserver.payload.request.TransferUnitGoldRequest;
import com.server.blockchainserver.payload.request.TypeGoldRequest;
import com.server.blockchainserver.payload.request.forum_request.PostRequest;
import com.server.blockchainserver.payload.request.shopping_request.*;
import com.server.blockchainserver.payload.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface Services {

    ProductDTO getProductById(Long id);

    ProductDTO addNewProduct(ProductRequest productRequest);

    ProductDTO updateProduct(Long id, ProductRequest productRequest);

    boolean deleteProduct(Long id);

    ProductDTO updateProductImgUrl(Long id, ProductImageRequest Images) throws IOException;
    List<Product> getAllProductByTypeName(String typeGoldName, String search);


    List<CartItem> getListCartItemOfUser(Long userId);
//    List<CartItem> getListCartItem(Long userId);

    Cart getCartByUserId(Long userId);

    CartItem addProductToCart(ProductToCartRequest productToCartRequest);

    CartItem updateQuantityProductInCartItem(Long cartItemId, Long quantity);

    boolean removeProductFromCart(Long cartItemId);

    List<Order> getOrderList(Optional<Long> userId);

    Order getOrderById(Long orderId);

    Order createOrder(Long userId, CreateOrderRequest createOrderRequest);

//    Order createOrderNow(Long userId, CreateOrderNowRequest request);
    List<OrderDetail> getAllOrderDetailByOrderId(Long orderId);

    Order searchOrderByQrCode(String code);

    Order updateStatusReceived(Long orderId);
    Order userConfirmReceived(Long orderId);


    Discount createDiscountCode(DiscountCodeRequest discountCodeRequest);

    List<Discount> getAllDiscountCode();

    Discount getDiscountCodeById(Long id);

//    Discount getDiscountCodeByCode(String code, Long userId);

    boolean deleteDiscountCode(Long id);

    List<DiscountCodeOfUser> getAllDiscountCodeOfUser(Long userId, Optional<Boolean> expire, String search);

    DiscountCodeOfUser getDiscountCode(Long userId, Long discountCodeId);

    DiscountCodeOfUser createDiscountCodeOfUser(Long discountId, Long userId);

    boolean deleteDiscountCodeExpired(Long userId, Long discountCodeOfUserId) throws Exception;

    // Những Function bên dưới đã được làm lúc 28/02/2024

    List<ProductCategory> getAllCategory(String search);

    ProductCategory getCategoryById(Long categoryId);

    ProductCategory creatProductCategory(String categoryName);

    ProductCategory updateProductCategory(Long categoryId, String categoryName);

    boolean deleteProductCategory(Long categoryId);


    List<Product> filterProductByCategoryName(List<Long> categorIdList);

    List<ProductDTO> filterProductWithSort(String search, Optional<Boolean> asc, Optional<Long> minPrice, Optional<Long> maxPrice, Optional<Integer> avgReview, Optional<List<Long>> categoryIDS, Optional<List<Long>> typeGoldIds, String typeOptionName) throws Exception;

    List<PostDTO> getAllPost();

    List<PostDTO> getFilterAndSortedPost(String search, Optional<Long> categoryId, FilterHideEnum filterHideEnum, Instant fromDate, Instant toDate, boolean asc);

    PostDTO getPostById(Long postId);

    PostDTO createPost(Long userId, PostRequest postRequest, MultipartFile imgUrl) throws IOException;

    PostDTO updatePost(Long postId, PostRequest postRequest, MultipartFile imgUrl) throws IOException;

    PostDTO deletePost(Long postId);

    List<PostDTO> searchPostByTitle(String search);
//
//    List<PostDTO> getAllPostByCategoryPostId(Long categoryPostId, String search);

    List<PostDTO> getAllPostWithIsPinned();


    List<RateDTO> getAllRate(String search);
//
//    List<RateDTO> getAllRateByUserIdAndPostId(Long userId, Long postId);

    List<RateDTO> getAllRateOfPost(Long postId);

    RateDTO getRateById(Long rateId);

    RateDTO createRate(Long userId, Long postId, String content, MultipartFile imageRate) throws IOException;

    RateDTO updateRateOfUserInPost(Long rateId, Long userId, Long postId, String content, MultipartFile imageRate) throws IOException;

    boolean deleteRate(Long rateId, Long userId, Long postId);

    List<RateDTO> getFilteredRate(String search, Optional<Long> userId, Optional<Long> postId, Boolean showHiding);


    List<CategoryPost> showAllCategoryPost(String search);

    CategoryPost getCategoryPostById(Long id);

    CategoryPost createCategoryPost(String categoryName);

    CategoryPost updateCategoryPost(Long categoryPostId, String categoryName);

    boolean deleteCategoryPost(Long categoryPostId);


    List<Order> checkProductReceived(Long userId, Long productId);

    ReviewProduct checkReview(Long userId, Long productId);
    List<ReviewProductDTO> getAllReviewOfAllProduct(String search);

    List<ReviewProductDTO> getAllReviewInProduct(Long productId);

    ReviewProductDTO createReview(Long userId, Long productId, ReviewProductRequest reviewRequest, MultipartFile imgReview) throws IOException;

    ReviewProductDTO updateReview(Long userId, Long productId, ReviewProductRequest reviewRequest, MultipartFile imgReview) throws IOException;

    boolean deleteReviewed(Long reviewProductId, Long productId, Long userId);


    DashboardOrderResponse calculateOrderSalesStatistics(String type, Long monthChoose, Long quarterChoose, Long yearChoose);
    DashboardOrderResponse calculateOrderSalesStatisticsFromAndTo(FromAndToRequest request);

    DashboardTransactionResponse calculateTransactionSellSales(String type, Long monthChoose, Long quarterChoose, Long yearChoose);
    DashboardTransactionResponse calculateTransactionBuySales(String type, Long monthChoose, Long quarterChoose, Long yearChoose);
    DashboardTransactionResponse calculateTransactionSellSalesByFromAndTo(FromAndToRequest request);
    DashboardTransactionResponse calculateTransactionBuySalesByFromAndTo(FromAndToRequest request);

    QuantityStatisticResponse quantityStatistics();

    DashboardWithdrawResponse calculateWithdrawGoldStatistics(String type, Long monthChoose, Long quarterChoose, Long yearChoose);
    DashboardWithdrawResponse calculateWithdrawGoldStatisticsFromAndTo(FromAndToRequest request);

    List<PaymentHistoryDTO> historyDepositOfUser(Long userId);


    List<TypeGold> getAllTypeGold();
    TypeGold getTypeGoldById(Long typeGoldId);
    TypeGold createTypeGold(TypeGoldRequest request);
    TypeGold updateTypeGold(Long typeGoldId, TypeGoldRequest request);
    boolean deleteTypeGold(Long typeGoldId);
    StatisticProductResponse filterByTypeGold(Long typeGoldId);

    List<TransferUnitGold> getAllInformationTransfer();
    TransferUnitGold createInformationTransfer(TransferUnitGoldRequest request);
    TransferUnitGold updateInformationTransfer(Long id, TransferUnitGoldRequest request);
    boolean deleteInformationTransfer(Long id);
}


