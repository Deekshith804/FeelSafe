// src/mobile/services/emergencyService.js
// ─────────────────────────────────────────────────────────────────
// Handles the instantaneous Emergency Sequence when a shake is detected.
// Performs all actions concurrently without a countdown.
// ─────────────────────────────────────────────────────────────────

import { Geolocation } from '@capacitor/geolocation';
// Native API for vibration is standard web API `navigator.vibrate` when supported
// But we can also fallback to Capacitor Haptics if needed. The user requested `navigator.vibrate`

// ── Config — edit to match your emergency contact ────────────────
const EMERGENCY_CONFIG = {
  userName: 'FeelSafe User',
  primaryContactPhone: '+919110614561',
  whatsappContacts: ['+919110614561'],
  backendBase: 'http://10.72.26.190:5000',
  userId: 1,
};

// Event Target to notify UI to show the red overlay
export const emergencyEvents = new EventTarget();

export async function triggerFullEmergencySequence() {
  console.log("SOS Triggered");

  // 1. Vibrate device (Strong pulse pattern)
  if (navigator.vibrate) {
    navigator.vibrate([500, 300, 500, 300, 1000]);
  }

  // 2. Play Siren Sound
  try {
    const audio = new Audio('/assets/audio/siren.mp3');
    audio.play();
  } catch (err) {
    console.error("Audio play failed", err);
  }

  // 3. (Removed) Do not show Fullscreen Overlay UI


  // 4. Fetch GPS Location
  let lat = null;
  let lon = null;
  try {
    const position = await Geolocation.getCurrentPosition({
      enableHighAccuracy: true,
      timeout: 5000
    });
    lat = position.coords.latitude;
    lon = position.coords.longitude;
    console.log("Location fetched successfully");
  } catch (err) {
    console.error("Location fetch failed", err);
  }

  // 5. Trigger Backend Quick SOS
  try {
    fetch(`${EMERGENCY_CONFIG.backendBase}/api/quick-sos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        lat,
        lon,
        user_id: EMERGENCY_CONFIG.userId,
        trigger: "shake"
      })
    });
  } catch (err) {
    console.error("Backend quick-sos failed", err);
  }

  // 6. Launch WhatsApp
  const mapsLink = (lat && lon) 
    ? `https://maps.google.com/?q=${lat},${lon}`
    : 'Location unavailable';

  const waText = `HELP! I may be in danger.\nThis SOS was triggered automatically by shake detection.\n\nMy live location:\n${mapsLink}`;
  const encoded = encodeURIComponent(waText);
  
  // We use the first contact for WhatsApp
  const waPhone = EMERGENCY_CONFIG.whatsappContacts[0].replace(/[^0-9]/g, '');
  window.open(`https://wa.me/${waPhone}?text=${encoded}`, '_system');
  console.log("WhatsApp launched");

  // 7. Open Emergency Dialer
  // Slight delay to ensure WA intent fires first, otherwise Android might swallow one intent
  setTimeout(() => {
    window.location.href = `tel:${EMERGENCY_CONFIG.primaryContactPhone}`;
    console.log("Emergency call started via location.href");
  }, 1500);
}

export function getEmergencyConfig() {
  return { ...EMERGENCY_CONFIG };
}
