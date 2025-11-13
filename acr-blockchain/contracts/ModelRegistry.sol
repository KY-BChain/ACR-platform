// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/access/AccessControl.sol";
import "@openzeppelin/contracts/security/ReentrancyGuard.sol";

/**
 * @title ModelRegistry
 * @dev Immutable registry for federated learning model versions
 * Records model CIDs, hashes, and metadata on RSK blockchain
 */
contract ModelRegistry is AccessControl, ReentrancyGuard {
    bytes32 public constant AGGREGATOR_ROLE = keccak256("AGGREGATOR_ROLE");
    bytes32 public constant VALIDATOR_ROLE = keccak256("VALIDATOR_ROLE");

    struct ModelVersion {
        string cid;           // IPFS CID
        bytes32 modelHash;    // SHA-256 hash of model weights
        string architecture;  // e.g., "VIT_MONAI"
        uint256 round;        // Training round number
        uint256 numParticipants; // Number of hospitals contributed
        uint256 timestamp;
        address publisher;    // Address of aggregator
        bool isActive;
    }

    // Model ID => Version => ModelVersion
    mapping(string => mapping(string => ModelVersion)) public models;

    // Model ID => Latest version
    mapping(string => string) public latestVersion;

    // Events
    event ModelRegistered(
        string indexed modelId,
        string version,
        string cid,
        bytes32 modelHash,
        uint256 round,
        uint256 timestamp
    );

    event ModelDeactivated(string indexed modelId, string version);

    constructor() {
        _grantRole(DEFAULT_ADMIN_ROLE, msg.sender);
        _grantRole(AGGREGATOR_ROLE, msg.sender);
    }

    /**
     * @dev Register a new model version
     * Only callable by addresses with AGGREGATOR_ROLE
     */
    function registerModel(
        string memory modelId,
        string memory version,
        string memory cid,
        bytes32 modelHash,
        string memory architecture,
        uint256 round,
        uint256 numParticipants
    ) external onlyRole(AGGREGATOR_ROLE) nonReentrant {
        require(bytes(modelId).length > 0, "Model ID cannot be empty");
        require(bytes(version).length > 0, "Version cannot be empty");
        require(bytes(cid).length > 0, "CID cannot be empty");
        require(modelHash != bytes32(0), "Model hash cannot be zero");
        require(!models[modelId][version].isActive, "Model version already exists");

        ModelVersion memory newModel = ModelVersion({
            cid: cid,
            modelHash: modelHash,
            architecture: architecture,
            round: round,
            numParticipants: numParticipants,
            timestamp: block.timestamp,
            publisher: msg.sender,
            isActive: true
        });

        models[modelId][version] = newModel;
        latestVersion[modelId] = version;

        emit ModelRegistered(modelId, version, cid, modelHash, round, block.timestamp);
    }

    /**
     * @dev Get model information
     */
    function getModel(string memory modelId, string memory version)
        external
        view
        returns (ModelVersion memory)
    {
        require(models[modelId][version].isActive, "Model not found or inactive");
        return models[modelId][version];
    }

    /**
     * @dev Get latest model version
     */
    function getLatestModel(string memory modelId)
        external
        view
        returns (ModelVersion memory)
    {
        string memory version = latestVersion[modelId];
        require(bytes(version).length > 0, "No model found for this ID");
        return models[modelId][version];
    }

    /**
     * @dev Verify model integrity
     */
    function verifyModel(string memory modelId, string memory version, bytes32 expectedHash)
        external
        view
        returns (bool)
    {
        ModelVersion memory model = models[modelId][version];
        return model.isActive && model.modelHash == expectedHash;
    }

    /**
     * @dev Deactivate a model version (in case of security issues)
     */
    function deactivateModel(string memory modelId, string memory version)
        external
        onlyRole(DEFAULT_ADMIN_ROLE)
    {
        require(models[modelId][version].isActive, "Model already inactive");
        models[modelId][version].isActive = false;
        emit ModelDeactivated(modelId, version);
    }
}
