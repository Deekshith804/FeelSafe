// src/mobile/mobileOnly.js
// ─────────────────────────────────────────────────────────────────
// Single source of truth: is this code running inside the real
// Capacitor Android app, or in a browser/desktop tab?
//
// All mobile-only features MUST call isMobileApp() before doing
// anything native. This keeps the web build completely unaffected.
// ─────────────────────────────────────────────────────────────────

import { Capacitor } from '@capacitor/core';

/**
 * Returns true only when running inside the Capacitor Android (or iOS) shell.
 * Always false in a regular browser tab — web dashboard stays untouched.
 */
export function isMobileApp() {
  return Capacitor.isNativePlatform();
}

/**
 * Returns true only on Android specifically.
 */
export function isAndroid() {
  return Capacitor.getPlatform() === 'android';
}

/**
 * Returns true only for the Android Capacitor app (the combo we care about).
 */
export function isAndroidApp() {
  return isMobileApp() && isAndroid();
}
