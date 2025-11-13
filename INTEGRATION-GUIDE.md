# ACR Platform - Integration Guide

This guide explains how to integrate your existing ACR components into the platform scaffold.

## 🌳 Branch Structure

```
ACR-platform/
├── main (empty - awaiting final merge)
├── claude/acr-platform-codebase-v0.8-... (scaffold - DO NOT MODIFY)
├── develop (integration branch - merge features here)
├── feature/ontology-integration (your ACR.owl + SWRL + SQWRL)
├── feature/website-integration (your 6-page website)
└── feature/database-integration (SQLite databases + demo data)
```

## 📋 Integration Phases

### Phase 1: Ontology Integration (PRIORITY 1) ✨

Your ACR OWL Ontology with SWRL and SQWRL rules is the foundation of the diagnostic reasoning system.

**Steps:**

1. Switch to the ontology integration branch:
   ```bash
   git checkout feature/ontology-integration
   ```

2. Replace placeholder files with your real ontology:
   ```bash
   # Backup the placeholder files first
   mv acr-ontology/ACR.owl acr-ontology/ACR.owl.backup

   # Copy your ontology file
   cp /path/to/your/ACR.owl acr-ontology/ACR.owl

   # Copy your SWRL rules
   cp /path/to/your/swrl/rules/* acr-ontology/swrl_rules/

   # Copy your SQWRL queries
   cp /path/to/your/sqwrl/queries/* acr-ontology/sqwrl_queries/
   ```

3. Update the ontology mapping:
   ```bash
   # Edit this file to reflect your actual classes, properties, and rules
   nano acr-ontology/ontology_mapping.json
   ```

4. Test the ontology:
   ```bash
   cd acr-agents
   python -c "
   from owlready2 import get_ontology
   onto = get_ontology('../acr-ontology/ACR.owl').load()
   print('Ontology loaded successfully!')
   print(f'Classes: {len(list(onto.classes()))}')
   print(f'Properties: {len(list(onto.properties()))}')
   "
   ```

5. Commit your changes:
   ```bash
   git add acr-ontology/
   git commit -m "feat: Integrate production ACR OWL ontology with SWRL/SQWRL rules"
   ```

**What to provide:**
- ACR.owl file
- All SWRL rule files (.swrl)
- All SQWRL query files (.sqwrl)
- Documentation of your ontology classes and properties

---

### Phase 2: Website Integration (PRIORITY 2) 🌐

Your 6-page website provides the user interface for the platform.

**Steps:**

1. Switch to the website integration branch:
   ```bash
   git checkout feature/website-integration
   ```

2. Understand your current website structure:
   ```
   Your Website Pages:
   1. Login/Registration
   2. Dashboard
   3. Case View
   4. [Page 4 name?]
   5. [Page 5 name?]
   6. [Page 6 name?]
   ```

3. Map your pages to the ACR structure:
   ```bash
   acr-web-portal/src/
   ├── pages/
   │   ├── Login.tsx           # Your login page → React component
   │   ├── Dashboard.tsx       # Your dashboard → React component
   │   ├── CaseView.tsx        # Your case view → React component
   │   ├── [YourPage4].tsx
   │   ├── [YourPage5].tsx
   │   └── [YourPage6].tsx
   ```

4. **Integration Options:**

   **Option A: Port to React (Recommended for long-term)**
   - Convert your HTML/CSS/JS pages to React components
   - Use the existing TypeScript types from `acr-core`
   - Connect to the API endpoints

   **Option B: Embed as Static Pages (Quick integration)**
   - Keep your existing HTML/CSS/JS files
   - Place them in `acr-web-portal/public/`
   - Create React wrappers that load them

5. Connect to API:
   ```typescript
   // Example: src/services/api.ts
   import axios from 'axios';

   const API_BASE = 'http://localhost:3000';

   export const getCases = async () => {
     const response = await axios.get(`${API_BASE}/api/cases`);
     return response.data;
   };
   ```

6. Commit your changes:
   ```bash
   git add acr-web-portal/
   git commit -m "feat: Integrate production 6-page website"
   ```

**What to provide:**
- Your 6 HTML/CSS/JS page files
- Any assets (images, styles, scripts)
- Description of what each page does
- Current routing structure

---

### Phase 3: Database Integration (PRIORITY 3) 💾

Your SQLite databases contain real data and user authentication.

**Steps:**

1. Switch to the database integration branch:
   ```bash
   git checkout feature/database-integration
   ```

2. Add your databases to the project:
   ```bash
   # Create data directory
   mkdir -p data

   # Copy your databases
   cp /path/to/acr_clinical_trail.db data/
   cp /path/to/user.db data/

   # Add to .gitignore (don't commit sensitive data)
   echo "data/*.db" >> .gitignore
   ```

3. Create SQLite adapter for the API:
   ```bash
   # Create adapter file
   nano acr-api-gateway/src/db/sqlite-adapter.ts
   ```

   ```typescript
   import Database from 'better-sqlite3';

   export class SQLiteAdapter {
     private clinicalDb: Database.Database;
     private userDb: Database.Database;

     constructor() {
       this.clinicalDb = new Database('../../data/acr_clinical_trail.db');
       this.userDb = new Database('../../data/user.db');
     }

     // Methods to query your databases
     getClinicalRecords() {
       return this.clinicalDb.prepare('SELECT * FROM your_table').all();
     }

     getUserByEmail(email: string) {
       return this.userDb.prepare('SELECT * FROM users WHERE email = ?').get(email);
     }
   }
   ```

4. Update API routes to use SQLite:
   ```bash
   nano acr-api-gateway/src/routes/cases.ts
   # Import and use the SQLiteAdapter
   ```

5. Update .env to use SQLite:
   ```bash
   # Instead of PostgreSQL
   DATABASE_TYPE=sqlite
   CLINICAL_DB_PATH=./data/acr_clinical_trail.db
   USER_DB_PATH=./data/user.db
   ```

6. Commit your changes:
   ```bash
   git add acr-api-gateway/src/db/
   git add data/.gitkeep  # Keep directory in git
   git commit -m "feat: Integrate SQLite databases for clinical data and user auth"
   ```

**What to provide:**
- Schema of both SQLite databases (table structures)
- Sample queries you currently use
- Description of the 200 demo records structure
- How EmailJS integration works

---

## 🔄 Merging to Develop

After each feature is complete and tested:

```bash
# Switch to develop branch
git checkout develop

# Merge the feature
git merge feature/ontology-integration
# Test thoroughly

git merge feature/website-integration
# Test thoroughly

git merge feature/database-integration
# Test thoroughly

# When everything works together
git checkout main
git merge develop
```

---

## 🎯 What I Need From You

To help integrate your existing work, please provide:

### 1. Ontology Files
- [ ] ACR.owl file
- [ ] All SWRL rule files
- [ ] All SQWRL query files
- [ ] Brief description of main classes and properties

### 2. Website Files
- [ ] All 6 page files (HTML/CSS/JS or framework used)
- [ ] Description of each page's purpose
- [ ] Any dependencies (libraries, frameworks)
- [ ] Screenshots or workflow description

### 3. Database Files
- [ ] Schema export of acr_clinical_trail.db
- [ ] Schema export of user.db
- [ ] Sample query examples
- [ ] Description of the 200 demo records

### 4. Integration Preferences
- [ ] Do you want to keep SQLite or migrate to PostgreSQL?
- [ ] Is your website React, or another framework?
- [ ] What's your priority order for integration?

---

## 📞 Next Steps

1. **Start with Ontology** (most critical)
   - Share your ACR.owl and SWRL/SQWRL files
   - We'll integrate them into the platform
   - Test reasoning with the agents

2. **Then Website**
   - Share your 6 pages
   - Decide on integration approach
   - Connect to API

3. **Finally Databases**
   - Share schemas
   - Create API adapters
   - Test with your 200 demo records

---

## 🛠️ Current Working State

✅ Platform scaffold running
✅ API Gateway: http://localhost:3000
✅ Web Portal: http://localhost:5174
✅ Branches created for integration
⏳ Ready for your existing components

**Let's start with the ontology integration!**

Share your ACR.owl file and SWRL/SQWRL rules, and I'll help integrate them properly into the platform. 🚀
