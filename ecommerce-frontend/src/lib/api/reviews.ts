import apiClient from './client';
import {
  ApiResponse,
  ReviewResponse,
  CreateReviewRequest,
  PageResponse,
} from '../types/api';

export const reviewsApi = {
  // Create review
  createReview: async (data: CreateReviewRequest): Promise<ApiResponse<ReviewResponse>> => {
    return apiClient.post('/api/reviews', data);
  },

  // Get review by ID
  getReview: async (reviewId: number): Promise<ApiResponse<ReviewResponse>> => {
    return apiClient.get(`/api/reviews/${reviewId}`);
  },

  // Update review
  updateReview: async (reviewId: number, data: CreateReviewRequest): Promise<ApiResponse<ReviewResponse>> => {
    return apiClient.put(`/api/reviews/${reviewId}`, data);
  },

  // Delete review
  deleteReview: async (reviewId: number): Promise<ApiResponse<string>> => {
    return apiClient.delete(`/api/reviews/${reviewId}`);
  },

  // Get product reviews
  getProductReviews: async (
    productId: number,
    params?: { page?: number; size?: number; status?: string }
  ): Promise<ApiResponse<PageResponse<ReviewResponse>>> => {
    return apiClient.get(`/api/reviews/product/${productId}`, { params });
  },

  // Get current user reviews
  getCurrentUserReviews: async (params?: {
    page?: number;
    size?: number;
  }): Promise<ApiResponse<any>> => {
    return apiClient.get('/api/reviews/user', { params });
  },

  // Mark review as helpful
  markReviewHelpful: async (reviewId: number): Promise<ApiResponse<string>> => {
    return apiClient.post(`/api/reviews/${reviewId}/helpful`);
  },

  // Update review status (Admin)
  updateReviewStatus: async (reviewId: number, status: string): Promise<ApiResponse<ReviewResponse>> => {
    return apiClient.put(`/api/reviews/${reviewId}/status`, null, {
      params: { status },
    });
  },

  // Get pending reviews (Admin)
  getPendingReviews: async (params?: {
    page?: number;
    size?: number;
  }): Promise<ApiResponse<any>> => {
    return apiClient.get('/api/reviews/admin/pending', { params });
  },
};
