// services/api.js
// Complete FeelSafe API service layer — all calls go through here.
import { isMobileApp } from '../mobile/mobileOnly';
import { registerPlugin } from '@capacitor/core';

let ShowcaseBackend = null;
try {
  if (isMobileApp()) {
    ShowcaseBackend = registerPlugin('ShowcaseBackend');
  }
} catch (e) {
  console.error('Failed to register ShowcaseBackend plugin:', e);
}

async function callShowcase(method, params = {}) {
  if (ShowcaseBackend && ShowcaseBackend[method]) {
    try {
      const res = await ShowcaseBackend[method](params);
      return res;
    } catch (err) {
      console.error(`[ShowcaseBackend] ${method} call failed:`, err);
      throw err;
    }
  }
  throw new Error(`ShowcaseBackend plugin or method ${method} not initialized.`);
}

function blobToBase64(blob) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onerror = reject;
    reader.onload = () => {
      resolve(reader.result.split(',')[1]); // Extract base64 part
    };
    reader.readAsDataURL(blob);
  });
}

// Production backend on Render (HTTPS — works on physical devices without cleartext rules)
const API_BASE = import.meta.env.VITE_API_BASE || 'https://hackathon-project-w4aa.onrender.com';

async function apiCall(endpoint, options = {}, fallback = null) {
  try {
    const res = await fetch(`${API_BASE}${endpoint}`, {
      headers: { 'Content-Type': 'application/json', ...options.headers },
      ...options,
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();
    return data;
  } catch (err) {
    console.error(`[API] ${endpoint} failed:`, err.message);
    if (fallback !== null) return fallback;
    throw err;
  }
}

// ── Health ────────────────────────────────────────────────────────────────────
export const checkHealth = () =>
  isMobileApp() ? callShowcase('checkHealth') : apiCall('/health');

// ── Threat ────────────────────────────────────────────────────────────────────
export const analyzeThreat = (text, lat = null, lon = null, userId = 1, userName = 'FeelSafe User', tripId = null) =>
  isMobileApp()
    ? callShowcase('analyzeThreat', { text, lat, lon, user_id: userId, user_name: userName, trip_id: tripId })
    : apiCall('/api/analyze-threat', {
        method: 'POST',
        body: JSON.stringify({ text, lat, lon, user_id: userId, user_name: userName, trip_id: tripId }),
      }, {
        success: true,
        risk_level: 'LOW',
        message: 'Unable to connect to backend.',
        score: 0,
        matched_keywords: [],
        action_tips: [],
        auto_escalated: false,
        escalation_result: null,
      });

// ── Trip ──────────────────────────────────────────────────────────────────────
export const startTrip = (originLat, originLon, destLat, destLon, originName = '', destName = '', userId = 1) =>
  isMobileApp()
    ? callShowcase('startTrip', {
        origin_lat: originLat, origin_lon: originLon,
        dest_lat: destLat,     dest_lon: destLon,
        origin_name: originName, dest_name: destName,
        user_id: userId,
      })
    : apiCall('/api/start-trip', {
        method: 'POST',
        body: JSON.stringify({
          origin_lat: originLat, origin_lon: originLon,
          dest_lat: destLat,     dest_lon: destLon,
          origin_name: originName, dest_name: destName,
          user_id: userId,
        }),
      });

export const endTrip = (tripId) =>
  isMobileApp()
    ? callShowcase('endTrip', { trip_id: tripId })
    : apiCall('/api/end-trip', {
        method: 'POST',
        body: JSON.stringify({ trip_id: tripId }),
      });

export const checkDeviation = (tripId, currentLat, currentLon) =>
  isMobileApp()
    ? callShowcase('checkDeviation', { trip_id: tripId, current_lat: currentLat, current_lon: currentLon })
    : apiCall('/api/check-deviation', {
        method: 'POST',
        body: JSON.stringify({ trip_id: tripId, current_lat: currentLat, current_lon: currentLon }),
      });

export const getTrip = (tripId) =>
  isMobileApp()
    ? callShowcase('getTrip', { trip_id: tripId })
    : apiCall(`/api/trip/${tripId}`);

export const getActiveTrips = (userId = null) =>
  isMobileApp()
    ? callShowcase('getActiveTrips', { user_id: userId })
    : apiCall(`/api/active-trips${userId ? `?user_id=${userId}` : ''}`);

export const getTripHistory = (userId = 1, limit = 10) =>
  isMobileApp()
    ? callShowcase('getTripHistory', { user_id: userId, limit })
    : apiCall(`/api/trip-history?user_id=${userId}&limit=${limit}`, {}, {
        success: true,
        trips: [],
        count: 0,
      });

// ── SafeRoute ─────────────────────────────────────────────────────────────────
export const getSafestRoute = (originLat, originLon, destLat, destLon) =>
  isMobileApp()
    ? callShowcase('getSafestRoute', { origin_lat: originLat, origin_lon: originLon, dest_lat: destLat, dest_lon: destLon })
    : apiCall('/api/safest-route', {
        method: 'POST',
        body: JSON.stringify({ origin_lat: originLat, origin_lon: originLon, dest_lat: destLat, dest_lon: destLon }),
      }, {
        success: true,
        safest_route: { name: 'Main Road', safety_score: 75, safety_label: 'Safe', distance_km: 8, explanation: 'Fallback route.' },
        shortest_route: { name: 'Direct Route', safety_score: 55, safety_label: 'Moderate', distance_km: 5 },
        alternative_routes: [],
        all_routes_ranked: [],
        explanation: 'Backend unavailable.',
        route_count: 1,
      });

export const submitRouteFeedback = (routeId, rating, isUnsafe = false, comment = '') =>
  isMobileApp()
    ? callShowcase('submitRouteFeedback', { route_id: routeId, rating, is_unsafe_report: isUnsafe, comment })
    : apiCall('/api/submit-route-feedback', {
        method: 'POST',
        body: JSON.stringify({ route_id: routeId, rating, is_unsafe_report: isUnsafe, comment }),
      });

export const getRouteStats = (routeId) =>
  isMobileApp()
    ? callShowcase('getRouteStats', { route_id: routeId })
    : apiCall(`/api/route-stats/${routeId}`);

// ── Emergency ─────────────────────────────────────────────────────────────────
export const triggerEmergency = (lat, lon, userId = 1, userName = 'FeelSafe User', tripId = null, contactPhone = null, riskLevel = 'HIGH', threatText = '') =>
  isMobileApp()
    ? callShowcase('triggerEmergency', { lat, lon, user_id: userId, user_name: userName, trip_id: tripId, contact_phone: contactPhone, risk_level: riskLevel, threat_text: threatText })
    : apiCall('/api/emergency-alert', {
        method: 'POST',
        body: JSON.stringify({ lat, lon, user_id: userId, user_name: userName, trip_id: tripId, contact_phone: contactPhone, risk_level: riskLevel, threat_text: threatText }),
      }, {
        success: true,
        whatsapp_link: `https://wa.me/?text=EMERGENCY+ALERT`,
        maps_link: `https://www.google.com/maps?q=${lat},${lon}`,
        emergency_numbers: { police: '100', ambulance: '108', women_helpline: '1091', national_emergency: '112' },
        nearby_police: [],
        nearby_hospitals: [],
        auto_contacts_notified: [],
        escalation_level: 1,
      });

export const retryEmergency = (lat, lon, previousAttempt, userId = 1) =>
  isMobileApp()
    ? callShowcase('retryEmergency', { lat, lon, previous_attempt: previousAttempt, user_id: userId })
    : apiCall('/api/emergency-retry', {
        method: 'POST',
        body: JSON.stringify({ lat, lon, previous_attempt: previousAttempt, user_id: userId }),
      });

// ── Contacts ──────────────────────────────────────────────────────────────────
export const getContacts = (userId = 1) =>
  isMobileApp()
    ? callShowcase('getContacts', { user_id: userId })
    : apiCall(`/api/contacts?user_id=${userId}`, {}, { success: true, contacts: [], count: 0 });

export const addContact = (name, phone, relation = 'Contact', mediumAlert = true, highAlert = true, userId = 1) =>
  isMobileApp()
    ? callShowcase('addContact', { name, phone, relation, medium_alert_enabled: mediumAlert, high_alert_enabled: highAlert, user_id: userId })
    : apiCall('/api/contacts', {
        method: 'POST',
        body: JSON.stringify({ name, phone, relation, medium_alert_enabled: mediumAlert, high_alert_enabled: highAlert, user_id: userId }),
      });

export const updateContact = (contactId, updates) =>
  isMobileApp()
    ? callShowcase('updateContact', { contactId, updates })
    : apiCall(`/api/contacts/${contactId}`, {
        method: 'PUT',
        body: JSON.stringify(updates),
      });

export const deleteContact = (contactId) =>
  isMobileApp()
    ? callShowcase('deleteContact', { contactId })
    : apiCall(`/api/contacts/${contactId}`, { method: 'DELETE' });

// ── Community ─────────────────────────────────────────────────────────────────
export const getCommunityFeed = (limit = 10) =>
  isMobileApp()
    ? callShowcase('getCommunityFeed', { limit })
    : apiCall(`/api/community/feed?limit=${limit}`, {}, {
        success: true,
        feed: [
          { area: 'Connaught Place', issue: 'Safe zone — CCTV active', severity: 'LOW', color: '#00FF9D', time: '2 min ago', source: 'community_intel' },
          { area: 'MG Road Underpass', issue: 'Poor lighting reported', severity: 'HIGH', color: '#FF3B5C', time: '15 min ago', source: 'community_intel' },
        ],
        count: 2,
      });

export const getCommunityStats = (userId = 1) =>
  isMobileApp()
    ? callShowcase('getCommunityStats', { user_id: userId })
    : apiCall(`/api/community/stats?user_id=${userId}`, {}, {
        success: true,
        stats: { total_trips: 0, active_trips: 0, sos_alerts: 0, community_reports: 0, avg_safety_score: 72 },
      });

// ── Voice ─────────────────────────────────────────────────────────────────────
/**
 * Upload an audio blob for Whisper transcription + threat analysis.
 */
export const analyzeVoice = async (audioBlob, { tripId, userId, lat, lon } = {}) => {
  if (isMobileApp()) {
    try {
      const base64 = await blobToBase64(audioBlob);
      const res = await callShowcase('analyzeVoice', { base64, trip_id: tripId, user_id: userId, lat, lon });
      return res;
    } catch (err) {
      console.error('[API] analyzeVoice (Showcase) failed:', err.message);
      return { success: false, transcript: '', risk_level: 'LOW', error: err.message };
    }
  }

  const form = new FormData();
  form.append('audio', audioBlob, 'recording.webm');
  if (tripId) form.append('trip_id', tripId);
  if (userId) form.append('user_id', userId);
  if (lat)    form.append('lat', lat);
  if (lon)    form.append('lon', lon);
  try {
    const res = await fetch(`${API_BASE}/api/analyze-voice`, { method: 'POST', body: form });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return await res.json();
  } catch (err) {
    console.error('[API] /api/analyze-voice failed:', err.message);
    return { success: false, transcript: '', risk_level: 'LOW', error: err.message };
  }
};

// ── Recordings ────────────────────────────────────────────────────────────────
export const getRecordingsForTrip = (tripId) =>
  isMobileApp()
    ? callShowcase('getRecordingsForTrip', { trip_id: tripId })
    : apiCall(`/api/recordings/${tripId}`, {}, { success: true, recordings: [], count: 0 });

export const getRecordingsForUser = (userId = 1, limit = 10) =>
  isMobileApp()
    ? callShowcase('getRecordingsForUser', { user_id: userId, limit })
    : apiCall(`/api/recordings/user/${userId}?limit=${limit}`, {}, { success: true, recordings: [], count: 0 });

// ── Quick SOS ─────────────────────────────────────────────────────────────────
export const quickSOS = (lat, lon, userId = 1, tripId = null, trigger = 'button') =>
  isMobileApp()
    ? callShowcase('quickSOS', { lat, lon, user_id: userId, trip_id: tripId, trigger })
    : apiCall('/api/quick-sos', {
        method: 'POST',
        body: JSON.stringify({ lat, lon, user_id: userId, trip_id: tripId, trigger }),
      }, {
        success: true,
        escalation_level: 3,
        whatsapp_link: `https://wa.me/?text=EMERGENCY`,
        anchors: {},
        auto_contacts_notified: [],
      });

// ── Safety Anchors ────────────────────────────────────────────────────────────
export const getSafetyAnchors = (lat, lon, radiusM = 1000) =>
  isMobileApp()
    ? callShowcase('getSafetyAnchors', { lat, lon, radius_m: radiusM })
    : apiCall('/api/safety-anchors', {
        method: 'POST',
        body: JSON.stringify({ lat, lon, radius_m: radiusM }),
      }, {
        success: true,
        anchors: { police: [], hospital: [], pharmacy: [], supermarket: [] },
        total_found: 0,
        nearest_police_km: null,
        nearest_hospital_km: null,
      });

// ── Detailed Trip Feedback ────────────────────────────────────────────────────
export const submitDetailedFeedback = (tripId, ratings, userId = 1) =>
  isMobileApp()
    ? callShowcase('submitDetailedFeedback', {
        trip_id:              tripId,
        user_id:              userId,
        safety_rating:        ratings.safety   ?? 3,
        lighting_rating:      ratings.lighting  ?? 3,
        crowd_rating:         ratings.crowd     ?? 3,
        incident_reported:    ratings.incident  ?? false,
        incident_description: ratings.incidentDesc ?? null,
      })
    : apiCall('/api/trip-detailed-feedback', {
        method: 'POST',
        body: JSON.stringify({
          trip_id:              tripId,
          user_id:              userId,
          safety_rating:        ratings.safety   ?? 3,
          lighting_rating:      ratings.lighting  ?? 3,
          crowd_rating:         ratings.crowd     ?? 3,
          incident_reported:    ratings.incident  ?? false,
          incident_description: ratings.incidentDesc ?? null,
        }),
      });
