# ACR Platform Setup Guide

## Prerequisites

- **Node.js**: v18.0.0 or higher
- **npm**: v9.0.0 or higher
- **Python**: 3.10 or higher
- **Docker** (optional, for containerized services)
- **Git**

## Step 1: Clone and Pull Latest Changes

```bash
cd dapp/acr-platform
git pull origin claude/acr-platform-codebase-v0.8-011CV3DNWn4kjcUf7LPqaZyv
```

## Step 2: Install Node.js Dependencies

```bash
npm install --legacy-peer-deps
```

**Note**: The `--legacy-peer-deps` flag is needed due to some peer dependency conflicts in Web3 packages.

## Step 3: Install Python Dependencies

### For Agents Module

```bash
cd acr-agents
pip install -r requirements.txt
cd ..
```

**Optional**: Use a virtual environment (recommended):
```bash
cd acr-agents
python -m venv venv
source venv/bin/activate  # On macOS/Linux
# or
venv\Scripts\activate  # On Windows
pip install -r requirements.txt
cd ..
```

### For Federated Learning Module

```bash
cd acr-federated-ml
pip install -r requirements.txt
cd ..
```

**Note**: Installing PyTorch and MONAI may take several minutes.

## Step 4: Environment Configuration

```bash
# Copy the example environment file
cp .env.example .env

# Edit with your preferred editor
nano .env  # or vim, code, etc.
```

### Required Environment Variables

Update these in `.env`:

```bash
# Database (if using local PostgreSQL)
DATABASE_URL=postgresql://acr_user:your_password@localhost:5432/acr_platform

# JWT Secret (generate a random string)
JWT_SECRET=your-super-secret-jwt-key-change-this

# CORS (for development, use localhost)
CORS_ORIGIN=http://localhost:3001

# RSK Blockchain (testnet for development)
RSK_RPC_URL=https://public-node.testnet.rsk.co
RSK_CHAIN_ID=31
```

## Step 5: Start Services

### Option A: Using Docker Compose (Recommended)

```bash
# Start all services (PostgreSQL, Redis, IPFS, API, Web Portal)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Option B: Manual Start (Development)

**Terminal 1 - Start PostgreSQL & Redis:**
```bash
# If you have them installed locally
# Start PostgreSQL
pg_ctl -D /usr/local/var/postgres start

# Start Redis
redis-server
```

**Terminal 2 - Start API Gateway:**
```bash
cd acr-api-gateway
npm run dev
```

**Terminal 3 - Start Web Portal:**
```bash
cd acr-web-portal
npm run dev
```

**Terminal 4 - Start Agent System:**
```bash
cd acr-agents
source venv/bin/activate  # If using virtual environment
python src/base_agent.py
```

## Step 6: Deploy Smart Contracts (Optional)

If you want to use blockchain features:

```bash
cd acr-blockchain

# Install dependencies
npm install

# Add your private key to .env
echo "PRIVATE_KEY=your_private_key_here" >> .env

# Deploy to RSK testnet
npm run deploy:testnet
```

**Note**: You'll need RSK testnet RBTC. Get free testnet tokens from:
https://faucet.rsk.co

## Step 7: Verify Installation

### Check API Gateway
```bash
curl http://localhost:3000/health
```

Expected response:
```json
{
  "status": "healthy",
  "timestamp": "2025-11-13T...",
  "version": "0.8.0"
}
```

### Check Web Portal
Open browser to: http://localhost:3001

### Check Docker Services
```bash
docker-compose ps
```

All services should show "Up" status.

## Common Issues & Solutions

### Issue 1: Port Already in Use

```bash
# Find process using port 3000
lsof -i :3000

# Kill the process
kill -9 <PID>
```

### Issue 2: Python Package Installation Fails

Try using a virtual environment:
```bash
python -m venv venv
source venv/bin/activate
pip install --upgrade pip
pip install -r requirements.txt
```

### Issue 3: Docker Permission Denied

On macOS/Linux:
```bash
sudo usermod -aG docker $USER
newgrp docker
```

### Issue 4: PostgreSQL Connection Failed

Update `DATABASE_URL` in `.env` with correct credentials.

Or use Docker Compose which handles this automatically.

### Issue 5: npm install peer dependency errors

Always use:
```bash
npm install --legacy-peer-deps
```

## Development Workflow

### Running Tests

```bash
# Run all tests
npm test

# Run specific module tests
cd acr-core && npm test
cd acr-api-gateway && npm test

# Python tests
cd acr-agents && pytest
cd acr-federated-ml && pytest
```

### Linting and Formatting

```bash
# Lint all TypeScript code
npm run lint

# Format all code
npm run format
```

### Building for Production

```bash
# Build all packages
npm run build

# Build Docker images
docker-compose build
```

## Next Steps

1. **Read the Documentation**: Check `docs/architecture.md` for system overview
2. **Explore the Ontology**: Open `acr-ontology/ACR.owl` in Protégé
3. **Review Smart Contracts**: Check `acr-blockchain/contracts/`
4. **Test the API**: Use Postman or curl to test endpoints
5. **Deploy to Kubernetes**: See `docs/deployment/kubernetes.md`

## Getting Help

- **Documentation**: See `docs/` directory
- **Issues**: Report bugs on GitHub
- **Architecture Questions**: See `docs/architecture.md`

## Quick Commands Reference

```bash
# Install everything
npm install --legacy-peer-deps

# Start development environment
docker-compose up -d

# Start API (without Docker)
cd acr-api-gateway && npm run dev

# Start Web Portal (without Docker)
cd acr-web-portal && npm run dev

# Deploy contracts
cd acr-blockchain && npm run deploy:testnet

# Run tests
npm test

# Clean and reinstall
npm run clean && npm install --legacy-peer-deps
```

---

**Version**: 0.8.0
**Last Updated**: November 13, 2025
