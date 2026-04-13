// generate-swrl-languages.js
// Complete SWRL/SQWRL multi-language generator
// Run from: /dapp/acragent/final_ftp/
// Output: /lang/SWRL_rules/{lang}/rules.json

const fs = require('fs');
const path = require('path');

// Complete rules database with all 8 language translations
const completeRulesDatabase = {
    swrlRules: [
        {
            id: 1,
            category: "biomarker",
            technical: "Patient(?p) ^ hasERStatus(?p, Positive) ^ hasPRStatus(?p, Positive) ^ hasHER2Status(?p, Negative) ^ hasKi67(?p, ?k) ^ swrlb:lessThan(?k, 20) -> hasMolecularSubtype(?p, LuminalA)",
            translations: {
                en: "If ER and PR are positive and HER2 is negative and Ki-67 < 20%, classify as Luminal A subtype",
                zh: "如果ER和PR均为阳性且HER2为阴性且Ki-67 < 20%，分类为Luminal A亚型",
                fr: "Si ER et PR sont positifs et HER2 est négatif et Ki-67 < 20%, classer comme sous-type Luminal A",
                de: "Wenn ER und PR positiv sind und HER2 negativ ist und Ki-67 < 20%, als Luminal-A-Subtyp klassifizieren",
                ja: "ERとPRが陽性でHER2が陰性、かつKi-67 < 20%の場合、Luminal Aサブタイプとして分類",
                ko: "ER과 PR이 양성이고 HER2가 음성이며 Ki-67 < 20%인 경우, Luminal A 아형으로 분류",
                ru: "Если ER и PR положительные и HER2 отрицательный и Ki-67 < 20%, классифицировать как подтип Luminal A",
                ar: "إذا كان ER وPR إيجابيين وHER2 سلبي وKi-67 < 20%، صنف كنوع Luminal A"
            }
        },
        {
            id: 2,
            category: "biomarker", 
            technical: "Patient(?p) ^ hasERStatus(?p, Positive) ^ hasPRStatus(?p, Positive) ^ hasHER2Status(?p, Negative) ^ hasKi67(?p, ?k) ^ swrlb:greaterThanOrEqual(?k, 20) -> hasMolecularSubtype(?p, LuminalB)",
            translations: {
                en: "If ER and PR are positive and HER2 is negative and Ki-67 ≥ 20%, classify as Luminal B subtype",
                zh: "如果ER和PR均为阳性且HER2为阴性且Ki-67 ≥ 20%，分类为Luminal B亚型",
                fr: "Si ER et PR sont positifs et HER2 est négatif et Ki-67 ≥ 20%, classer comme sous-type Luminal B",
                de: "Wenn ER und PR positiv sind und HER2 negativ ist und Ki-67 ≥ 20%, als Luminal-B-Subtyp klassifizieren",
                ja: "ERとPRが陽性でHER2が陰性、かつKi-67 ≥ 20%の場合、Luminal Bサブタイプとして分類",
                ko: "ER과 PR이 양성이고 HER2가 음성이며 Ki-67 ≥ 20%인 경우, Luminal B 아형으로 분류",
                ru: "Если ER и PR положительные и HER2 отрицательный и Ki-67 ≥ 20%, классифицировать как подтип Luminal B", 
                ar: "إذا كان ER وPR إيجابيين وHER2 سلبي وKi-67 ≥ 20%، صنف كنوع Luminal B"
            }
        },
        {
            id: 3,
            category: "biomarker",
            technical: "Patient(?p) ^ hasHER2Status(?p, Positive) -> hasMolecularSubtype(?p, HER2Positive)",
            translations: {
                en: "If HER2 is positive, classify as HER2-positive subtype",
                zh: "如果HER2为阳性，分类为HER2阳性亚型",
                fr: "Si HER2 est positif, classer comme sous-type HER2-positif",
                de: "Wenn HER2 positiv ist, als HER2-positiven Subtyp klassifizieren",
                ja: "HER2が陽性の場合、HER2陽性サブタイプとして分類",
                ko: "HER2가 양성인 경우, HER2 양성 아형으로 분류",
                ru: "Если HER2 положительный, классифицировать как HER2-положительный подтип",
                ar: "إذا كان HER2 إيجابي، صنف كنوع HER2 إيجابي"
            }
        },
        {
            id: 4,
            category: "biomarker",
            technical: "Patient(?p) ^ hasERStatus(?p, Negative) ^ hasPRStatus(?p, Negative) ^ hasHER2Status(?p, Negative) -> hasMolecularSubtype(?p, TripleNegative)",
            translations: {
                en: "If ER, PR, and HER2 are all negative, classify as Triple Negative subtype",
                zh: "如果ER、PR和HER2均为阴性，分类为三阴性亚型",
                fr: "Si ER, PR et HER2 sont tous négatifs, classer comme sous-type Triple Négatif",
                de: "Wenn ER, PR und HER2 alle negativ sind, als Triple-Negativen-Subtyp klassifizieren",
                ja: "ER、PR、HER2がすべて陰性の場合、トリプルネガティブサブタイプとして分類",
                ko: "ER, PR, HER2가 모두 음성인 경우, 삼중 음성 아형으로 분류",
                ru: "Если ER, PR и HER2 все отрицательные, классифицировать как тройной отрицательный подтип",
                ar: "إذا كان ER وPR وHER2 جميعها سلبية، صنف كنوع ثلاثي السلبي"
            }
        },
        {
            id: 5,
            category: "risk",
            technical: "Patient(?p) ^ hasTumorSize(?p, ?size) ^ swrlb:greaterThan(?size, 50) -> hasRiskLevel(?p, High)",
            translations: {
                en: "If tumor size > 5cm, classify as high risk",
                zh: "如果肿瘤大小 > 5厘米，分类为高风险",
                fr: "Si la taille de la tumeur > 5cm, classer comme risque élevé",
                de: "Wenn Tumorgröße > 5cm, als hohes Risiko klassifizieren",
                ja: "腫瘍サイズ＞5cmの場合、高リスクとして分類",
                ko: "종양 크기 > 5cm이면, 고위험으로 분류",
                ru: "Если размер опухоли > 5см, классифицировать как высокий риск",
                ar: "إذا كان حجم الورم > 5 سم، صنف كخطر مرتفع"
            }
        },
        {
            id: 6,
            category: "risk",
            technical: "Patient(?p) ^ hasLymphNodeStatus(?p, Positive) ^ hasPositiveNodes(?p, ?n) ^ swrlb:greaterThan(?n, 3) -> hasRiskLevel(?p, High)",
            translations: {
                en: "If lymph node positive with >3 positive nodes, classify as high risk",
                zh: "如果淋巴结阳性且阳性淋巴结数 > 3，分类为高风险",
                fr: "Si ganglion lymphatique positif avec >3 ganglions positifs, classer comme risque élevé",
                de: "Wenn Lymphknoten positiv mit >3 positiven Knoten, als hohes Risiko klassifizieren",
                ja: "リンパ節陽性で陽性リンパ節数＞3の場合、高リスクとして分類",
                ko: "림프절 양성이고 양성 림프절 수 > 3이면, 고위험으로 분류",
                ru: "Если лимфатический узел положительный с >3 положительными узлами, классифицировать как высокий риск",
                ar: "إذا كانت العقد الليمفاوية إيجابية مع >3 عقد إيجابية، صنف كخطر مرتفع"
            }
        },
        {
            id: 7,
            category: "risk",
            technical: "Patient(?p) ^ hasGrade(?p, 3) -> hasRiskLevel(?p, High)",
            translations: {
                en: "If tumor grade is 3, classify as high risk",
                zh: "如果肿瘤分级为3级，分类为高风险",
                fr: "Si le grade de la tumeur est 3, classer comme risque élevé",
                de: "Wenn Tumorgrad 3 ist, als hohes Risiko klassifizieren",
                ja: "腫瘍グレードが3の場合、高リスクとして分類",
                ko: "종양 등급이 3이면, 고위험으로 분류",
                ru: "Если степень опухоли 3, классифицировать как высокий риск",
                ar: "إذا كانت درجة الورم 3، صنف كخطر مرتفع"
            }
        },
        {
            id: 8,
            category: "risk",
            technical: "Patient(?p) ^ hasLVIStatus(?p, Positive) -> hasRiskLevel(?p, Intermediate)",
            translations: {
                en: "If lymphovascular invasion present, classify as intermediate risk",
                zh: "如果存在淋巴血管侵犯，分类为中风险",
                fr: "Si invasion lymphovasculaire présente, classer comme risque intermédiaire",
                de: "Wenn lymphovaskuläre Invasion vorhanden ist, als mittleres Risiko klassifizieren",
                ja: "リンパ管侵襲が存在する場合、中リスクとして分類",
                ko: "림프관 침범이 있으면, 중간 위험으로 분류",
                ru: "Если присутствует лимфоваскулярная инвазия, классифицировать как промежуточный риск",
                ar: "إذا كان هناك غزو وعائي لمفي، صنف كخطر متوسط"
            }
        },
        {
            id: 9,
            category: "treatment",
            technical: "Patient(?p) ^ hasMolecularSubtype(?p, LuminalA) -> recommendTreatment(?p, EndocrineTherapy)",
            translations: {
                en: "If Luminal A subtype, recommend endocrine therapy",
                zh: "如果是Luminal A亚型，推荐内分泌治疗",
                fr: "Si sous-type Luminal A, recommander la thérapie endocrinienne",
                de: "Wenn Luminal-A-Subtyp, endokrine Therapie empfehlen",
                ja: "Luminal Aサブタイプの場合、内分泌療法を推奨",
                ko: "Luminal A 아형인 경우, 내분비 치료 권장",
                ru: "Если подтип Luminal A, рекомендовать эндокринную терапию",
                ar: "إذا كان النوع Luminal A، أوص بالعلاج الهرموني"
            }
        },
        {
            id: 10,
            category: "treatment",
            technical: "Patient(?p) ^ hasMolecularSubtype(?p, LuminalB) -> recommendTreatment(?p, ChemotherapyPlusEndocrine)",
            translations: {
                en: "If Luminal B subtype, recommend chemotherapy plus endocrine therapy",
                zh: "如果是Luminal B亚型，推荐化疗加内分泌治疗",
                fr: "Si sous-type Luminal B, recommander chimiothérapie plus thérapie endocrinienne",
                de: "Wenn Luminal-B-Subtyp, Chemotherapie plus endokrine Therapie empfehlen",
                ja: "Luminal Bサブタイプの場合、化学療法＋内分泌療法を推奨",
                ko: "Luminal B 아형인 경우, 화학요법 더하기 내분비 치료 권장",
                ru: "Если подтип Luminal B, рекомендовать химиотерапию плюс эндокринную терапию",
                ar: "إذا كان النوع Luminal B، أوص بالعلاج الكيميائي بالإضافة إلى العلاج الهرموني"
            }
        },
        {
            id: 11,
            category: "treatment",
            technical: "Patient(?p) ^ hasMolecularSubtype(?p, HER2Positive) -> recommendTreatment(?p, AntiHER2Therapy)",
            translations: {
                en: "If HER2-positive subtype, recommend anti-HER2 therapy",
                zh: "如果是HER2阳性亚型，推荐抗HER2治疗",
                fr: "Si sous-type HER2-positif, recommander la thérapie anti-HER2",
                de: "Wenn HER2-positiver Subtyp, Anti-HER2-Therapie empfehlen",
                ja: "HER2陽性サブタイプの場合、抗HER2療法を推奨",
                ko: "HER2 양성 아형인 경우, 항HER2 치료 권장",
                ru: "Если HER2-положительный подтип, рекомендовать анти-HER2 терапию",
                ar: "إذا كان النوع HER2 إيجابي، أوص بالعلاج المضاد لـHER2"
            }
        },
        {
            id: 12,
            category: "treatment",
            technical: "Patient(?p) ^ hasMolecularSubtype(?p, TripleNegative) -> recommendTreatment(?p, Chemotherapy)",
            translations: {
                en: "If Triple Negative subtype, recommend chemotherapy",
                zh: "如果是三阴性亚型，推荐化疗",
                fr: "Si sous-type Triple Négatif, recommander la chimiothérapie",
                de: "Wenn Triple-Negativer-Subtyp, Chemotherapie empfehlen",
                ja: "トリプルネガティブサブタイプの場合、化学療法を推奨",
                ko: "삼중 음성 아형인 경우, 화학요법 권장",
                ru: "Если тройной отрицательный подтип, рекомендовать химиотерапию",
                ar: "إذا كان النوع ثلاثي السلبي، أوص بالعلاج الكيميائي"
            }
        },
        {
            id: 13,
            category: "imaging",
            technical: "Patient(?p) ^ hasImagingStudy(?p, ?study) ^ hasTextureScore(?study, ?t) ^ swrlb:greaterThan(?t, 0.8) ^ hasShapeIrregularity(?study, ?s) ^ swrlb:greaterThan(?s, 0.75) -> hasRiskLevel(?p, High)",
            translations: {
                en: "If imaging shows texture > 0.8 AND shape irregularity > 0.75, classify as high risk",
                zh: "如果影像显示纹理 > 0.8 且形状不规则性 > 0.75，分类为高风险",
                fr: "Si l'imagerie montre texture > 0,8 ET irrégularité de forme > 0,75, classer comme risque élevé",
                de: "Wenn Bildgebung Textur > 0,8 UND Formunregelmäßigkeit > 0,75 zeigt, als hohes Risiko klassifizieren",
                ja: "画像でテクスチャ＞0.8かつ形状不均一性＞0.75の場合、高リスクとして分類",
                ko: "영상에서 질감 > 0.8이고 형태 불규칙성 > 0.75이면, 고위험으로 분류",
                ru: "Если визуализация показывает текстуру > 0,8 И нерегулярность формы > 0,75, классифицировать как высокий риск",
                ar: "إذا أظهر التصوير ملمس > 0.8 وعدم انتظام الشكل > 0.75، صنف كخطر مرتفع"
            }
        },
        {
            id: 14,
            category: "imaging",
            technical: "Patient(?p) ^ hasImagingStudy(?p, ?study) ^ hasEnhancementScore(?study, ?e) ^ swrlb:greaterThan(?e, 0.9) -> hasSuspicionLevel(?p, High)",
            translations: {
                en: "If enhancement score > 0.9, classify as high suspicion",
                zh: "如果增强评分 > 0.9，分类为高度可疑",
                fr: "Si score de rehaussement > 0,9, classer comme suspicion élevée",
                de: "Wenn Kontrastmittel-Score > 0,9, als hohen Verdacht klassifizieren",
                ja: "増強スコア＞0.9の場合、高疑いとして分類",
                ko: "조영 점수 > 0.9이면, 높은 의심으로 분류",
                ru: "Если оценка усиления > 0,9, классифицировать как высокое подозрение",
                ar: "إذا كانت درجة التعزيز > 0.9، صنف كاشتباه مرتفع"
            }
        },
        {
            id: 15,
            category: "monitoring",
            technical: "Patient(?p) ^ hasRiskLevel(?p, High) -> requiresFollowupInterval(?p, 3)",
            translations: {
                en: "If high risk, require 3-month follow-up interval",
                zh: "如果是高风险，需要3个月随访间隔",
                fr: "Si risque élevé, nécessiter un intervalle de suivi de 3 mois",
                de: "Wenn hohes Risiko, 3-monatiges Nachsorgeintervall erforderlich",
                ja: "高リスクの場合、3ヶ月のフォローアップ間隔を必要とする",
                ko: "고위험인 경우, 3개월 추적 관찰 간격 필요",
                ru: "Если высокий риск, требуется интервал наблюдения 3 месяца",
                ar: "إذا كان خطر مرتفع، يتطلب فاصل متابعة 3 أشهر"
            }
        },
        {
            id: 16,
            category: "monitoring",
            technical: "Patient(?p) ^ hasRiskLevel(?p, Intermediate) -> requiresFollowupInterval(?p, 6)",
            translations: {
                en: "If intermediate risk, require 6-month follow-up interval",
                zh: "如果是中风险，需要6个月随访间隔",
                fr: "Si risque intermédiaire, nécessiter un intervalle de suivi de 6 mois",
                de: "Wenn mittleres Risiko, 6-monatiges Nachsorgeintervall erforderlich",
                ja: "中リスクの場合、6ヶ月のフォローアップ間隔を必要とする",
                ko: "중간 위험인 경우, 6개월 추적 관찰 간격 필요",
                ru: "Если промежуточный риск, требуется интервал наблюдения 6 месяцев",
                ar: "إذا كان خطر متوسط، يتطلب فاصل متابعة 6 أشهر"
            }
        },
        {
            id: 17,
            category: "monitoring",
            technical: "Patient(?p) ^ hasRiskLevel(?p, Low) -> requiresFollowupInterval(?p, 12)",
            translations: {
                en: "If low risk, require 12-month follow-up interval",
                zh: "如果是低风险，需要12个月随访间隔",
                fr: "Si faible risque, nécessiter un intervalle de suivi de 12 mois",
                de: "Wenn niedriges Risiko, 12-monatiges Nachsorgeintervall erforderlich",
                ja: "低リスクの場合、12ヶ月のフォローアップ間隔を必要とする",
                ko: "저위험인 경우, 12개월 추적 관찰 간격 필요",
                ru: "Если низкий риск, требуется интервал наблюдения 12 месяцев",
                ar: "إذا كان خطر منخفض، يتطلب فاصل متابعة 12 شهرًا"
            }
        },
        {
            id: 18,
            category: "genetic",
            technical: "Patient(?p) ^ hasBRCAStatus(?p, Positive) -> recommendGeneticCounseling(?p, true)",
            translations: {
                en: "If BRCA positive, recommend genetic counseling",
                zh: "如果BRCA阳性，推荐遗传咨询",
                fr: "Si BRCA positif, recommander le conseil génétique",
                de: "Wenn BRCA positiv, genetische Beratung empfehlen",
                ja: "BRCA陽性の場合、遺伝カウンセリングを推奨",
                ko: "BRCA 양성인 경우, 유전 상담 권장",
                ru: "Если BRCA положительный, рекомендовать генетическое консультирование",
                ar: "إذا كان BRCA إيجابي، أوص بالاستشارة الوراثية"
            }
        },
        {
            id: 19,
            category: "genetic",
            technical: "Patient(?p) ^ hasFamilyHistory(?p, Strong) -> recommendGeneticTesting(?p, true)",
            translations: {
                en: "If strong family history, recommend genetic testing",
                zh: "如果有强家族史，推荐基因检测",
                fr: "Si antécédents familiaux importants, recommander le test génétique",
                de: "Wenn starke Familienanamnese, genetischen Test empfehlen",
                ja: "強い家族歴がある場合、遺伝子検査を推奨",
                ko: "강한 가족력이 있는 경우, 유전자 검사 권장",
                ru: "Если сильный семейный анамнез, рекомендовать генетическое тестирование",
                ar: "إذا كان هناك تاريخ عائلي قوي، أوص بالفحص الجيني"
            }
        },
        {
            id: 20,
            category: "response",
            technical: "Patient(?p) ^ hasTreatmentResponse(?p, ?resp) ^ swrlb:lessThan(?resp, 0.5) -> requiresTreatmentAdjustment(?p, true)",
            translations: {
                en: "If treatment response < 50%, require treatment adjustment",
                zh: "如果治疗反应 < 50%，需要调整治疗",
                fr: "Si réponse au traitement < 50%, nécessiter un ajustement du traitement",
                de: "Wenn Therapieansprechen < 50%, Therapieanpassung erforderlich",
                ja: "治療反応＜50%の場合、治療調整を必要とする",
                ko: "치료 반응 < 50%인 경우, 치료 조정 필요",
                ru: "Если ответ на лечение < 50%, требуется корректировка лечения",
                ar: "إذا كان استجابة العلاج < 50%، يتطلب تعديل العلاج"
            }
        },
        {
            id: 21,
            category: "response",
            technical: "Patient(?p) ^ hasPathologicalResponse(?p, Complete) -> hasPrognosis(?p, Favorable)",
            translations: {
                en: "If pathological complete response, classify as favorable prognosis",
                zh: "如果病理完全缓解，分类为良好预后",
                fr: "Si réponse pathologique complète, classer comme pronostic favorable",
                de: "Wenn pathologische Komplettremission, als günstige Prognose klassifizieren",
                ja: "病理学的完全奏功の場合、良好な予後として分類",
                ko: "병리학적 완전 반응인 경우, 유리한 예후로 분류",
                ru: "Если патологический полный ответ, классифицировать как благоприятный прогноз",
                ar: "إذا كان الاستجابة المرضية كاملة، صنف كإنذار موات"
            }
        },
        {
            id: 22,
            category: "federated",
            technical: "Patient(?p) ^ hasDataQualityScore(?p, ?q) ^ swrlb:greaterThan(?q, 90) ^ hasConsentStatus(?p, Approved) ^ hasImagingQuality(?p, Standard) -> includeInFederatedLearning(?p, true)",
            translations: {
                en: "Include in federated learning if: data quality > 90%, consent approved, imaging meets standards",
                zh: "如果数据质量 > 90%、同意批准、影像符合标准，则纳入联邦学习",
                fr: "Inclure dans l'apprentissage fédéré si : qualité des données > 90%, consentement approuvé, imagerie répond aux normes",
                de: "In Federated Learning aufnehmen wenn: Datenqualität > 90%, Einwilligung genehmigt, Bildgebung erfüllt Standards",
                ja: "以下の場合に連合学習に含める：データ品質＞90%、同意承認済み、画像が基準を満たす",
                ko: "다음의 경우 연합 학습에 포함: 데이터 품질 > 90%, 동의 승인, 영상 기준 충족",
                ru: "Включить в федеративное обучение если: качество данных > 90%, согласие одобрено, визуализация соответствует стандартам",
                ar: "ضم إلى التعلم الموحد إذا: جودة البيانات > 90%، الموافقة معتمدة، التصوير يفي بالمعايير"
            }
        }
    ],
    
    sqwrlQueries: [
        {
            id: 1,
            technical: "Patient(?p) ^ hasMolecularSubtype(?p, LuminalA) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients with Luminal A subtype",
                zh: "选择Luminal A亚型患者",
                fr: "Sélectionner les patients avec sous-type Luminal A",
                de: "Patienten mit Luminal-A-Subtyp auswählen",
                ja: "Luminal Aサブタイプの患者を選択",
                ko: "Luminal A 아형 환자 선택",
                ru: "Выбрать пациентов с подтипом Luminal A",
                ar: "اختر المرضى بنوع Luminal A"
            }
        },
        {
            id: 2,
            technical: "Patient(?p) ^ hasMolecularSubtype(?p, LuminalB) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients with Luminal B subtype", 
                zh: "选择Luminal B亚型患者",
                fr: "Sélectionner les patients avec sous-type Luminal B",
                de: "Patienten mit Luminal-B-Subtyp auswählen",
                ja: "Luminal Bサブタイプの患者を選択",
                ko: "Luminal B 아형 환자 선택",
                ru: "Выбрать пациентов с подтипом Luminal B",
                ar: "اختر المرضى بنوع Luminal B"
            }
        },
        {
            id: 3,
            technical: "Patient(?p) ^ hasMolecularSubtype(?p, HER2Positive) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients with HER2-positive subtype",
                zh: "选择HER2阳性亚型患者",
                fr: "Sélectionner les patients avec sous-type HER2-positif",
                de: "Patienten mit HER2-positivem Subtyp auswählen",
                ja: "HER2陽性サブタイプの患者を選択",
                ko: "HER2 양성 아형 환자 선택",
                ru: "Выбрать пациентов с HER2-положительным подтипом",
                ar: "اختر المرضى بنوع HER2 إيجابي"
            }
        },
        {
            id: 4,
            technical: "Patient(?p) ^ hasMolecularSubtype(?p, TripleNegative) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients with Triple Negative subtype",
                zh: "选择三阴性亚型患者",
                fr: "Sélectionner les patients avec sous-type Triple Négatif",
                de: "Patienten mit Triple-Negativen-Subtyp auswählen",
                ja: "トリプルネガティブサブタイプの患者を選択",
                ko: "삼중 음성 아형 환자 선택",
                ru: "Выбрать пациентов с тройным отрицательным подтипом",
                ar: "اختر المرضى بنوع ثلاثي السلبي"
            }
        },
        {
            id: 5,
            technical: "Patient(?p) ^ hasRiskLevel(?p, High) -> sqwrl:select(?p)",
            translations: {
                en: "Select high-risk patients",
                zh: "选择高风险患者",
                fr: "Sélectionner les patients à haut risque",
                de: "Hochrisikopatienten auswählen",
                ja: "高リスク患者を選択",
                ko: "고위험 환자 선택",
                ru: "Выбрать пациентов высокого риска",
                ar: "اختر المرضى ذوي الخطورة العالية"
            }
        },
        {
            id: 6,
            technical: "Patient(?p) ^ recommendTreatment(?p, Chemotherapy) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients recommended for chemotherapy",
                zh: "选择推荐化疗的患者",
                fr: "Sélectionner les patients recommandés pour la chimiothérapie",
                de: "Patienten auswählen, für die Chemotherapie empfohlen wird",
                ja: "化学療法が推奨される患者を選択",
                ko: "화학요법이 권장되는 환자 선택",
                ru: "Выбрать пациентов, рекомендованных для химиотерапии",
                ar: "اختر المرضى الموصى لهم بالعلاج الكيميائي"
            }
        },
        {
            id: 7,
            technical: "Patient(?p) ^ recommendTreatment(?p, EndocrineTherapy) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients recommended for endocrine therapy",
                zh: "选择推荐内分泌治疗的患者",
                fr: "Sélectionner les patients recommandés pour la thérapie endocrinienne",
                de: "Patienten auswählen, für die endokrine Therapie empfohlen wird",
                ja: "内分泌療法が推奨される患者を選択",
                ko: "내분비 치료가 권장되는 환자 선택",
                ru: "Выбрать пациентов, рекомендованных для эндокринной терапии",
                ar: "اختر المرضى الموصى لهم بالعلاج الهرموني"
            }
        },
        {
            id: 8,
            technical: "Patient(?p) ^ hasAge(?p, ?age) ^ swrlb:lessThan(?age, 50) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients under 50 years old",
                zh: "选择50岁以下的患者",
                fr: "Sélectionner les patients de moins de 50 ans",
                de: "Patienten unter 50 Jahren auswählen",
                ja: "50歳未満の患者を選択",
                ko: "50세 미만 환자 선택",
                ru: "Выбрать пациентов младше 50 лет",
                ar: "اختر المرضى تحت 50 سنة"
            }
        },
        {
            id: 9,
            technical: "Patient(?p) ^ hasTumorSize(?p, ?size) ^ swrlb:greaterThan(?size, 20) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients with tumor size > 2cm",
                zh: "选择肿瘤大小 > 2厘米的患者",
                fr: "Sélectionner les patients avec taille de tumeur > 2cm",
                de: "Patienten mit Tumorgröße > 2cm auswählen",
                ja: "腫瘍サイズ＞2cmの患者を選択",
                ko: "종양 크기 > 2cm인 환자 선택",
                ru: "Выбрать пациентов с размером опухоли > 2см",
                ar: "اختر المرضى بحجم ورم > 2 سم"
            }
        },
        {
            id: 10,
            technical: "Patient(?p) ^ hasLymphNodeStatus(?p, Positive) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients with positive lymph nodes",
                zh: "选择淋巴结阳性的患者",
                fr: "Sélectionner les patients avec ganglions lymphatiques positifs",
                de: "Patienten mit positiven Lymphknoten auswählen",
                ja: "リンパ節陽性の患者を選択",
                ko: "양성 림프절 환자 선택",
                ru: "Выбрать пациентов с положительными лимфатическими узлами",
                ar: "اختر المرضى بعقد ليمفاوية إيجابية"
            }
        },
        {
            id: 11,
            technical: "Patient(?p) ^ hasBRCAStatus(?p, Positive) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients with BRCA positive status",
                zh: "选择BRCA阳性的患者",
                fr: "Sélectionner les patients avec statut BRCA positif",
                de: "Patienten mit BRCA-positivem Status auswählen",
                ja: "BRCA陽性の患者を選択",
                ko: "BRCA 양성 상태 환자 선택",
                ru: "Выбрать пациентов с положительным статусом BRCA",
                ar: "اختر المرضى بحالة BRCA إيجابية"
            }
        },
        {
            id: 12,
            technical: "Patient(?p) ^ requiresGeneticCounseling(?p, true) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients requiring genetic counseling",
                zh: "选择需要遗传咨询的患者",
                fr: "Sélectionner les patients nécessitant un conseil génétique",
                de: "Patienten auswählen, die genetische Beratung benötigen",
                ja: "遺伝カウンセリングが必要な患者を選択",
                ko: "유전 상담이 필요한 환자 선택",
                ru: "Выбрать пациентов, требующих генетического консультирования",
                ar: "اختر المرضى الذين يحتاجون استشارة وراثية"
            }
        },
        {
            id: 13,
            technical: "Patient(?p) ^ hasTreatmentResponse(?p, ?resp) ^ swrlb:lessThan(?resp, 0.5) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients with treatment response < 50%",
                zh: "选择治疗反应 < 50%的患者",
                fr: "Sélectionner les patients avec réponse au traitement < 50%",
                de: "Patienten mit Therapieansprechen < 50% auswählen",
                ja: "治療反応＜50%の患者を選択",
                ko: "치료 반응 < 50%인 환자 선택",
                ru: "Выбрать пациентов с ответом на лечение < 50%",
                ar: "اختر المرضى باستجابة علاج < 50%"
            }
        },
        {
            id: 14,
            technical: "Patient(?p) ^ hasPathologicalResponse(?p, Complete) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients with pathological complete response",
                zh: "选择病理完全缓解的患者",
                fr: "Sélectionner les patients avec réponse pathologique complète",
                de: "Patienten mit pathologischer Komplettremission auswählen",
                ja: "病理学的完全奏功の患者を選択",
                ko: "병리학적 완전 반응 환자 선택",
                ru: "Выбрать пациентов с патологическим полным ответом",
                ar: "اختر المرضى باستجابة مرضية كاملة"
            }
        },
        {
            id: 15,
            technical: "Patient(?p) ^ includeInFederatedLearning(?p, true) -> sqwrl:select(?p)",
            translations: {
                en: "Select patients eligible for federated learning",
                zh: "选择符合联邦学习条件的患者",
                fr: "Sélectionner les patients éligibles pour l'apprentissage fédéré",
                de: "Patienten auswählen, die für Federated Learning geeignet sind",
                ja: "連合学習の適格患者を選択",
                ko: "연합 학습 적격 환자 선택",
                ru: "Выбрать пациентов, подходящих для федеративного обучения",
                ar: "اختر المرضى المؤهلين للتعلم الموحد"
            }
        }
    ]
};

function generateAllLanguageFiles() {
    console.log('🚀 Generating complete SWRL/SQWRL language files...');
    
    const languages = ['en', 'zh', 'fr', 'de', 'ja', 'ko', 'ru', 'ar'];
    
    languages.forEach(lang => {
        const langDir = path.join(__dirname, 'lang', 'SWRL_rules', lang);
        
        // Create directory if it doesn't exist
        if (!fs.existsSync(langDir)) {
            fs.mkdirSync(langDir, { recursive: true });
        }
        
        // Generate SEPARATE SWRL rules array
        const swrlRules = completeRulesDatabase.swrlRules.map(rule => ({
            id: rule.id,
            category: rule.category,
            technical: rule.technical,
            natural: rule.translations[lang]
        }));
        
        // Generate SEPARATE SQWRL queries array  
        const sqwrlQueries = completeRulesDatabase.sqwrlQueries.map(query => ({
            id: query.id,
            technical: query.technical,
            natural: query.translations[lang]
        }));
        
        // Write SEPARATE SWRL rules file
        const rulesFilePath = path.join(langDir, 'rules.json');
        fs.writeFileSync(rulesFilePath, JSON.stringify(swrlRules, null, 2));
        
        // Write SEPARATE SQWRL queries file
        const queriesFilePath = path.join(langDir, 'queries.json');
        fs.writeFileSync(queriesFilePath, JSON.stringify(sqwrlQueries, null, 2));
        
        console.log(`✅ Generated ${lang}: ${swrlRules.length} SWRL, ${sqwrlQueries.length} SQWRL`);
    });
    
    console.log('\n🎉 All language files generated successfully!');
    console.log('📁 Location: /dapp/acragent/final_ftp/lang/SWRL_rules/{lang}/');
    console.log('📄 Files: rules.json (SWRL) + queries.json (SQWRL)');
    console.log(`📊 Total: 22 SWRL rules + 15 SQWRL queries in 8 languages`);
}

// Run the generation
generateAllLanguageFiles();