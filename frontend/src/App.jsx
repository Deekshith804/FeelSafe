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
    checkHealth()
      .then((data) => {
        if (data?.status === 'ok') {
          console.log('[FeelSafe] ✅ Backend connected:', data);
          alert('Backend Connected successfully!');
        } else {
          console.warn('[FeelSafe] ⚠️ Backend responded but status unexpected:', data);
          alert('Backend responded with unexpected status.');
        }
      })
      .catch((err) => {
        console.warn('[FeelSafe] ❌ Backend unreachable at startup:', err.message);
        alert('Backend Unreachable: ' + err.message);
      });
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
