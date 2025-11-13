FROM python:3.10-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    libgomp1 \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements and install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY src/ ./src/
COPY ../acr-ontology/ ./ontology/

# Set environment variables
ENV PYTHONUNBUFFERED=1
ENV ONTOLOGY_PATH=/app/ontology/ACR.owl

# Expose ports for agent communication
EXPOSE 8001 8002 8003 8004

# Run the base agent system
CMD ["python", "src/base_agent.py"]
