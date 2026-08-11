'use client';

import { create } from 'zustand';
import type { UserMeResponse } from '@/types';
import { authApi } from '@/services/api/auth.api';
import { userApi } from '@/services/api/user.api';
import { getAccessToken } from '@/services/api/client';
import { normalizeError } from '@/lib/errors';

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

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,

  login: async (email: string, password: string) => {
    set({ isLoading: true, error: null });
    try {
      await authApi.login(email, password);
      const user = await userApi.getMe();
      set({ user, isAuthenticated: true, isLoading: false });
    } catch (err: unknown) {
      set({ error: normalizeError(err, 'Login failed').message, isLoading: false, isAuthenticated: false });
      throw err;
    }
  },

  register: async (username: string, email: string, password: string) => {
    set({ isLoading: true, error: null });
    try {
      await authApi.register(username, email, password);
      await authApi.login(email, password);
      const user = await userApi.getMe();
      set({ user, isAuthenticated: true, isLoading: false });
    } catch (err: unknown) {
      set({ error: normalizeError(err, 'Registration failed').message, isLoading: false });
      throw err;
    }
  },

  logout: async () => {
    set({ isLoading: true });
    try {
      await authApi.logout();
    } catch {
      // Even if logout fails, clear local state
    }
    set({ user: null, isAuthenticated: false, isLoading: false, error: null });
  },

  fetchUser: async () => {
    set({ isLoading: true });
    try {
      if (!getAccessToken()) {
        await authApi.refresh();
      }
      const user = await userApi.getMe();
      set({ user, isAuthenticated: true, isLoading: false });
    } catch {
      set({ user: null, isAuthenticated: false, isLoading: false });
    }
  },

  clearError: () => set({ error: null }),
}));

if (typeof window !== 'undefined') {
  window.addEventListener('auth:unauthorized', () => {
    useAuthStore.setState({ user: null, isAuthenticated: false, isLoading: false, error: null });
  });
}
