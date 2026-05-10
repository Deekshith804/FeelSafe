# FeelSafe Production Deployment Guide

This document outlines the steps to deploy the FeelSafe backend to Render and sync the Android application with the production environment.

## 1. Backend Deployment (Render)

1. **Create a Web Service** on Render.
2. **Connect your Repository** containing the `backend` folder.
3. **Build Command**: `pip install -r requirements.txt` (Run from the `backend` directory).
4. **Start Command**: `gunicorn "app:create_app()"` (Run from the `backend` directory).
5. **Environment Variables**: Add all variables from your local `.env` to the Render Dashboard:
   - `GROQ_API_KEY`
   - `TWILIO_ACCOUNT_SID`
   - `TWILIO_AUTH_TOKEN`
   - `TELEGRAM_BOT_TOKEN`
   - `TELEGRAM_CHAT_ID`
   - `FLASK_HOST=0.0.0.0`
   - `FLASK_PORT=10000` (Render default)

## 2. Frontend & Android Sync

After updating the production URL in `src/services/api.js`, follow these steps to rebuild the app:

1. **Build the Frontend**:
   ```bash
   cd frontend
   npm run build
   ```
2. **Sync Capacitor**:
   ```bash
   npx cap sync android
   ```
3. **Build APK**: Open the `android` folder in Android Studio and build the production APK.

## 3. Important Notes
- **No Localhost**: The app now points to `https://feelsafe-backend.onrender.com`.
- **Offline Frontend**: The frontend is now bundled inside the APK. It will load even if your laptop is off.
- **Standalone App**: The app no longer depends on local Wi-Fi or your laptop's IP address.
