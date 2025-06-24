/// API Connection
final String urlConnection = 'http://192.168.1.8:1081/msa/api/';
final String urlSupabase = 'https://lmtqwglnnbgsrxhelpxz.supabase.co';
final String anonKey =
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxtdHF3Z2xubmJnc3J4aGVscHh6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU3MTI1NzIsImV4cCI6MjA2MTI4ODU3Mn0.5D6-g10oFKgB5eJw7jbJPGtOsr2BmrYnm5pTpfjA_J0';

//=================================API ENDPOINTS=================================
//____________________________________USER______________________________________
final String register = 'user/register';
final String login = 'user/login';
final String verifyOtp = 'user/verify-otp';
final String resetPass = 'user/reset-password';
final String resetPassWithoutOtp = 'user/reset-password/';
final String updateUser = 'user/';
final String deleteUser = 'user/';
final String getUserByEmail = 'user/email/'; //user/email/{email}
final String refreshTokenUrl = 'user/refresh';

//____________________________________PRODUCT___________________________________
final String getAllProducts = 'product'; //product?page=1&pageSize=10
final String filterAndSortProducts =
    'product/filter-products'; //product/filter?minPrice=100&maxPrice=500&color=red&categoryId=2&page=1&pageSize=10
final String getAllProductsInBranch =
    'product/branch/'; //product/branch/1?page=1&size=10&sortBy=price&sortDirection=desc
final String searchProductsInBranch =
    'product/branch/'; //product/branch/10/search?keyword=phone&minPrice=100&maxPrice=500&color=black&page=1&pageSize=15&sortBy=price&sortDirection=desc
final String getProductById = 'product/'; //product/1
final String searchProducts =
    'product/search'; //product/search?keyword=smartphone&minPrice=200&maxPrice=1000&color=red&categoryId=5&manufacturerId=3&page=2&pageSize=20&sortBy=price&sortDirection=desc
final String createProduct = 'product';
final String updateProduct = 'product/';
final String deleteProduct = 'product/';
final String filterProduct = 'product/filter';

//____________________________________MANUFACTURER____________________________________

//____________________________________CATEGORY____________________________________
final String getAllCategories = 'category'; //category?page=1&pageSize=10
final String getCategoryById = 'category/';
final String createCategory = 'category';
final String deleteCategory = 'category/';
final String updateCategory = 'category/';

//____________________________________PROMOCODE____________________________________
final String getAllPromoCode =
    'promo-code/user/'; //promo-code?page=1&pageSize=10
final String getPromoCodeById = 'promo-code/';
final String createPromoCode = 'promo-code';
final String updatePromoCode = 'promo-code/';
final String deletePromoCode = 'promo-code/';

//____________________________________CART____________________________________
final String getOrCreateCart = 'cart/user/';

//____________________________________CARTITEM____________________________________
final String getCartItem = 'cart-item/'; //cart-item/1/3{cartId}/{productId}
final String calculateCartTotal = 'cart-item/calculate-total/'; //{cartId}
final String getCartItemById = 'cart-item/by-id/';
final String getCartItemsByCartId = 'cart-item/by-cart/';
final String getAllCartItem = 'cart-item/all-by-cart/';
final String addToCart =
    'cart-item/add'; //cart-item/add?userId=1&productId=7&branchId=3&quantity=1
final String createCartItem = 'cart-item';
final String updateCartItemsSelection =
    'cart-item/update-selection?isSelected=';
final String deleteCartItem = 'cart-item/';
final String clearCart = 'cart-item/clear/';

//____________________________________ORDER____________________________________
final String createOrder = 'order';
final String getOrderById = 'order/';
final String searchOrdersByPhoneNumber =
    'order/search'; //order/search?phoneNumber=0912345678&page=0&pageSize=5
final String getOrdersByUserIdAndStatus =
    'order/user/'; //order/user/6/status/PENDING?page=0&pageSize=10
final String previewOrder =
    'order/preview'; //order/preview?userId=6&cartId=1&promoCodes=TET2026,TET2027
final String getAllOrders =
    'order'; //order?page=1&size=20&sortBy=grandTotal&sortDirection=asc
final String getRevenueStatistics =
    'order/revenue/statistics'; //order/revenue/statistics?year=2025&month=5&branchId=1&userId=6
final String getOrdersByBranchId =
    'order/branch/'; //order/branch/1?page=1&size=20&sortDirection=asc
final String updateOrder = 'order/';
final String deleteOrder = 'order/';

final String getAllBranch = 'branch';

//____________________________________GOSHIP____________________________________
final String getCities = 'shipment/cities';
final String getDistricts = 'shipment/districts/';
final String getWards = 'shipment/wards/';

//____________________________________FEEDBACK____________________________________
final String getFeedbackByIdUrl = 'feedback/';
final String getAllFeedbacksByProductIdUrl = 'feedback/product/';
final String updateFeedbackUrl = 'feedback/';
final String createFeedbackUrl = 'feedback';
final String deleteFeedbackUrl = 'feedback/';

/// SECURE STORAGE
final String accessTokenKey = 'ACCESS_TOKEN';
final String refreshTokenKey = 'REFRESH_TOKEN';
final String deviceIdKey = 'DEVICEID';
final String emailKey = 'mwang38203@gmail.com';
final String userModelKey = 'USERMODEL';
final String cartModelKey = 'CARTMODEL';
final String branchModelKey = 'BRANCHMODEL';

///Colors
final String appBarColor = '#007A5E';
final String appBarGradientColor = '#00B894';
final String backgroundColor = '#E0F7F1';

final String iconColor = '#50C878';

final String primaryTextColor = '#333333';
final String secondaryTextColor = '#666666';

final String primaryErrorColor = '#D32F2F';
final String secondaryErrorColor = '#FF6B6B';

final String snackBarColor = '#2E7D6F';

// final String dropShadowColor = '#0D47A1';
final String dropShadowColor = '#B2DFDB';
final String borderColor = '#E0E0E0';
final String borderColorGreen = '#1EBD58';

final String primaryButtonColor = '#00A86B';
final String secondaryButtonColor = '#29AB87';

final String titleDialogErrorColor = '#D32F2F';
final String titleDialogSuccessColor = '#4CAF50';

final String primaryButtonColorRed = '#FA5A7D';
final String secondaryButtonColorRed = '#FFE2E5';

final String primaryColorGreen = '#3CD856';
final String secondaryColorGreen = '#DCFCE7';

final String primaryColorOrange = '#FF947A';
final String secondaryColorOrange = '#FFF4DE';

final String primaryColorPurple = '#BF83FF';
final String secondaryColorPurple = '#F3E8FF';

final String actionColor = '#1EBD58';
final String secondaryActionColor = '#D6EDE1';

///Image
final String welcome1 = 'assets/images/welcome1.png';
final String welcome2 = 'assets/images/welcome2.png';
final String welcome3 = 'assets/images/welcome3.png';

final String iconFacebook = 'assets/icons/icon_facebook.png';
final String iconGoogle = 'assets/icons/icon_google.png';

// === UI / Error / AppBar ===
final String imgError404 = 'assets/images/err404.png';
final String imgAppBarIllustration = 'assets/images/image_appbar.png';
final String imgOtp = 'assets/images/otp.png';
final String imgResetPassword = 'assets/images/dat_mau_khau.png';
final String imgForgotPassword = 'assets/images/quen_mat_khau.png';

// === Categories / Products ===
final String imgIconApp = 'assets/images/icon_app.png';
final String imgCategoryBanhNgot = 'assets/images/banh_ngot.png';
final String imgCategoryBotGiat = 'assets/images/bot_giat.png';
final String imgCategoryDoHop = 'assets/images/do_hop.png';
final String imgCategoryGao = 'assets/images/gao.png';
final String imgCategoryGiaVi = 'assets/images/gia_vi.png';
final String imgCategoryMi = 'assets/images/mi.png';
final String imgCategoryNguCoc = 'assets/images/ngu_coc.png';
final String imgCategoryNuocNgot = 'assets/images/nuoc_ngot.png';
final String imgCategorySuaHop = 'assets/images/sua_do_hop.png';
final String imgCategorySuaTam = 'assets/images/sua_tam.png';
final String imgCategoryThitCa = 'assets/images/thit-ca.png';
final String imgCategoryThuCung = 'assets/images/thu_cung.png';
final String imgCategoryTraiCay = 'assets/images/trai_cay.png';
final String imgCategoryTrungSua = 'assets/images/trung_sua.png';

/// Avatar
final String avtMen1 = 'assets/images/avt_men1.jpg';
final String avtMen2 = 'assets/images/avt_men2.jpg';
final String avtMen3 = 'assets/images/avt_men3.jpg';
final String avtMen4 = 'assets/images/avt_men4.jpg';
final String avtMen5 = 'assets/images/avt_men5.jpg';
final String avtMen6 = 'assets/images/avt_men6.jpg';

final String avtWomen1 = 'assets/images/avt_women1.jpg';
final String avtWomen2 = 'assets/images/avt_women2.jpg';
final String avtWomen3 = 'assets/images/avt_women3.jpg';
final String avtWomen4 = 'assets/images/avt_women4.jpg';
final String avtWomen5 = 'assets/images/avt_women5.jpg';
final String avtWomen6 = 'assets/images/avt_women6.jpg';
final String avtWomen7 = 'assets/images/avt_women7.jpg';
final String avtWomen8 = 'assets/images/avt_women8.jpg';
final String avtWomen9 = 'assets/images/avt_women9.jpg';

final String avtAdmin = 'assets/images/avt_admin.jpg';
final String avtManager = 'assets/images/avt_manager.jpg';

final String imgBranch = 'assets/images/img_branch.png';
final String imgProductDefault =
    'https://lmtqwglnnbgsrxhelpxz.supabase.co/storage/v1/object/sign/msa/715ddffcb1448a27614572608ab8b6e5.jpg?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InN0b3JhZ2UtdXJsLXNpZ25pbmcta2V5XzE5ZTM1ODc1LTdlODYtNDljMy1iOWE3LTQ5OWQ5MGRkOTk3MyJ9.eyJ1cmwiOiJtc2EvNzE1ZGRmZmNiMTQ0OGEyNzYxNDU3MjYwOGFiOGI2ZTUuanBnIiwiaWF0IjoxNzQ3NDgyNzk2LCJleHAiOjE3NzkwMTg3OTZ9.tDTHIEe7SvgaKbuftP9MlsjONd4XULSlWdS9GjDMFlo';
