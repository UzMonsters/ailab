import { api, setAccessToken } from '@/shared/api/client';
import type {
  AuthRegisterRequest,
  AuthRegisterResponse,
  AuthLoginRequest,
  AuthTokenResponse,
  VerifyEmailRequest,
  VerifyEmailResponse,
  ResendVerificationRequest,
  LinkedProvidersResponse,
} from '@/types';

export const authApi = {
  register: (username: string, email: string, password: string) =>
    api.post<AuthRegisterResponse>('/api/v1/auth/register', { username, email, password } satisfies AuthRegisterRequest),

  verifyEmail: (token: string) =>
    api.post<VerifyEmailResponse>('/api/v1/auth/email/verify', { token } satisfies VerifyEmailRequest),

  resendVerification: (email: string) =>
    api.post<void>('/api/v1/auth/email/verification/resend', { email } satisfies ResendVerificationRequest),

  login: (email: string, password: string) =>
    api.post<AuthTokenResponse>('/api/v1/auth/login', { email, password } satisfies AuthLoginRequest).then((res) => {
      if (res.accessToken) {
        setAccessToken(res.accessToken);
      }
      return res;
    }),

  refresh: () =>
    api.post<AuthTokenResponse>('/api/v1/auth/refresh', {}).then((res) => {
      if (res.accessToken) {
        setAccessToken(res.accessToken);
      }
      return res;
    }),

  logout: async () => {
    try {
      return await api.post<{ success: boolean }>('/api/v1/auth/logout');
    } finally {
      setAccessToken(null);
    }
  },

  getGoogleLinkUrl: () =>
    api.get<{ url: string }>('/api/v1/auth/oauth/google/link'),

  getLinkedProviders: () =>
    api.get<LinkedProvidersResponse>('/api/v1/users/me/providers'),

  unlinkProvider: (provider: string) =>
    api.delete<void>(`/api/v1/users/me/providers/${encodeURIComponent(provider)}`),
};
