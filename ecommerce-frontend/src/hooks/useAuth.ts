import { useEffect } from 'react';
import { useAuthStore } from '@/lib/store/authStore';

export function useAuth() {
  const { user, isAuthenticated, checkAuth, logout } = useAuthStore();

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  return {
    user,
    isAuthenticated,
    isAdmin: user?.role === 'ADMIN',
    logout,
  };
}
