"use strict";
/**
 * Case-related types for ACR Platform
 * A Case represents a single diagnostic run for a patient
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.ModalityType = exports.CaseStatus = void 0;
var CaseStatus;
(function (CaseStatus) {
    CaseStatus["PENDING"] = "PENDING";
    CaseStatus["IN_PROGRESS"] = "IN_PROGRESS";
    CaseStatus["AGENT_ANALYSIS"] = "AGENT_ANALYSIS";
    CaseStatus["CONSENSUS"] = "CONSENSUS";
    CaseStatus["COMPLETED"] = "COMPLETED";
    CaseStatus["REVIEWED"] = "REVIEWED";
    CaseStatus["ARCHIVED"] = "ARCHIVED";
})(CaseStatus || (exports.CaseStatus = CaseStatus = {}));
var ModalityType;
(function (ModalityType) {
    ModalityType["COLPOSCOPY"] = "COLPOSCOPY";
    ModalityType["CYTOLOGY"] = "CYTOLOGY";
    ModalityType["HISTOPATHOLOGY"] = "HISTOPATHOLOGY";
    ModalityType["HPV_TEST"] = "HPV_TEST";
    ModalityType["IMMUNOHISTOCHEMISTRY"] = "IMMUNOHISTOCHEMISTRY";
})(ModalityType || (exports.ModalityType = ModalityType = {}));
//# sourceMappingURL=case.js.map