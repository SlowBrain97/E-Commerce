import apiClient from './client';
import {
  ApiResponse,
  DashboardOverviewDTO,
  SalesStatsDTO,
  ProductStatsDTO,
  UserStatsDTO,
  OrderStatsDTO,
  RecentOrdersDTO,
  TopProductsDTO,
} from '../types/api';

export const dashboardApi = {
  // Get dashboard overview
  getDashboardOverview: async (): Promise<ApiResponse<DashboardOverviewDTO>> => {
    return apiClient.get('/api/dashboard/overview');
  },

  // Get sales stats
  getSalesStats: async (): Promise<ApiResponse<SalesStatsDTO>> => {
    return apiClient.get('/api/dashboard/sales');
  },

  // Get product stats
  getProductStats: async (): Promise<ApiResponse<ProductStatsDTO>> => {
    return apiClient.get('/api/dashboard/products');
  },

  // Get user stats
  getUserStats: async (): Promise<ApiResponse<UserStatsDTO>> => {
    return apiClient.get('/api/dashboard/users');
  },

  // Get order stats
  getOrderStats: async (): Promise<ApiResponse<OrderStatsDTO>> => {
    return apiClient.get('/api/dashboard/orders');
  },

  // Get recent orders
  getRecentOrders: async (): Promise<ApiResponse<RecentOrdersDTO>> => {
    return apiClient.get('/api/dashboard/recent-orders');
  },

  // Get top products
  getTopProducts: async (): Promise<ApiResponse<TopProductsDTO>> => {
    return apiClient.get('/api/dashboard/top-products');
  },
};
