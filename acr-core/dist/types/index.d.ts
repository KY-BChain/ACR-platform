/**
 * Central export for all ACR Platform types
 */
export * from './patient';
export * from './case';
export * from './model';
export * from './agent';
export * from './blockchain';
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
//# sourceMappingURL=index.d.ts.map