CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS charities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    featured BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    plan_type VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    stripe_customer_id VARCHAR(120),
    stripe_subscription_id VARCHAR(120),
    start_date DATE NOT NULL,
    renewal_date DATE,
    cancelled_at DATE,
    amount DECIMAL(10,2) NOT NULL,
    charity_percentage DECIMAL(5,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS scores (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    score_value INT NOT NULL CHECK (score_value BETWEEN 1 AND 45),
    score_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS draws (
    id BIGSERIAL PRIMARY KEY,
    draw_month DATE NOT NULL,
    mode VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    published_at TIMESTAMP,
    jackpot_rollover DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS draw_numbers (
    id BIGSERIAL PRIMARY KEY,
    draw_id BIGINT NOT NULL REFERENCES draws(id) ON DELETE CASCADE,
    number_value INT NOT NULL CHECK (number_value BETWEEN 1 AND 45)
);

CREATE TABLE IF NOT EXISTS winners (
    id BIGSERIAL PRIMARY KEY,
    draw_id BIGINT NOT NULL REFERENCES draws(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    match_count INT NOT NULL CHECK (match_count IN (3,4,5)),
    prize_amount DECIMAL(12,2) NOT NULL,
    proof_url VARCHAR(500),
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payout_status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE IF NOT EXISTS monthly_pool_snapshots (
    id BIGSERIAL PRIMARY KEY,
    draw_id BIGINT NOT NULL REFERENCES draws(id),
    total_pool DECIMAL(12,2) NOT NULL,
    pool_3_match DECIMAL(12,2) NOT NULL,
    pool_4_match DECIMAL(12,2) NOT NULL,
    pool_5_match DECIMAL(12,2) NOT NULL
);
