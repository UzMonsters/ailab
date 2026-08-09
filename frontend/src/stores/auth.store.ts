'use client';

import { create } from 'zustand';
import type { UserMeResponse } from '@/types';
import { authApi } from '@/services/api/auth.api';
import { userApi } from '@/services/api/user.api';
import { getAccessToken } from '@/services/api/client';

const MOCK_MODE = false;

const MOCK_USER: UserMeResponse = {
  id: '00000000-0000-0000-0000-000000000001',
  username: 'researcher',
  email: 'researcher@ailab.dev',
  role: 'ROLE_USER',
  avatarUrl: null,
  createdAt: new Date().toISOString(),
};

interface AuthState {
  user: UserMeResponse | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;

  login: (usernameOrEmail: string, password: string) => Promise<void>;
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

  login: async (usernameOrEmail: string, password: string) => {
    set({ isLoading: true, error: null });
    try {
      if (MOCK_MODE) {
        window.setTimeout(() => {
          set({ user: MOCK_USER, isAuthenticated: true, isLoading: false });
        }, 400);
        return;
      }
      await authApi.login(usernameOrEmail, password);
      const user = await userApi.getMe();
      set({ user, isAuthenticated: true, isLoading: false });
    } catch (err: any) {
      set({ error: err.message || 'Login failed', isLoading: false, isAuthenticated: false });
      throw err;
    }
  },

  register: async (username: string, email: string, password: string) => {
    set({ isLoading: true, error: null });
    try {
      if (MOCK_MODE) {
        window.setTimeout(() => {
          set({ user: { ...MOCK_USER, username, email }, isAuthenticated: true, isLoading: false });
        }, 400);
        return;
      }
      await authApi.register(username, email, password);
      await authApi.login(username, password);
      const user = await userApi.getMe();
      set({ user, isAuthenticated: true, isLoading: false });
    } catch (err: any) {
      set({ error: err.message || 'Registration failed', isLoading: false });
      throw err;
    }
  },

  logout: async () => {
    set({ isLoading: true });
    try {
      if (!MOCK_MODE) {
        await authApi.logout();
      }
    } catch {
      // Even if logout fails, clear local state
    }
    set({ user: null, isAuthenticated: false, isLoading: false, error: null });
  },

  fetchUser: async () => {
    const token = getAccessToken();
    if (MOCK_MODE || !token) {
      set({ user: MOCK_MODE ? MOCK_USER : null, isAuthenticated: MOCK_MODE, isLoading: false });
      return;
    }
    set({ isLoading: true });
    try {
      const user = await userApi.getMe();
      set({ user, isAuthenticated: true, isLoading: false });
    } catch {
      set({ user: null, isAuthenticated: false, isLoading: false });
    }
  },

  clearError: () => set({ error: null }),
}));
