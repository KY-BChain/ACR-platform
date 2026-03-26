
import sqlite3

def text_or_none(v):
    if v is None:
        return None
    s = str(v).strip()
    return s if s else None

def normalize_sex(v):
    s = text_or_none(v)
    if not s:
        return "女"
    sl = s.lower()
    if s in ("女", "Female", "female", "F") or sl == "female":
        return "女"
    if s in ("男", "Male", "male", "M") or sl == "male":
        return "男"
    return "女"

def infer_bca_subtype(diagnosis):
    diagnosis = diagnosis or ""
    d = diagnosis.lower()
    if "lobular" in d or "小叶" in diagnosis:
        return "浸润性小叶癌"
    if "dcis" in d or "导管原位" in diagnosis:
        return "DCIS"
    if "lcis" in d or "小叶原位" in diagnosis:
        return "LCIS"
    if "ductal" in d or "导管" in diagnosis:
        return "浸润性导管癌"
    return "其他"

def normalize_grade(v):
    s = text_or_none(v)
    if not s:
        return "2"
    s = s.upper().replace("GRADE", "").replace("G", "").replace(" ", "")
    if s in ("1", "2", "3"):
        return s
    return "2"

def normalize_specimen_type(v):
    s = text_or_none(v)
    if not s:
        return "空心针活检"
    if s in ("穿刺活检", "芯针活检", "空心针活检", "粗针活检"):
        return "空心针活检"
    if "切除" in s and "保乳" in s:
        return "保乳手术标本"
    if "乳房切除" in s:
        return "乳房切除术标本"
    if "切除" in s:
        return "切除活检"
    if "淋巴" in s:
        return "淋巴结标本"
    return "空心针活检"

def er_pr_status(pct):
    if pct is None:
        return "无法确定"
    try:
        return "阳性" if float(pct) >= 1 else "阴性"
    except Exception:
        return "无法确定"

def her2_map(v):
    s = text_or_none(v)
    if not s:
        return ("0", "阴性")
    u = s.upper().strip()
    if u in ("0", "1+", "2+", "3+"):
        if u == "3+":
            return (u, "阳性")
        if u == "2+":
            return (u, "不确定")
        return (u, "阴性")
    if "NEG" in u or "阴" in s:
        return ("0", "阴性")
    if "POS" in u or "阳" in s:
        return ("3+", "阳性")
    return ("0", "阴性")

def ki67_category(v):
    try:
        x = float(v)
    except Exception:
        return None
    if x < 14:
        return "低(<14%)"
    if x <= 30:
        return "中(14-30%)"
    return "高(>30%)"

def lab_marker_name(test_name):
    s = (test_name or "").strip().upper()
    mapping = {
        "CA15-3": "CA15-3",
        "CEA": "CEA",
        "CA125": "CA125",
        "CA27.29": "CA27.29",
    }
    return mapping.get(s)

def import_records(source_db_path, target_db_path):
    src = sqlite3.connect(source_db_path)
    src.row_factory = sqlite3.Row
    dst = sqlite3.connect(target_db_path)
    dst.row_factory = sqlite3.Row

    s = src.cursor()
    d = dst.cursor()
    d.execute("PRAGMA foreign_keys = ON")

    patient_map = {}
    d.execute("SELECT patient_id, patient_local_id FROM patient")
    for row in d.fetchall():
        patient_map[row["patient_local_id"]] = row["patient_id"]

    s.execute("""
        SELECT id, 患者姓名本地, 患者本地标识符, 年龄推导, 性别, 民族, 联系电话, date_of_birth
        FROM patients
        ORDER BY id
    """)
    imported_patients = 0
    for row in s.fetchall():
        local_id = text_or_none(row["id"])
        if not local_id or local_id in patient_map:
            continue
        d.execute("""
            INSERT INTO patient (
                patient_local_id, patient_name_local, patient_id_number, patient_phone,
                birth_sex, birth_date, age, native_place
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            local_id,
            text_or_none(row["患者姓名本地"]),
            text_or_none(row["患者本地标识符"]),
            text_or_none(row["联系电话"]),
            normalize_sex(row["性别"]),
            text_or_none(row["date_of_birth"]) or "1970-01-01",
            row["年龄推导"] if row["年龄推导"] is not None else 0,
            text_or_none(row["民族"]),
        ))
        patient_map[local_id] = d.lastrowid
        imported_patients += 1

    s.execute("""
        SELECT patient_id, specimen_type, diagnosis, tumor_size, grade, report_date, pathologist
        FROM pathology_reports
        ORDER BY id
    """)
    imported_pathology = 0
    pathology_map = {}
    for row in s.fetchall():
        legacy_pid = text_or_none(row["patient_id"])
        if not legacy_pid:
            continue
        accession = f"LEGACY-PATH-{legacy_pid}-{(text_or_none(row['report_date']) or '2024-01-01')}"
        d.execute("SELECT pathology_id FROM pathology_report WHERE accession_number = ? LIMIT 1", (accession,))
        existing = d.fetchone()
        if existing:
            pathology_id = existing["pathology_id"]
        else:
            diagnosis = text_or_none(row["diagnosis"]) or "Legacy pathology import"
            d.execute("""
                INSERT INTO pathology_report (
                    biopsy_id, specimen_type, report_date, accession_number,
                    diagnosis_short_text, histologic_subtype, tumor_size_max_mm,
                    histologic_grade, report_full_text, pathologist_name
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                None,
                normalize_specimen_type(row["specimen_type"]),
                text_or_none(row["report_date"]) or "2024-01-01",
                accession,
                diagnosis,
                infer_bca_subtype(diagnosis),
                row["tumor_size"] if row["tumor_size"] is not None else 0,
                normalize_grade(row["grade"]),
                f"Imported from legacy pathology_reports table. Diagnosis: {diagnosis}.",
                text_or_none(row["pathologist"]),
            ))
            pathology_id = d.lastrowid
            imported_pathology += 1
        pathology_map[legacy_pid] = pathology_id

    s.execute("""
        SELECT patient_id, `ER结果标志和百分比`, `PR结果标志和百分比`, `HER2最终解释`, `Ki-67增殖指数`, test_date
        FROM receptor_assays
        ORDER BY id
    """)
    imported_receptors = 0
    for row in s.fetchall():
        legacy_pid = text_or_none(row["patient_id"])
        pathology_id = pathology_map.get(legacy_pid)
        if not pathology_id:
            continue
        d.execute("SELECT 1 FROM receptor_assay WHERE pathology_id = ? LIMIT 1", (pathology_id,))
        if d.fetchone():
            continue
        er_pct = row["ER结果标志和百分比"]
        pr_pct = row["PR结果标志和百分比"]
        her2_score, her2_final = her2_map(row["HER2最终解释"])
        ki67 = row["Ki-67增殖指数"]
        d.execute("""
            INSERT INTO receptor_assay (
                pathology_id, er_status, er_percentage, pr_status, pr_percentage,
                her2_ihc_score, her2_final_interpretation, ki67_index, ki67_category, assay_date
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            pathology_id,
            er_pr_status(er_pct),
            int(er_pct) if er_pct is not None else None,
            er_pr_status(pr_pct),
            int(pr_pct) if pr_pct is not None else None,
            her2_score,
            her2_final,
            ki67,
            ki67_category(ki67),
            text_or_none(row["test_date"]),
        ))
        imported_receptors += 1

    s.execute("""
        SELECT patient_id, test_name, result_value, unit, reference_range, collected_at
        FROM lab_results
        ORDER BY id
    """)
    imported_labs = 0
    for row in s.fetchall():
        legacy_pid = text_or_none(row["patient_id"])
        canon_pid = patient_map.get(legacy_pid)
        if not canon_pid:
            continue
        marker = lab_marker_name(row["test_name"])
        if not marker:
            continue
        try:
            value = float(row["result_value"])
        except Exception:
            value = None
        d.execute("""
            INSERT INTO laboratory_test (
                patient_id, marker_type, value, unit, reference_range, abnormal_flag, test_date, timepoint
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            canon_pid,
            marker,
            value,
            text_or_none(row["unit"]) or "U/mL",
            text_or_none(row["reference_range"]),
            None,
            text_or_none(row["collected_at"])[:10] if text_or_none(row["collected_at"]) else None,
            "随访",
        ))
        imported_labs += 1

    dst.commit()
    src.close()
    dst.close()
    return {
        "imported_patients_into_canonical_patient": imported_patients,
        "imported_pathology_into_canonical_pathology_report": imported_pathology,
        "imported_receptors_into_canonical_receptor_assay": imported_receptors,
        "imported_labs_into_canonical_laboratory_test": imported_labs,
    }

if __name__ == "__main__":
    source = r"/mnt/data/acr_clinical_trail.db"
    target = r"/mnt/data/acr_clinical_trail_imaging_metadata_enhanced_prefilled_toulouse_demo.db"
    result = import_records(source, target)
    print(result)
