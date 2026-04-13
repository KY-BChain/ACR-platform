// acr_ontology_rules.js - Dynamic SWRL/SQWRL Rules Engine
// Interface to ACR OWL Ontology for clinical decision support

class ACROntologyEngine {
    constructor() {
        this.rules = {};
        this.queries = {};
        this.currentLanguage = 'en';
        this.initialized = false;
        this.languageChangeCallbacks = [];
    }

    async initialize() {
        try {
            // Try to load from ACR OWL Ontology API first
            await this.loadFromOntologyAPI();
        } catch (error) {
            console.warn('Failed to load from ontology API, using static rules:', error);
            // Fallback to static rules
            this.loadStaticRules();
        }
        this.initialized = true;
        
        // Initialize with current language from sessionStorage or default to English
        const savedLanguage = sessionStorage.getItem('selectedLanguage') || 'en';
        this.setLanguage(savedLanguage);
        
        // Listen for language changes
        this.setupLanguageListener();
    }

    setupLanguageListener() {
        // Listen for language change events from the main page
        document.addEventListener('languageChanged', (event) => {
            if (event.detail && event.detail.language) {
                this.setLanguage(event.detail.language);
                this.notifyLanguageChange();
            }
        });

        // Also watch for changes to the language select dropdown
        const languageSelect = document.getElementById('languageSelect');
        if (languageSelect) {
            languageSelect.addEventListener('change', (event) => {
                this.setLanguage(event.target.value);
                this.notifyLanguageChange();
            });
        }
    }

    onLanguageChange(callback) {
        this.languageChangeCallbacks.push(callback);
    }

    notifyLanguageChange() {
        this.languageChangeCallbacks.forEach(callback => {
            callback(this.currentLanguage);
        });
    }

    async loadFromOntologyAPI() {
        // This would interface with your ACR OWL Ontology backend
        const API_BASE_URL = window.location.hostname === 'localhost' 
            ? 'http://localhost:5050/api' 
            : 'https://www.acragent.com/api';

        try {
            // Example API endpoints - adjust based on your actual ontology API
            const [rulesResponse, queriesResponse] = await Promise.all([
                fetch(`${API_BASE_URL}/ontology/swrl-rules`),
                fetch(`${API_BASE_URL}/ontology/sqwrl-queries`)
            ]);

            if (rulesResponse.ok && queriesResponse.ok) {
                this.rules = await rulesResponse.json();
                this.queries = await queriesResponse.json();
            } else {
                throw new Error('Ontology API not available');
            }
        } catch (error) {
            throw new Error(`Ontology API error: ${error.message}`);
        }
    }

    loadStaticRules() {
    // Comprehensive SWRL Rules (22) - English
    this.rules = {
        en: [
            {
                id: 1,
                natural: "If ER and PR are positive and HER2 is negative and Ki-67 < 20%, classify as Luminal A subtype",
                technical: "Patient(?p) ^ hasERStatus(?p, Positive) ^ hasPRStatus(?p, Positive) ^ hasHER2Status(?p, Negative) ^ hasKi67(?p, ?k) ^ swrlb:lessThan(?k, 20) -> hasMolecularSubtype(?p, LuminalA)",
                category: "biomarker"
            },
            {
                id: 2,
                natural: "If ER positive and HER2 positive, classify as Luminal B HER2-positive subtype",
                technical: "Patient(?p) ^ hasERStatus(?p, Positive) ^ hasHER2Status(?p, Positive) -> hasMolecularSubtype(?p, LuminalB_HER2Positive)",
                category: "biomarker"
            },
            {
                id: 3,
                natural: "If ER negative, PR negative, and HER2 positive, classify as HER2-enriched subtype",
                technical: "Patient(?p) ^ hasERStatus(?p, Negative) ^ hasPRStatus(?p, Negative) ^ hasHER2Status(?p, Positive) -> hasMolecularSubtype(?p, HER2Enriched)",
                category: "biomarker"
            },
            {
                id: 4,
                natural: "If ER negative, PR negative, and HER2 negative, classify as Triple Negative subtype",
                technical: "Patient(?p) ^ hasERStatus(?p, Negative) ^ hasPRStatus(?p, Negative) ^ hasHER2Status(?p, Negative) -> hasMolecularSubtype(?p, TripleNegative)",
                category: "biomarker"
            },
            {
                id: 5,
                natural: "If tumor size > 5cm or >4 positive lymph nodes, classify as high risk",
                technical: "Patient(?p) ^ hasTumorSize(?p, ?size) ^ hasPositiveLymphNodes(?p, ?nodes) ^ (swrlb:greaterThan(?size, 50) v swrlb:greaterThan(?nodes, 4)) -> hasRiskLevel(?p, High)",
                category: "risk"
            },
            {
                id: 6,
                natural: "If Luminal A subtype and postmenopausal, recommend endocrine therapy",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, LuminalA) ^ hasMenopausalStatus(?p, Postmenopausal) -> recommendsTherapy(?p, EndocrineTherapy)",
                category: "treatment"
            },
            {
                id: 7,
                natural: "If HER2 positive subtype, recommend anti-HER2 targeted therapy",
                technical: "Patient(?p) ^ (hasMolecularSubtype(?p, LuminalB_HER2Positive) v hasMolecularSubtype(?p, HER2Enriched)) -> recommendsTherapy(?p, AntiHER2Therapy)",
                category: "treatment"
            },
            {
                id: 8,
                natural: "If Triple Negative subtype and tumor > 2cm, recommend chemotherapy",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, TripleNegative) ^ hasTumorSize(?p, ?size) ^ swrlb:greaterThan(?size, 20) -> recommendsTherapy(?p, Chemotherapy)",
                category: "treatment"
            },
            {
                id: 9,
                natural: "If high risk and HER2 positive, recommend dual anti-HER2 therapy",
                technical: "Patient(?p) ^ hasRiskLevel(?p, High) ^ hasHER2Status(?p, Positive) -> recommendsTherapy(?p, DualAntiHER2Therapy)",
                category: "treatment"
            },
            {
                id: 10,
                natural: "If bone metastasis present, recommend bone-targeted therapy",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Bone) -> recommendsTherapy(?p, BoneTargetedTherapy)",
                category: "treatment"
            },
            {
                id: 11,
                natural: "If BRCA mutation positive and Triple Negative, recommend PARP inhibitor",
                technical: "Patient(?p) ^ hasBRCAMutation(?p, Positive) ^ hasMolecularSubtype(?p, TripleNegative) -> recommendsTherapy(?p, PARPInhibitor)",
                category: "treatment"
            },
            {
                id: 12,
                natural: "If age < 40 and high grade tumor, recommend genetic counseling",
                technical: "Patient(?p) ^ hasAge(?p, ?age) ^ hasTumorGrade(?p, High) ^ swrlb:lessThan(?age, 40) -> recommendsAction(?p, GeneticCounseling)",
                category: "risk"
            },
            {
                id: 13,
                natural: "If prior radiotherapy and new skin changes, monitor for radiation recall",
                technical: "Patient(?p) ^ hasPriorTherapy(?p, Radiotherapy) ^ hasSymptom(?p, SkinChanges) -> requiresMonitoring(?p, RadiationRecallMonitoring)",
                category: "monitoring"
            },
            {
                id: 14,
                natural: "If left-sided tumor and receiving anti-HER2 therapy, monitor cardiac function",
                technical: "Patient(?p) ^ hasTumorLaterality(?p, Left) ^ receivesTherapy(?p, AntiHER2Therapy) -> requiresMonitoring(?p, CardiacFunction)",
                category: "monitoring"
            },
            {
                id: 15,
                natural: "If liver metastasis and elevated LFTs, requires dose modification",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Liver) ^ hasLabValue(?p, LFTs, ?value) ^ swrlb:greaterThan(?value, 3) -> requiresAction(?p, DoseModification)",
                category: "treatment"
            },
            {
                id: 16,
                natural: "If symptomatic bone metastasis, recommend palliative radiotherapy",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Bone) ^ hasSymptom(?p, Pain) -> recommendsTherapy(?p, PalliativeRadiotherapy)",
                category: "treatment"
            },
            {
                id: 17,
                natural: "If brain metastasis and HER2 positive, recommend CNS-penetrating therapy",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Brain) ^ hasHER2Status(?p, Positive) -> recommendsTherapy(?p, CNSPenetratingTherapy)",
                category: "treatment"
            },
            {
                id: 18,
                natural: "If PD-L1 positive Triple Negative breast cancer, recommend immunotherapy",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, TripleNegative) ^ hasPDL1Status(?p, Positive) -> recommendsTherapy(?p, Immunotherapy)",
                category: "treatment"
            },
            {
                id: 19,
                natural: "If persistent neutropenia during chemotherapy, requires growth factor support",
                technical: "Patient(?p) ^ receivesTherapy(?p, Chemotherapy) ^ hasLabValue(?p, Neutrophils, ?value) ^ swrlb:lessThan(?value, 1.0) -> requiresAction(?p, GrowthFactorSupport)",
                category: "monitoring"
            },
            {
                id: 20,
                natural: "If premenopausal with hormone receptor positive disease, recommend ovarian suppression",
                technical: "Patient(?p) ^ hasMenopausalStatus(?p, Premenopausal) ^ (hasERStatus(?p, Positive) v hasPRStatus(?p, Positive)) -> recommendsTherapy(?p, OvarianSuppression)",
                category: "treatment"
            },
            {
                id: 21,
                natural: "If pathological complete response after neoadjuvant therapy, continue adjuvant therapy",
                technical: "Patient(?p) ^ receivedTherapy(?p, NeoadjuvantTherapy) ^ hasResponse(?p, PathologicalCompleteResponse) -> recommendsTherapy(?p, AdjuvantTherapy)",
                category: "treatment"
            },
            {
                id: 22,
                natural: "If progression on first-line endocrine therapy, recommend second-line options",
                technical: "Patient(?p) ^ receivedTherapy(?p, FirstLineEndocrineTherapy) ^ hasDiseaseStatus(?p, ProgressiveDisease) -> recommendsTherapy(?p, SecondLineTherapy)",
                category: "treatment"
            }
        ],

        // Chinese Version - 中文版本
        zh: [
            {
                id: 1,
                natural: "如果ER和PR阳性，HER2阴性，且Ki-67 < 20%，分类为Luminal A亚型",
                technical: "Patient(?p) ^ hasERStatus(?p, Positive) ^ hasPRStatus(?p, Positive) ^ hasHER2Status(?p, Negative) ^ hasKi67(?p, ?k) ^ swrlb:lessThan(?k, 20) -> hasMolecularSubtype(?p, LuminalA)",
                category: "biomarker"
            },
            {
                id: 2,
                natural: "如果ER阳性且HER2阳性，分类为Luminal B HER2阳性亚型",
                technical: "Patient(?p) ^ hasERStatus(?p, Positive) ^ hasHER2Status(?p, Positive) -> hasMolecularSubtype(?p, LuminalB_HER2Positive)",
                category: "biomarker"
            },
            {
                id: 3,
                natural: "如果ER阴性，PR阴性，且HER2阳性，分类为HER2富集亚型",
                technical: "Patient(?p) ^ hasERStatus(?p, Negative) ^ hasPRStatus(?p, Negative) ^ hasHER2Status(?p, Positive) -> hasMolecularSubtype(?p, HER2Enriched)",
                category: "biomarker"
            },
            {
                id: 4,
                natural: "如果ER阴性，PR阴性，且HER2阴性，分类为三阴性亚型",
                technical: "Patient(?p) ^ hasERStatus(?p, Negative) ^ hasPRStatus(?p, Negative) ^ hasHER2Status(?p, Negative) -> hasMolecularSubtype(?p, TripleNegative)",
                category: "biomarker"
            },
            {
                id: 5,
                natural: "如果肿瘤大小 > 5cm 或 >4个阳性淋巴结，分类为高风险",
                technical: "Patient(?p) ^ hasTumorSize(?p, ?size) ^ hasPositiveLymphNodes(?p, ?nodes) ^ (swrlb:greaterThan(?size, 50) v swrlb:greaterThan(?nodes, 4)) -> hasRiskLevel(?p, High)",
                category: "risk"
            },
            {
                id: 6,
                natural: "如果Luminal A亚型且绝经后，推荐内分泌治疗",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, LuminalA) ^ hasMenopausalStatus(?p, Postmenopausal) -> recommendsTherapy(?p, EndocrineTherapy)",
                category: "treatment"
            },
            {
                id: 7,
                natural: "如果HER2阳性亚型，推荐抗HER2靶向治疗",
                technical: "Patient(?p) ^ (hasMolecularSubtype(?p, LuminalB_HER2Positive) v hasMolecularSubtype(?p, HER2Enriched)) -> recommendsTherapy(?p, AntiHER2Therapy)",
                category: "treatment"
            },
            {
                id: 8,
                natural: "如果三阴性亚型且肿瘤 > 2cm，推荐化疗",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, TripleNegative) ^ hasTumorSize(?p, ?size) ^ swrlb:greaterThan(?size, 20) -> recommendsTherapy(?p, Chemotherapy)",
                category: "treatment"
            },
            {
                id: 9,
                natural: "如果高风险且HER2阳性，推荐双重抗HER2治疗",
                technical: "Patient(?p) ^ hasRiskLevel(?p, High) ^ hasHER2Status(?p, Positive) -> recommendsTherapy(?p, DualAntiHER2Therapy)",
                category: "treatment"
            },
            {
                id: 10,
                natural: "如果存在骨转移，推荐骨靶向治疗",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Bone) -> recommendsTherapy(?p, BoneTargetedTherapy)",
                category: "treatment"
            },
            {
                id: 11,
                natural: "如果BRCA突变阳性且三阴性，推荐PARP抑制剂",
                technical: "Patient(?p) ^ hasBRCAMutation(?p, Positive) ^ hasMolecularSubtype(?p, TripleNegative) -> recommendsTherapy(?p, PARPInhibitor)",
                category: "treatment"
            },
            {
                id: 12,
                natural: "如果年龄 < 40岁且高级别肿瘤，推荐遗传咨询",
                technical: "Patient(?p) ^ hasAge(?p, ?age) ^ hasTumorGrade(?p, High) ^ swrlb:lessThan(?age, 40) -> recommendsAction(?p, GeneticCounseling)",
                category: "risk"
            },
            {
                id: 13,
                natural: "如果既往放疗且新出现皮肤变化，监测放射性回忆反应",
                technical: "Patient(?p) ^ hasPriorTherapy(?p, Radiotherapy) ^ hasSymptom(?p, SkinChanges) -> requiresMonitoring(?p, RadiationRecallMonitoring)",
                category: "monitoring"
            },
            {
                id: 14,
                natural: "如果左侧肿瘤且接受抗HER2治疗，监测心功能",
                technical: "Patient(?p) ^ hasTumorLaterality(?p, Left) ^ receivesTherapy(?p, AntiHER2Therapy) -> requiresMonitoring(?p, CardiacFunction)",
                category: "monitoring"
            },
            {
                id: 15,
                natural: "如果肝转移且肝功能升高，需要剂量调整",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Liver) ^ hasLabValue(?p, LFTs, ?value) ^ swrlb:greaterThan(?value, 3) -> requiresAction(?p, DoseModification)",
                category: "treatment"
            },
            {
                id: 16,
                natural: "如果有症状的骨转移，推荐姑息性放疗",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Bone) ^ hasSymptom(?p, Pain) -> recommendsTherapy(?p, PalliativeRadiotherapy)",
                category: "treatment"
            },
            {
                id: 17,
                natural: "如果脑转移且HER2阳性，推荐中枢神经系统穿透治疗",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Brain) ^ hasHER2Status(?p, Positive) -> recommendsTherapy(?p, CNSPenetratingTherapy)",
                category: "treatment"
            },
            {
                id: 18,
                natural: "如果PD-L1阳性三阴性乳腺癌，推荐免疫治疗",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, TripleNegative) ^ hasPDL1Status(?p, Positive) -> recommendsTherapy(?p, Immunotherapy)",
                category: "treatment"
            },
            {
                id: 19,
                natural: "如果化疗期间持续中性粒细胞减少，需要生长因子支持",
                technical: "Patient(?p) ^ receivesTherapy(?p, Chemotherapy) ^ hasLabValue(?p, Neutrophils, ?value) ^ swrlb:lessThan(?value, 1.0) -> requiresAction(?p, GrowthFactorSupport)",
                category: "monitoring"
            },
            {
                id: 20,
                natural: "如果绝经前且激素受体阳性疾病，推荐卵巢抑制",
                technical: "Patient(?p) ^ hasMenopausalStatus(?p, Premenopausal) ^ (hasERStatus(?p, Positive) v hasPRStatus(?p, Positive)) -> recommendsTherapy(?p, OvarianSuppression)",
                category: "treatment"
            },
            {
                id: 21,
                natural: "如果新辅助治疗后达到病理完全缓解，继续辅助治疗",
                technical: "Patient(?p) ^ receivedTherapy(?p, NeoadjuvantTherapy) ^ hasResponse(?p, PathologicalCompleteResponse) -> recommendsTherapy(?p, AdjuvantTherapy)",
                category: "treatment"
            },
            {
                id: 22,
                natural: "如果一线内分泌治疗进展，推荐二线治疗方案",
                technical: "Patient(?p) ^ receivedTherapy(?p, FirstLineEndocrineTherapy) ^ hasDiseaseStatus(?p, ProgressiveDisease) -> recommendsTherapy(?p, SecondLineTherapy)",
                category: "treatment"
            }
        ]
    };

    // Comprehensive SQWRL Queries (15) - English
    this.queries = {
        en: [
            {
                id: 1,
                natural: "Select all patients with Luminal A subtype who are candidates for endocrine therapy",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, LuminalA) ^ sqwrl:select(?p)"
            },
            {
                id: 2,
                natural: "Find patients with HER2-positive disease who have not received anti-HER2 therapy",
                technical: "Patient(?p) ^ hasHER2Status(?p, Positive) ^ not receivesTherapy(?p, AntiHER2Therapy) ^ sqwrl:select(?p)"
            },
            {
                id: 3,
                natural: "Count patients with Triple Negative subtype and tumor size > 2cm",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, TripleNegative) ^ hasTumorSize(?p, ?size) ^ swrlb:greaterThan(?size, 20) ^ sqwrl:count(?p)"
            },
            {
                id: 4,
                natural: "List patients with high-risk features requiring intensive monitoring",
                technical: "Patient(?p) ^ hasRiskLevel(?p, High) ^ sqwrl:select(?p)"
            },
            {
                id: 5,
                natural: "Find premenopausal patients eligible for ovarian suppression therapy",
                technical: "Patient(?p) ^ hasMenopausalStatus(?p, Premenopausal) ^ (hasERStatus(?p, Positive) v hasPRStatus(?p, Positive)) ^ sqwrl:select(?p)"
            },
            {
                id: 6,
                natural: "Select patients with bone metastasis who may benefit from bone-targeted agents",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Bone) ^ sqwrl:select(?p)"
            },
            {
                id: 7,
                natural: "Identify patients with BRCA mutations for targeted therapy consideration",
                technical: "Patient(?p) ^ hasBRCAMutation(?p, Positive) ^ sqwrl:select(?p)"
            },
            {
                id: 8,
                natural: "Find patients with brain metastasis requiring specialized treatment approaches",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Brain) ^ sqwrl:select(?p)"
            },
            {
                id: 9,
                natural: "List patients with PD-L1 positive Triple Negative breast cancer for immunotherapy",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, TripleNegative) ^ hasPDL1Status(?p, Positive) ^ sqwrl:select(?p)"
            },
            {
                id: 10,
                natural: "Select patients experiencing treatment-related toxicities requiring intervention",
                technical: "Patient(?p) ^ hasToxicity(?p, ?tox) ^ swrlb:greaterThan(?tox, 2) ^ sqwrl:select(?p)"
            },
            {
                id: 11,
                natural: "Find patients with pathological complete response after neoadjuvant therapy",
                technical: "Patient(?p) ^ receivedTherapy(?p, NeoadjuvantTherapy) ^ hasResponse(?p, PathologicalCompleteResponse) ^ sqwrl:select(?p)"
            },
            {
                id: 12,
                natural: "Identify patients with progressive disease on current therapy line",
                technical: "Patient(?p) ^ hasDiseaseStatus(?p, ProgressiveDisease) ^ sqwrl:select(?p)"
            },
            {
                id: 13,
                natural: "List patients requiring dose modifications due to organ dysfunction",
                technical: "Patient(?p) ^ requiresAction(?p, DoseModification) ^ sqwrl:select(?p)"
            },
            {
                id: 14,
                natural: "Find patients with cardiac risk factors receiving cardiotoxic therapies",
                technical: "Patient(?p) ^ hasCardiacRiskFactor(?p, true) ^ receivesTherapy(?p, ?therapy) ^ hasCardiotoxicityPotential(?therapy, true) ^ sqwrl:select(?p)"
            },
            {
                id: 15,
                natural: "Select patients eligible for clinical trial participation based on molecular profile",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, ?subtype) ^ hasEligibilityCriteria(?p, ClinicalTrial) ^ sqwrl:select(?p)"
            }
        ],

        // Chinese Version - 中文版本
        zh: [
            {
                id: 1,
                natural: "选择所有Luminal A亚型且适合内分泌治疗的患者",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, LuminalA) ^ sqwrl:select(?p)"
            },
            {
                id: 2,
                natural: "查找HER2阳性疾病但未接受抗HER2治疗的患者",
                technical: "Patient(?p) ^ hasHER2Status(?p, Positive) ^ not receivesTherapy(?p, AntiHER2Therapy) ^ sqwrl:select(?p)"
            },
            {
                id: 3,
                natural: "统计三阴性亚型且肿瘤大小 > 2cm的患者数量",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, TripleNegative) ^ hasTumorSize(?p, ?size) ^ swrlb:greaterThan(?size, 20) ^ sqwrl:count(?p)"
            },
            {
                id: 4,
                natural: "列出具有高风险特征需要强化监测的患者",
                technical: "Patient(?p) ^ hasRiskLevel(?p, High) ^ sqwrl:select(?p)"
            },
            {
                id: 5,
                natural: "查找适合卵巢抑制治疗的绝经前患者",
                technical: "Patient(?p) ^ hasMenopausalStatus(?p, Premenopausal) ^ (hasERStatus(?p, Positive) v hasPRStatus(?p, Positive)) ^ sqwrl:select(?p)"
            },
            {
                id: 6,
                natural: "选择可能受益于骨靶向药物的骨转移患者",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Bone) ^ sqwrl:select(?p)"
            },
            {
                id: 7,
                natural: "识别具有BRCA突变需要考虑靶向治疗的患者",
                technical: "Patient(?p) ^ hasBRCAMutation(?p, Positive) ^ sqwrl:select(?p)"
            },
            {
                id: 8,
                natural: "查找需要特殊治疗方法的脑转移患者",
                technical: "Patient(?p) ^ hasMetastasisSite(?p, Brain) ^ sqwrl:select(?p)"
            },
            {
                id: 9,
                natural: "列出PD-L1阳性三阴性乳腺癌适合免疫治疗的患者",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, TripleNegative) ^ hasPDL1Status(?p, Positive) ^ sqwrl:select(?p)"
            },
            {
                id: 10,
                natural: "选择经历治疗相关毒性需要干预的患者",
                technical: "Patient(?p) ^ hasToxicity(?p, ?tox) ^ swrlb:greaterThan(?tox, 2) ^ sqwrl:select(?p)"
            },
            {
                id: 11,
                natural: "查找新辅助治疗后达到病理完全缓解的患者",
                technical: "Patient(?p) ^ receivedTherapy(?p, NeoadjuvantTherapy) ^ hasResponse(?p, PathologicalCompleteResponse) ^ sqwrl:select(?p)"
            },
            {
                id: 12,
                natural: "识别当前治疗方案出现疾病进展的患者",
                technical: "Patient(?p) ^ hasDiseaseStatus(?p, ProgressiveDisease) ^ sqwrl:select(?p)"
            },
            {
                id: 13,
                natural: "列出因器官功能不全需要剂量调整的患者",
                technical: "Patient(?p) ^ requiresAction(?p, DoseModification) ^ sqwrl:select(?p)"
            },
            {
                id: 14,
                natural: "查找具有心脏风险因素且接受心脏毒性治疗的患者",
                technical: "Patient(?p) ^ hasCardiacRiskFactor(?p, true) ^ receivesTherapy(?p, ?therapy) ^ hasCardiotoxicityPotential(?therapy, true) ^ sqwrl:select(?p)"
            },
            {
                id: 15,
                natural: "选择基于分子特征符合临床试验参与资格的患者",
                technical: "Patient(?p) ^ hasMolecularSubtype(?p, ?subtype) ^ hasEligibilityCriteria(?p, ClinicalTrial) ^ sqwrl:select(?p)"
            }
        ]
    };
}

    setLanguage(lang) {
        if (this.rules[lang] && this.queries[lang]) {
            this.currentLanguage = lang;
            // Save to sessionStorage
            sessionStorage.setItem('selectedLanguage', lang);
            console.log(`✅ Ontology engine language changed to: ${lang}`);
            return true;
        }
        console.warn(`Language ${lang} not available, falling back to English`);
        this.currentLanguage = 'en';
        sessionStorage.setItem('selectedLanguage', 'en');
        return false;
    }

    getSWRLRules() {
        return this.rules[this.currentLanguage] || this.rules['en'];
    }

    getSQWRLQueries() {
        return this.queries[this.currentLanguage] || this.queries['en'];
    }

    getRuleCount() {
        const rules = this.getSWRLRules();
        return rules ? rules.length : 0;
    }

    getQueryCount() {
        const queries = this.getSQWRLQueries();
        return queries ? queries.length : 0;
    }
}

// Create global instance
window.ACROntologyEngine = new ACROntologyEngine();

// Legacy global variable for backward compatibility
window.ONTOLATOR_RULES = {
    swrlRules: [],
    sqwrlQueries: []
};

// Function to refresh the rules display when language changes
function refreshOntologyDisplay() {
    if (window.ONTOLATOR_RULES && window.populateSWRLRules && window.populateSQWRLQueries) {
        window.ONTOLATOR_RULES.swrlRules = window.ACROntologyEngine.getSWRLRules();
        window.ONTOLATOR_RULES.sqwrlQueries = window.ACROntologyEngine.getSQWRLQueries();
        
        window.populateSWRLRules();
        window.populateSQWRLQueries();
        
        console.log('🔄 Ontology display refreshed for language:', window.ACROntologyEngine.currentLanguage);
    }
}

// Initialize and populate legacy variable  
window.ACROntologyEngine.initialize().then(() => {
    window.ONTOLATOR_RULES.swrlRules = ACROntologyEngine.getSWRLRules();
    window.ONTOLATOR_RULES.sqwrlQueries = ACROntologyEngine.getSQWRLQueries();
    
    // Listen for language changes
    ACROntologyEngine.onLanguageChange(() => {
        refreshOntologyDisplay();
    });
    
    console.log('✅ ACR Ontology Engine initialized with', 
        window.ONTOLATOR_RULES.swrlRules.length, 'SWRL rules and',
        window.ONTOLATOR_RULES.sqwrlQueries.length, 'SQWRL queries in', 
        ACROntologyEngine.currentLanguage);
}).catch(error => {
    console.error('❌ Failed to initialize ACR Ontology Engine:', error);
});