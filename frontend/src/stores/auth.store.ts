'use client';

import { create } from 'zustand';
import type { UserMeResponse } from '@/types';
import { ApiError } from '@/shared/api/client';
import { authApi } from '@/entities/auth/api/auth.api';
import { userApi } from '@/entities/user/api/user.api';
import { errorMessage } from '@/shared/utils/errorMessage';

interface AuthState {
  user: UserMeResponse | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  verificationEmail: string | null;
  unverifiedEmail: string | null;

  login: (email: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  resendVerification: (email: string) => Promise<void>;
  logout: () => Promise<void>;
  fetchUser: () => Promise<void>;
  clearError: () => void;
  setVerificationEmail: (email: string | null) => void;
  clearUnverifiedEmail: () => void;
}

let fetchUserPromise: Promise<void> | null = null;

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
  verificationEmail: null,
  unverifiedEmail: null,

  login: async (email, password) => {
    set({ isLoading: true, error: null, unverifiedEmail: null });
    try {
      await authApi.login(email, password);
      await get().fetchUser();
    } catch (e: unknown) {
      if (e instanceof ApiError && (e.status === 403 && (e.code === 'EMAIL_NOT_VERIFIED' || e.message?.includes('EMAIL_NOT_VERIFIED') || e.message?.toLowerCase().includes('not verified')))) {
        set({
          unverifiedEmail: email.trim().toLowerCase(),
          error: 'EMAIL_NOT_VERIFIED',
          isLoading: false,
        });
      } else {
        set({ error: errorMessage(e, 'Login failed'), isLoading: false });
      }
    }
  },

  register: async (username, email, password) => {
    set({ isLoading: true, error: null, verificationEmail: null });
    try {
      await authApi.register(username, email, password);
      set({
        verificationEmail: email.trim().toLowerCase(),
        isLoading: false,
        error: null,
      });
    } catch (e: unknown) {
      set({ error: errorMessage(e, 'Registration failed'), isLoading: false });
    }
  },

  resendVerification: async (email: string) => {
    await authApi.resendVerification(email.trim().toLowerCase());
  },

  setVerificationEmail: (email: string | null) => set({ verificationEmail: email }),
  clearUnverifiedEmail: () => set({ unverifiedEmail: null }),

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
    const isPublic = /^\/(en|ru|uz)?(\/auth|\/|\/admin.*)?$/.test(path);
    if (!isPublic) {
      const locale = path.split('/')[1] || 'ru';
      window.location.replace(`/${locale}/auth`);
    }
  });
}
