import { Router } from 'express';
import { supabase, isSupabaseConfigured, inMemoryStore } from '../supabase.js';

const router = Router();

// POST /api/telemetry — Ingest sensor telemetry packet
router.post('/', async (req, res) => {
  try {
    const {
      deviceUserId = 'research_volunteer_01',
      sensorFusionStatus = 'Calibrated',
      motionCadenceSpm = 0,
      gpsAccuracyMeters = 0.0,
      batteryOptimizationIgnored = true,
      unsyncedLocationPoints = 0,
      isOnline = true
    } = req.body;

    const payload = {
      device_user_id: deviceUserId,
      sensor_fusion_status: sensorFusionStatus,
      motion_cadence_spm: Number(motionCadenceSpm) || 0,
      gps_accuracy_meters: Number(gpsAccuracyMeters) || 0.0,
      battery_optimization_ignored: Boolean(batteryOptimizationIgnored),
      unsynced_location_points: Number(unsyncedLocationPoints) || 0,
      is_online: Boolean(isOnline),
      recorded_at: new Date().toISOString()
    };

    if (isSupabaseConfigured) {
      const { data, error } = await supabase
        .from('telemetry_logs')
        .insert([payload])
        .select()
        .single();

      if (error) throw error;
      return res.status(201).json({ success: true, data });
    }

    const record = { id: inMemoryStore.telemetry.length + 1, ...payload };
    inMemoryStore.telemetry.push(record);
    return res.status(201).json({ success: true, data: record });
  } catch (err) {
    return res.status(500).json({ error: 'Failed to record telemetry', details: err.message });
  }
});

// GET /api/telemetry — Fetch recent telemetry logs
router.get('/', async (req, res) => {
  try {
    if (isSupabaseConfigured) {
      const { data, error } = await supabase
        .from('telemetry_logs')
        .select('*')
        .order('recorded_at', { ascending: false })
        .limit(50);

      if (error) throw error;
      return res.json(data);
    }

    return res.json(inMemoryStore.telemetry.slice(-50).reverse());
  } catch (err) {
    return res.status(500).json({ error: 'Failed to fetch telemetry', details: err.message });
  }
});

export default router;
