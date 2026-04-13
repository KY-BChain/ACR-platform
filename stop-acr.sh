#!/bin/bash

# ACR Platform Stop Script
# Stops all services gracefully

SESSION="acr-platform"

echo "🛑 Stopping ACR Platform..."

# Check if tmux session exists
if ! tmux has-session -t $SESSION 2>/dev/null; then
    echo "ℹ️  No active session found."
else
    echo "🔄 Killing tmux session '$SESSION'..."
    tmux kill-session -t $SESSION
    echo "✅ tmux session stopped"
fi

# Kill any lingering processes on our ports
echo "🔍 Checking for processes on ports 3000-3001..."

for port in 3000 3001; do
    PID=$(lsof -ti :$port)
    if [ ! -z "$PID" ]; then
        echo "   Killing process $PID on port $port"
        kill -9 $PID 2>/dev/null
    fi
done

# Optionally stop PostgreSQL and Redis
read -p "🤔 Stop PostgreSQL and Redis? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🔄 Stopping PostgreSQL..."
    brew services stop postgresql@15

    echo "🔄 Stopping Redis..."
    brew services stop redis

    echo "✅ Database services stopped"
else
    echo "ℹ️  Database services left running"
fi

echo ""
echo "✅ ACR Platform stopped!"
echo ""
echo "📌 To start again: ./start-acr.sh"
echo ""
