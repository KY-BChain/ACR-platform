"""
Base Agent Class for ACR Platform
Implements BDI (Beliefs, Desires, Intentions) architecture using Fetch.ai
"""

from abc import ABC, abstractmethod
from typing import List, Dict, Any, Optional
from datetime import datetime
from enum import Enum
import asyncio
import json
from loguru import logger

from uagents import Agent, Context, Model
from owlready2 import get_ontology, sync_reasoner_hermit
import redis.asyncio as redis


class AgentStatus(Enum):
    IDLE = "IDLE"
    ACTIVE = "ACTIVE"
    PROCESSING = "PROCESSING"
    WAITING = "WAITING"
    ERROR = "ERROR"
    OFFLINE = "OFFLINE"


class Belief(Model):
    """Represents an agent's belief"""
    belief_id: str
    assertion: str
    confidence: float
    evidence: List[Dict[str, Any]]
    timestamp: datetime


class Intention(Model):
    """Represents an agent's intention"""
    intention_id: str
    goal: str
    priority: int
    status: str
    plan: List[Dict[str, Any]]
    created_at: datetime


class AgentMessage(Model):
    """Message format for inter-agent communication"""
    message_id: str
    from_agent: str
    to_agent: str
    protocol: str
    performative: str
    content: Dict[str, Any]
    conversation_id: Optional[str] = None
    in_reply_to: Optional[str] = None
    timestamp: datetime


class BaseACRAgent(ABC):
    """
    Abstract base class for all ACR agents
    Implements BDI architecture with ontology-based reasoning
    """

    def __init__(
        self,
        name: str,
        agent_type: str,
        ontology_path: str,
        redis_url: str = "redis://localhost:6379",
        port: int = 8000,
    ):
        self.name = name
        self.agent_type = agent_type
        self.status = AgentStatus.IDLE
        self.ontology_path = ontology_path

        # Initialize Fetch.ai agent
        self.agent = Agent(name=name, port=port, seed=name)

        # Beliefs, Desires, Intentions
        self.beliefs: List[Belief] = []
        self.intentions: List[Intention] = []

        # Load ontology
        self.ontology = None
        self.load_ontology()

        # Redis for message queue
        self.redis_client = None
        self.redis_url = redis_url

        # Active cases
        self.active_cases: Dict[str, Any] = {}

        # Performance metrics
        self.metrics = {
            "cases_processed": 0,
            "total_processing_time": 0.0,
            "average_confidence": 0.0,
            "error_count": 0,
        }

        logger.info(f"Initialized {self.agent_type} agent: {self.name}")

    def load_ontology(self):
        """Load OWL ontology for reasoning"""
        try:
            self.ontology = get_ontology(self.ontology_path).load()
            logger.info(f"Loaded ontology from {self.ontology_path}")
        except Exception as e:
            logger.error(f"Failed to load ontology: {e}")
            raise

    async def connect_redis(self):
        """Connect to Redis for message queue"""
        try:
            self.redis_client = await redis.from_url(self.redis_url, decode_responses=True)
            logger.info("Connected to Redis")
        except Exception as e:
            logger.error(f"Failed to connect to Redis: {e}")
            raise

    def add_belief(self, assertion: str, confidence: float, evidence: List[Dict[str, Any]]):
        """Add a new belief to the belief base"""
        belief = Belief(
            belief_id=f"{self.name}_{len(self.beliefs)}",
            assertion=assertion,
            confidence=confidence,
            evidence=evidence,
            timestamp=datetime.now(),
        )
        self.beliefs.append(belief)
        logger.debug(f"Added belief: {assertion} with confidence {confidence}")

    def add_intention(self, goal: str, priority: int, plan: List[Dict[str, Any]]):
        """Add a new intention"""
        intention = Intention(
            intention_id=f"{self.name}_int_{len(self.intentions)}",
            goal=goal,
            priority=priority,
            status="PENDING",
            plan=plan,
            created_at=datetime.now(),
        )
        self.intentions.append(intention)
        logger.debug(f"Added intention: {goal} with priority {priority}")

    def reason(self):
        """
        Perform ontological reasoning using HermiT reasoner
        Updates beliefs based on SWRL rules
        """
        try:
            with self.ontology:
                sync_reasoner_hermit(infer_property_values=True, infer_data_property_values=True)
            logger.debug("Reasoning completed successfully")
        except Exception as e:
            logger.error(f"Reasoning failed: {e}")
            self.metrics["error_count"] += 1

    @abstractmethod
    async def analyze_case(self, case_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Analyze a case and generate findings
        Must be implemented by concrete agent classes
        """
        pass

    @abstractmethod
    def get_capabilities(self) -> List[Dict[str, Any]]:
        """
        Return the agent's capabilities
        Must be implemented by concrete agent classes
        """
        pass

    async def process_case(self, case_id: str, case_data: Dict[str, Any]) -> Dict[str, Any]:
        """Process a case through the full agent workflow"""
        start_time = datetime.now()
        self.status = AgentStatus.PROCESSING
        self.active_cases[case_id] = case_data

        try:
            logger.info(f"Processing case {case_id}")

            # 1. Analyze the case (agent-specific)
            findings = await self.analyze_case(case_data)

            # 2. Update beliefs based on findings
            self.add_belief(
                assertion=f"hasGrade({case_id}, {findings['grade']})",
                confidence=findings["confidence"],
                evidence=[{"source": "IMAGE_ANALYSIS", "data": findings}],
            )

            # 3. Perform ontological reasoning
            self.reason()

            # 4. Generate structured output
            result = {
                "case_id": case_id,
                "agent_id": self.name,
                "agent_type": self.agent_type,
                "findings": findings,
                "beliefs": [b.dict() for b in self.beliefs if case_id in b.assertion],
                "timestamp": datetime.now().isoformat(),
            }

            # Update metrics
            processing_time = (datetime.now() - start_time).total_seconds()
            self.metrics["cases_processed"] += 1
            self.metrics["total_processing_time"] += processing_time
            self.metrics["average_confidence"] = (
                self.metrics["average_confidence"] * (self.metrics["cases_processed"] - 1)
                + findings["confidence"]
            ) / self.metrics["cases_processed"]

            logger.info(
                f"Completed case {case_id} in {processing_time:.2f}s with confidence {findings['confidence']:.3f}"
            )

            return result

        except Exception as e:
            logger.error(f"Error processing case {case_id}: {e}")
            self.metrics["error_count"] += 1
            self.status = AgentStatus.ERROR
            raise

        finally:
            self.active_cases.pop(case_id, None)
            self.status = AgentStatus.IDLE

    async def send_message(self, to_agent: str, content: Dict[str, Any], performative: str = "INFORM"):
        """Send message to another agent via Redis"""
        if not self.redis_client:
            await self.connect_redis()

        message = AgentMessage(
            message_id=f"{self.name}_{datetime.now().timestamp()}",
            from_agent=self.name,
            to_agent=to_agent,
            protocol="acr:agent_communication",
            performative=performative,
            content=content,
            conversation_id=None,
            timestamp=datetime.now(),
        )

        channel = f"agent:{to_agent}:messages"
        await self.redis_client.rpush(channel, message.json())
        logger.debug(f"Sent {performative} message to {to_agent}")

    async def receive_messages(self):
        """Receive messages from Redis queue"""
        if not self.redis_client:
            await self.connect_redis()

        channel = f"agent:{self.name}:messages"

        while True:
            message_json = await self.redis_client.blpop(channel, timeout=1)
            if message_json:
                message = AgentMessage.parse_raw(message_json[1])
                await self.handle_message(message)
            await asyncio.sleep(0.1)

    async def handle_message(self, message: AgentMessage):
        """Handle incoming message from another agent"""
        logger.info(f"Received {message.performative} from {message.from_agent}")

        if message.performative == "REQUEST":
            # Handle case analysis request
            case_id = message.content.get("case_id")
            case_data = message.content.get("case_data")

            if case_id and case_data:
                result = await self.process_case(case_id, case_data)
                await self.send_message(
                    message.from_agent, {"result": result}, performative="INFORM"
                )

    def get_status(self) -> Dict[str, Any]:
        """Return current agent status"""
        return {
            "agent_id": self.name,
            "agent_type": self.agent_type,
            "status": self.status.value,
            "active_cases": list(self.active_cases.keys()),
            "beliefs_count": len(self.beliefs),
            "intentions_count": len(self.intentions),
            "metrics": self.metrics,
            "last_heartbeat": datetime.now().isoformat(),
        }

    async def run(self):
        """Run the agent"""
        await self.connect_redis()

        # Start message listener
        asyncio.create_task(self.receive_messages())

        # Heartbeat loop
        while True:
            logger.debug(f"Agent {self.name} heartbeat - Status: {self.status.value}")
            await asyncio.sleep(30)


if __name__ == "__main__":
    # Example usage
    logger.info("ACR Agent System starting...")
    # Concrete agents will be instantiated from their respective modules
