// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/access/AccessControl.sol";

/**
 * @title ERC3643Identity
 * @dev Hospital identity management following ERC-3643 standard
 * Implements KYC/verification for hospital nodes
 */
contract ERC3643Identity is AccessControl {
    bytes32 public constant VERIFIER_ROLE = keccak256("VERIFIER_ROLE");

    enum IdentityStatus {
        Pending,
        Verified,
        Suspended,
        Revoked
    }

    struct Identity {
        address wallet;
        string did;          // Decentralized Identifier
        string hospitalName;
        string country;
        IdentityStatus status;
        address verifiedBy;
        uint256 verificationDate;
        uint256 createdAt;
        bool exists;
    }

    struct Claim {
        string claimType;    // e.g., "medical_license", "data_protection_cert"
        bytes32 claimData;   // Encrypted claim data
        address issuer;
        uint256 issuedAt;
        uint256 expiresAt;
        bool isValid;
    }

    // Address => Identity
    mapping(address => Identity) public identities;

    // Address => ClaimType => Claim
    mapping(address => mapping(string => Claim)) public claims;

    // Events
    event IdentityCreated(address indexed wallet, string did, string hospitalName);
    event IdentityVerified(address indexed wallet, address indexed verifier);
    event IdentityStatusChanged(address indexed wallet, IdentityStatus newStatus);
    event ClaimAdded(address indexed wallet, string claimType, address indexed issuer);

    constructor() {
        _grantRole(DEFAULT_ADMIN_ROLE, msg.sender);
        _grantRole(VERIFIER_ROLE, msg.sender);
    }

    /**
     * @dev Create a new hospital identity
     */
    function createIdentity(
        string memory did,
        string memory hospitalName,
        string memory country
    ) external {
        require(!identities[msg.sender].exists, "Identity already exists");
        require(bytes(did).length > 0, "DID cannot be empty");

        Identity memory newIdentity = Identity({
            wallet: msg.sender,
            did: did,
            hospitalName: hospitalName,
            country: country,
            status: IdentityStatus.Pending,
            verifiedBy: address(0),
            verificationDate: 0,
            createdAt: block.timestamp,
            exists: true
        });

        identities[msg.sender] = newIdentity;

        emit IdentityCreated(msg.sender, did, hospitalName);
    }

    /**
     * @dev Verify a hospital identity
     * Only callable by addresses with VERIFIER_ROLE
     */
    function verifyIdentity(address wallet) external onlyRole(VERIFIER_ROLE) {
        require(identities[wallet].exists, "Identity does not exist");
        require(identities[wallet].status == IdentityStatus.Pending, "Identity not pending");

        identities[wallet].status = IdentityStatus.Verified;
        identities[wallet].verifiedBy = msg.sender;
        identities[wallet].verificationDate = block.timestamp;

        emit IdentityVerified(wallet, msg.sender);
    }

    /**
     * @dev Add a claim to an identity
     */
    function addClaim(
        address wallet,
        string memory claimType,
        bytes32 claimData,
        uint256 expiresAt
    ) external onlyRole(VERIFIER_ROLE) {
        require(identities[wallet].exists, "Identity does not exist");
        require(identities[wallet].status == IdentityStatus.Verified, "Identity not verified");

        Claim memory newClaim = Claim({
            claimType: claimType,
            claimData: claimData,
            issuer: msg.sender,
            issuedAt: block.timestamp,
            expiresAt: expiresAt,
            isValid: true
        });

        claims[wallet][claimType] = newClaim;

        emit ClaimAdded(wallet, claimType, msg.sender);
    }

    /**
     * @dev Check if an identity is verified
     */
    function isVerified(address wallet) external view returns (bool) {
        return identities[wallet].exists && identities[wallet].status == IdentityStatus.Verified;
    }

    /**
     * @dev Get identity information
     */
    function getIdentity(address wallet) external view returns (Identity memory) {
        require(identities[wallet].exists, "Identity does not exist");
        return identities[wallet];
    }

    /**
     * @dev Suspend an identity
     */
    function suspendIdentity(address wallet) external onlyRole(DEFAULT_ADMIN_ROLE) {
        require(identities[wallet].exists, "Identity does not exist");
        identities[wallet].status = IdentityStatus.Suspended;
        emit IdentityStatusChanged(wallet, IdentityStatus.Suspended);
    }

    /**
     * @dev Revoke an identity
     */
    function revokeIdentity(address wallet) external onlyRole(DEFAULT_ADMIN_ROLE) {
        require(identities[wallet].exists, "Identity does not exist");
        identities[wallet].status = IdentityStatus.Revoked;
        emit IdentityStatusChanged(wallet, IdentityStatus.Revoked);
    }
}
