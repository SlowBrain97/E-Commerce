import apiClient from './client';
import {
  ApiResponse,
  CategoryResponse,
  CategoryCreateRequest,
  CategoryUpdateRequest,
} from '../types/api';

export const categoriesApi = {
  // Get all categories in hierarchy
  getAllCategoriesInHierarchy: async (): Promise<ApiResponse<CategoryResponse[]>> => {
    return apiClient.get('/api/categories/hierarchy');
  },

  // Get category by ID
  getCategoryById: async (id: number): Promise<ApiResponse<CategoryResponse>> => {
    return apiClient.get(`/api/categories/${id}`);
  },

  // Create category (Admin)
  createCategory: async (data: CategoryCreateRequest): Promise<ApiResponse<CategoryResponse>> => {
    return apiClient.post('/api/categories', data);
  },

  // Update category (Admin)
  updateCategory: async (id: number, data: CategoryUpdateRequest): Promise<ApiResponse<CategoryResponse>> => {
    return apiClient.put(`/api/categories/${id}`, data);
  },

  // Delete category (Admin)
  deleteCategory: async (id: number): Promise<ApiResponse<string>> => {
    return apiClient.delete(`/api/categories/${id}`);
  },

  // Get main categories
  getMainCategories: async (): Promise<ApiResponse<CategoryResponse[]>> => {
    return apiClient.get('/api/categories/main-categories');
  },

  // Get subcategories
  getSubcategories: async (parentId: number): Promise<ApiResponse<CategoryResponse[]>> => {
    return apiClient.get(`/api/categories/${parentId}/subcategories`);
  },

  // Get featured categories
  getFeaturedCategories: async (): Promise<ApiResponse<CategoryResponse[]>> => {
    return apiClient.get('/api/categories/featured');
  },

  // Get categories with products
  getCategoriesWithProducts: async (): Promise<ApiResponse<CategoryResponse[]>> => {
    return apiClient.get('/api/categories/with-products');
  },

  // Search categories
  searchCategories: async (q: string): Promise<ApiResponse<CategoryResponse[]>> => {
    return apiClient.get('/api/categories/search', { params: { q } });
  },

  // Set featured status (Admin)
  setFeatured: async (id: number, featured: boolean): Promise<ApiResponse<CategoryResponse>> => {
    return apiClient.put(`/api/categories/${id}/featured`, null, {
      params: { featured },
    });
  },

  // Set active status (Admin)
  setActive: async (id: number, active: boolean): Promise<ApiResponse<CategoryResponse>> => {
    return apiClient.put(`/api/categories/${id}/active`, null, {
      params: { active },
    });
  },

  // Get category stats (Admin)
  getCategoryStats: async (): Promise<ApiResponse<Record<string, any>>> => {
    return apiClient.get('/api/categories/stats');
  },
};
