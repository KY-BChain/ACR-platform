"use strict";
/**
 * Blockchain and Smart Contract types
 * RSK-based ERC-3643 identity and tokenomics
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.TokenType = exports.IdentityStatus = void 0;
var IdentityStatus;
(function (IdentityStatus) {
    IdentityStatus["PENDING"] = "PENDING";
    IdentityStatus["VERIFIED"] = "VERIFIED";
    IdentityStatus["SUSPENDED"] = "SUSPENDED";
    IdentityStatus["REVOKED"] = "REVOKED";
})(IdentityStatus || (exports.IdentityStatus = IdentityStatus = {}));
var TokenType;
(function (TokenType) {
    TokenType["ACR_RWA"] = "ACR_RWA";
    TokenType["ACR_GOV"] = "ACR_GOV";
})(TokenType || (exports.TokenType = TokenType = {}));
//# sourceMappingURL=blockchain.js.map