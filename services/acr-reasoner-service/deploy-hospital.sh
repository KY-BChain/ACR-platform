#!/bin/bash
# ACR Platform - Hospital Deployment Script
# "DATA STAYS. RULES TRAVEL."

set -e

echo "╔════════════════════════════════════════════════════════════╗"
echo "║   ACR Platform - Hospital Deployment                       ║"
echo "║   'DATA STAYS. RULES TRAVEL.'                               ║"
echo "╚════════════════════════════════════════════════════════════╝"

# Check prerequisites
command -v docker >/dev/null 2>&1 || { echo "❌ Docker not installed"; exit 1; }
command -v docker-compose >/dev/null 2>&1 || { echo "❌ Docker Compose not installed"; exit 1; }

echo "✅ Prerequisites check passed"

# Check .env file
if [ ! -f .env ]; then
    echo "⚠️  .env file not found. Creating from template..."
    cp .env.example .env
    echo "⚠️  IMPORTANT: Edit .env and set DB_PASSWORD and HOSPITAL_ID"
    exit 1
fi

# Pull latest images
echo "📥 Pulling latest ACR Platform images..."
docker-compose pull

# Start services
echo "🚀 Starting ACR Platform services..."
docker-compose up -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 30

# Health check
echo "🏥 Checking service health..."
curl -f http://localhost:8080/api/v1/health || { echo "❌ Reasoner health check failed"; exit 1; }

echo "✅ ACR Platform deployed successfully!"
echo ""
echo "Next steps:"
echo "  1. Test inference: curl -X POST http://localhost:8080/api/v1/infer ..."
echo "  2. View logs: docker-compose logs -f"
echo "  3. Access database: psql -h localhost -U acr_admin -d acr_platform"
