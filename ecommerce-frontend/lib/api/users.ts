import apiClient from './client';
import {
  ApiResponse,
  UserResponse,
  UpdateProfileRequest,
} from '../types/api';

export const usersApi = {
  // Get current user profile
  getCurrentUserProfile: async (): Promise<ApiResponse<UserResponse>> => {
    return apiClient.get('/api/users/profile');
  },

  // Update profile
  updateProfile: async (data: UpdateProfileRequest): Promise<ApiResponse<UserResponse>> => {
    return apiClient.put('/api/users/profile', data);
  },

  // Get user profile by ID
  getUserProfile: async (userId: number): Promise<ApiResponse<UserResponse>> => {
    return apiClient.get(`/api/users/profile/${userId}`);
  },

  // Verify email
  verifyEmail: async (token: string): Promise<ApiResponse<string>> => {
    return apiClient.post('/api/users/verify-email', null, {
      params: { token },
    });
  },

  // Deactivate account
  deactivateAccount: async (): Promise<ApiResponse<string>> => {
    return apiClient.post('/api/users/deactivate');
  },

  // Get all users (Admin)
  getAllUsers: async (params?: {
    page?: number;
    size?: number;
  }): Promise<ApiResponse<any>> => {
    return apiClient.get('/api/users/admin/all', { params });
  },

  // Search users (Admin)
  searchUsers: async (
    query: string,
    params?: { page?: number; size?: number }
  ): Promise<ApiResponse<any>> => {
    return apiClient.get('/api/users/admin/search', {
      params: { query, ...params },
    });
  },

  // Get user stats (Admin)
  getUserStats: async (): Promise<ApiResponse<Record<string, any>>> => {
    return apiClient.get('/api/users/admin/stats');
  },
};
