export interface MenuResponse {
  id: number;
  menuName: string;
  menuPath: string;
  parentMenuId?: number | null;
  menuIcon?: string;
  sortOrder?: number;
  status?: string;
  children?: MenuResponse[];
}

export interface MenuCreateRequest {
  menuName: string;
  menuPath: string;
  parentMenuId?: number | null;
  menuIcon?: string;
  sortOrder: number;
  status?: string;
}

export interface MenuUpdateRequest {
  menuName?: string;
  menuPath?: string;
  parentMenuId?: number | null;
  menuIcon?: string;
  sortOrder?: number;
  status?: string;
}
