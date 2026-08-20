export interface PermissionResponse {
  permissionId: string;
  perParentId?: string;
  permissionName?: string;
  permissionType?: string;
}

export interface PermissionCreateRequest {
  permissionId: string;
  perParentId?: string;
  permissionName?: string;
  permissionType?: string;
}
