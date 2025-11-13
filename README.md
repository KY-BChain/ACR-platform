# ACR Platform v0.8

**AI-Powered Cervical Cancer Risk Assessment Platform**

A comprehensive, privacy-preserving federated learning platform for cervical cancer diagnosis combining ontology-based reasoning, agentic AI, blockchain identity management, and differential privacy.

## Overview

The ACR Platform integrates:
- **Ontology-Based Reasoning**: OWL ontology with SWRL rules for clinical decision support
- **Agentic AI**: Fetch.ai-based intelligent agents for radiology, pathology, and genomics
- **Federated Learning**: MONAI-based vision transformers with differential privacy (ε=0.7, δ=10^-6)
- **Blockchain Identity**: RSK-based ERC-3643 identity management and model registry
- **Zero-Knowledge Proofs**: Privacy-preserving consent and data verification
- **Tokenomics**: $ACR_RWA and $ACR_GOV tokens for incentivizing data contribution

## Architecture

```
acr-platform/
├── acr-core/              # Shared types, constants, utilities
├── acr-ontology/          # OWL ontology, SWRL/SQWRL rules
├── acr-agents/            # Fetch.ai intelligent agents
├── acr-federated-ml/      # Federated learning with MONAI
├── acr-blockchain/        # RSK smart contracts
├── acr-privacy/           # ZKP, differential privacy
├── acr-api-gateway/       # Fastify backend API
├── acr-web-portal/        # React frontend
└── acr-infrastructure/    # Kubernetes, Docker, IaC
```

## Key Features

### Privacy-Preserving AI
- Local model training with differential privacy guarantees
- Zero-knowledge proofs for consent verification
- Homomorphic encryption for secure gradient aggregation
- No raw patient data leaves hospital premises

### Ontology-Driven Decision Making
- Comprehensive OWL ontology for cervical cancer domain
- SWRL rules for automated reasoning and inference
- SQWRL queries for consensus mechanisms
- HermiT reasoner integration

### Agentic System
- Specialized agents for radiology, pathology, genomics
- Mixture of Agents (MoA) consensus
- Fetch.ai protocol-based communication
- Reusable skills and protocols

### Blockchain Integration
- ERC-3643 compliant identity management
- Immutable model registry on RSK
- DAO for SWRL rule governance
- Gasless transactions via ERC-4337

## Quick Start

### Prerequisites
- Node.js 18+
- Python 3.10+
- Docker & Docker Compose
- Kubernetes (for production)
- RSK node or testnet access

### Installation

```bash
# Clone the repository
git clone https://github.com/KY-BChain/ACR-platform.git
cd ACR-platform

# Install dependencies for all modules
npm install
cd acr-agents && pip install -r requirements.txt
cd ../acr-federated-ml && pip install -r requirements.txt
cd ../acr-blockchain && npm install
cd ../acr-api-gateway && npm install
cd ../acr-web-portal && npm install
```

### Running Locally

```bash
# Start the API gateway
cd acr-api-gateway
npm run dev

# Start the web portal
cd acr-web-portal
npm run dev

# Start the agent system
cd acr-agents
python src/base_agent.py

# Start federated learning aggregator
cd acr-federated-ml
python src/aggregation/secure_aggregator.py
```

### Running with Docker

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f
```

## Workflow

### Single Diagnostic Run
1. Clinician logs in with ERC-3643 wallet (gasless via ERC-4337)
2. Selects patient case from dashboard (anonymized metadata only)
3. Local agents activate and process DICOM/EHR data locally
4. Agents run inference, apply SWRL rules, generate findings
5. Consensus agent performs MoA voting
6. Results displayed with confidence scores and rule explanations
7. Decision logged to blockchain immutably

### Federated Learning Cycle
1. Global aggregator publishes model CID to blockchain
2. Hospitals pull model and train locally for one epoch
3. Local updates encrypted and noise added (DP with ε=0.7)
4. ZKP proof generated for privacy verification
5. Secure aggregation performed at central server
6. New global model validated and published to IPFS
7. Model CID recorded on blockchain

## Tokenomics

- **$ACR_RWA**: Non-transferable ERC-3643 token representing hospital identity and data quota
- **$ACR_GOV**: Transferable ERC-20 governance token for voting and staking

Hospitals earn $ACR_GOV for:
- Contributing verified model updates
- Participating in consensus mechanisms
- Submitting high-quality SWRL rules

## Technology Stack

- **ML/AI**: MONAI, PyTorch, Vision Transformers
- **Agents**: Fetch.ai, Python
- **Ontology**: OWL, SWRL, HermiT Reasoner, Protégé
- **Backend**: Node.js, Fastify, TypeScript
- **Frontend**: React, TypeScript, Web3.js
- **Blockchain**: RSK, Solidity, Hardhat, ERC-3643, ERC-4337
- **Privacy**: Circom (ZKP), Differential Privacy, Paillier/CKKS
- **Infrastructure**: Kubernetes, Docker, Terraform, Ansible
- **Storage**: IPFS, PostgreSQL, Redis

## Documentation

- [Architecture Overview](docs/architecture.md)
- [API Documentation](docs/api/)
- [Ontology Guide](docs/ontology/)
- [Deployment Guide](docs/deployment/)
- [Compliance Documentation](docs/compliance/)

## Testing

```bash
# Run unit tests
npm run test:unit

# Run integration tests
npm run test:integration

# Run end-to-end tests
npm run test:e2e

# Run all tests
npm test
```

## Deployment

### Development
```bash
kubectl apply -f acr-infrastructure/k8s/dev/
```

### Staging
```bash
kubectl apply -f acr-infrastructure/k8s/staging/
```

### Production
```bash
kubectl apply -f acr-infrastructure/k8s/prod/
```

## Security

- All patient data processed locally at hospitals
- Differential privacy guarantees (ε=0.7, δ=10^-6)
- Zero-knowledge proofs for consent verification
- End-to-end encryption for all communications
- Regular security audits and penetration testing

## Compliance

- GDPR compliant
- PIPL (China) compliant
- HIPAA ready
- Full audit trail on blockchain

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

- Website: https://acr-platform.io
- Email: contact@acr-platform.io
- Discord: https://discord.gg/acr-platform

## Acknowledgments

- MONAI for medical imaging framework
- Fetch.ai for agent framework
- RSK for blockchain infrastructure
- OpenMined for federated learning inspiration

---

**Version**: 0.8
**Last Updated**: November 12, 2025
**Status**: Development
