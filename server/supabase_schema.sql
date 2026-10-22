-- =========================================================================
-- YATRA (SIH 2026) — Supabase PostgreSQL Database Schema
-- Urban Mobility & Multimodal Travel Behaviour Research Platform
-- =========================================================================

-- 1. Enable UUID Extension (standard in PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Trips Table
-- Stores passive/detected and user-verified multimodal journeys
CREATE TABLE IF NOT EXISTS public.trips (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    client_trip_id BIGINT NOT NULL,
    device_user_id TEXT NOT NULL DEFAULT 'research_volunteer_01',
    origin_name TEXT NOT NULL,
    destination_name TEXT NOT NULL,
    start_time BIGINT NOT NULL,
    end_time BIGINT NOT NULL,
    start_time_formatted TEXT NOT NULL,
    end_time_formatted TEXT NOT NULL,
    date_label TEXT NOT NULL,
    distance_km DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    duration_minutes INT NOT NULL DEFAULT 0,
    primary_mode TEXT NOT NULL,
    modes_chain JSONB NOT NULL DEFAULT '[]'::jsonb,
    segments_json TEXT NOT NULL DEFAULT '',
    confidence_score REAL NOT NULL DEFAULT 1.0,
    is_verified_by_user BOOLEAN NOT NULL DEFAULT FALSE,
    purpose TEXT,
    companions TEXT,
    gps_point_count INT NOT NULL DEFAULT 0,
    co2_saved_kg DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    raw_sensor_quality INT NOT NULL DEFAULT 100,
    synced_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Prevent duplicates from device re-syncing
    CONSTRAINT unique_device_client_trip UNIQUE (client_trip_id, device_user_id)
);

-- Indexes for frequent queries (by mode, date, sync)
CREATE INDEX IF NOT EXISTS idx_trips_primary_mode ON public.trips(primary_mode);
CREATE INDEX IF NOT EXISTS idx_trips_start_time ON public.trips(start_time DESC);
CREATE INDEX IF NOT EXISTS idx_trips_device_user ON public.trips(device_user_id);

-- 3. Sensor Telemetry Logs Table
-- High-rate periodic sensor calibration and sync diagnostics
CREATE TABLE IF NOT EXISTS public.telemetry_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    device_user_id TEXT NOT NULL DEFAULT 'research_volunteer_01',
    sensor_fusion_status TEXT,
    motion_cadence_spm INT DEFAULT 0,
    gps_accuracy_meters REAL DEFAULT 0.0,
    battery_optimization_ignored BOOLEAN DEFAULT TRUE,
    unsynced_location_points INT DEFAULT 0,
    is_online BOOLEAN DEFAULT TRUE,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_telemetry_device ON public.telemetry_logs(device_user_id);
CREATE INDEX IF NOT EXISTS idx_telemetry_recorded ON public.telemetry_logs(recorded_at DESC);

-- 4. Mobility Analytics View
-- Aggregated statistical view for urban planners & SIH research jury
CREATE OR REPLACE VIEW public.mobility_insights AS
SELECT
    COUNT(*) AS total_trips,
    COALESCE(SUM(distance_km), 0) AS total_distance_km,
    COALESCE(SUM(co2_saved_kg), 0) AS total_co2_saved_kg,
    COALESCE(AVG(confidence_score), 0) AS average_confidence,
    COUNT(CASE WHEN is_verified_by_user = TRUE THEN 1 END) AS verified_trips_count,
    ROUND(
        (COUNT(CASE WHEN is_verified_by_user = TRUE THEN 1 END)::numeric / NULLIF(COUNT(*), 0)::numeric) * 100, 
        2
    ) AS verification_rate_pct,
    COUNT(CASE WHEN primary_mode = 'BUS' THEN 1 END) AS bus_trips,
    COUNT(CASE WHEN primary_mode = 'WALK' THEN 1 END) AS walk_trips,
    COUNT(CASE WHEN primary_mode = 'TRAIN' THEN 1 END) AS train_trips,
    COUNT(CASE WHEN primary_mode = 'CAR' THEN 1 END) AS car_trips,
    COUNT(CASE WHEN primary_mode = 'BIKE' THEN 1 END) AS bike_trips,
    COUNT(CASE WHEN primary_mode = 'AUTO' THEN 1 END) AS auto_trips,
    COUNT(CASE WHEN primary_mode = 'BICYCLE' THEN 1 END) AS bicycle_trips
FROM public.trips;

-- 5. Row Level Security (RLS) Setup
-- Allows anonymous and authenticated submissions for research surveys
ALTER TABLE public.trips ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.telemetry_logs ENABLE ROW LEVEL SECURITY;

-- Allow public read access to trips
CREATE POLICY "Allow public read on trips"
    ON public.trips
    FOR SELECT
    TO anon, authenticated
    USING (true);

-- Allow public insert/upsert for mobile sync
CREATE POLICY "Allow public insert on trips"
    ON public.trips
    FOR INSERT
    TO anon, authenticated
    WITH CHECK (true);

CREATE POLICY "Allow public update on trips"
    ON public.trips
    FOR UPDATE
    TO anon, authenticated
    USING (true)
    WITH CHECK (true);

-- Allow public telemetry logging
CREATE POLICY "Allow public read on telemetry"
    ON public.telemetry_logs
    FOR SELECT
    TO anon, authenticated
    USING (true);

CREATE POLICY "Allow public insert on telemetry"
    ON public.telemetry_logs
    FOR INSERT
    TO anon, authenticated
    WITH CHECK (true);
