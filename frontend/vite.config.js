import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  define: {
    // Inject backend URL at build time — change this when IP changes
    __API_BASE__: JSON.stringify(process.env.VITE_API_BASE || 'https://hackathon-project-w4aa.onrender.com'),
  },
  server: {
    host: true,   // listen on 0.0.0.0 so mobile devices can reach this dev server
    port: 5173,
  },
})
