"""
Pathology Agent for ACR Platform
Specializes in cytology and histopathology analysis
"""

from typing import Dict, Any, List
from loguru import logger

from ..base_agent import BaseACRAgent
from ..skills.image_analysis import ImageAnalysisSkill
from ..skills.swrl_reasoner import SWRLReasonerSkill


class PathologyAgent(BaseACRAgent):
    """
    Pathology Agent for analyzing cytology and histopathology slides
    Performs cell morphology and tissue architecture analysis
    """

    def __init__(
        self,
        name: str = "pathology_agent",
        ontology_path: str = "./ontology/ACR.owl",
        model_path: str = None,
        redis_url: str = "redis://localhost:6379",
        port: int = 8002,
    ):
        super().__init__(
            name=name,
            agent_type="PATHOLOGY",
            ontology_path=ontology_path,
            redis_url=redis_url,
            port=port,
        )

        self.image_analysis_skill = ImageAnalysisSkill(model_path)
        self.swrl_reasoner_skill = SWRLReasonerSkill(self.ontology)

        self.swrl_rules = [
            "diagnostic.swrl:Rule5",  # LSIL classification
            "diagnostic.swrl:Rule6",  # HSIL classification
            "diagnostic.swrl:Rule7",  # Cancer detection
            "diagnostic.swrl:Rule9",  # Multi-agent agreement
            "diagnostic.swrl:Rule12",  # Inconsistent findings
        ]

        logger.info(f"PathologyAgent initialized")

    def get_capabilities(self) -> List[Dict[str, Any]]:
        return [
            {
                "id": "cytology_analysis",
                "name": "Cytology Slide Analysis",
                "requiredModalities": ["CYTOLOGY", "HISTOPATHOLOGY"],
                "outputTypes": ["CervicalGrade", "RiskLevel"],
            },
            {
                "id": "histopathology_analysis",
                "name": "Histopathology Analysis",
                "requiredModalities": ["HISTOPATHOLOGY"],
                "outputTypes": ["CervicalGrade", "TissueArchitecture"],
            },
        ]

    async def analyze_case(self, case_data: Dict[str, Any]) -> Dict[str, Any]:
        """Analyze pathology slides"""
        try:
            images = case_data.get("images", [])
            pathology_images = [
                img
                for img in images
                if img.get("modality_type") in ["CYTOLOGY", "HISTOPATHOLOGY"]
            ]

            if not pathology_images:
                return self._empty_findings()

            primary_image = pathology_images[0]
            image_path = primary_image.get("local_path")

            logger.info(f"Analyzing pathology slide: {image_path}")

            # Analyze cell morphology
            analysis_result = await self.image_analysis_skill.analyze_image(image_path)

            # Extract pathology-specific features
            features = self._extract_pathology_features(analysis_result)

            # Apply SWRL reasoning
            swrl_result = self.swrl_reasoner_skill.apply_rules(
                {
                    "grade": analysis_result["predicted_grade"],
                    "confidence": analysis_result["confidence"],
                    "features": features,
                }
            )

            findings = {
                "grade": analysis_result["predicted_grade"],
                "risk_level": self._map_grade_to_risk(analysis_result["predicted_grade"]),
                "confidence": analysis_result["confidence"],
                "features": features,
                "regions": [],
                "reasoning": swrl_result.get("reasoning", ""),
                "applied_rules": swrl_result.get("applied_rules", []),
            }

            return findings

        except Exception as e:
            logger.error(f"Error in pathology analysis: {e}")
            raise

    def _extract_pathology_features(self, analysis_result: Dict[str, Any]) -> List[str]:
        """Extract pathology-specific features"""
        features = []

        # Nuclear features
        if analysis_result.get("nuclear_enlargement", False):
            features.append("nuclear_enlargement")

        if analysis_result.get("hyperchromasia", False):
            features.append("hyperchromasia")

        # Tissue architecture
        if analysis_result.get("loss_of_polarity", False):
            features.append("loss_of_polarity")

        if analysis_result.get("mitotic_figures", 0) > 5:
            features.append("increased_mitotic_activity")

        return features

    def _map_grade_to_risk(self, grade: str) -> str:
        grade_risk_map = {
            "NORMAL": "LOW",
            "ASCUS": "MODERATE",
            "LSIL": "MODERATE",
            "HSIL": "HIGH",
            "CANCER": "VERY_HIGH",
        }
        return grade_risk_map.get(grade, "UNKNOWN")

    def _empty_findings(self) -> Dict[str, Any]:
        return {
            "grade": "UNKNOWN",
            "risk_level": "UNKNOWN",
            "confidence": 0.0,
            "features": [],
            "regions": [],
        }
