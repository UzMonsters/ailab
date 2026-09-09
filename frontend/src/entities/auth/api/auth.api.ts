import { api, setAccessToken } from '@/shared/api/client';
import type {
  AuthRegisterRequest,
  AuthRegisterResponse,
  AuthLoginRequest,
  AuthTokenResponse,
} from '@/types';

export const authApi = {
  register: (username: string, email: string, password: string) =>
    api.post<AuthRegisterResponse>('/api/v1/auth/register', { username, email, password }),

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
};
