CREATE TABLE IF NOT EXISTS user_charity_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    charity_id BIGINT NOT NULL REFERENCES charities(id),
    percentage DECIMAL(5,2) NOT NULL
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
