'use client';

import { create } from 'zustand';
import type { UserMeResponse } from '@/types';

interface AuthState {
  user: UserMeResponse | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;

  login: (email: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  fetchUser: () => Promise<void>;
  clearError: () => void;
}

import { authApi } from '@/entities/auth/api/auth.api';
import { userApi } from '@/entities/user/api/user.api';
import { errorMessage } from '@/shared/utils/errorMessage';
let fetchUserPromise: Promise<void> | null = null;

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,

  login: async (email, password) => {
    set({ isLoading: true, error: null });
    try {
      await authApi.login(email, password);
      await get().fetchUser();
    } catch (e: unknown) {
      set({ error: errorMessage(e, 'Login failed'), isLoading: false });
    }
  },

  register: async (username, email, password) => {
    set({ isLoading: true, error: null });
    try {
      await authApi.register(username, email, password);
      await get().login(email, password);
    } catch (e: unknown) {
      set({ error: errorMessage(e, 'Registration failed'), isLoading: false });
    }
  },

  logout: async () => {
    try {
      await authApi.logout();
    } finally {
      set({ user: null, isAuthenticated: false, isLoading: false, error: null });
    }
  },

  fetchUser: () => {
    if (fetchUserPromise) return fetchUserPromise;
    
    const promise = (async () => {
      set({ isLoading: true, error: null });
      try {
        const user = await userApi.getMe();
        set({ user, isAuthenticated: true, isLoading: false });
      } catch (e: unknown) {
        set({ user: null, isAuthenticated: false, isLoading: false, error: errorMessage(e, 'Failed to fetch user') });
      }
    })();
    
    fetchUserPromise = promise;
    void promise.finally(() => {
      if (fetchUserPromise === promise) fetchUserPromise = null;
    });
    return promise;
  },

  clearError: () => set({ error: null }),
}));

if (typeof window !== 'undefined') {
  window.addEventListener('auth:unauthorized', () => {
    useAuthStore.setState({ user: null, isAuthenticated: false, isLoading: false, error: null });
    const path = window.location.pathname;
    const isPublic = /^\/(en|ru|uz)?(\/auth|\/)?$/.test(path);
    if (!isPublic) {
      const locale = path.split('/')[1] || 'ru';
      window.location.replace(`/${locale}/auth`);
    }
  });
}
