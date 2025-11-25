import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { UserInfo, LoginRequest, RegisterRequest } from '../types/api';
import { authApi } from '../api/auth';
import { toast } from 'react-hot-toast';

interface AuthState {
  user: UserInfo | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  
  // Actions
  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  setUser: (user: UserInfo | null) => void;
  clearAuth: () => void;
  checkAuth: () => Promise<void>;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      isAuthenticated: false,
      isLoading: false,

      login: async (credentials: LoginRequest) => {
        try {
          set({ isLoading: true });
          const response = await authApi.login(credentials);
          
          if (response.success) {
            const { user } = response.data;
            set({
              user,
              isAuthenticated: true,
              isLoading: false,
            });
            
            toast.success('Login successful!');
          }
        } catch (error: any) {
          set({ isLoading: false });
          toast.error(error.response?.data?.message || 'Login failed');
          throw error;
        }
      },

      register: async (data: RegisterRequest) => {
        try {
          set({ isLoading: true });
          const response = await authApi.register(data);
          
          if (response.success) {
            const { user } = response.data;
            set({
              user,
              isAuthenticated: true,
              isLoading: false,
            });
            
            toast.success('Registration successful!');
          }
        } catch (error: any) {
          set({ isLoading: false });
          toast.error(error.response?.data?.message || 'Registration failed');
          throw error;
        }
      },

      logout: async () => {
        try {
          await authApi.logout();
        } catch (error) {
          console.error('Logout error:', error);
        } finally {
          set({
            user: null,
            isAuthenticated: false,
          });
          localStorage.clear();
          document.location.reload();
          toast.success('Logged out successfully');
        }
      },

      setUser: (user: UserInfo | null) => {
        set({ user});
      },


      clearAuth: () => {
        localStorage.removeItem('user');
        
        set({
          user: null,
          isAuthenticated: false,
        });
      },

      checkAuth: async () => {
        
        try {
          set({ isLoading: true });
          const response = await authApi.getCurrentUser();
          
          if (response.success) {
            set({
              user: response.data,
              isAuthenticated: true,
            });
          }
        } catch (error) {
          console.error('Auth check failed:', error);
          set({ isLoading: false });
        }
      },
    }),
    {
      name: 'userInfo-storage',
      partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);
