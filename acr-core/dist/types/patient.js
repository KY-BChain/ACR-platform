"use strict";
/**
 * Patient-related types for ACR Platform
 * NOTE: All patient data is anonymized and processed locally
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.CervicalGrade = exports.HPVStrain = exports.RiskLevel = void 0;
var RiskLevel;
(function (RiskLevel) {
    RiskLevel["LOW"] = "LOW";
    RiskLevel["MODERATE"] = "MODERATE";
    RiskLevel["HIGH"] = "HIGH";
    RiskLevel["VERY_HIGH"] = "VERY_HIGH";
})(RiskLevel || (exports.RiskLevel = RiskLevel = {}));
var HPVStrain;
(function (HPVStrain) {
    HPVStrain["HPV_16"] = "HPV_16";
    HPVStrain["HPV_18"] = "HPV_18";
    HPVStrain["HPV_31"] = "HPV_31";
    HPVStrain["HPV_33"] = "HPV_33";
    HPVStrain["HPV_45"] = "HPV_45";
    HPVStrain["HPV_52"] = "HPV_52";
    HPVStrain["HPV_58"] = "HPV_58";
    HPVStrain["OTHER_HIGH_RISK"] = "OTHER_HIGH_RISK";
    HPVStrain["LOW_RISK"] = "LOW_RISK";
    HPVStrain["NEGATIVE"] = "NEGATIVE";
})(HPVStrain || (exports.HPVStrain = HPVStrain = {}));
var CervicalGrade;
(function (CervicalGrade) {
    CervicalGrade["NORMAL"] = "NORMAL";
    CervicalGrade["ASCUS"] = "ASCUS";
    CervicalGrade["LSIL"] = "LSIL";
    CervicalGrade["HSIL"] = "HSIL";
    CervicalGrade["ASC_H"] = "ASC_H";
    CervicalGrade["AGC"] = "AGC";
    CervicalGrade["CANCER"] = "CANCER";
})(CervicalGrade || (exports.CervicalGrade = CervicalGrade = {}));
//# sourceMappingURL=patient.js.map