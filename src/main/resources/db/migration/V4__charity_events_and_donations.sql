ALTER TABLE charities
    ADD COLUMN IF NOT EXISTS upcoming_events TEXT;

CREATE TABLE IF NOT EXISTS independent_donations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    charity_id BIGINT NOT NULL REFERENCES charities(id),
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
