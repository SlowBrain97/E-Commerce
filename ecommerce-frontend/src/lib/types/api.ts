// API Response Types
export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
  success: boolean;
  path: string;
  validationErrors?: Record<string, string>;
  timestamp: string;
}

// User Types
export interface UserInfo {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  role: string;
  isVerified: boolean;
  avatarUrl: string;
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  avatarUrl: string;
  role: string;
  isActive: boolean;
  isVerified: boolean;
  lastLoginAt: string;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateProfileRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  avatarUrl?: string;
}

// Auth Types
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserInfo;
}

export interface LoginRequest {
  emailOrUsername: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

// Product Types
export interface ProductVariantDTO {
  id?: number;
  variantType: string;
  variantValue: string;
  variantDescription?: string;
  price: number;
  compareAtPrice?: number;
  stockQuantity?: number;
  sortOrder?: number;
}

export interface ProductImageDTO {
  imageUrl: string;
  altText?: string;
  isPrimary?: boolean;
  sortOrder?: number;
}

export interface CategoryDTO {
  id: number;
  name: string;
  description: string;
}

export interface ProductResponse {
  id: number;
  name: string;
  description: string;
  shortDescription: string;
  sku: string;
  price: number;
  compareAtPrice?: number;
  stockQuantity: number;
  isActive: boolean;
  isFeatured: boolean;
  weight?: number;
  dimensions?: string;
  tags?: string;
  averageRating: number;
  reviewCount: number;
  primaryImageUrl: string;
  allImageUrls: string[];
  category: CategoryDTO;
  variants?: ProductVariantDTO[];
}

export interface ProductCreateRequest {
  name: string;
  description?: string;
  shortDescription?: string;
  sku: string;
  price: number;
  compareAtPrice?: number;
  stockQuantity?: number;
  categoryId: number;
  tags?: string;
  weight?: number;
  dimensions?: string;
  images?: ProductImageDTO[];
  variants?: ProductVariantDTO[];
}

export interface ProductUpdateRequest {
  name: string;
  description?: string;
  shortDescription?: string;
  price: number;
  compareAtPrice?: number;
  stockQuantity?: number;
  categoryId: number;
  tags?: string;
  weight?: number;
  dimensions?: string;
}

export interface ProductSearchRequest {
  searchTerm?: string;
  categoryId?: number;
  minPrice?: number;
  maxPrice?: number;
  inStock?: boolean;
  sortBy?: string;
  sortDirection?: string;
  page?: number;
  size?: number;
}

// Category Types
export interface CategoryResponse {
  id: number;
  name: string;
  description: string;
  imageUrl: string;
  icon: string;
  isActive: boolean;
  isFeatured: boolean;
  sortOrder: number;
  parentId?: number;
  subcategoryCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CategoryCreateRequest {
  name: string;
  description?: string;
  imageUrl?: string;
  icon?: string;
  isActive?: boolean;
  isFeatured?: boolean;
  sortOrder?: number;
  parentId?: number;
}

export interface CategoryUpdateRequest {
  name?: string;
  description?: string;
  imageUrl?: string;
  icon?: string;
  isActive?: boolean;
  isFeatured?: boolean;
  sortOrder?: number;
  parentId?: number;
}

// Cart Types
export interface CartItemResponse {
  id: string;
  productId: string;
  productName: string;
  productImage: string;
  price: number;
  quantity: number;
  maxQuantity: number;
  variantId?: string;
  variantName?: string;
  addedAt: string;
}

export interface CartResponse {
  items: CartItemResponse[];
  totalItems: number;
  totalPrice: number;
  currency: string;
}

export interface AddToCartRequest {
  productId: string;
  variantId?: string;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}

// Review Types
export interface ReviewResponse {
  id: number;
  productId: number;
  productName: string;
  userId: number;
  userName: string;
  userAvatar: string;
  rating: number;
  title: string;
  comment: string;
  isVerifiedPurchase: boolean;
  status: string;
  isFeatured: boolean;
  helpfulVotes: number;
  totalVotes: number;
  helpfulnessPercentage: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateReviewRequest {
  productId: number;
  rating: number;
  title?: string;
  comment?: string;
}

// Pagination Types
export interface PageResponse<T> {
  data: T[];
  size: number;
  page: number;
  totalElements: number;
  totalPages: number;
  lastPage: boolean;
}

// Dashboard Types
export interface DashboardOverviewDTO {
  salesStats: SalesStatsDTO;
  productStats: ProductStatsDTO;
  userStats: UserStatsDTO;
  orderStats: OrderStatsDTO;
  recentOrders: RecentOrdersDTO;
  topProducts: TopProductsDTO;
}

export interface SalesStatsDTO {
  totalRevenue: number;
  monthlyRevenue: number;
  weeklyRevenue: number;
  dailyRevenue: number;
  totalOrders: number;
  monthlyOrders: number;
  weeklyOrders: number;
  dailyOrders: number;
  averageOrderValue: number;
  revenueGrowthPercentage: number;
  orderGrowthPercentage: number;
}

export interface ProductStatsDTO {
  totalProducts: number;
  activeProducts: number;
  inactiveProducts: number;
  lowStockProducts: number;
  outOfStockProducts: number;
  totalCategories: number;
  topSellingProductId?: number;
  topSellingProductName?: string;
  topSellingProductSales?: number;
}

export interface UserStatsDTO {
  totalUsers: number;
  activeUsers: number;
  inactiveUsers: number;
  newUsersToday: number;
  newUsersThisWeek: number;
  newUsersThisMonth: number;
  userGrowthPercentage: number;
  totalAdmins: number;
  totalCustomers: number;
}

export interface OrderStatsDTO {
  totalOrders: number;
  pendingOrders: number;
  processingOrders: number;
  shippedOrders: number;
  deliveredOrders: number;
  cancelledOrders: number;
  returnedOrders: number;
  orderCompletionRate: number;
  averageDeliveryTime: number;
}

export interface RecentOrderItemDTO {
  orderId: number;
  customerName: string;
  totalAmount: number;
  status: string;
  orderDate: string;
  paymentMethod: string;
}

export interface RecentOrdersDTO {
  recentOrders: RecentOrderItemDTO[];
  totalCount: number;
}

export interface TopProductItemDTO {
  productId: number;
  productName: string;
  categoryName: string;
  totalSold: number;
  totalRevenue: number;
  productImage: string;
  averageRating: number;
}

export interface TopProductsDTO {
  topProducts: TopProductItemDTO[];
  totalCount: number;
}
