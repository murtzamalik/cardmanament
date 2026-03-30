/**
 * Backend API response wrapper. All endpoints return this shape.
 */
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T | null;
}
