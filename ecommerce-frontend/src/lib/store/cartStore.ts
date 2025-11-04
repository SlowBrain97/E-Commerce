import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { CartResponse, CartItemResponse, AddToCartRequest } from '../types/api';
import { cartApi } from '../api/cart';
import { toast } from 'react-hot-toast';

interface CartState {
  cart: CartResponse | null;
  isLoading: boolean;
  
  // Actions
  fetchCart: () => Promise<void>;
  addToCart: (data: AddToCartRequest) => Promise<void>;
  updateCartItem: (itemId: number, quantity: number) => Promise<void>;
  removeFromCart: (itemId: number) => Promise<void>;
  clearCart: () => Promise<void>;
  syncCart: (items: AddToCartRequest[]) => Promise<void>;
}

export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      cart: null,
      isLoading: false,

      fetchCart: async () => {
        try {
          set({ isLoading: true });
          const response = await cartApi.getCart();
          
          if (response.success) {
            set({ cart: response.data, isLoading: false });
          }
        } catch (error: any) {
          set({ isLoading: false });
          console.error('Failed to fetch cart:', error);
        }
      },

      addToCart: async (data: AddToCartRequest) => {
        try {
          set({ isLoading: true });
          const response = await cartApi.addToCart(data);
          
          if (response.success) {
            // Refresh cart
            await get().fetchCart();
            toast.success('Added to cart!');
          }
        } catch (error: any) {
          set({ isLoading: false });
          toast.error(error.response?.data?.message || 'Failed to add to cart');
          throw error;
        }
      },

      updateCartItem: async (itemId: number, quantity: number) => {
        try {
          set({ isLoading: true });
          const response = await cartApi.updateCartItem(itemId, { quantity });
          
          if (response.success) {
            // Refresh cart
            await get().fetchCart();
            toast.success('Cart updated!');
          }
        } catch (error: any) {
          set({ isLoading: false });
          toast.error(error.response?.data?.message || 'Failed to update cart');
          throw error;
        }
      },

      removeFromCart: async (itemId: number) => {
        try {
          set({ isLoading: true });
          const response = await cartApi.removeFromCart(itemId);
          
          if (response.success) {
            // Refresh cart
            await get().fetchCart();
            toast.success('Item removed from cart');
          }
        } catch (error: any) {
          set({ isLoading: false });
          toast.error(error.response?.data?.message || 'Failed to remove item');
          throw error;
        }
      },

      clearCart: async () => {
        try {
          set({ isLoading: true });
          const response = await cartApi.clearCart();
          
          if (response.success) {
            set({ cart: null, isLoading: false });
            toast.success('Cart cleared');
          }
        } catch (error: any) {
          set({ isLoading: false });
          toast.error(error.response?.data?.message || 'Failed to clear cart');
          throw error;
        }
      },

      syncCart: async (items: AddToCartRequest[]) => {
        try {
          set({ isLoading: true });
          const response = await cartApi.syncCart(items);
          
          if (response.success) {
            set({ cart: response.data, isLoading: false });
          }
        } catch (error: any) {
          set({ isLoading: false });
          console.error('Failed to sync cart:', error);
        }
      },
    }),
    {
      name: 'cart-storage',
      partialize: (state) => ({
        cart: state.cart,
      }),
    }
  )
);
