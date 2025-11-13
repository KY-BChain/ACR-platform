"""
Differentially Private Federated Averaging (DP-FedAvg)
Implements federated learning with differential privacy guarantees
"""

from typing import Dict, List, Any
import torch
import torch.nn as nn
from opacus import PrivacyEngine
from opacus.utils.batch_memory_manager import BatchMemoryManager
from loguru import logger
import numpy as np


class DPFedAvgTrainer:
    """
    Trainer for DP-FedAvg algorithm
    Privacy parameters: ε=0.7, δ=10^-6
    """

    def __init__(
        self,
        model: nn.Module,
        epsilon: float = 0.7,
        delta: float = 1e-6,
        max_grad_norm: float = 1.0,
        learning_rate: float = 0.001,
        device: str = "cpu",
    ):
        self.model = model.to(device)
        self.device = device
        self.epsilon = epsilon
        self.delta = delta
        self.max_grad_norm = max_grad_norm
        self.learning_rate = learning_rate

        # Privacy engine
        self.privacy_engine = None

        # Optimizer
        self.optimizer = torch.optim.Adam(self.model.parameters(), lr=learning_rate)

        # Loss function
        self.criterion = nn.CrossEntropyLoss()

        logger.info(f"Initialized DP-FedAvg with ε={epsilon}, δ={delta}")

    def attach_privacy_engine(self, data_loader, epochs: int = 1):
        """Attach Opacus privacy engine to optimizer"""
        self.privacy_engine = PrivacyEngine()

        self.model, self.optimizer, data_loader = self.privacy_engine.make_private(
            module=self.model,
            optimizer=self.optimizer,
            data_loader=data_loader,
            noise_multiplier=self._compute_noise_multiplier(),
            max_grad_norm=self.max_grad_norm,
        )

        logger.info("Privacy engine attached to model")

        return data_loader

    def _compute_noise_multiplier(self) -> float:
        """Compute noise multiplier for target epsilon"""
        # Simplified calculation - in production use opacus.accountants
        return np.sqrt(2 * np.log(1.25 / self.delta)) / self.epsilon

    def train_one_epoch(self, data_loader) -> Dict[str, float]:
        """
        Train model for one epoch with differential privacy
        """
        self.model.train()
        total_loss = 0.0
        correct = 0
        total = 0

        for batch_idx, (data, target) in enumerate(data_loader):
            data, target = data.to(self.device), target.to(self.device)

            self.optimizer.zero_grad()

            # Forward pass
            output = self.model(data)
            loss = self.criterion(output, target)

            # Backward pass with DP
            loss.backward()

            # Gradient clipping and noise addition handled by Opacus
            self.optimizer.step()

            # Metrics
            total_loss += loss.item()
            _, predicted = output.max(1)
            total += target.size(0)
            correct += predicted.eq(target).sum().item()

        accuracy = 100.0 * correct / total
        avg_loss = total_loss / len(data_loader)

        # Get privacy spent
        epsilon_spent = self.privacy_engine.get_epsilon(self.delta) if self.privacy_engine else 0.0

        metrics = {
            "loss": avg_loss,
            "accuracy": accuracy,
            "epsilon_spent": epsilon_spent,
        }

        logger.info(
            f"Epoch complete: Loss={avg_loss:.4f}, Accuracy={accuracy:.2f}%, ε={epsilon_spent:.4f}"
        )

        return metrics

    def get_model_update(self) -> Dict[str, torch.Tensor]:
        """Get model parameters for federated aggregation"""
        return {name: param.data.clone() for name, param in self.model.named_parameters()}

    def set_model_parameters(self, parameters: Dict[str, torch.Tensor]):
        """Set model parameters from global model"""
        with torch.no_grad():
            for name, param in self.model.named_parameters():
                if name in parameters:
                    param.data.copy_(parameters[name])

    def evaluate(self, data_loader) -> Dict[str, float]:
        """Evaluate model on validation set"""
        self.model.eval()
        total_loss = 0.0
        correct = 0
        total = 0

        with torch.no_grad():
            for data, target in data_loader:
                data, target = data.to(self.device), target.to(self.device)

                output = self.model(data)
                loss = self.criterion(output, target)

                total_loss += loss.item()
                _, predicted = output.max(1)
                total += target.size(0)
                correct += predicted.eq(target).sum().item()

        accuracy = 100.0 * correct / total
        avg_loss = total_loss / len(data_loader)

        return {
            "loss": avg_loss,
            "accuracy": accuracy,
        }
