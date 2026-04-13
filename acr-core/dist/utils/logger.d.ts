/**
 * Logging utility with privacy-aware features
 */
export declare enum LogLevel {
    DEBUG = "DEBUG",
    INFO = "INFO",
    WARN = "WARN",
    ERROR = "ERROR"
}
export interface LogEntry {
    timestamp: Date;
    level: LogLevel;
    module: string;
    message: string;
    data?: Record<string, unknown>;
    error?: Error;
}
declare class Logger {
    private minLevel;
    private enabledModules;
    constructor();
    private shouldLog;
    private redactSensitiveData;
    private log;
    debug(module: string, message: string, data?: Record<string, unknown>): void;
    info(module: string, message: string, data?: Record<string, unknown>): void;
    warn(module: string, message: string, data?: Record<string, unknown>): void;
    error(module: string, message: string, error?: Error, data?: Record<string, unknown>): void;
}
export declare const logger: Logger;
export {};
//# sourceMappingURL=logger.d.ts.map