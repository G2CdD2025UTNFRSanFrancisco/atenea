-- V5__create_transition_tokens.sql

CREATE TABLE transition_tokens (
    id               BIGINT PRIMARY KEY,
    account_id       BIGINT    NOT NULL,
    device_id        TEXT      NOT NULL,
    purpose          TEXT      NOT NULL,
    token_hash       TEXT      NOT NULL UNIQUE,
    created_at       TIMESTAMP NOT NULL,
    expires_at       TIMESTAMP NOT NULL,
    consumed_at      TIMESTAMP,
    revoked_at       TIMESTAMP
);

CREATE INDEX idx_transition_tokens_account
    ON transition_tokens (account_id);

CREATE INDEX idx_transition_tokens_device
    ON transition_tokens (device_id);

CREATE INDEX idx_transition_tokens_purpose
    ON transition_tokens (purpose);

CREATE INDEX idx_transition_tokens_expires
    ON transition_tokens (expires_at);

CREATE INDEX idx_transition_tokens_active
    ON transition_tokens (token_hash)
    WHERE consumed_at IS NULL AND revoked_at IS NULL;

