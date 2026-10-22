import { Router } from 'express';
import { supabase, isSupabaseConfigured, inMemoryStore } from '../supabase.js';

const router = Router();

// GET /api/insights — City-scale mobility analytics for research dashboards
router.get('/', async (req, res) => {
  try {
    if (isSupabaseConfigured) {
      // First try the pre-calculated view
      const { data: viewData, error: viewError } = await supabase
        .from('mobility_insights')
        .select('*')
        .maybeSingle();

      if (!viewError && viewData) {
        return res.json({
          totalTrips: Number(viewData.total_trips) || 0,
          totalDistanceKm: Math.round((Number(viewData.total_distance_km) || 0) * 10) / 10,
          totalCo2SavedKg: Math.round((Number(viewData.total_co2_saved_kg) || 0) * 100) / 100,
          averageConfidence: Math.round((Number(viewData.average_confidence) || 0) * 100) / 100,
          verifiedTripsCount: Number(viewData.verified_trips_count) || 0,
          verificationRatePct: Number(viewData.verification_rate_pct) || 0,
          modeBreakdown: {
            BUS: Number(viewData.bus_trips) || 0,
            WALK: Number(viewData.walk_trips) || 0,
            TRAIN: Number(viewData.train_trips) || 0,
            CAR: Number(viewData.car_trips) || 0,
            BIKE: Number(viewData.bike_trips) || 0,
            AUTO: Number(viewData.auto_trips) || 0,
            BICYCLE: Number(viewData.bicycle_trips) || 0
          }
        });
      }

      // Fallback direct query on trips table
      const { data: trips, error: tripsError } = await supabase.from('trips').select('*');
      if (tripsError) throw tripsError;
      return res.json(calculateInsights(trips || []));
    }

    return res.json(calculateInsights(inMemoryStore.trips));
  } catch (err) {
    console.error('Error generating insights:', err);
    return res.status(500).json({ error: 'Failed to compute mobility insights', details: err.message });
  }
});

function calculateInsights(trips) {
  const totalTrips = trips.length;
  const totalDistanceKm = trips.reduce((sum, t) => sum + (Number(t.distance_km) || 0), 0);
  const totalCo2SavedKg = trips.reduce((sum, t) => sum + (Number(t.co2_saved_kg) || 0), 0);
  const verifiedCount = trips.filter(t => t.is_verified_by_user).length;
  const avgConfidence = totalTrips > 0
    ? trips.reduce((sum, t) => sum + (Number(t.confidence_score) || 0), 0) / totalTrips
    : 0;

  const modeBreakdown = {
    WALK: 0,
    BUS: 0,
    TRAIN: 0,
    CAR: 0,
    BIKE: 0,
    AUTO: 0,
    BICYCLE: 0,
    OTHER: 0
  };

  trips.forEach(t => {
    const m = (t.primary_mode || 'OTHER').toUpperCase();
    if (modeBreakdown[m] !== undefined) {
      modeBreakdown[m]++;
    } else {
      modeBreakdown.OTHER++;
    }
  });

  return {
    totalTrips,
    totalDistanceKm: Math.round(totalDistanceKm * 10) / 10,
    totalCo2SavedKg: Math.round(totalCo2SavedKg * 100) / 100,
    averageConfidence: Math.round(avgConfidence * 100) / 100,
    verifiedTripsCount: verifiedCount,
    verificationRatePct: totalTrips > 0 ? Math.round((verifiedCount / totalTrips) * 100) : 0,
    modeBreakdown
  };
}

export default router;
