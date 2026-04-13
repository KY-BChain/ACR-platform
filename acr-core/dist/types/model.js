"use strict";
/**
 * Federated Learning Model-related types
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.TrainingStatus = exports.ModelArchitecture = void 0;
var ModelArchitecture;
(function (ModelArchitecture) {
    ModelArchitecture["VIT_MONAI"] = "VIT_MONAI";
    ModelArchitecture["UNET_MONAI"] = "UNET_MONAI";
    ModelArchitecture["RESNET_MONAI"] = "RESNET_MONAI";
    ModelArchitecture["CUSTOM"] = "CUSTOM";
})(ModelArchitecture || (exports.ModelArchitecture = ModelArchitecture = {}));
var TrainingStatus;
(function (TrainingStatus) {
    TrainingStatus["IDLE"] = "IDLE";
    TrainingStatus["DOWNLOADING_MODEL"] = "DOWNLOADING_MODEL";
    TrainingStatus["TRAINING"] = "TRAINING";
    TrainingStatus["UPLOADING_UPDATE"] = "UPLOADING_UPDATE";
    TrainingStatus["COMPLETED"] = "COMPLETED";
    TrainingStatus["FAILED"] = "FAILED";
})(TrainingStatus || (exports.TrainingStatus = TrainingStatus = {}));
//# sourceMappingURL=model.js.map