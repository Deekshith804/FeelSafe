// src/mobile/ShakeSOSController.js
// ─────────────────────────────────────────────────────────────────
// Controller that utilizes cordova-plugin-shake to detect shakes.
// Must only run on native devices when enabled by preferences.
// ─────────────────────────────────────────────────────────────────

import { Capacitor } from '@capacitor/core';
import { triggerFullEmergencySequence } from './services/emergencyService';

let isWatching = false;
let cooldownActive = false;

// We need a debounced listener to prevent multiple triggers
const onShakeDetected = () => {
  if (cooldownActive) return;

  console.log("Shake detected");
  cooldownActive = true;
  
  // Trigger emergency immediately
  triggerFullEmergencySequence();

  // 10-second cooldown as requested
  setTimeout(() => {
    cooldownActive = false;
  }, 10000);
};

export const startShakeListener = () => {
  if (!Capacitor.isNativePlatform()) {
    console.log('[ShakeController] Non-native platform, shake detection unavailable.');
    return;
  }

  if (isWatching) return;

  // Use the global cordova shake object
  if (window.shake) {
    // 30 is the default sensitivity (represents aggressive shake)
    window.shake.startWatch(onShakeDetected, 30);
    isWatching = true;
    console.log('[ShakeController] Started watching for shakes.');
  } else {
    console.warn('[ShakeController] window.shake is undefined. cordova-plugin-shake may not be loaded properly.');
  }
};

export const stopShakeListener = () => {
  if (!Capacitor.isNativePlatform()) return;
  
  if (window.shake && isWatching) {
    window.shake.stopWatch();
    isWatching = false;
    console.log('[ShakeController] Stopped watching for shakes.');
  }
};
