// src/mobile/components/MobileEmergencyOverlay.jsx
import { useState, useEffect } from 'react';
import { isMobileApp } from '../mobileOnly';
import { emergencyEvents, getEmergencyConfig } from '../services/emergencyService';

const styles = {
  overlay: {
    position: 'fixed',
    inset: 0,
    zIndex: 99999,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'linear-gradient(135deg, #FF0000 0%, #8B0000 100%)',
    color: '#fff',
    fontFamily: "'Inter', 'Segoe UI', sans-serif",
    padding: '24px',
    boxSizing: 'border-box',
  },
  pulseText: {
    fontSize: '32px',
    fontWeight: '900',
    letterSpacing: '2px',
    color: '#FFFFFF',
    marginBottom: '20px',
    textAlign: 'center',
    animation: 'sosBlink 0.5s ease-in-out infinite',
  },
  statusBox: {
    background: 'rgba(0,0,0,0.5)',
    padding: '20px',
    borderRadius: '12px',
    width: '100%',
    maxWidth: '300px',
    display: 'flex',
    flexDirection: 'column',
    gap: '12px',
    fontSize: '14px'
  }
};

export default function MobileEmergencyOverlay() {
  if (!isMobileApp()) return null;

  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const handleShow = () => setVisible(true);
    emergencyEvents.addEventListener('showOverlay', handleShow);
    return () => emergencyEvents.removeEventListener('showOverlay', handleShow);
  }, []);

  if (!visible) return null;

  const config = getEmergencyConfig();

  return (
    <div style={styles.overlay}>
      <div style={styles.pulseText}>EMERGENCY<br/>ALERT SENT</div>
      <div style={styles.statusBox}>
        <div>✅ GPS Location Fetched</div>
        <div>✅ Quick SOS API Triggered</div>
        <div>✅ WhatsApp Message Sent</div>
        <div>✅ Calling {config.primaryContactPhone}</div>
      </div>
      <button 
        onClick={() => setVisible(false)}
        style={{ marginTop: '40px', padding: '12px 24px', background: 'transparent', border: '2px solid white', color: 'white', borderRadius: '30px', fontWeight: 'bold' }}>
        DISMISS
      </button>
    </div>
  );
}
