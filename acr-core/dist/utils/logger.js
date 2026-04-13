"use strict";
/**
 * Logging utility with privacy-aware features
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.logger = exports.LogLevel = void 0;
var LogLevel;
(function (LogLevel) {
    LogLevel["DEBUG"] = "DEBUG";
    LogLevel["INFO"] = "INFO";
    LogLevel["WARN"] = "WARN";
    LogLevel["ERROR"] = "ERROR";
})(LogLevel || (exports.LogLevel = LogLevel = {}));
class Logger {
    minLevel;
    enabledModules;
    constructor() {
        this.minLevel = process.env.LOG_LEVEL || LogLevel.INFO;
        this.enabledModules = new Set(process.env.LOG_MODULES?.split(',') || ['*']);
    }
    shouldLog(level, module) {
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
    redactSensitiveData(data) {
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
        const redacted = {};
        for (const [key, value] of Object.entries(data)) {
            if (sensitiveKeys.some((sk) => key.toLowerCase().includes(sk))) {
                redacted[key] = '[REDACTED]';
            }
            else if (typeof value === 'object' && value !== null) {
                redacted[key] = this.redactSensitiveData(value);
            }
            else {
                redacted[key] = value;
            }
        }
        return redacted;
    }
    log(entry) {
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
    debug(module, message, data) {
        this.log({
            timestamp: new Date(),
            level: LogLevel.DEBUG,
            module,
            message,
            data,
        });
    }
    info(module, message, data) {
        this.log({
            timestamp: new Date(),
            level: LogLevel.INFO,
            module,
            message,
            data,
        });
    }
    warn(module, message, data) {
        this.log({
            timestamp: new Date(),
            level: LogLevel.WARN,
            module,
            message,
            data,
        });
    }
    error(module, message, error, data) {
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
exports.logger = new Logger();
//# sourceMappingURL=logger.js.map