/**
 * ACR Ontology Bridge
 * 
 * JavaScript integration layer for acr-pathway.html
 * 
 * Architecture:
 * 1. Attempts to call ontology reasoning API first
 * 2. Falls back to hardcoded JS logic if API unavailable
 * 3. Preserves existing UI without modifications
 * 
 * Usage in acr-pathway.html:
 * <script src="scripts/ontology-bridge.js"></script>
 * 
 * Then replace existing inference call with:
 * await ontologyBridge.performInference(patientData);
 */

const ontologyBridge = (function() {
    'use strict';

    const API_BASE_URL = 'http://localhost:8080';
    const INFERENCE_ENDPOINT = '/api/infer';
    const TIMEOUT_MS = 15000; // 15 seconds

    /**
     * Main inference function
     * Tries ontology API first, falls back to JS if unavailable
     */
    async function performInference(patientData) {
        console.log('[ACR Bridge] Starting inference for patient:', patientData.patient?.id);

        try {
            // Attempt ontology reasoning via API
            const ontologyResult = await callOntologyAPI(patientData);
            
            if (ontologyResult && ontologyResult.inferenceSource === 'ontology-swrl') {
                console.log('[ACR Bridge] ✓ Ontology inference successful');
                return ontologyResult;
            }

        } catch (error) {
            console.warn('[ACR Bridge] Ontology API failed:', error.message);
            console.log('[ACR Bridge] → Falling back to JavaScript reasoner');
        }

        // Fallback to existing hardcoded JavaScript logic
        return executeJavaScriptFallback(patientData);
    }

    /**
     * Call ontology reasoning API
     */
    async function callOntologyAPI(patientData) {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), TIMEOUT_MS);

        try {
            const response = await fetch(API_BASE_URL + INFERENCE_ENDPOINT, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(patientData),
                signal: controller.signal
            });

            clearTimeout(timeoutId);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const result = await response.json();
            
            // Validate response structure
            if (!result.patientInfo || !result.inferenceSource) {
                throw new Error('Invalid response structure from API');
            }

            return result;

        } catch (error) {
            clearTimeout(timeoutId);
            
            if (error.name === 'AbortError') {
                throw new Error('API timeout');
            }
            
            throw error;
        }
    }

    /**
     * JavaScript fallback (preserves existing logic)
     * This function should call your existing hardcoded inference functions
     */
    function executeJavaScriptFallback(patientData) {
        // Call existing functions from acr-pathway.html
        // These functions already exist in your demo website
        
        if (typeof executeSWRLRules === 'function') {
            const swrlResult = executeSWRLRules(patientData);
            const sqwrlResult = executeSQWRLQueries(patientData);
            
            return {
                ...swrlResult,
                ...sqwrlResult,
                inferenceSource: 'javascript-fallback'
            };
        }

        // If functions not available, return minimal response
        console.error('[ACR Bridge] JavaScript fallback functions not found');
        return {
            patientInfo: {
                id: patientData.patient?.id || 'unknown',
                inferenceSource: 'error'
            },
            error: 'Neither ontology API nor JavaScript fallback available'
        };
    }

    /**
     * Health check
     */
    async function checkHealth() {
        try {
            const response = await fetch(API_BASE_URL + '/api/health', {
                method: 'GET',
                headers: { 'Accept': 'application/json' }
            });

            if (response.ok) {
                const health = await response.json();
                console.log('[ACR Bridge] Health check:', health);
                return health;
            }

            return null;

        } catch (error) {
            console.warn('[ACR Bridge] Health check failed:', error.message);
            return null;
        }
    }

    // Public API
    return {
        performInference,
        checkHealth
    };

})();

// Make available globally
window.ontologyBridge = ontologyBridge;

console.log('[ACR Bridge] Ontology bridge initialized');