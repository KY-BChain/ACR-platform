#!/bin/bash

# ACR Platform Startup Script (Without Docker)
# For macOS development environment

SESSION="acr-platform"
BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "🚀 Starting ACR Platform..."

# Check if tmux is installed
if ! command -v tmux &> /dev/null; then
    echo "❌ tmux is not installed. Installing via Homebrew..."
    brew install tmux
fi

# Check if PostgreSQL is running
if ! brew services list | grep postgresql@15 | grep started > /dev/null; then
    echo "🔄 Starting PostgreSQL..."
    brew services start postgresql@15
    sleep 2
fi

# Check if Redis is running
if ! brew services list | grep redis | grep started > /dev/null; then
    echo "🔄 Starting Redis..."
    brew services start redis
    sleep 2
fi

# Verify services
echo "✅ Checking services..."
if ! redis-cli ping > /dev/null 2>&1; then
    echo "❌ Redis is not responding"
    exit 1
fi

echo "✅ Redis: Running"

if ! psql -U acr_user -d acr_platform -c "SELECT 1;" > /dev/null 2>&1; then
    echo "⚠️  PostgreSQL connection failed. You may need to create the database first."
    echo "   Run: psql postgres -c \"CREATE DATABASE acr_platform;\""
fi

# Check if session already exists
tmux has-session -t $SESSION 2>/dev/null

if [ $? == 0 ]; then
    echo "⚠️  Session '$SESSION' already exists. Attaching..."
    tmux attach-session -t $SESSION
    exit 0
fi

# Create new tmux session
echo "📦 Creating tmux session..."
tmux new-session -d -s $SESSION -n "API"

# Window 0: API Gateway
echo "🌐 Starting API Gateway..."
tmux send-keys -t $SESSION:0 "cd $BASE_DIR/acr-api-gateway" C-m
tmux send-keys -t $SESSION:0 "echo '🌐 API Gateway - Port 3000'" C-m
tmux send-keys -t $SESSION:0 "npm run dev" C-m

# Window 1: Web Portal
echo "🖥️  Starting Web Portal..."
tmux new-window -t $SESSION:1 -n "Web"
tmux send-keys -t $SESSION:1 "cd $BASE_DIR/acr-web-portal" C-m
tmux send-keys -t $SESSION:1 "echo '🖥️  Web Portal - Port 3001'" C-m
tmux send-keys -t $SESSION:1 "sleep 3 && npm run dev" C-m

# Window 2: Agents (Optional)
echo "🤖 Starting Agent System..."
tmux new-window -t $SESSION:2 -n "Agents"
tmux send-keys -t $SESSION:2 "cd $BASE_DIR/acr-agents" C-m
tmux send-keys -t $SESSION:2 "echo '🤖 Agent System'" C-m
tmux send-keys -t $SESSION:2 "echo 'Press Enter to start agents (or Ctrl+C to skip)'" C-m
tmux send-keys -t $SESSION:2 "# python src/base_agent.py"

# Window 3: Monitoring
echo "📊 Setting up monitoring..."
tmux new-window -t $SESSION:3 -n "Monitor"
tmux send-keys -t $SESSION:3 "cd $BASE_DIR" C-m
tmux send-keys -t $SESSION:3 "echo '📊 System Monitor'" C-m
tmux send-keys -t $SESSION:3 "echo ''" C-m
tmux send-keys -t $SESSION:3 "echo 'Useful commands:'" C-m
tmux send-keys -t $SESSION:3 "echo '  - redis-cli monitor (watch Redis)'" C-m
tmux send-keys -t $SESSION:3 "echo '  - psql -U acr_user -d acr_platform (database)'" C-m
tmux send-keys -t $SESSION:3 "echo '  - curl http://localhost:3000/health (API health)'" C-m
tmux send-keys -t $SESSION:3 "echo '  - htop (system resources)'" C-m
tmux send-keys -t $SESSION:3 "echo ''" C-m

# Select first window
tmux select-window -t $SESSION:0

# Print instructions
echo ""
echo "✅ ACR Platform is starting!"
echo ""
echo "📌 Services:"
echo "   - API Gateway: http://localhost:3000"
echo "   - Web Portal:  http://localhost:3001"
echo "   - PostgreSQL:  localhost:5432"
echo "   - Redis:       localhost:6379"
echo ""
echo "📚 tmux commands:"
echo "   - Ctrl+B then number (0-3): Switch windows"
echo "   - Ctrl+B then d: Detach (services keep running)"
echo "   - Ctrl+B then [: Scroll mode (q to exit)"
echo "   - Ctrl+C in a window: Stop that service"
echo ""
echo "🛑 To stop everything:"
echo "   ./stop-acr.sh"
echo "   or: tmux kill-session -t $SESSION"
echo ""

# Attach to session
sleep 2
tmux attach-session -t $SESSION
