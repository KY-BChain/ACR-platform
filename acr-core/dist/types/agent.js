"use strict";
/**
 * Agent-related types for ACR Platform
 * Based on Fetch.ai agent framework
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.AgentStatus = exports.AgentType = void 0;
var AgentType;
(function (AgentType) {
    AgentType["RADIOLOGY"] = "RADIOLOGY";
    AgentType["PATHOLOGY"] = "PATHOLOGY";
    AgentType["GENOMICS"] = "GENOMICS";
    AgentType["CONSENSUS"] = "CONSENSUS";
    AgentType["COORDINATOR"] = "COORDINATOR";
})(AgentType || (exports.AgentType = AgentType = {}));
var AgentStatus;
(function (AgentStatus) {
    AgentStatus["IDLE"] = "IDLE";
    AgentStatus["ACTIVE"] = "ACTIVE";
    AgentStatus["PROCESSING"] = "PROCESSING";
    AgentStatus["WAITING"] = "WAITING";
    AgentStatus["ERROR"] = "ERROR";
    AgentStatus["OFFLINE"] = "OFFLINE";
})(AgentStatus || (exports.AgentStatus = AgentStatus = {}));
//# sourceMappingURL=agent.js.map