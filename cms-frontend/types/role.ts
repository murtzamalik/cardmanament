export interface RoleResponse {
  id?: number;
  groupId: string;
  groupName?: string;
  active?: boolean;
}

export interface RoleCreateRequest {
  groupId: string;
  groupName?: string;
  active?: boolean;
}

export interface RoleUpdateRequest {
  groupName?: string;
  active?: boolean;
}
