import apiClient from './client';
import {
  ApiResponse,
  ProductResponse,
  ProductCreateRequest,
  ProductUpdateRequest,
  ProductSearchRequest,
  PageResponse,
} from '../types/api';

export const productsApi = {
  // Get all products with pagination
  getAllProducts: async (params?: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDirection?: string;
  }): Promise<ApiResponse<PageResponse<ProductResponse>>> => {
    return apiClient.get('/api/products', { params });
  },

  // Get product by ID
  getProductById: async (id: number): Promise<ApiResponse<ProductResponse>> => {
    return apiClient.get(`/api/products/${id}`);
  },

  // Create product (Admin)
  createProduct: async (data: ProductCreateRequest): Promise<ApiResponse<ProductResponse>> => {
    return apiClient.post('/api/products', data);
  },

  // Update product (Admin)
  updateProduct: async (id: number, data: ProductUpdateRequest): Promise<ApiResponse<ProductResponse>> => {
    return apiClient.put(`/api/products/${id}`, data);
  },

  // Delete product (Admin)
  deleteProduct: async (id: number): Promise<ApiResponse<string>> => {
    return apiClient.delete(`/api/products/${id}`);
  },

  // Search products
  searchProducts: async (data: ProductSearchRequest): Promise<ApiResponse<PageResponse<ProductResponse>>> => {
    return apiClient.post('/api/products/search', data);
  },

  // Simple search
  simpleSearch: async (params: {
    q: string;
    page?: number;
    size?: number;
  }): Promise<ApiResponse<PageResponse<ProductResponse>>> => {
    return apiClient.get('/api/products/search/simple', { params });
  },

  // Get featured products
  getFeaturedProducts: async (): Promise<ApiResponse<ProductResponse[]>> => {
    return apiClient.get('/api/products/featured');
  },

  // Get products by category
  getProductsByCategory: async (
    categoryId: number,
    params?: { page?: number; size?: number }
  ): Promise<ApiResponse<PageResponse<ProductResponse>>> => {
    return apiClient.get(`/api/products/category/${categoryId}`, { params });
  },

  // Get related products
  getRelatedProducts: async (id: number): Promise<ApiResponse<ProductResponse[]>> => {
    return apiClient.get(`/api/products/${id}/related`);
  },

  // Get low stock products (Admin)
  getLowStockProducts: async (threshold?: number): Promise<ApiResponse<ProductResponse[]>> => {
    return apiClient.get('/api/products/admin/low-stock', {
      params: { threshold: threshold || 10 },
    });
  },

  // Update stock (Admin)
  updateStock: async (id: number, stockQuantity: number): Promise<ApiResponse<ProductResponse>> => {
    return apiClient.patch(`/api/products/${id}/stock`, null, {
      params: { stockQuantity },
    });
  },
};
