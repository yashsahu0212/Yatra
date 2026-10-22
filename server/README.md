# 🌐 YATRA Backend Server & Supabase Cloud Integration

> Cloud sync gateway and research database for **YATRA (Smart India Hackathon 2026)**.  
> Synchronizes passive and verified multimodal trips, sensor telemetry, and urban mobility research insights from the Android app to Supabase (PostgreSQL).

---

## 🚀 Quick Start (Local Development)

### 1. Install & Run Server
```bash
cd server
npm install
npm run dev
```

The server starts on `http://localhost:5000` (or `http://10.0.2.2:5000` from Android Emulator).  
*Note: If Supabase credentials are not configured in `.env`, the server automatically operates in local memory fallback mode so the app works immediately.*

---

## 🗄️ Setting Up Supabase (Free Tier in 3 Minutes)

1. **Create Account / Project**:
   - Go to [supabase.com](https://supabase.com) and create a free account.
   - Click **"New Project"**, name it `yatra-mobility`, set a secure database password, and choose the nearest region (e.g. Mumbai / India).
2. **Execute Database Schema**:
   - In your Supabase dashboard, click **SQL Editor** on the left menu.
   - Click **"New Query"**, copy the entire contents of `server/supabase_schema.sql`, and click **Run**.
   - This creates:
     - `public.trips` (with indexed multimodal fields and duplicate prevention)
     - `public.telemetry_logs` (sensor telemetry and calibration status)
     - `public.mobility_insights` (aggregated statistics view for city planners)
     - Row-Level Security (RLS) policies allowing secure mobile sync.
3. **Get Your API Keys**:
   - Go to **Project Settings** → **API**.
   - Copy **Project URL** and **anon / public** key.
4. **Update `server/.env`**:
   ```env
   PORT=5000
   SUPABASE_URL=https://xxxxxxxxxxxx.supabase.co
   SUPABASE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```
5. Restart your server (`npm run dev`). You'll see:
   ```
   [Supabase] ✅ Connected to Supabase Cloud instance: https://...
   ```

---

## 📡 REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/health` | Server status & Supabase connection health |
| `GET` | `/api/trips` | List all synced trips (supports `?mode=BUS&verified=true`) |
| `GET` | `/api/trips/:id` | Fetch single trip details |
| `POST` | `/api/trips/sync` | **Batch Sync Endpoint** from Android app |
| `POST` | `/api/telemetry` | Upload sensor diagnostics packet |
| `GET` | `/api/insights` | City-wide modal share %, total km, and CO₂ savings |

---

## ☁️ Free 1-Click Cloud Deployment (Render / Railway)

### Deploy to Render (Free Web Service)
1. Push this repository to GitHub.
2. Go to [render.com](https://render.com) and sign in.
3. Click **New +** → **Web Service**.
4. Connect your repo and configure:
   - **Root Directory**: `server`
   - **Build Command**: `npm install`
   - **Start Command**: `node src/server.js`
   - **Environment Variables**: Add `SUPABASE_URL` and `SUPABASE_KEY`.
5. Click **Deploy**. Render will give you a public HTTPS URL like:
   `https://yatra-backend.onrender.com`
6. In the YATRA Android app, you can enter this URL in the **Offline Sync** screen to sync directly from any physical Android phone!
