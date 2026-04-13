// auth.js - PHP VERSION
// ────────────────────────────────────────────────────────────────────────────
// Works with /api/auth.php (PHP backend)
// Compatible with both local PHP server and ISP hosting
// ────────────────────────────────────────────────────────────────────────────

// Auto-detect API base URL
const API_BASE_URL = window.location.hostname === 'localhost'
    ? 'http://localhost:5050/api'  // Local PHP server
    : 'https://www.acragent.com/api';  // Production ISP

// ────────────────────────────────────────────────────────────────────────────
// Helper: call API and return JSON or throw error
// ────────────────────────────────────────────────────────────────────────────
async function apiCall(endpoint, payload) {
  const url = `${API_BASE_URL}/${endpoint}`;
  
  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    // Read response text
    const text = await res.text();
    let data;
    
    try {
      data = text ? JSON.parse(text) : {};
    } catch (e) {
      console.error('Invalid JSON response:', text);
      throw new Error(`Server returned invalid JSON: ${text.slice(0, 100)}`);
    }

    if (!res.ok) {
      throw new Error(data.error || `HTTP ${res.status} ${res.statusText}`);
    }
    
    // Check PHP response format
    if (!data.success) {
      throw new Error(data.error || 'Request failed');
    }
    
    return data;
  } catch (err) {
    console.error('API call failed:', err);
    throw err;
  }
}

// ────────────────────────────────────────────────────────────────────────────
// Login Handler
// ────────────────────────────────────────────────────────────────────────────
window.handleLogin = async function() {
  const emailEl  = document.getElementById('authEmail');
  const passEl   = document.getElementById('authPassword');
  const email    = emailEl?.value.trim();
  const password = passEl?.value.trim();

  if (!email || !password) {
    return alert('Please enter both email and password.');
  }

  try {
    // Call PHP auth endpoint
    const response = await apiCall('auth.php?action=login', { 
      email, 
      password 
    });
    
    // Save user data from PHP response
    const user = response.data.user;
    const token = response.data.token;
    
    // Store in sessionStorage
    sessionStorage.setItem('user', JSON.stringify(user));
    sessionStorage.setItem('token', token);
    
    // Also store in localStorage for persistence
    localStorage.setItem('acr_session_token', token);
    localStorage.setItem('acr_user_id', user.id);
    localStorage.setItem('acr_username', user.username);
    localStorage.setItem('acr_user_email', user.email);
    localStorage.setItem('acr_user_role', user.role);
    localStorage.setItem('acr_login_time', Date.now().toString());

    // Success message
    console.log('Login successful:', user.username);

    // Redirect to main application
    window.location.href = 'acr-owl.html';
    
  } catch (err) {
    console.error('Login error:', err);
    alert(`Login failed: ${err.message}`);
  }
};

// ────────────────────────────────────────────────────────────────────────────
// Registration Handler (after email verification on ACR-contact.html)
// ────────────────────────────────────────────────────────────────────────────
window.handleRegistration = async function() {
  const emailEl  = document.getElementById('regEmail');
  const passEl   = document.getElementById('regPassword');
  const instEl   = document.getElementById('regInstitution');
  const email    = emailEl?.value.trim();
  const password = passEl?.value.trim();
  const institution = instEl?.value.trim();

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return alert('Invalid email format');
  }
  if (!password || password.length < 6) {
    return alert('Password must be at least 6 characters');
  }

  try {
    // Call PHP auth endpoint
    const response = await apiCall('auth.php?action=register', { 
      email, 
      password,
      institution: institution || null
    });
    
    console.log('Registration successful:', response.data.username);
    alert(`Registration successful! Welcome ${response.data.username}. You can now log in.`);
    
    // Switch to login form if function exists
    if (typeof window.showForm === 'function') {
      window.showForm('login');
    }
    
  } catch (err) {
    console.error('Registration error:', err);
    alert(`Registration failed: ${err.message}`);
  }
};

// ────────────────────────────────────────────────────────────────────────────
// Test User Helper (for development)
// ────────────────────────────────────────────────────────────────────────────
// Creates test user in database if needed
window.createTestUser = async function() {
  try {
    const response = await apiCall('auth.php?action=register', {
      email: 'test@blockenergy.eu',
      password: 'BlockEnergy888',
      institution: 'Test Institution'
    });
    console.log('Test user created:', response.data);
    alert('Test user created! Email: test@blockenergy.eu, Password: BlockEnergy888');
  } catch (err) {
    // If user already exists, that's fine
    if (err.message.includes('already exists')) {
      console.log('Test user already exists');
      alert('Test user already exists. Use: test@blockenergy.eu / BlockEnergy888');
    } else {
      console.error('Failed to create test user:', err);
      alert(`Error: ${err.message}`);
    }
  }
};

console.log('✅ Auth.js (PHP version) loaded');
console.log('📡 API Base URL:', API_BASE_URL);
console.log('🔧 Test user: test@blockenergy.eu / BlockEnergy888');
console.log('💡 Run createTestUser() in console if needed');
