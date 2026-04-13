# ACR Platform - Openllet Reasoner Microservice

**Version:** 2.1.0  
**Architecture:** Distributed DAPP - "DATA STAYS. RULES TRAVEL."  
**License:** Apache 2.0 (Open Source Core)

---

## 🎯 Purpose

The **Openllet Reasoner Microservice** is the core clinical reasoning engine of the ACR Platform. It provides guideline-based clinical decision support for medical AI applications.

**Key Features:**
- ✅ Domain-agnostic OWL/SWRL reasoning
- ✅ Edge computing deployment (on-premise)
- ✅ Privacy-preserving (patient data never leaves hospital)
- ✅ GDPR/HIPAA/Chinese data localization compliant
- ✅ Horizontally scalable (distributed deployment)
- ✅ Modular (pluggable ontologies for different medical domains)

---

## 🏗️ Architecture Principle

### **"DATA STAYS. RULES TRAVEL."**

```
┌─────────────────────────────────────────────┐
│  Hospital A (Dublin)                        │
│  ├─ Patient Data (stays here)              │
│  └─ ACR Reasoner Container (deployed here) │ ← RULES travel here
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│  Hospital B (Zhengzhou)                     │
│  ├─ Patient Data (stays here)              │
│  └─ ACR Reasoner Container (deployed here) │ ← RULES travel here
└─────────────────────────────────────────────┘
```

**NOT centralized processing:**
- ❌ Patient data sent to central server
- ✅ Reasoner deployed to where data is
- ✅ Processing happens locally at hospital
- ✅ Only aggregated gradients shared (federated learning)

---

## 🚀 Quick Start

### **Prerequisites**

- Docker Engine 20.10+
- Docker Compose 2.0+
- 2 GB RAM minimum (4 GB recommended)
- 10 GB disk space

### **Deploy Complete Hospital Stack**

```bash
# 1. Configure environment
cp .env.example .env
# Edit .env and set DB_PASSWORD, HOSPITAL_ID

# 2. Start all services
docker-compose up -d

# 3. Verify deployment
docker-compose ps
docker-compose logs -f openllet-reasoner
```

### **Test Clinical Inference**

```bash
curl -X POST http://localhost:8080/api/v1/infer \
  -H "Content-Type: application/json" \
  -d '{
    "age": 55,
    "er": 90,
    "pr": 80,
    "her2": "阴性",
    "ki67": 10
  }'

# Expected response:
# {
#   "molecularSubtype": "LuminalA",
#   "executionPath": "PRIMARY",
#   "inferenceTimeMs": 150
# }
```

---

## 📦 Deployment Options

### **Option 1: Docker Compose (Recommended)**

Best for: 1-10 concurrent users, single server

```bash
docker-compose up -d
```

### **Option 2: Kubernetes**

Best for: 100+ concurrent users, high availability

```bash
kubectl apply -f hospital-deployment.yaml
```

### **Option 3: Standalone JAR**

Best for: Development/testing

```bash
mvn clean package
java -jar target/openllet-reasoner-2.1.0.jar
```

---

## 🔧 Configuration

### **Switch Medical Domains**

```bash
# Via environment variable
export ACR_ONTOLOGY_DOMAIN=lung-cancer

# Via docker-compose
environment:
  - ACR_ONTOLOGY_DOMAIN=lung-cancer
```

### **Update Ontology (Zero Downtime)**

```bash
# Copy new ontology files
cp ACR_Ontology_v2_2.owl ./ontologies/breast-cancer/

# Restart reasoner
docker-compose restart openllet-reasoner
```

---

## 🔒 Compliance & Privacy

✅ **GDPR (EU):** Data residency enforced  
✅ **HIPAA (US):** Access controls + audit trails  
✅ **Chinese Data Localization:** On-premise deployment

---

## 📊 Monitoring

```bash
# Health check
curl http://localhost:8080/api/v1/health

# Metrics (Prometheus)
curl http://localhost:8080/actuator/prometheus

# Logs
docker-compose logs -f openllet-reasoner
```

---

## 💰 Commercial Licensing

**Basic (Open Source):** FREE  
**Professional:** $50,000/hospital/year  
**Enterprise:** $200,000/hospital/year  

---

## 🆘 Support

**Community:** https://github.com/KY-BChain/ACR-platform/issues  
**Enterprise:** support@acragent.com

---

**Built with ❤️ for global healthcare. "DATA STAYS. RULES TRAVEL."**