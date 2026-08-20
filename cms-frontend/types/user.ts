export interface UserResponse {
  id?: number;
  loginId: string;
  fullName?: string;
  emailAddress?: string;
  appId?: string;
  active?: boolean;
  roleIds?: string[];
  roleNames?: string[];
}

export interface UserCreateRequest {
  loginId: string;
  password?: string;
  fullName?: string;
  emailAddress?: string;
  appId?: string;
  groupIds?: string[];
}

export interface UserUpdateRequest {
  fullName?: string;
  password?: string;
  active?: boolean;
  groupIds?: string[];
}
