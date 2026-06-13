import { useEffect } from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import StartTrip from './pages/StartTrip';
import Emergency from './pages/Emergency';
import SafeRoute from './pages/SafeRoute';
import Dashboard from './pages/Dashboard';
import { checkHealth } from './services/api';
// Mobile-only: Shake-to-SOS (self-guards on web, returns null)
import MobileEmergencyOverlay from './mobile/components/MobileEmergencyOverlay';

function App() {
  // ── Backend connectivity test on startup ──────────────────────────────────
  useEffect(() => {
    const BACKEND = import.meta.env.VITE_API_BASE || 'https://hackathon-project-w4aa.onrender.com';

    // Render free-tier instances spin down after inactivity — retry once after 8 s
    const tryConnect = (attempt = 1) => {
      checkHealth()
        .then((data) => {
          if (data?.status === 'ok' || data?.status === 'active') {
            console.log('[FeelSafe] ✅ Backend connected:', data);
          } else {
            console.warn('[FeelSafe] ⚠️ Backend responded but status unexpected:', data?.status);
          }
        })
        .catch((err) => {
          if (attempt < 2) {
            console.warn(`[FeelSafe] ⏳ Backend cold-starting, retrying in 8 s… (${err.message})`);
            setTimeout(() => tryConnect(2), 8000);
          } else {
            console.error(`[FeelSafe] ❌ Backend unreachable: ${err.message}\nURL: ${BACKEND}/health`);
            // Only alert on second failure so cold-start is silent to the user
            alert(`FeelSafe backend is temporarily unavailable.\n\nURL: ${BACKEND}\nError: ${err.message}\n\nTry again in a few seconds.`);
          }
        });
    };

    tryConnect();
  }, []);

  return (
    <div className="min-h-screen bg-[#0B1020] text-white">
      {/* Mobile-only: Shake-to-SOS overlay — renders null on web */}
      <MobileEmergencyOverlay />

      <Navbar />
      <main className="pb-20 md:pb-0">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/start-trip" element={<StartTrip />} />
          <Route path="/emergency" element={<Emergency />} />
          <Route path="/safe-route" element={<SafeRoute />} />
          <Route path="/dashboard" element={<Dashboard />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
