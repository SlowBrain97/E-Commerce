import apiClient from './client';
import {
  ApiResponse,
  CartResponse,
  CartItemResponse,
  AddToCartRequest,
  UpdateCartItemRequest,
} from '../types/api';

export const cartApi = {
  // Get cart
  getCart: async (): Promise<ApiResponse<CartResponse>> => {
    return apiClient.get('/api/cart');
  },

  // Add to cart
  addToCart: async (data: AddToCartRequest): Promise<ApiResponse<CartItemResponse>> => {
    return apiClient.post('/api/cart/add', data);
  },

  // Update cart item
  updateCartItem: async (itemId: number, data: UpdateCartItemRequest): Promise<ApiResponse<CartItemResponse>> => {
    return apiClient.put(`/api/cart/item/${itemId}`, data);
  },

  // Remove from cart
  removeFromCart: async (itemId: number): Promise<ApiResponse<string>> => {
    return apiClient.delete(`/api/cart/item/${itemId}`);
  },

  // Clear cart
  clearCart: async (): Promise<ApiResponse<string>> => {
    return apiClient.delete('/api/cart');
  },

  // Get cart total
  getCartTotal: async (): Promise<ApiResponse<number>> => {
    return apiClient.get('/api/cart/total');
  },

  // Get cart count
  getCartCount: async (): Promise<ApiResponse<number>> => {
    return apiClient.get('/api/cart/count');
  },

  // Sync cart
  syncCart: async (items: AddToCartRequest[]): Promise<ApiResponse<CartResponse>> => {
    return apiClient.post('/api/cart/sync', items);
  },

  // Validate cart
  validateCart: async (): Promise<ApiResponse<string>> => {
    return apiClient.post('/api/cart/validate');
  },
};
