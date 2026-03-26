/**
 * ACR ONTOLOGY REASONER - FRONTEND INTEGRATION
 * 
 * This JavaScript integrates the ACR Ontology Interface reasoning API
 * with the existing acr_pathway.html webpage.
 * 
 * KEY PRINCIPLE: Only update CONTENT, never change DESIGN
 * - Uses existing HTML element IDs
 * - Updates textContent and innerHTML only
 * - No CSS modifications
 * - No layout changes
 * 
 * USAGE:
 * 1. Add this script to acr_pathway.html <script> section
 * 2. Ensure HTML has required element IDs (see HTML_ELEMENTS_REQUIRED below)
 * 3. Start ACR Ontology Interface server on port 8080
 * 4. Test with sample patient data
 * 
 * @version 2.0
 */

// ==================== CONFIGURATION ====================

const ACR_API_CONFIG = {
    baseURL: 'http://localhost:8080/api',
    endpoints: {
        health: '/health',
        infer: '/infer',
        trace: '/trace/latest',
        agentiveExport: '/agentive/export'
    },
    timeout: 30000  // 30 seconds
};

// ==================== REQUIRED HTML ELEMENTS ====================

/**
 * These element IDs must exist in acr_pathway.html.
 * If your HTML uses different IDs, update this mapping.
 */
const HTML_ELEMENTS = {
    // Input form elements
    patientIdInput: 'patient-id',
    ageInput: 'age',
    tumorSizeInput: 'tumor-size',
    erStatusInput: 'er-status',
    prStatusInput: 'pr-status',
    her2StatusInput: 'her2-status',
    ki67Input: 'ki67',
    nodalStatusInput: 'nodal-status',
    gradeInput: 'grade',
    
    // Action buttons
    performReasoningBtn: 'perform-reasoning-btn',
    resetFormBtn: 'reset-form-btn',
    
    // Results display containers
    resultsContainer: 'inference-results',
    loadingIndicator: 'loading-indicator',
    errorMessage: 'error-message',
    
    // Individual result fields
    molecularSubtypeDisplay: 'molecular-subtype-display',
    riskLevelDisplay: 'risk-level-display',
    confidenceScoreDisplay: 'confidence-score',
    treatmentsList: 'treatment-recommendations-list',
    biomarkersList: 'biomarkers-list',
    monitoringList: 'monitoring-requirements-list',
    reasoningTraceDisplay: 'reasoning-trace-display',
    inferredConditionsList: 'inferred-conditions-list'
};

// ==================== MAIN FUNCTIONS ====================

/**
 * Initialize the reasoner integration when page loads
 */
function initializeACRReasoner() {
    console.log('🧬 Initializing ACR Ontology Reasoner Integration...');
    
    // Check server health
    checkServerHealth();
    
    // Attach event listeners
    attachEventListeners();
    
    // Hide results container initially
    hideResults();
    
    console.log('✅ ACR Reasoner Integration initialized');
}

/**
 * Check if ACR Ontology Interface server is running
 */
async function checkServerHealth() {
    try {
        const response = await fetch(`${ACR_API_CONFIG.baseURL}${ACR_API_CONFIG.endpoints.health}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        
        if (response.ok) {
            const health = await response.json();
            console.log('✅ Server health:', health);
            showStatusMessage('ACR Reasoner connected', 'success');
        } else {
            throw new Error(`Health check failed: ${response.status}`);
        }
    } catch (error) {
        console.error('❌ Server connection failed:', error);
        showStatusMessage('ACR Reasoner offline - start server on port 8080', 'warning');
    }
}

/**
 * Attach event listeners to form elements
 */
function attachEventListeners() {
    // Perform reasoning button
    const reasoningBtn = document.getElementById(HTML_ELEMENTS.performReasoningBtn);
    if (reasoningBtn) {
        reasoningBtn.addEventListener('click', handlePerformReasoning);
    }
    
    // Reset form button
    const resetBtn = document.getElementById(HTML_ELEMENTS.resetFormBtn);
    if (resetBtn) {
        resetBtn.addEventListener('click', handleResetForm);
    }
    
    // Optional: Enter key to submit
    document.addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && e.target.tagName === 'INPUT') {
            handlePerformReasoning(e);
        }
    });
}

/**
 * Main handler: Perform reasoning on patient data
 */
async function handlePerformReasoning(event) {
    if (event) event.preventDefault();
    
    console.log('🔬 Starting reasoning process...');
    
    // Validate and collect form data
    const patientData = collectPatientData();
    if (!patientData) {
        showError('Please fill in required fields: Patient ID, ER/PR/HER2 status');
        return;
    }
    
    // Show loading state
    showLoading('Analyzing patient data with OWL reasoning...');
    hideResults();
    hideError();
    
    try {
        // Call reasoning API
        const inferenceResult = await performInference(patientData);
        
        // Display results
        displayInferenceResults(inferenceResult);
        
        // Show success
        showResults();
        hideLoading();
        
        console.log('✅ Reasoning completed successfully');
        
    } catch (error) {
        console.error('❌ Reasoning failed:', error);
        showError(`Reasoning failed: ${error.message}`);
        hideLoading();
    }
}

/**
 * Call ACR Ontology Interface API to perform inference
 */
async function performInference(patientData) {
    const url = `${ACR_API_CONFIG.baseURL}${ACR_API_CONFIG.endpoints.infer}`;
    
    console.log('📡 Calling API:', url);
    console.log('📦 Patient data:', patientData);
    
    const response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(patientData),
        timeout: ACR_API_CONFIG.timeout
    });
    
    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`API error ${response.status}: ${errorText}`);
    }
    
    const result = await response.json();
    console.log('📊 Inference result:', result);
    
    return result;
}

/**
 * Collect patient data from form inputs
 */
function collectPatientData() {
    const getInputValue = (id) => {
        const element = document.getElementById(id);
        return element ? element.value : null;
    };
    
    const getNumericValue = (id) => {
        const value = getInputValue(id);
        return value ? parseFloat(value) : null;
    };
    
    const patientData = {
        patientId: getInputValue(HTML_ELEMENTS.patientIdInput),
        age: getNumericValue(HTML_ELEMENTS.ageInput),
        tumorSize: getNumericValue(HTML_ELEMENTS.tumorSizeInput),
        erStatus: getInputValue(HTML_ELEMENTS.erStatusInput),
        prStatus: getInputValue(HTML_ELEMENTS.prStatusInput),
        her2Status: getInputValue(HTML_ELEMENTS.her2StatusInput),
        ki67: getNumericValue(HTML_ELEMENTS.ki67Input),
        nodalStatus: getInputValue(HTML_ELEMENTS.nodalStatusInput),
        grade: getInputValue(HTML_ELEMENTS.gradeInput)
    };
    
    // Validate required fields
    if (!patientData.patientId || 
        !patientData.erStatus || 
        !patientData.prStatus || 
        !patientData.her2Status) {
        return null;
    }
    
    return patientData;
}

/**
 * Display inference results in HTML (NO DESIGN CHANGES)
 */
function displayInferenceResults(result) {
    console.log('🖥️ Displaying results...');
    
    // 1. Molecular Subtype
    displayMolecularSubtype(result.patientInfo.molecularSubtype);
    
    // 2. Risk Level
    displayRiskLevel(result.patientInfo.riskLevel);
    
    // 3. Confidence Score
    displayConfidenceScore(result.confidence);
    
    // 4. Treatment Recommendations
    displayTreatmentRecommendations(result.treatmentRecommendations);
    
    // 5. Biomarkers
    displayBiomarkers(result.candidateBiomarkers);
    
    // 6. Monitoring Requirements
    displayMonitoring(result.monitoring);
    
    // 7. Reasoning Trace
    displayReasoningTrace(result.reasoning);
    
    // 8. Inferred Conditions
    displayInferredConditions(result.inferredConditions);
}

/**
 * Display molecular subtype
 */
function displayMolecularSubtype(subtype) {
    const element = document.getElementById(HTML_ELEMENTS.molecularSubtypeDisplay);
    if (!element) return;
    
    element.textContent = subtype;
    
    // Add subtype-specific CSS class (if your CSS supports it)
    element.className = `subtype-${subtype.toLowerCase().replace('_', '-')}`;
}

/**
 * Display risk level
 */
function displayRiskLevel(riskLevel) {
    const element = document.getElementById(HTML_ELEMENTS.riskLevelDisplay);
    if (!element) return;
    
    element.textContent = riskLevel;
    
    // Add risk-specific CSS class
    element.className = `risk-${riskLevel.toLowerCase()}`;
}

/**
 * Display confidence score
 */
function displayConfidenceScore(confidence) {
    const element = document.getElementById(HTML_ELEMENTS.confidenceScoreDisplay);
    if (!element) return;
    
    const confidencePercent = (confidence * 100).toFixed(1);
    element.textContent = `${confidencePercent}%`;
    
    // Add confidence class
    if (confidence > 0.8) {
        element.className = 'confidence-high';
    } else if (confidence > 0.6) {
        element.className = 'confidence-medium';
    } else {
        element.className = 'confidence-low';
    }
}

/**
 * Display treatment recommendations
 */
function displayTreatmentRecommendations(treatments) {
    const listElement = document.getElementById(HTML_ELEMENTS.treatmentsList);
    if (!listElement) return;
    
    // Clear existing content
    listElement.innerHTML = '';
    
    if (!treatments || treatments.length === 0) {
        listElement.innerHTML = '<li class="no-results">No treatment recommendations available</li>';
        return;
    }
    
    // Add each treatment
    treatments.forEach((treatment, index) => {
        const li = document.createElement('li');
        li.className = 'treatment-item';
        li.innerHTML = `
            <div class="treatment-number">${index + 1}.</div>
            <div class="treatment-details">
                <strong class="medication-name">${treatment.medicationName}</strong><br>
                <span class="dose-info">Dose: ${treatment.dose}</span><br>
                <span class="frequency-info">Frequency: ${treatment.frequency}</span><br>
                <em class="rationale">${treatment.rationale}</em>
            </div>
        `;
        listElement.appendChild(li);
    });
}

/**
 * Display biomarkers
 */
function displayBiomarkers(biomarkers) {
    const listElement = document.getElementById(HTML_ELEMENTS.biomarkersList);
    if (!listElement) return;
    
    listElement.innerHTML = '';
    
    if (!biomarkers || biomarkers.length === 0) {
        listElement.innerHTML = '<li class="no-results">No biomarkers identified</li>';
        return;
    }
    
    biomarkers.forEach(biomarker => {
        const li = document.createElement('li');
        li.className = 'biomarker-item';
        li.textContent = biomarker;
        listElement.appendChild(li);
    });
}

/**
 * Display monitoring requirements
 */
function displayMonitoring(monitoring) {
    const listElement = document.getElementById(HTML_ELEMENTS.monitoringList);
    if (!listElement) return;
    
    listElement.innerHTML = '';
    
    if (!monitoring || monitoring.length === 0) {
        listElement.innerHTML = '<li class="no-results">No monitoring requirements specified</li>';
        return;
    }
    
    monitoring.forEach(requirement => {
        const li = document.createElement('li');
        li.className = 'monitoring-item';
        li.textContent = requirement;
        listElement.appendChild(li);
    });
}

/**
 * Display reasoning trace
 */
function displayReasoningTrace(reasoning) {
    const displayElement = document.getElementById(HTML_ELEMENTS.reasoningTraceDisplay);
    if (!displayElement) return;
    
    displayElement.innerHTML = '';
    
    if (!reasoning || reasoning.length === 0) {
        displayElement.innerHTML = '<p class="no-results">No reasoning trace available</p>';
        return;
    }
    
    reasoning.forEach((step, index) => {
        const p = document.createElement('p');
        p.className = 'reasoning-step';
        p.innerHTML = `<strong>${index + 1}.</strong> ${step}`;
        displayElement.appendChild(p);
    });
}

/**
 * Display inferred conditions
 */
function displayInferredConditions(conditions) {
    const listElement = document.getElementById(HTML_ELEMENTS.inferredConditionsList);
    if (!listElement) return;
    
    listElement.innerHTML = '';
    
    if (!conditions || conditions.length === 0) {
        listElement.innerHTML = '<li class="no-results">No conditions inferred</li>';
        return;
    }
    
    conditions.forEach(condition => {
        const li = document.createElement('li');
        li.className = 'condition-item';
        li.textContent = condition;
        listElement.appendChild(li);
    });
}

// ==================== UI STATE MANAGEMENT ====================

/**
 * Show loading indicator
 */
function showLoading(message = 'Processing...') {
    const loader = document.getElementById(HTML_ELEMENTS.loadingIndicator);
    if (loader) {
        loader.textContent = message;
        loader.style.display = 'block';
    }
}

/**
 * Hide loading indicator
 */
function hideLoading() {
    const loader = document.getElementById(HTML_ELEMENTS.loadingIndicator);
    if (loader) {
        loader.style.display = 'none';
    }
}

/**
 * Show results container
 */
function showResults() {
    const container = document.getElementById(HTML_ELEMENTS.resultsContainer);
    if (container) {
        container.style.display = 'block';
    }
}

/**
 * Hide results container
 */
function hideResults() {
    const container = document.getElementById(HTML_ELEMENTS.resultsContainer);
    if (container) {
        container.style.display = 'none';
    }
}

/**
 * Show error message
 */
function showError(message) {
    const errorDiv = document.getElementById(HTML_ELEMENTS.errorMessage);
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    }
    console.error('❌ Error:', message);
}

/**
 * Hide error message
 */
function hideError() {
    const errorDiv = document.getElementById(HTML_ELEMENTS.errorMessage);
    if (errorDiv) {
        errorDiv.style.display = 'none';
    }
}

/**
 * Show status message (for health check)
 */
function showStatusMessage(message, type = 'info') {
    console.log(`[${type.toUpperCase()}] ${message}`);
    // Optional: display in UI status bar if available
}

/**
 * Reset form
 */
function handleResetForm(event) {
    if (event) event.preventDefault();
    
    // Reset all input fields
    Object.values(HTML_ELEMENTS).forEach(id => {
        const element = document.getElementById(id);
        if (element && (element.tagName === 'INPUT' || element.tagName === 'SELECT')) {
            element.value = '';
        }
    });
    
    // Hide results and errors
    hideResults();
    hideError();
    
    console.log('🔄 Form reset');
}

// ==================== EXPORT FOR AGENTIVE AI ====================

/**
 * Export inference result for Agentive AI platform
 * This creates a properly formatted package for Fetch.ai uAgent consumption
 */
async function exportForAgentiveAI(patientData) {
    const url = `${ACR_API_CONFIG.baseURL}${ACR_API_CONFIG.endpoints.agentiveExport}`;
    
    console.log('🤖 Exporting for Agentive AI...');
    
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(patientData)
        });
        
        if (!response.ok) {
            throw new Error(`Export failed: ${response.status}`);
        }
        
        const aiPackage = await response.json();
        console.log('📦 Agentive AI package:', aiPackage);
        
        return aiPackage;
        
    } catch (error) {
        console.error('❌ Agentive AI export failed:', error);
        throw error;
    }
}

// ==================== INITIALIZATION ====================

/**
 * Initialize when DOM is ready
 */
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeACRReasoner);
} else {
    initializeACRReasoner();
}

// ==================== GLOBAL API EXPOSURE ====================

/**
 * Expose functions for manual testing in browser console
 */
window.ACRReasoner = {
    performInference,
    exportForAgentiveAI,
    checkServerHealth,
    collectPatientData,
    displayInferenceResults
};

console.log('📚 ACR Reasoner API exposed as window.ACRReasoner');
console.log('   Usage: ACRReasoner.performInference(patientData)');
