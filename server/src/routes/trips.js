import { Router } from 'express';
import { supabase, isSupabaseConfigured, inMemoryStore } from '../supabase.js';

const router = Router();

// Helper to normalize client trip payload to DB column format
function mapTripToDb(trip, deviceUserId) {
  return {
    client_trip_id: trip.clientTripId ?? trip.id,
    device_user_id: deviceUserId || trip.deviceUserId || 'research_volunteer_01',
    origin_name: trip.originName || 'Unknown Origin',
    destination_name: trip.destinationName || 'Unknown Destination',
    start_time: trip.startTime || Date.now(),
    end_time: trip.endTime || Date.now(),
    start_time_formatted: trip.startTimeFormatted || '',
    end_time_formatted: trip.endTimeFormatted || '',
    date_label: trip.dateLabel || 'Today',
    distance_km: Number(trip.distanceKm) || 0.0,
    duration_minutes: Number(trip.durationMinutes) || 0,
    primary_mode: trip.primaryMode || 'WALK',
    modes_chain: Array.isArray(trip.modesChain) ? trip.modesChain : [],
    segments_json: trip.segmentsJson || '',
    confidence_score: Number(trip.confidenceScore) || 1.0,
    is_verified_by_user: Boolean(trip.isVerifiedByUser),
    purpose: trip.purpose || null,
    companions: trip.companions || null,
    gps_point_count: Number(trip.gpsPointCount) || 0,
    co2_saved_kg: Number(trip.co2SavedKg) || 0.0,
    raw_sensor_quality: Number(trip.rawSensorQuality) || 100,
    synced_at: new Date().toISOString()
  };
}

// GET /api/trips — Retrieve all trips (with optional mode and date filtering)
router.get('/', async (req, res) => {
  try {
    const { mode, verified } = req.query;

    if (isSupabaseConfigured) {
      let query = supabase.from('trips').select('*').order('start_time', { ascending: false });
      
      if (mode) {
        query = query.eq('primary_mode', mode.toUpperCase());
      }
      if (verified !== undefined) {
        query = query.eq('is_verified_by_user', verified === 'true');
      }

      const { data, error } = await query;
      if (error) throw error;
      return res.json(data);
    }

    // In-memory fallback
    let trips = [...inMemoryStore.trips];
    if (mode) {
      trips = trips.filter(t => t.primary_mode === mode.toUpperCase());
    }
    if (verified !== undefined) {
      trips = trips.filter(t => t.is_verified_by_user === (verified === 'true'));
    }
    return res.json(trips);
  } catch (err) {
    console.error('Error fetching trips:', err);
    return res.status(500).json({ error: 'Failed to fetch trips', details: err.message });
  }
});

// GET /api/trips/:id — Retrieve single trip
router.get('/:id', async (req, res) => {
  try {
    const tripId = Number(req.params.id);

    if (isSupabaseConfigured) {
      const { data, error } = await supabase
        .from('trips')
        .select('*')
        .eq('id', tripId)
        .single();

      if (error) {
        return res.status(404).json({ error: 'Trip not found' });
      }
      return res.json(data);
    }

    const trip = inMemoryStore.trips.find(t => t.id === tripId || t.client_trip_id === tripId);
    if (!trip) {
      return res.status(404).json({ error: 'Trip not found' });
    }
    return res.json(trip);
  } catch (err) {
    return res.status(500).json({ error: 'Failed to retrieve trip', details: err.message });
  }
});

// POST /api/trips/sync — Mobile Batch Sync Endpoint
router.post('/sync', async (req, res) => {
  try {
    const { trips = [], telemetry, deviceUserId = 'research_volunteer_01' } = req.body;

    if (!Array.isArray(trips) || trips.length === 0) {
      return res.status(400).json({
        success: false,
        message: 'No trips provided in sync payload',
        syncedTripIds: []
      });
    }

    const records = trips.map(t => mapTripToDb(t, deviceUserId));
    const clientIds = trips.map(t => t.clientTripId ?? t.id);

    if (isSupabaseConfigured) {
      // Upsert trips into Supabase
      const { data, error } = await supabase
        .from('trips')
        .upsert(records, { onConflict: 'client_trip_id,device_user_id' })
        .select('client_trip_id, id');

      if (error) {
        console.error('[Supabase Upsert Error]:', error);
        throw error;
      }

      // Record optional telemetry packet
      if (telemetry) {
        await supabase.from('telemetry_logs').insert([{
          device_user_id: deviceUserId,
          sensor_fusion_status: telemetry.sensorFusionStatus || 'Calibrated',
          motion_cadence_spm: telemetry.motionCadenceSpm || 0,
          gps_accuracy_meters: telemetry.gpsAccuracyMeters || 0.0,
          battery_optimization_ignored: telemetry.batteryOptimizationIgnored ?? true,
          unsynced_location_points: telemetry.unsyncedLocationPoints || 0
        }]);
      }

      return res.json({
        success: true,
        message: `Successfully synchronized ${trips.length} trip(s) to Supabase cloud database`,
        syncedTripIds: clientIds,
        serverTimestamp: Date.now(),
        backend: 'supabase'
      });
    }

    // In-memory fallback mode
    records.forEach(r => {
      const existingIdx = inMemoryStore.trips.findIndex(
        t => t.client_trip_id === r.client_trip_id && t.device_user_id === r.device_user_id
      );
      if (existingIdx >= 0) {
        inMemoryStore.trips[existingIdx] = { ...inMemoryStore.trips[existingIdx], ...r };
      } else {
        inMemoryStore.trips.push({ id: inMemoryStore.trips.length + 1, ...r });
      }
    });

    if (telemetry) {
      inMemoryStore.telemetry.push({
        id: inMemoryStore.telemetry.length + 1,
        device_user_id: deviceUserId,
        ...telemetry,
        recorded_at: new Date().toISOString()
      });
    }

    return res.json({
      success: true,
      message: `Successfully synchronized ${trips.length} trip(s) to local research gateway`,
      syncedTripIds: clientIds,
      serverTimestamp: Date.now(),
      backend: 'in_memory_fallback'
    });
  } catch (err) {
    console.error('Error in /api/trips/sync:', err);
    return res.status(500).json({
      success: false,
      message: 'Failed to synchronize trips',
      details: err.message
    });
  }
});

// POST /api/trips — Single Trip Creation
router.post('/', async (req, res) => {
  try {
    const dbRecord = mapTripToDb(req.body, req.body.deviceUserId);

    if (isSupabaseConfigured) {
      const { data, error } = await supabase
        .from('trips')
        .insert([dbRecord])
        .select()
        .single();

      if (error) throw error;
      return res.status(201).json(data);
    }

    const newTrip = { id: inMemoryStore.trips.length + 1, ...dbRecord };
    inMemoryStore.trips.push(newTrip);
    return res.status(201).json(newTrip);
  } catch (err) {
    return res.status(500).json({ error: 'Failed to create trip', details: err.message });
  }
});

export default router;
