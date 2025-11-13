"""
Genomics Agent for ACR Platform
Specializes in HPV detection and biomarker analysis
"""

from typing import Dict, Any, List
from loguru import logger

from ..base_agent import BaseACRAgent
from ..skills.swrl_reasoner import SWRLReasonerSkill


class GenomicsAgent(BaseACRAgent):
    """
    Genomics Agent for HPV strain detection and biomarker analysis
    """

    def __init__(
        self,
        name: str = "genomics_agent",
        ontology_path: str = "./ontology/ACR.owl",
        redis_url: str = "redis://localhost:6379",
        port: int = 8003,
    ):
        super().__init__(
            name=name,
            agent_type="GENOMICS",
            ontology_path=ontology_path,
            redis_url=redis_url,
            port=port,
        )

        self.swrl_reasoner_skill = SWRLReasonerSkill(self.ontology)

        self.swrl_rules = [
            "diagnostic.swrl:Rule1",  # High-risk HPV detection
            "diagnostic.swrl:Rule2",  # HSIL with high-risk HPV
            "diagnostic.swrl:Rule3",  # Normal with negative HPV
            "diagnostic.swrl:Rule4",  # ASCUS with high-risk HPV
            "diagnostic.swrl:Rule11",  # P16INK4a positive
        ]

        logger.info("GenomicsAgent initialized")

    def get_capabilities(self) -> List[Dict[str, Any]]:
        return [
            {
                "id": "hpv_detection",
                "name": "HPV Strain Detection",
                "requiredModalities": ["HPV_TEST"],
                "outputTypes": ["HPVStrain", "RiskLevel"],
            },
            {
                "id": "biomarker_analysis",
                "name": "Biomarker Analysis",
                "requiredModalities": ["IMMUNOHISTOCHEMISTRY"],
                "outputTypes": ["BiomarkerStatus"],
            },
        ]

    async def analyze_case(self, case_data: Dict[str, Any]) -> Dict[str, Any]:
        """Analyze genomic data"""
        try:
            genomic_profile = case_data.get("genomic_profile", {})

            if not genomic_profile:
                return self._empty_findings()

            # Extract HPV status
            hpv_status = genomic_profile.get("hpv_status", "UNKNOWN")
            hpv_strains = genomic_profile.get("hpv_strains", [])

            # Extract biomarkers
            p16_status = genomic_profile.get("p16INK4a_status")
            ki67_status = genomic_profile.get("ki67_status")

            # Determine risk level based on HPV strains
            risk_level = self._assess_hpv_risk(hpv_strains)

            # Determine grade based on biomarkers
            grade = self._assess_grade_from_biomarkers(p16_status, ki67_status, hpv_strains)

            # Confidence based on data completeness
            confidence = self._calculate_confidence(genomic_profile)

            # Apply SWRL reasoning
            swrl_result = self.swrl_reasoner_skill.apply_rules(
                {
                    "hpv_status": hpv_status,
                    "hpv_strains": hpv_strains,
                    "p16INK4a": p16_status,
                    "ki67": ki67_status,
                }
            )

            findings = {
                "grade": grade,
                "risk_level": risk_level,
                "confidence": confidence,
                "features": self._extract_genomic_features(genomic_profile),
                "hpv_strains": hpv_strains,
                "biomarkers": {
                    "p16INK4a": p16_status,
                    "ki67": ki67_status,
                },
                "reasoning": swrl_result.get("reasoning", ""),
                "applied_rules": swrl_result.get("applied_rules", []),
            }

            return findings

        except Exception as e:
            logger.error(f"Error in genomics analysis: {e}")
            raise

    def _assess_hpv_risk(self, hpv_strains: List[str]) -> str:
        """Assess risk based on HPV strains"""
        high_risk_strains = ["HPV_16", "HPV_18"]
        moderate_risk_strains = ["HPV_31", "HPV_33", "HPV_45", "HPV_52", "HPV_58"]

        if any(strain in high_risk_strains for strain in hpv_strains):
            return "VERY_HIGH"
        elif any(strain in moderate_risk_strains for strain in hpv_strains):
            return "HIGH"
        elif hpv_strains and hpv_strains[0] != "NEGATIVE":
            return "MODERATE"
        else:
            return "LOW"

    def _assess_grade_from_biomarkers(
        self, p16_status: str, ki67_status: str, hpv_strains: List[str]
    ) -> str:
        """Assess grade from biomarkers"""
        if p16_status == "POSITIVE" and ki67_status == "POSITIVE":
            if any(strain in ["HPV_16", "HPV_18"] for strain in hpv_strains):
                return "HSIL"
            else:
                return "LSIL"
        elif p16_status == "POSITIVE":
            return "ASCUS"
        else:
            return "NORMAL"

    def _calculate_confidence(self, genomic_profile: Dict[str, Any]) -> float:
        """Calculate confidence based on data completeness"""
        fields = ["hpv_status", "hpv_strains", "p16INK4a_status", "ki67_status"]
        present = sum(1 for field in fields if genomic_profile.get(field))
        return min(0.9, present / len(fields))

    def _extract_genomic_features(self, genomic_profile: Dict[str, Any]) -> List[str]:
        """Extract genomic features"""
        features = []

        if genomic_profile.get("hpv_status") == "POSITIVE":
            features.append("hpv_positive")

        if genomic_profile.get("p16INK4a_status") == "POSITIVE":
            features.append("p16_positive")

        if genomic_profile.get("ki67_status") == "POSITIVE":
            features.append("ki67_positive")

        return features

    def _empty_findings(self) -> Dict[str, Any]:
        return {
            "grade": "UNKNOWN",
            "risk_level": "UNKNOWN",
            "confidence": 0.0,
            "features": [],
            "hpv_strains": [],
        }
