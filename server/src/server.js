import express from 'express';
import cors from 'cors';
import morgan from 'morgan';
import dotenv from 'dotenv';
import { isSupabaseConfigured } from './supabase.js';
import tripsRouter from './routes/trips.js';
import telemetryRouter from './routes/telemetry.js';
import insightsRouter from './routes/insights.js';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware
app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));
app.use(morgan('dev'));

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({
    status: 'healthy',
    service: 'YATRA Mobility Cloud Backend (SIH 2026)',
    timestamp: new Date().toISOString(),
    supabaseConnected: isSupabaseConfigured,
    storageEngine: isSupabaseConfigured ? 'Supabase PostgreSQL Cloud' : 'In-Memory Fallback'
  });
});

app.get('/', (req, res) => {
  res.json({
    name: 'YATRA Mobility Research Server',
    version: '1.0.0',
    documentation: {
      health: 'GET /health',
      trips: 'GET /api/trips',
      singleTrip: 'GET /api/trips/:id',
      syncTrips: 'POST /api/trips/sync',
      telemetry: 'POST /api/telemetry',
      insights: 'GET /api/insights'
    },
    supabaseStatus: isSupabaseConfigured ? 'Connected' : 'Pending configuration in .env'
  });
});

// API Routes
app.use('/api/trips', tripsRouter);
app.use('/api/telemetry', telemetryRouter);
app.use('/api/insights', insightsRouter);

// 404 Handler
app.use((req, res) => {
  res.status(404).json({ error: 'Endpoint not found', path: req.originalUrl });
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error('[Unhandled Server Error]:', err);
  res.status(500).json({ error: 'Internal Server Error', message: err.message });
});

// Start Server
app.listen(PORT, '0.0.0.0', () => {
  console.log('================================================================');
  console.log(`🚀 YATRA Backend Server running on http://localhost:${PORT}`);
  console.log(`📡 For Android Emulator use: http://10.0.2.2:${PORT}`);
  console.log(`☁️  Supabase Database: ${isSupabaseConfigured ? 'ONLINE (Cloud Connected)' : 'LOCAL FALLBACK (See server/.env)'}`);
  console.log('================================================================');
});
