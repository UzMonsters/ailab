import { api } from './client';

export interface AuthResponse {
  token: string;
  user: { id: string; name: string; email: string; role: string };
}

export const authApi = {
  login: (email: string, password: string) =>
    api.post<AuthResponse>('/auth/login', { email, password }),
  register: (data: { name: string; email: string; password: string; discipline: string }) =>
    api.post<AuthResponse>('/auth/register', data),
  logout: () => api.post('/auth/logout', {}),
};
