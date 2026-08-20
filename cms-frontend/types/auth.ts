import type { MenuResponse } from '@/types/menu';
export interface LoginRequest {
  loginId: string;
  password: string;
}

/** Matches backend LoginResponse: token, loginId, fullName, expiresIn, roles */
export interface LoginResponse {
  token: string;
  loginId: string;
  fullName: string;
  expiresIn: number;
  roles: string[];
  menus?: MenuResponse[];
}

export interface IAuthService {
  login(loginId: string, password: string): Promise<LoginResponse>;
  refresh(): Promise<LoginResponse | null>;
  changePassword(currentPassword: string, newPassword: string): Promise<void>;
  logout(): void;
}
