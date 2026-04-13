/**
 * Logging utility with privacy-aware features
 */

export enum LogLevel {
  DEBUG = 'DEBUG',
  INFO = 'INFO',
  WARN = 'WARN',
  ERROR = 'ERROR',
}

export interface LogEntry {
  timestamp: Date;
  level: LogLevel;
  module: string;
  message: string;
  data?: Record<string, unknown>;
  error?: Error;
}

class Logger {
  private minLevel: LogLevel;
  private enabledModules: Set<string>;

  constructor() {
    this.minLevel = (process.env.LOG_LEVEL as LogLevel) || LogLevel.INFO;
    this.enabledModules = new Set(
      process.env.LOG_MODULES?.split(',') || ['*']
    );
  }

  private shouldLog(level: LogLevel, module: string): boolean {
    const levelPriority = {
      [LogLevel.DEBUG]: 0,
      [LogLevel.INFO]: 1,
      [LogLevel.WARN]: 2,
      [LogLevel.ERROR]: 3,
    };

    if (levelPriority[level] < levelPriority[this.minLevel]) {
      return false;
    }

    if (this.enabledModules.has('*')) {
      return true;
    }

    return this.enabledModules.has(module);
  }

  private redactSensitiveData(data: Record<string, unknown>): Record<string, unknown> {
    const sensitiveKeys = [
      'password',
      'secret',
      'token',
      'key',
      'privateKey',
      'patientName',
      'email',
      'phone',
    ];

    const redacted: Record<string, unknown> = {};

    for (const [key, value] of Object.entries(data)) {
      if (sensitiveKeys.some((sk) => key.toLowerCase().includes(sk))) {
        redacted[key] = '[REDACTED]';
      } else if (typeof value === 'object' && value !== null) {
        redacted[key] = this.redactSensitiveData(value as Record<string, unknown>);
      } else {
        redacted[key] = value;
      }
    }

    return redacted;
  }

  private log(entry: LogEntry): void {
    if (!this.shouldLog(entry.level, entry.module)) {
      return;
    }

    const redactedData = entry.data
      ? this.redactSensitiveData(entry.data)
      : undefined;

    const logMessage = {
      timestamp: entry.timestamp.toISOString(),
      level: entry.level,
      module: entry.module,
      message: entry.message,
      ...(redactedData && { data: redactedData }),
      ...(entry.error && {
        error: {
          message: entry.error.message,
          stack: entry.error.stack,
        },
      }),
    };

    const output = JSON.stringify(logMessage);

    switch (entry.level) {
      case LogLevel.ERROR:
        console.error(output);
        break;
      case LogLevel.WARN:
        console.warn(output);
        break;
      default:
        console.log(output);
    }
  }

  debug(module: string, message: string, data?: Record<string, unknown>): void {
    this.log({
      timestamp: new Date(),
      level: LogLevel.DEBUG,
      module,
      message,
      data,
    });
  }

  info(module: string, message: string, data?: Record<string, unknown>): void {
    this.log({
      timestamp: new Date(),
      level: LogLevel.INFO,
      module,
      message,
      data,
    });
  }

  warn(module: string, message: string, data?: Record<string, unknown>): void {
    this.log({
      timestamp: new Date(),
      level: LogLevel.WARN,
      module,
      message,
      data,
    });
  }

  error(
    module: string,
    message: string,
    error?: Error,
    data?: Record<string, unknown>
  ): void {
    this.log({
      timestamp: new Date(),
      level: LogLevel.ERROR,
      module,
      message,
      error,
      data,
    });
  }
}

export const logger = new Logger();
