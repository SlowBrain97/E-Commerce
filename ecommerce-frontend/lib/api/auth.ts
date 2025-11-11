import apiClient from './client';
import {
  ApiResponse,
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  ChangePasswordRequest,
  RefreshTokenRequest,
  UserInfo,
} from '../types/api';

export const authApi = {
  // Login
  login: async (data: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
    return apiClient.post('/auth/login', data);
  },

  // Register
  register: async (data: RegisterRequest): Promise<ApiResponse<AuthResponse>> => {
    return apiClient.post('/auth/register', data);
  },

  // Logout
  logout: async (): Promise<ApiResponse<Record<string, string>>> => {
    return apiClient.post('/auth/logout');
  },

  // Get current user
  getCurrentUser: async (): Promise<ApiResponse<UserInfo>> => {
    return apiClient.get('/auth/me');
  },

  // Refresh token
  refreshToken: async (data: RefreshTokenRequest): Promise<ApiResponse<Record<string, any>>> => {
    return apiClient.post('/auth/refresh', data);
  },

  // Change password
  changePassword: async (data: ChangePasswordRequest): Promise<ApiResponse<Record<string, string>>> => {
    return apiClient.post('/auth/change-password', data);
  },

  // OAuth2 callback
  oauth2Callback: async (data: Record<string, string>): Promise<ApiResponse<AuthResponse>> => {
    return apiClient.post('/auth/oauth2/callback', data);
  },
};
