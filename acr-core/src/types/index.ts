/**
 * Central export for all ACR Platform types
 */

// Patient types
export * from './patient';

// Case types
export * from './case';

// Model and Federated Learning types
export * from './model';

// Agent types
export * from './agent';

// Blockchain types
export * from './blockchain';

// Common utility types
export interface Result<T, E = Error> {
  success: boolean;
  data?: T;
  error?: E;
}

export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  hasMore: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    details?: unknown;
  };
  timestamp: Date;
}
