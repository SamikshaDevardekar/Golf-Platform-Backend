# Golf Platform MVP

Full-stack MVP for subscription golf scoring, monthly draws, charity contributions, and admin operations.

## Stack
- Backend: Spring Boot + PostgreSQL (Supabase Postgres compatible)
- Frontend: React + Vite (deploy to Vercel)
- Payments: Stripe Checkout and webhook endpoint

## Local run

### Backend
1. Create PostgreSQL DB.
2. Set env vars:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET`
   - `STRIPE_SECRET_KEY`
   - `STRIPE_WEBHOOK_SECRET`
3. Run:
   - `./mvnw spring-boot:run`

### Frontend
1. `cd frontend`
2. `npm install`
3. `npm run dev`

## Core endpoints
- Auth: `/auth/register`, `/auth/login`
- Subscription: `/subscriptions/activate`, `/subscriptions/status`
- Scores: `/scores` (POST, GET)
- Draw Admin: `/admin/draw/simulate/random`, `/admin/draw/simulate/algorithmic`, `/admin/draw/{id}/publish`
- Charity: `/charities`, `/charities/admin`, `/charities/select`
- Winners: `/winners/admin`, `/winners/admin/{id}/verify`, `/winners/admin/{id}/paid`
- Reports: `/admin/reports`

## Deployment
- Frontend: deploy `frontend` folder to Vercel.
- Backend: deploy Spring app to Railway/Render/Fly.
- DB: use Supabase Postgres connection string for backend datasource.
