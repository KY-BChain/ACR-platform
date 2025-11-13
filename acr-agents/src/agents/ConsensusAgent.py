"""
Consensus Agent for ACR Platform
Implements Mixture of Agents (MoA) consensus mechanism
"""

from typing import Dict, Any, List
from collections import Counter
import numpy as np
from loguru import logger

from ..base_agent import BaseACRAgent


class ConsensusAgent(BaseACRAgent):
    """
    Consensus Agent for aggregating findings from multiple agents
    Uses Mixture of Agents (MoA) with weighted voting
    """

    def __init__(
        self,
        name: str = "consensus_agent",
        ontology_path: str = "./ontology/ACR.owl",
        redis_url: str = "redis://localhost:6379",
        port: int = 8004,
    ):
        super().__init__(
            name=name,
            agent_type="CONSENSUS",
            ontology_path=ontology_path,
            redis_url=redis_url,
            port=port,
        )

        # Weights for different agent types
        self.agent_weights = {
            "RADIOLOGY": 0.4,
            "PATHOLOGY": 0.4,
            "GENOMICS": 0.2,
        }

        logger.info("ConsensusAgent initialized")

    def get_capabilities(self) -> List[Dict[str, Any]]:
        return [
            {
                "id": "moa_consensus",
                "name": "Mixture of Agents Consensus",
                "requiredModalities": ["ALL"],
                "outputTypes": ["FinalGrade", "FinalRiskLevel", "ConsensusScore"],
            },
            {
                "id": "treatment_recommendation",
                "name": "Treatment Recommendation",
                "requiredModalities": ["ALL"],
                "outputTypes": ["Treatment"],
            },
        ]

    async def analyze_case(self, case_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Perform consensus analysis on agent findings
        """
        try:
            agent_findings = case_data.get("agent_findings", [])

            if not agent_findings or len(agent_findings) < 2:
                logger.warning("Insufficient agent findings for consensus")
                return self._empty_consensus()

            logger.info(f"Performing consensus on {len(agent_findings)} agent findings")

            # 1. Weighted voting for grade
            final_grade, grade_confidence = self._weighted_vote_grade(agent_findings)

            # 2. Weighted voting for risk level
            final_risk, risk_confidence = self._weighted_vote_risk(agent_findings)

            # 3. Calculate consensus strength
            consensus_strength = self._calculate_consensus_strength(agent_findings)

            # 4. Aggregate features
            all_features = self._aggregate_features(agent_findings)

            # 5. Check for conflicts
            has_conflicts = self._detect_conflicts(agent_findings)

            # 6. Generate treatment recommendation
            treatment = self._recommend_treatment(final_grade, final_risk)

            consensus_result = {
                "grade": final_grade,
                "risk_level": final_risk,
                "confidence": (grade_confidence + risk_confidence) / 2,
                "consensus_strength": consensus_strength,
                "features": all_features,
                "has_conflicts": has_conflicts,
                "treatment_recommendation": treatment,
                "agent_votes": self._get_agent_votes(agent_findings),
                "reasoning": self._generate_reasoning(agent_findings, final_grade, final_risk),
            }

            logger.info(
                f"Consensus reached: Grade={final_grade}, Risk={final_risk}, Strength={consensus_strength:.3f}"
            )

            return consensus_result

        except Exception as e:
            logger.error(f"Error in consensus analysis: {e}")
            raise

    def _weighted_vote_grade(self, agent_findings: List[Dict[str, Any]]) -> tuple:
        """Weighted voting for cervical grade"""
        grade_scores = {}

        for finding in agent_findings:
            agent_type = finding.get("agent_type")
            grade = finding.get("findings", {}).get("grade")
            confidence = finding.get("findings", {}).get("confidence", 0.5)
            weight = self.agent_weights.get(agent_type, 0.33)

            weighted_score = confidence * weight

            if grade in grade_scores:
                grade_scores[grade] += weighted_score
            else:
                grade_scores[grade] = weighted_score

        # Get grade with highest weighted score
        final_grade = max(grade_scores, key=grade_scores.get)
        final_confidence = grade_scores[final_grade] / sum(grade_scores.values())

        return final_grade, final_confidence

    def _weighted_vote_risk(self, agent_findings: List[Dict[str, Any]]) -> tuple:
        """Weighted voting for risk level"""
        risk_scores = {}

        for finding in agent_findings:
            agent_type = finding.get("agent_type")
            risk = finding.get("findings", {}).get("risk_level")
            confidence = finding.get("findings", {}).get("confidence", 0.5)
            weight = self.agent_weights.get(agent_type, 0.33)

            weighted_score = confidence * weight

            if risk in risk_scores:
                risk_scores[risk] += weighted_score
            else:
                risk_scores[risk] = weighted_score

        final_risk = max(risk_scores, key=risk_scores.get)
        final_confidence = risk_scores[final_risk] / sum(risk_scores.values())

        return final_risk, final_confidence

    def _calculate_consensus_strength(self, agent_findings: List[Dict[str, Any]]) -> float:
        """Calculate how strongly agents agree"""
        grades = [f.get("findings", {}).get("grade") for f in agent_findings]
        grade_counts = Counter(grades)
        most_common_count = grade_counts.most_common(1)[0][1]

        return most_common_count / len(grades)

    def _aggregate_features(self, agent_findings: List[Dict[str, Any]]) -> List[str]:
        """Aggregate all features from agents"""
        all_features = set()

        for finding in agent_findings:
            features = finding.get("findings", {}).get("features", [])
            all_features.update(features)

        return list(all_features)

    def _detect_conflicts(self, agent_findings: List[Dict[str, Any]]) -> bool:
        """Detect if agents have conflicting findings"""
        grades = [f.get("findings", {}).get("grade") for f in agent_findings]

        # Check if we have both NORMAL and HSIL/CANCER
        has_normal = "NORMAL" in grades
        has_severe = any(g in ["HSIL", "CANCER"] for g in grades)

        return has_normal and has_severe

    def _recommend_treatment(self, grade: str, risk_level: str) -> str:
        """Recommend treatment based on grade and risk"""
        if grade == "NORMAL" or risk_level == "LOW":
            return "Watchful Waiting"
        elif grade in ["ASCUS", "LSIL"] or risk_level == "MODERATE":
            return "Colposcopy"
        elif grade == "HSIL" or risk_level == "HIGH":
            return "LEEP Procedure"
        elif grade == "CANCER" or risk_level == "VERY_HIGH":
            return "Hysterectomy / Specialist Referral"
        else:
            return "Further Evaluation Required"

    def _get_agent_votes(self, agent_findings: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """Get individual agent votes"""
        votes = []

        for finding in agent_findings:
            votes.append({
                "agent_id": finding.get("agent_id"),
                "agent_type": finding.get("agent_type"),
                "vote": finding.get("findings", {}).get("grade"),
                "confidence": finding.get("findings", {}).get("confidence"),
                "weight": self.agent_weights.get(finding.get("agent_type"), 0.33),
            })

        return votes

    def _generate_reasoning(
        self, agent_findings: List[Dict[str, Any]], final_grade: str, final_risk: str
    ) -> str:
        """Generate human-readable reasoning"""
        num_agents = len(agent_findings)
        consensus_strength = self._calculate_consensus_strength(agent_findings)

        reasoning = f"Consensus reached based on {num_agents} agent analyses with {consensus_strength*100:.1f}% agreement. "
        reasoning += f"Final classification: {final_grade} with {final_risk} risk level. "

        if consensus_strength >= 0.75:
            reasoning += "Strong consensus among agents. "
        elif consensus_strength >= 0.5:
            reasoning += "Moderate consensus among agents. "
        else:
            reasoning += "Weak consensus - human review recommended. "

        return reasoning

    def _empty_consensus(self) -> Dict[str, Any]:
        return {
            "grade": "UNKNOWN",
            "risk_level": "UNKNOWN",
            "confidence": 0.0,
            "consensus_strength": 0.0,
            "features": [],
            "has_conflicts": True,
        }
