import { createClient } from '@supabase/supabase-js';
import dotenv from 'dotenv';

dotenv.config();

const supabaseUrl = process.env.SUPABASE_URL || '';
const supabaseKey = process.env.SUPABASE_KEY || '';

export const isSupabaseConfigured = Boolean(
  supabaseUrl &&
  supabaseUrl.startsWith('http') &&
  !supabaseUrl.includes('your-project-id') &&
  supabaseKey &&
  !supabaseKey.includes('your-supabase-')
);

export const supabase = isSupabaseConfigured
  ? createClient(supabaseUrl, supabaseKey, {
      auth: { persistSession: false }
    })
  : null;

if (isSupabaseConfigured) {
  console.log(`[Supabase] ✅ Connected to Supabase Cloud instance: ${supabaseUrl}`);
} else {
  console.log('[Supabase] ⚠️  Running in Local Fallback mode (Placeholder credentials detected in server/.env).');
  console.log('[Supabase] 👉 To connect to live Supabase, enter your SUPABASE_URL and SUPABASE_KEY in server/.env.');
}

// In-Memory store for when Supabase credentials haven't been provided yet
export const inMemoryStore = {
  trips: [
    {
      id: 1,
      client_trip_id: 1,
      device_user_id: 'research_volunteer_01',
      origin_name: 'Home (Sector 14)',
      destination_name: 'National Institute of Technology',
      start_time: Date.now() - 4 * 3600 * 1000,
      end_time: Date.now() - (4 * 3600 - 31 * 60) * 1000,
      start_time_formatted: '08:42 AM',
      end_time_formatted: '09:13 AM',
      date_label: 'Today',
      distance_km: 8.4,
      duration_minutes: 31,
      primary_mode: 'BUS',
      modes_chain: ['WALK', 'BUS', 'WALK'],
      segments_json: 'Walk:0.6km,Bus:7.2km,Walk:0.6km',
      confidence_score: 0.96,
      is_verified_by_user: true,
      purpose: 'EDUCATION',
      companions: 'ALONE',
      gps_point_count: 284,
      co2_saved_kg: 1.45,
      raw_sensor_quality: 98,
      created_at: new Date().toISOString()
    }
  ],
  telemetry: []
};
