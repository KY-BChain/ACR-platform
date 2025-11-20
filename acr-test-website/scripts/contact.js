// contact.js - FIXED FOR PHP + SQLite BACKEND
// ────────────────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    const form         = document.getElementById('researchContactForm');
    const otpSection   = document.getElementById('verificationSection');
    const countdownEl  = document.getElementById('countdown');
    const verifyBtn    = document.getElementById('verifyBtn');
    const resendLink   = document.getElementById('resendLink');
    const statusEl     = document.getElementById('statusMessage');
  
    let otpCode     = '';
    let expiresAt   = 0;
    let otpTimerId  = null;
  
    // Format seconds → "MM:SS"
    function formatTime(sec) {
      const m = String(Math.floor(sec / 60)).padStart(2, '0');
      const s = String(sec % 60).padStart(2, '0');
      return `${m}:${s}`;
    }
  
    // Show transient status
    function showStatus(msg, type = '') {
      statusEl.textContent   = msg;
      statusEl.className     = 'status-message ' + type;
      statusEl.style.display = 'block';
      setTimeout(() => (statusEl.style.display = 'none'), 5000);
    }
  
    // Start or restart the 3‑minute OTP countdown
    function startOtpTimer() {
      let remaining = 180; // seconds
      clearInterval(otpTimerId);
      verifyBtn.disabled      = false;
      resendLink.style.display = 'none';
      countdownEl.textContent  = formatTime(remaining);
  
      otpTimerId = setInterval(() => {
        remaining--;
        countdownEl.textContent = formatTime(remaining);
  
        if (remaining <= 0) {
          clearInterval(otpTimerId);
          showStatus('⌛ Code expired. Please resend.', 'error');
          verifyBtn.disabled      = true;
          resendLink.style.display = 'block';
        }
      }, 1000);
    }
  
    // Generate a 6‑digit OTP
    function genOtp() {
      return String(Math.floor(100000 + Math.random() * 900000));
    }
  
    // Send OTP via EmailJS "One‑Time password" template
    function sendOtp(institution, email) {
      otpCode   = genOtp();
      expiresAt = Date.now() + 3 * 60 * 1000;
  
      // Format an expiry time string, e.g. "14:32"
      const expiresTime = new Date(expiresAt).toLocaleTimeString('en-GB', {
        hour:   '2-digit',
        minute: '2-digit'
      });
  
      return emailjs.send('ACR-Outlook', 'contact_otp', {
        to_email:    email,
        institution: institution,
        otp_code:    otpCode,
        time:        expiresTime
      });
    }
  
    // Hook up the form submission
    form.addEventListener('submit', (e) => {
      e.preventDefault();
  
      const inst = form.institution.value.trim();
      const mail = form.email.value.trim();
  
      if (!inst) {
        showStatus('Please enter your institution name.', 'error');
        return;
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(mail)) {
        showStatus('Please enter a valid email address.', 'error');
        return;
      }
  
      sendOtp(inst, mail)
        .then(() => {
          form.style.display        = 'none';
          otpSection.style.display  = 'block';
          startOtpTimer();
          document.querySelector('.otp-input[data-index="0"]').focus();
          showStatus('✅ Verification code sent!', 'success');
        })
        .catch((err) => {
          console.error('EmailJS send error:', err);
          showStatus('Failed to send verification email. Try again later.', 'error');
        });
    });
  
    // Verify the user‑entered code, register via backend, then send inquiry email
    verifyBtn.addEventListener('click', async () => {
      const entry = Array.from(document.querySelectorAll('.otp-input'))
        .map((i) => i.value.trim())
        .join('');
  
      // Check expiration
      if (Date.now() > expiresAt) {
        clearInterval(otpTimerId);
        showStatus('⌛ Code expired. Please resend.', 'error');
        verifyBtn.disabled      = true;
        resendLink.style.display = 'block';
        return;
      }
  
      // Check code
      if (entry !== otpCode) {
        showStatus('❌ Incorrect code. Please try again.', 'error');
        return;
      }
  
      // Valid code
      clearInterval(otpTimerId);
      showStatus('✅ Verified—registering user…', 'success');
  
      try {
        // 1) Register new user in SQLite backend via PHP
        const regRes = await fetch('/api/auth.php?action=register', {
          method:  'POST',
          headers: { 'Content-Type': 'application/json' },
          body:    JSON.stringify({
            email:       form.email.value.trim(),
            password:    otpCode,  // Use OTP as temporary password
            institution: form.institution.value.trim()
          })
        });
        const regData = await regRes.json();
        
        if (!regData.success) {
          throw new Error(regData.error || 'Registration failed');
        }
  
        // 2) Send the actual contact request email
        await emailjs.send('ACR-Outlook', 'contact_request', {
          to_email:    'acr@blockenergy.eu',
          institution: form.institution.value.trim(),
          email:       form.email.value.trim(),
          interests:   form.interests.value.trim()
        });
  
        showStatus(`🎉 Registered as ${regData.data.username}! Inquiry sent.`, 'success');
  
        // Reset form & UI after a brief delay
        setTimeout(() => {
          form.reset();
          otpSection.style.display  = 'none';
          form.style.display        = 'block';
        }, 3000);
  
      } catch (err) {
        console.error('Error during verification/register:', err);
        showStatus(`Error: ${err.message}`, 'error');
        verifyBtn.disabled = false;
      }
    });
  
    // Resend link
    resendLink.addEventListener('click', () => {
      clearInterval(otpTimerId);
      const inst = form.institution.value.trim();
      const mail = form.email.value.trim();
  
      sendOtp(inst, mail)
        .then(() => {
          startOtpTimer();
          showStatus('🔄 New verification code sent!', 'info');
        })
        .catch((err) => {
          console.error('Resend error:', err);
          showStatus('Resend failed. Please try again.', 'error');
        });
    });
  
    // Auto‑advance OTP inputs
    document.querySelectorAll('.otp-input').forEach((input) => {
      input.addEventListener('input', (e) => {
        if (e.target.value.length === 1) {
          const idx  = +e.target.dataset.index + 1;
          const next = document.querySelector(`.otp-input[data-index="${idx}"]`);
          next?.focus();
        }
      });
    });
  });
