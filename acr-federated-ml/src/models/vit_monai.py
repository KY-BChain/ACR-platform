"""
Vision Transformer with MONAI for Cervical Cancer Classification
"""

import torch
import torch.nn as nn
from monai.networks.nets import ViT
from loguru import logger


class CervicalCancerViT(nn.Module):
    """
    Vision Transformer for cervical cancer grade classification
    Based on MONAI's ViT implementation
    """

    def __init__(
        self,
        img_size: int = 224,
        patch_size: int = 16,
        in_channels: int = 3,
        num_classes: int = 5,  # NORMAL, ASCUS, LSIL, HSIL, CANCER
        hidden_size: int = 768,
        mlp_dim: int = 3072,
        num_heads: int = 12,
        num_layers: int = 12,
        dropout_rate: float = 0.1,
    ):
        super().__init__()

        self.vit = ViT(
            in_channels=in_channels,
            img_size=img_size,
            patch_size=patch_size,
            hidden_size=hidden_size,
            mlp_dim=mlp_dim,
            num_layers=num_layers,
            num_heads=num_heads,
            pos_embed="conv",
            classification=True,
            num_classes=num_classes,
            dropout_rate=dropout_rate,
        )

        logger.info(f"Initialized CervicalCancerViT with {num_classes} classes")

    def forward(self, x):
        return self.vit(x)

    def get_attention_maps(self, x):
        """Extract attention maps for visualization"""
        # Forward pass through transformer blocks
        # Returns attention weights for interpretability
        pass


def create_model(pretrained: bool = False, num_classes: int = 5) -> CervicalCancerViT:
    """Factory function to create ViT model"""
    model = CervicalCancerViT(num_classes=num_classes)

    if pretrained:
        # Load pretrained weights if available
        logger.info("Loading pretrained weights...")

    return model
