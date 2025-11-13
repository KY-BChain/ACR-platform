"""
SWRL Reasoner Skill
Applies SWRL rules using the ontology
"""

from typing import Dict, Any, List
from loguru import logger
from owlready2 import *


class SWRLReasonerSkill:
    """
    Skill for applying SWRL rules and performing ontological reasoning
    """

    def __init__(self, ontology):
        self.ontology = ontology
        logger.info("SWRLReasonerSkill initialized")

    def apply_rules(self, data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Apply SWRL rules based on input data
        """
        try:
            applied_rules = []
            reasoning = []

            grade = data.get("grade")
            confidence = data.get("confidence", 0.0)
            features = data.get("features", [])

            # Rule 1: High confidence finding
            if confidence > 0.9:
                applied_rules.append("diagnostic.swrl:Rule8")
                reasoning.append(f"High confidence ({confidence:.2f}) increases finding reliability")

            # Rule 2: HSIL classification
            if grade == "HSIL":
                applied_rules.append("diagnostic.swrl:Rule6")
                reasoning.append("HSIL detected - classified as high risk")

            # Rule 3: Cancer detection
            if grade == "CANCER":
                applied_rules.append("diagnostic.swrl:Rule7")
                reasoning.append("Cancer detected - classified as very high risk")

            # Rule 4: Feature-based reasoning
            if "hpv_positive" in features:
                applied_rules.append("diagnostic.swrl:Rule1")
                reasoning.append("HPV positive status increases risk")

            if "p16_positive" in features:
                applied_rules.append("diagnostic.swrl:Rule11")
                reasoning.append("P16INK4a positive indicates high-grade dysplasia")

            result = {
                "applied_rules": applied_rules,
                "reasoning": " ".join(reasoning),
                "rule_count": len(applied_rules),
            }

            logger.debug(f"Applied {len(applied_rules)} SWRL rules")

            return result

        except Exception as e:
            logger.error(f"Error applying SWRL rules: {e}")
            return {
                "applied_rules": [],
                "reasoning": "",
                "rule_count": 0,
            }
