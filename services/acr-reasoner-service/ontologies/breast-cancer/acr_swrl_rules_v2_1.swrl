# acr_swrl_rules_v2_1.swrl
# ===========================================================================
# ACR Platform — SWRL Rule Set v2.1 (OR-split, Openllet-safe)
# ===========================================================================
# Total: 58 rules (R1-R58)
#
# Changes from v2.0:
#   - R2  (OR) split → R2, R3
#   - R7  (OR) split → R8, R9, R10, R11
#   - R8  (OR) split → R12, R13, R14
#   - R9  (OR) split → R15, R16, R17
#   - R11 (OR) split → R19, R20, R21
#   - R14 (OR) split → R24, R25
#   - R15 (OR) split → R26, R27, R28, R29
#   - All illegal OR (∨) syntax removed
#   - 7 original rules → 21 split rules + 37 unchanged = 58 total
#
# Categories:
#   1. Molecular Subtype Classification       (R1-R6)
#   2. Treatment Recommendations (CSCO/NCCN)  (R7-R18)
#   3. MDT Decision Triggers                  (R19-R23)
#   4. Staging and Risk Assessment             (R24-R30)
#   5. Follow-up and Surveillance              (R31-R33)
#   6. Quality Metrics & Guideline Adherence   (R34-R36)
#   7. Imaging Domain                          (R37-R39)
#   8. Pathology Reflex Domain                 (R40-R42)
#   9. Genetics Domain                         (R43-R44)
#  10. Surgery Domain                          (R45-R47)
#  11. Radiotherapy Domain                     (R48-R49)
#  12. Adjuvant Escalation/De-escalation       (R50-R52)
#  13. Safety / Contraindication Domain        (R53-R55)
#  14. Metastatic / Recurrence Sequencing      (R56-R58)
#
# Notes:
#   - All OR logic split into separate rules (plain SWRL has no infix OR).
#   - Chinese clinical terminology per CSCO/CACA/NCCN guidelines.
#   - Validate with clinicians before activation.
# ===========================================================================

@prefix : <https://medical-ai.org/ontologies/ACR#> .

# ═══════════════════════════════════════════════════════════════════════════
# Category 1: Molecular Subtype Classification (R1-R6)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 1: Luminal A Subtype Classification (from v2.0 R1)
Patient(?p) ^ hasER结果标志和百分比(?p, ?er) ^ hasPR结果标志和百分比(?p, ?pr) ^
hasHER2最终解释(?p, "阴性") ^ hasKi-67增殖指数(?p, ?ki67) ^
swrlb:greaterThan(?er, 0) ^ swrlb:greaterThan(?pr, 0) ^ swrlb:lessThan(?ki67, 14)
→ hasMolecularSubtype(?p, "LuminalA")

### Rule 2: Luminal B HER2- — Ki-67 high path (from v2.0 R2, split 1/2)
Patient(?p) ^ hasER结果标志和百分比(?p, ?er) ^ hasHER2最终解释(?p, "阴性") ^
hasKi-67增殖指数(?p, ?ki67) ^ swrlb:greaterThan(?er, 0) ^
swrlb:greaterThanOrEqual(?ki67, 14)
→ hasMolecularSubtype(?p, "LuminalB_HER2neg")

### Rule 3: Luminal B HER2- — PR low path (from v2.0 R2, split 2/2)
Patient(?p) ^ hasER结果标志和百分比(?p, ?er) ^ hasHER2最终解释(?p, "阴性") ^
hasPR结果标志和百分比(?p, ?pr) ^ swrlb:greaterThan(?er, 0) ^
swrlb:lessThanOrEqual(?pr, 20)
→ hasMolecularSubtype(?p, "LuminalB_HER2neg")

### Rule 4: HER2-Enriched Subtype Classification (from v2.0 R3)
Patient(?p) ^ hasER结果标志和百分比(?p, ?er) ^ hasPR结果标志和百分比(?p, ?pr) ^
hasHER2最终解释(?p, "阳性") ^ swrlb:equal(?er, 0) ^ swrlb:equal(?pr, 0)
→ hasMolecularSubtype(?p, "HER2Enriched")

### Rule 5: Triple Negative Subtype Classification (from v2.0 R4)
Patient(?p) ^ hasER结果标志和百分比(?p, ?er) ^ hasPR结果标志和百分比(?p, ?pr) ^
hasHER2最终解释(?p, "阴性") ^ swrlb:equal(?er, 0) ^ swrlb:equal(?pr, 0)
→ hasMolecularSubtype(?p, "TripleNegative")

### Rule 6: Luminal B HER2+ Subtype Classification (from v2.0 R5)
Patient(?p) ^ hasER结果标志和百分比(?p, ?er) ^ hasHER2最终解释(?p, "阳性") ^
swrlb:greaterThan(?er, 0)
→ hasMolecularSubtype(?p, "LuminalB_HER2pos")

# ═══════════════════════════════════════════════════════════════════════════
# Category 2: Treatment Recommendations (R7-R18)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 7: Luminal A Early Stage — Endocrine Therapy Only (from v2.0 R6)
Patient(?p) ^ hasMolecularSubtype(?p, "LuminalA") ^ has临床TNMcTNM(?p, ?tnm) ^
has肿瘤大小最大毫米(?p, ?size) ^ swrlb:lessThanOrEqual(?size, 20) ^
swrlb:contains(?tnm, "N0")
→ recommendTreatment(?p, "内分泌治疗") ^ hasGuidelineSource(?p, :CSCO) ^
hasRecommendationLevel(?p, :CategoryI) ^ treatmentRationale(?p, "小肿瘤、淋巴结阴性的LuminalA型，仅需内分泌治疗")

### Rule 8: HER2+ Neoadjuvant — T2 path (from v2.0 R7, split 1/4)
Patient(?p) ^ hasHER2最终解释(?p, "阳性") ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "T2")
→ recommendTreatment(?p, "新辅助化疗+抗HER2靶向治疗") ^
recommendedRegimen(?p, "THP方案（多西他赛+曲妥珠单抗+帕妥珠单抗）") ^
hasGuidelineSource(?p, :CSCO) ^ hasRecommendationLevel(?p, :CategoryI) ^
treatmentRationale(?p, "HER2阳性T2以上或N+，CSCO推荐新辅助THP方案")

### Rule 9: HER2+ Neoadjuvant — T3 path (from v2.0 R7, split 2/4)
Patient(?p) ^ hasHER2最终解释(?p, "阳性") ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "T3")
→ recommendTreatment(?p, "新辅助化疗+抗HER2靶向治疗") ^
recommendedRegimen(?p, "THP方案（多西他赛+曲妥珠单抗+帕妥珠单抗）") ^
hasGuidelineSource(?p, :CSCO) ^ hasRecommendationLevel(?p, :CategoryI) ^
treatmentRationale(?p, "HER2阳性T2以上或N+，CSCO推荐新辅助THP方案")

### Rule 10: HER2+ Neoadjuvant — N1 path (from v2.0 R7, split 3/4)
Patient(?p) ^ hasHER2最终解释(?p, "阳性") ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "N1")
→ recommendTreatment(?p, "新辅助化疗+抗HER2靶向治疗") ^
recommendedRegimen(?p, "THP方案（多西他赛+曲妥珠单抗+帕妥珠单抗）") ^
hasGuidelineSource(?p, :CSCO) ^ hasRecommendationLevel(?p, :CategoryI) ^
treatmentRationale(?p, "HER2阳性T2以上或N+，CSCO推荐新辅助THP方案")

### Rule 11: HER2+ Neoadjuvant — N2 path (from v2.0 R7, split 4/4)
Patient(?p) ^ hasHER2最终解释(?p, "阳性") ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "N2")
→ recommendTreatment(?p, "新辅助化疗+抗HER2靶向治疗") ^
recommendedRegimen(?p, "THP方案（多西他赛+曲妥珠单抗+帕妥珠单抗）") ^
hasGuidelineSource(?p, :CSCO) ^ hasRecommendationLevel(?p, :CategoryI) ^
treatmentRationale(?p, "HER2阳性T2以上或N+，CSCO推荐新辅助THP方案")

### Rule 12: TNBC PD-L1+ Immunotherapy — T2 path (from v2.0 R8, split 1/3)
Patient(?p) ^ hasMolecularSubtype(?p, "TripleNegative") ^
hasPDL1Status(?p, "阳性") ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "T2")
→ recommendTreatment(?p, "新辅助免疫治疗+化疗") ^
recommendedRegimen(?p, "帕博利珠单抗+化疗") ^
hasGuidelineSource(?p, :CSCO) ^ hasRecommendationLevel(?p, :CategoryI) ^
treatmentRationale(?p, "PD-L1阳性的三阴性乳腺癌，免疫治疗+化疗提高pCR率")

### Rule 13: TNBC PD-L1+ Immunotherapy — T3 path (from v2.0 R8, split 2/3)
Patient(?p) ^ hasMolecularSubtype(?p, "TripleNegative") ^
hasPDL1Status(?p, "阳性") ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "T3")
→ recommendTreatment(?p, "新辅助免疫治疗+化疗") ^
recommendedRegimen(?p, "帕博利珠单抗+化疗") ^
hasGuidelineSource(?p, :CSCO) ^ hasRecommendationLevel(?p, :CategoryI) ^
treatmentRationale(?p, "PD-L1阳性的三阴性乳腺癌，免疫治疗+化疗提高pCR率")

### Rule 14: TNBC PD-L1+ Immunotherapy — N+ path (from v2.0 R8, split 3/3)
Patient(?p) ^ hasMolecularSubtype(?p, "TripleNegative") ^
hasPDL1Status(?p, "阳性") ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "N+")
→ recommendTreatment(?p, "新辅助免疫治疗+化疗") ^
recommendedRegimen(?p, "帕博利珠单抗+化疗") ^
hasGuidelineSource(?p, :CSCO) ^ hasRecommendationLevel(?p, :CategoryI) ^
treatmentRationale(?p, "PD-L1阳性的三阴性乳腺癌，免疫治疗+化疗提高pCR率")

### Rule 15: Luminal B High-Risk Adjuvant — N1 path (from v2.0 R9, split 1/3)
Patient(?p) ^ hasMolecularSubtype(?p, "LuminalB_HER2neg") ^
has临床TNMcTNM(?p, ?tnm) ^ swrlb:contains(?tnm, "N1")
→ recommendTreatment(?p, "辅助化疗+内分泌治疗") ^
recommendedRegimen(?p, "AC-T或TC方案，然后5年内分泌治疗") ^
hasGuidelineSource(?p, :NCCN) ^ hasRecommendationLevel(?p, :CategoryI) ^
treatmentRationale(?p, "高危LuminalB型需化疗+内分泌治疗")

### Rule 16: Luminal B High-Risk Adjuvant — T3 path (from v2.0 R9, split 2/3)
Patient(?p) ^ hasMolecularSubtype(?p, "LuminalB_HER2neg") ^
has临床TNMcTNM(?p, ?tnm) ^ swrlb:contains(?tnm, "T3")
→ recommendTreatment(?p, "辅助化疗+内分泌治疗") ^
recommendedRegimen(?p, "AC-T或TC方案，然后5年内分泌治疗") ^
hasGuidelineSource(?p, :NCCN) ^ hasRecommendationLevel(?p, :CategoryI) ^
treatmentRationale(?p, "高危LuminalB型需化疗+内分泌治疗")

### Rule 17: Luminal B High-Risk Adjuvant — Grade 3 path (from v2.0 R9, split 3/3)
Patient(?p) ^ hasMolecularSubtype(?p, "LuminalB_HER2neg") ^
has组织学分级(?p, ?grade) ^ swrlb:equal(?grade, "3")
→ recommendTreatment(?p, "辅助化疗+内分泌治疗") ^
recommendedRegimen(?p, "AC-T或TC方案，然后5年内分泌治疗") ^
hasGuidelineSource(?p, :NCCN) ^ hasRecommendationLevel(?p, :CategoryI) ^
treatmentRationale(?p, "高危LuminalB型需化疗+内分泌治疗")

### Rule 18: HER2+ Adjuvant Anti-HER2 Duration (from v2.0 R10)
Patient(?p) ^ hasHER2最终解释(?p, "阳性") ^
has计划Or执行的手术类型和日期(?p, ?surgeryDate) ^
recommendTreatment(?p, ?treatment) ^ swrlb:contains(?treatment, "HER2")
→ hasHER2靶向药物和持续时间(?p, "曲妥珠单抗12个月") ^
hasGuidelineSource(?p, :CSCO) ^ hasGuidelineSource(?p, :NCCN) ^
treatmentRationale(?p, "HER2阳性标准抗HER2治疗12个月")

# ═══════════════════════════════════════════════════════════════════════════
# Category 3: MDT Decision Triggers (R19-R23)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 19: Mandatory MDT — TNBC with N2 (from v2.0 R11, split 1/3)
Patient(?p) ^ hasMolecularSubtype(?p, "TripleNegative") ^
has临床TNMcTNM(?p, ?tnm) ^ swrlb:contains(?tnm, "N2")
→ requiresMDT(?p, true) ^ mdtPriority(?p, "高优先级") ^
mdtReason(?p, "复杂病例：三阴性淋巴结转移")

### Rule 20: Mandatory MDT — Grade 3 large tumor (from v2.0 R11, split 2/3)
Patient(?p) ^ has组织学分级(?p, "3") ^
has肿瘤大小最大毫米(?p, ?size) ^ swrlb:greaterThan(?size, 50)
→ requiresMDT(?p, true) ^ mdtPriority(?p, "高优先级") ^
mdtReason(?p, "复杂病例：高级别大肿瘤")

### Rule 21: Mandatory MDT — Metastatic M1 (from v2.0 R11, split 3/3)
Patient(?p) ^ has临床TNMcTNM(?p, ?tnm) ^ swrlb:contains(?tnm, "M1")
→ requiresMDT(?p, true) ^ mdtPriority(?p, "高优先级") ^
mdtReason(?p, "复杂病例：晚期分期")

### Rule 22: MDT Required Before Neoadjuvant Therapy (from v2.0 R12)
Patient(?p) ^ recommendTreatment(?p, ?treatment) ^
swrlb:contains(?treatment, "新辅助")
→ requiresMDT(?p, true) ^ mdtReason(?p, "新辅助治疗前需MDT讨论")

### Rule 23: Young Age MDT Discussion (from v2.0 R13)
Patient(?p) ^ has年龄推导(?p, ?age) ^ swrlb:lessThan(?age, 40) ^
has家族癌症史(?p, ?familyHx) ^ swrlb:contains(?familyHx, "乳腺癌")
→ requiresMDT(?p, true) ^ recommendsGeneticTesting(?p, true) ^
mdtReason(?p, "年轻患者伴家族史，需遗传咨询和MDT讨论")

# ═══════════════════════════════════════════════════════════════════════════
# Category 4: Staging and Risk Assessment (R24-R30)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 24: Early Stage — T1N0M0 path (from v2.0 R14, split 1/2)
Patient(?p) ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "T1N0M0")
→ has总体分期分组(?p, "I期") ^ riskCategory(?p, "低危")

### Rule 25: Early Stage — T1N0 path (from v2.0 R14, split 2/2)
Patient(?p) ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "T1N0")
→ has总体分期分组(?p, "I期") ^ riskCategory(?p, "低危")

### Rule 26: Locally Advanced — T3 path (from v2.0 R15, split 1/4)
Patient(?p) ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "T3")
→ has总体分期分组(?p, "III期") ^ riskCategory(?p, "高危")

### Rule 27: Locally Advanced — T4 path (from v2.0 R15, split 2/4)
Patient(?p) ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "T4")
→ has总体分期分组(?p, "III期") ^ riskCategory(?p, "高危")

### Rule 28: Locally Advanced — N2 path (from v2.0 R15, split 3/4)
Patient(?p) ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "N2")
→ has总体分期分组(?p, "III期") ^ riskCategory(?p, "高危")

### Rule 29: Locally Advanced — N3 path (from v2.0 R15, split 4/4)
Patient(?p) ^ has临床TNMcTNM(?p, ?tnm) ^
swrlb:contains(?tnm, "N3")
→ has总体分期分组(?p, "III期") ^ riskCategory(?p, "高危")

### Rule 30: Metastatic Disease Classification (from v2.0 R16)
Patient(?p) ^ has临床TNMcTNM(?p, ?tnm) ^ swrlb:contains(?tnm, "M1")
→ has总体分期分组(?p, "IV期") ^ requiresPalliativeTreatment(?p, true) ^
treatmentIntent(?p, "姑息治疗")

# ═══════════════════════════════════════════════════════════════════════════
# Category 5: Follow-up and Surveillance (R31-R33)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 31: Standard Follow-up Schedule Years 1-2 (from v2.0 R17)
Patient(?p) ^ has计划Or执行的手术类型和日期(?p, ?surgeryDate) ^
swrlb:subtractDateTimes(?currentDate, ?surgeryDate, ?interval) ^
swrlb:lessThanOrEqual(?interval, 730)
→ has随访计划(?p, "每3-4个月临床检查+年度乳腺X线/超声") ^
followUpFrequency(?p, "每3-4个月")

### Rule 32: Extended Follow-up Schedule Years 3-5 (from v2.0 R18)
Patient(?p) ^ has计划Or执行的手术类型和日期(?p, ?surgeryDate) ^
swrlb:subtractDateTimes(?currentDate, ?surgeryDate, ?interval) ^
swrlb:greaterThan(?interval, 730) ^ swrlb:lessThanOrEqual(?interval, 1825)
→ has随访计划(?p, "每6个月临床检查+年度影像") ^
followUpFrequency(?p, "每6个月")

### Rule 33: Recurrence Detection Alert (from v2.0 R19)
Patient(?p) ^ has复发或进展标志(?p, "是") ^ has复发日期和部位(?p, ?recurrenceDate)
→ requiresUrgentMDT(?p, true) ^ alertType(?p, "复发") ^
alertMessage(?p, "检测到复发，需立即MDT讨论治疗方案")

# ═══════════════════════════════════════════════════════════════════════════
# Category 6: Quality Metrics & Guideline Adherence (R34-R36)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 34: ER+ No Endocrine Therapy Alert (from v2.0 R20)
Patient(?p) ^ hasER结果标志和百分比(?p, ?er) ^ swrlb:greaterThan(?er, 0) ^
has内分泌治疗药物和计划持续时间(?p, ?endocrine) ^ swrlb:equal(?endocrine, "无")
→ guidelineDeviation(?p, true) ^ deviationType(?p, "缺少推荐的内分泌治疗") ^
requiresJustification(?p, true)

### Rule 35: HER2+ Without Anti-HER2 Therapy Alert (from v2.0 R21)
Patient(?p) ^ hasHER2最终解释(?p, "阳性") ^
hasHER2靶向药物和持续时间(?p, ?her2Drug) ^ swrlb:equal(?her2Drug, "无")
→ guidelineDeviation(?p, true) ^ deviationType(?p, "缺少HER2靶向治疗") ^
requiresJustification(?p, true)

### Rule 36: Timely Treatment Initiation Check (from v2.0 R22)
Patient(?p) ^ has活检日期Or时间(?p, ?biopsyDate) ^
has全身治疗开始日期(?p, ?treatmentStart) ^
swrlb:subtractDateTimes(?treatmentStart, ?biopsyDate, ?days) ^
swrlb:greaterThan(?days, 60)
→ timelinessDeviation(?p, true) ^ deviationMessage(?p, "诊断到治疗超过60天") ^
requiresDocumentation(?p, true)

# ═══════════════════════════════════════════════════════════════════════════
# Category 7: Imaging Domain (R37-R39)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 37: BI-RADS 4 biopsy recommendation (from v2.0 R23)
Patient(?p) ^ has影像评估BI-RADS(?p, ?birads) ^ swrlb:contains(?birads, "4") -> has活检建议(?p, "建议粗针穿刺活检") ^ requiresMDT(?p, true)

### Rule 38: BI-RADS 5 urgent biopsy recommendation (from v2.0 R24)
Patient(?p) ^ has影像评估BI-RADS(?p, ?birads) ^ swrlb:contains(?birads, "5") -> has活检建议(?p, "高度怀疑恶性，建议尽快粗针穿刺活检") ^ alertType(?p, "高危影像")

### Rule 39: Imaging/pathology discordance (from v2.0 R25)
Patient(?p) ^ has影像病理一致性(?p, "不一致") -> requiresUrgentMDT(?p, true) ^ has活检建议(?p, "影像病理不一致，建议重复取材或病理复核")

# ═══════════════════════════════════════════════════════════════════════════
# Category 8: Pathology Reflex Domain (R40-R42)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 40: HER2 IHC 2+ without ISH/FISH reflex confirmation (from v2.0 R26)
Patient(?p) ^ hasHER2IHC评分(?p, "2+") ^ hasHER2ISH执行(?p, "否") -> guidelineDeviation(?p, true) ^ deviationType(?p, "HER2 2+ 缺少ISH/FISH复核") ^ requiresJustification(?p, true)

### Rule 41: HER2-low assignment from IHC 1+ (from v2.0 R27)
Patient(?p) ^ hasHER2IHC评分(?p, "1+") -> hasHER2低表达状态(?p, "是")

### Rule 42: HER2-low assignment from IHC 2+ ISH negative (from v2.0 R28)
Patient(?p) ^ hasHER2IHC评分(?p, "2+") ^ hasHER2FISH比率Or结果(?p, ?fish) ^ swrlb:contains(?fish, "阴性") -> hasHER2低表达状态(?p, "是")

# ═══════════════════════════════════════════════════════════════════════════
# Category 9: Genetics Domain (R43-R44)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 43: Young age + family history genetics referral (from v2.0 R29)
Patient(?p) ^ has年龄推导(?p, ?age) ^ swrlb:lessThan(?age, 50) ^ has家族癌症史(?p, ?fhx) ^ swrlb:contains(?fhx, "乳腺癌") -> recommendsGeneticTesting(?p, true) ^ has遗传咨询建议(?p, "建议胚系遗传咨询及BRCA检测")

### Rule 44: Germline BRCA pathogenic PARP consideration (from v2.0 R30)
Patient(?p) ^ has胚系BRCA状态(?p, ?brca) ^ swrlb:contains(?brca, "致病") -> hasPARP适应证(?p, "是") ^ has铂类适应证(?p, "可考虑")

# ═══════════════════════════════════════════════════════════════════════════
# Category 10: Surgery Domain (R45-R47)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 45: Breast-conserving eligibility (from v2.0 R31)
Patient(?p) ^ has肿瘤大小最大毫米(?p, ?size) ^ swrlb:lessThanOrEqual(?size, 30) ^ has肿瘤多灶性(?p, "单灶") -> has保乳手术适宜性(?p, "较适合") ^ has手术推荐(?p, "可评估保乳手术")

### Rule 46: Multifocal disease mastectomy discussion (from v2.0 R32)
Patient(?p) ^ has肿瘤多灶性(?p, ?mf) ^ swrlb:contains(?mf, "多") -> requiresMDT(?p, true) ^ has手术推荐(?p, "需讨论全乳切除或扩大切除")

### Rule 47: Positive margin re-excision discussion (from v2.0 R33)
Patient(?p) ^ has切缘手术标本(?p, ?margin) ^ swrlb:contains(?margin, "受累") -> requiresUrgentMDT(?p, true) ^ has手术推荐(?p, "建议讨论再切除或改行其他局部治疗")

# ═══════════════════════════════════════════════════════════════════════════
# Category 11: Radiotherapy Domain (R48-R49)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 48: Breast-conserving surgery whole breast RT (from v2.0 R34)
Patient(?p) ^ has计划Or执行的手术类型和日期(?p, ?surg) ^ swrlb:contains(?surg, "保乳") -> has放疗建议(?p, "建议术后全乳放疗")

### Rule 49: Node positive regional nodal RT discussion (from v2.0 R35)
Patient(?p) ^ has淋巴结阳性个数(?p, ?npos) ^ swrlb:greaterThan(?npos, 0) -> has放疗建议(?p, "建议评估区域淋巴结放疗")

# ═══════════════════════════════════════════════════════════════════════════
# Category 12: Adjuvant Escalation/De-escalation (R50-R52)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 50: Residual TNBC capecitabine consideration (from v2.0 R36)
Patient(?p) ^ hasMolecularSubtype(?p, "TripleNegative") ^ has残余病灶状态(?p, ?residual) ^ swrlb:contains(?residual, "残余") -> recommendTreatment(?p, "可考虑辅助卡培他滨") ^ hasGuidelineSource(?p, :NCCN)

### Rule 51: Residual HER2+ escalation (from v2.0 R37)
Patient(?p) ^ hasHER2最终解释(?p, "阳性") ^ has残余病灶状态(?p, ?residual) ^ swrlb:contains(?residual, "残余") -> recommendTreatment(?p, "可考虑术后升级抗HER2治疗") ^ hasGuidelineSource(?p, :NCCN)

### Rule 52: pCR after neoadjuvant HER2 continue course (from v2.0 R38)
Patient(?p) ^ hasHER2最终解释(?p, "阳性") ^ has病理完全缓解pCR(?p, true) -> hasHER2靶向药物和持续时间(?p, "按既定辅助抗HER2总疗程完成")

# ═══════════════════════════════════════════════════════════════════════════
# Category 13: Safety / Contraindication Domain (R53-R55)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 53: Low LVEF cardiac review before anti-HER2 (from v2.0 R39)
Patient(?p) ^ hasHER2最终解释(?p, "阳性") ^ hasLVEF百分比(?p, ?lvef) ^ swrlb:lessThan(?lvef, 50) -> guidelineDeviation(?p, true) ^ deviationType(?p, "HER2治疗前需心功能评估/心脏会诊") ^ requiresJustification(?p, true)

### Rule 54: Premenopausal endocrine context (from v2.0 R40)
Patient(?p) ^ hasER结果标志和百分比(?p, ?er) ^ swrlb:greaterThan(?er, 0) ^ has月经状况(?p, "绝经前") -> treatmentRationale(?p, "内分泌方案需结合绝经前状态评估")

### Rule 55: Pregnancy urgent MDT (from v2.0 R41)
Patient(?p) ^ has妊娠状态(?p, "妊娠中") -> requiresUrgentMDT(?p, true) ^ alertType(?p, "妊娠相关肿瘤") ^ alertMessage(?p, "妊娠中患者需在系统治疗或放疗前进行专门MDT评估")

# ═══════════════════════════════════════════════════════════════════════════
# Category 14: Metastatic / Recurrence Sequencing (R56-R58)
# ═══════════════════════════════════════════════════════════════════════════

### Rule 56: Metastatic bone-only spread (from v2.0 R42)
Patient(?p) ^ has总体分期分组(?p, "IV期") ^ has远处转移部位(?p, ?site) ^ swrlb:contains(?site, "骨") -> treatmentIntent(?p, "姑息治疗并评估局部控制/骨改良治疗")

### Rule 57: Metastatic HER2-low ADC consideration (from v2.0 R43)
Patient(?p) ^ has总体分期分组(?p, "IV期") ^ hasHER2低表达状态(?p, "是") ^ has转移治疗线别(?p, ?line) ^ swrlb:contains(?line, "二线") -> hasADC适应证(?p, "可评估")

### Rule 58: Oligometastatic MDT for local ablative options (from v2.0 R44)
Patient(?p) ^ has总体分期分组(?p, "IV期") ^ has寡转移标志(?p, true) -> requiresUrgentMDT(?p, true) ^ mdtReason(?p, "寡转移表型，需讨论系统治疗联合局部消融/放疗/手术")
