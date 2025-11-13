# Running ACR Platform Without Docker

This guide is for running ACR Platform on macOS when Docker is not available or blocked.

## System Requirements

- **macOS**: Sequoia 15.7.2+ (tested)
- **Python**: 3.10 or 3.11 (via pyenv recommended)
- **Node.js**: v18+
- **RAM**: 8GB minimum (16GB recommended for ML training)
- **Homebrew**: For installing system dependencies

## Step 1: Install System Dependencies

```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install PostgreSQL
brew install postgresql@15
brew services start postgresql@15

# Install Redis
brew install redis
brew services start redis

# Verify services are running
brew services list
```

## Step 2: Setup PostgreSQL Database

```bash
# Create database and user
psql postgres -c "CREATE USER acr_user WITH PASSWORD 'your_secure_password';"
psql postgres -c "CREATE DATABASE acr_platform OWNER acr_user;"
psql postgres -c "GRANT ALL PRIVILEGES ON DATABASE acr_platform TO acr_user;"

# Test connection
psql -U acr_user -d acr_platform -c "SELECT version();"
```

## Step 3: Pull Latest Code and Install Dependencies

```bash
cd ~/dapp/acr-platform

# Pull latest fixes
git pull origin claude/acr-platform-codebase-v0.8-011CV3DNWn4kjcUf7LPqaZyv

# Install Node.js dependencies
npm install --legacy-peer-deps

# Install Python dependencies for agents
cd acr-agents
pip install -r requirements.txt
cd ..

# Install Python dependencies for federated ML
cd acr-federated-ml
pip install -r requirements.txt
cd ..
```

## Step 4: Configure Environment Variables

```bash
# Copy example environment file
cp .env.example .env

# Edit with your preferred editor
nano .env
```

**Update these values in `.env`:**

```bash
# API Gateway
PORT=3000
HOST=0.0.0.0
NODE_ENV=development
LOG_LEVEL=info

# Database (use your PostgreSQL credentials)
DATABASE_URL=postgresql://acr_user:your_secure_password@localhost:5432/acr_platform

# Redis (local)
REDIS_URL=redis://localhost:6379

# JWT Secret (generate a random string)
JWT_SECRET=$(openssl rand -base64 32)

# CORS (for local development)
CORS_ORIGIN=http://localhost:3001

# IPFS (optional - can skip for now)
IPFS_URL=http://localhost:5001

# RSK Blockchain (testnet)
RSK_RPC_URL=https://public-node.testnet.rsk.co
RSK_CHAIN_ID=31

# Ontology
ONTOLOGY_PATH=./acr-ontology/ACR.owl

# Differential Privacy
DP_EPSILON=0.7
DP_DELTA=0.000001
```

## Step 5: Initialize Database Schema

```bash
# Create database schema (if using Prisma)
cd acr-api-gateway
npx prisma generate
npx prisma db push
cd ..
```

## Step 6: Start Services Manually

You'll need **4 separate terminal windows** or use **tmux** for better management.

### Terminal 1: API Gateway

```bash
cd ~/dapp/acr-platform/acr-api-gateway
npm run dev
```

Expected output:
```
Server listening on 0.0.0.0:3000
```

### Terminal 2: Web Portal

```bash
cd ~/dapp/acr-platform/acr-web-portal
npm run dev
```

Expected output:
```
Local: http://localhost:3001
```

### Terminal 3: Agent System (Optional)

```bash
cd ~/dapp/acr-platform/acr-agents
python src/base_agent.py
```

Expected output:
```
INFO - Initialized RADIOLOGY agent: radiology_agent
```

### Terminal 4: Monitor Logs

```bash
# Watch API logs
tail -f ~/dapp/acr-platform/acr-api-gateway/logs/*.log

# Or watch Redis
redis-cli monitor
```

## Using tmux for Better Terminal Management

Instead of 4 terminal windows, use tmux:

```bash
# Install tmux
brew install tmux

# Create a new session
tmux new -s acr

# Split windows
# Ctrl+B then % (vertical split)
# Ctrl+B then " (horizontal split)

# Navigate between panes
# Ctrl+B then arrow keys

# Example layout:
# Window 1: API Gateway
# Window 2: Web Portal
# Window 3: Agents
# Window 4: Logs/monitoring
```

Create a tmux startup script:

```bash
# Save as ~/dapp/acr-platform/start-acr.sh
#!/bin/bash

SESSION="acr"

# Create new session
tmux new-session -d -s $SESSION

# Window 1: API Gateway
tmux rename-window -t $SESSION:0 'API'
tmux send-keys -t $SESSION:0 'cd ~/dapp/acr-platform/acr-api-gateway && npm run dev' C-m

# Window 2: Web Portal
tmux new-window -t $SESSION:1 -n 'Web'
tmux send-keys -t $SESSION:1 'cd ~/dapp/acr-platform/acr-web-portal && npm run dev' C-m

# Window 3: Agents
tmux new-window -t $SESSION:2 -n 'Agents'
tmux send-keys -t $SESSION:2 'cd ~/dapp/acr-platform/acr-agents && python src/base_agent.py' C-m

# Window 4: Logs
tmux new-window -t $SESSION:3 -n 'Logs'
tmux send-keys -t $SESSION:3 'redis-cli monitor' C-m

# Attach to session
tmux attach-session -t $SESSION
```

Make it executable and run:

```bash
chmod +x start-acr.sh
./start-acr.sh
```

## Step 7: Verify Everything is Running

### Test API Gateway

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

### Test Web Portal

Open browser: http://localhost:3001

### Test PostgreSQL

```bash
psql -U acr_user -d acr_platform -c "SELECT NOW();"
```

### Test Redis

```bash
redis-cli ping
# Should return: PONG
```

## Performance Optimization for 8GB RAM

Since you have 8GB RAM, here are some optimizations:

### 1. Limit Node.js Memory

```bash
# In each package.json, update dev script:
"dev": "node --max-old-space-size=512 ..."
```

### 2. Disable Heavy ML Components

If not using ML features immediately:

```bash
# Comment out in .env
# Skip agent system for now
# Skip federated learning
```

### 3. Use Lightweight Alternatives

```bash
# Use SQLite instead of PostgreSQL for development
DATABASE_URL=sqlite:./dev.db

# Skip Redis and use in-memory cache
# REDIS_URL=memory://
```

## Troubleshooting

### Issue: Port Already in Use

```bash
# Find and kill process on port 3000
lsof -i :3000
kill -9 <PID>

# Or use a different port
PORT=3001 npm run dev
```

### Issue: PostgreSQL Connection Failed

```bash
# Check if PostgreSQL is running
brew services list

# Restart PostgreSQL
brew services restart postgresql@15

# Check logs
tail -f /usr/local/var/log/postgres.log
```

### Issue: Redis Connection Failed

```bash
# Check if Redis is running
brew services list

# Restart Redis
brew services restart redis

# Test connection
redis-cli ping
```

### Issue: Python Package Conflicts

```bash
# Use virtual environment
cd acr-agents
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

### Issue: Out of Memory

```bash
# Close unused applications
# Increase swap space
# Run only essential services (API + Web, skip agents for now)
```

## Minimal Setup (API + Web Only)

If you want to run only the essential components:

```bash
# Terminal 1: API Gateway only
cd acr-api-gateway
npm run dev

# Terminal 2: Web Portal only
cd acr-web-portal
npm run dev

# Skip agents and federated learning for now
# You can add them later when needed
```

## Stopping Services

### Stop all tmux sessions:
```bash
tmux kill-session -t acr
```

### Stop individual services:
```bash
# Press Ctrl+C in each terminal

# Or kill by port
lsof -i :3000 | grep LISTEN | awk '{print $2}' | xargs kill -9
lsof -i :3001 | grep LISTEN | awk '{print $2}' | xargs kill -9
```

### Stop PostgreSQL and Redis:
```bash
brew services stop postgresql@15
brew services stop redis
```

## Development Tips

### 1. Auto-restart on File Changes

The dev scripts already include auto-restart:
- API Gateway: `ts-node-dev` watches for changes
- Web Portal: `vite` has HMR (Hot Module Replacement)

### 2. Debugging

```bash
# API Gateway with debugging
cd acr-api-gateway
node --inspect src/server.ts

# Then open Chrome DevTools
# chrome://inspect
```

### 3. View Logs

```bash
# API Gateway logs
tail -f acr-api-gateway/logs/*.log

# System logs
log stream --predicate 'process == "node"' --info
```

### 4. Monitor Resource Usage

```bash
# Install htop
brew install htop
htop

# Or use Activity Monitor
open -a "Activity Monitor"
```

## Next Steps

1. ✅ All services running locally
2. ✅ No Docker required
3. ✅ Development environment ready

You can now:
- Access the web portal at http://localhost:3001
- Use the API at http://localhost:3000
- Deploy smart contracts (optional)
- Start developing features

## Quick Reference

```bash
# Start everything (using tmux)
./start-acr.sh

# Start minimal (no tmux)
# Terminal 1: cd acr-api-gateway && npm run dev
# Terminal 2: cd acr-web-portal && npm run dev

# Check health
curl http://localhost:3000/health

# View logs
tail -f acr-api-gateway/logs/*.log

# Stop services
tmux kill-session -t acr
# or Ctrl+C in each terminal

# Stop databases
brew services stop postgresql@15 redis
```

---

**Note**: This setup is optimized for a MacBook Pro with 8GB RAM. For production deployment, use Docker/Kubernetes with more resources.
