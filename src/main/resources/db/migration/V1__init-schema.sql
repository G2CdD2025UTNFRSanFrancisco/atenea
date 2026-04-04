-- V1__init.sql

CREATE TABLE accounts
(
    id                       BIGINT PRIMARY KEY,
    username                 TEXT      NOT NULL UNIQUE,
    version                  BIGINT NOT NULL,
    persistence_version      BIGINT,
    password_hash            TEXT      NOT NULL,
    password_change_required BOOLEAN   NOT NULL,
    password_last_changed_at TIMESTAMP NOT NULL,
    mfa_required             BOOLEAN DEFAULT FALSE,
    scopes                   BIGINT    NOT NULL,
    failed_login_attempts    INT,
    created_at               TIMESTAMP,
    updated_at               TIMESTAMP,
    deleted_at               TIMESTAMP,
    locked_until             TIMESTAMP
);

CREATE INDEX idx_accounts_username_active
    ON accounts (username)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_accounts_deleted
    ON accounts (deleted_at);

CREATE INDEX idx_accounts_locked
    ON accounts (locked_until);

CREATE INDEX idx_accounts_version
    ON accounts (id, version);

CREATE TABLE sessions
(
    id               BIGINT PRIMARY KEY,
    account_id       BIGINT    NOT NULL,
    device_id        TEXT      NOT NULL,
    version_snapshot BIGINT    NOT NULL,
    refresh_token    TEXT      NOT NULL UNIQUE,
    created_at       TIMESTAMP NOT NULL,
    expires_at       TIMESTAMP NOT NULL,
    revoked_at       TIMESTAMP
);

CREATE INDEX idx_sessions_refresh_active
    ON sessions (refresh_token)
    WHERE revoked_at IS NULL;

CREATE INDEX idx_sessions_account
    ON sessions (account_id);

CREATE INDEX idx_sessions_revoked
    ON sessions (revoked_at);

CREATE INDEX idx_sessions_expires
    ON sessions (expires_at);

CREATE TABLE devices
(
    id                  TEXT PRIMARY KEY,
    spot_id             TEXT      NOT NULL,
    secret_hash         TEXT      NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP NOT NULL,
    last_seen_at        TIMESTAMP NULL,
    deleted_at          TIMESTAMP
);

CREATE UNIQUE INDEX idx_devices_spot_unique
    ON devices (spot_id)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_devices_deleted
    ON devices (deleted_at);

CREATE INDEX idx_devices_updated
    ON devices (updated_at);

CREATE TABLE mfa_enrollments (
                                 account_id BIGINT PRIMARY KEY,
                                 state VARCHAR(32) NOT NULL,
                                 enrolled_at TIMESTAMP,
                                 last_verified_at TIMESTAMP,
                                 failed_attempts INT NOT NULL DEFAULT 0,
                                 locked_until TIMESTAMP,
                                 policy_max_failures INT NOT NULL,
                                 policy_lock_duration_seconds BIGINT NOT NULL,
                                 CONSTRAINT fk_mfa_enrollments_account
                                     FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE mfa_totp_factors (
                                  account_id BIGINT PRIMARY KEY,
                                  secret TEXT NOT NULL,
                                  issuer TEXT NOT NULL,
                                  label TEXT NOT NULL,
                                  activated_at TIMESTAMP,
                                  last_used_at TIMESTAMP,
                                  CONSTRAINT fk_mfa_totp_factors_enrollment
                                      FOREIGN KEY (account_id) REFERENCES mfa_enrollments(account_id)
);

CREATE TABLE mfa_recovery_codes (
                                    account_id BIGINT NOT NULL,
                                    code_hash TEXT NOT NULL,
                                    used BOOLEAN NOT NULL DEFAULT FALSE,
                                    CONSTRAINT pk_mfa_recovery_codes PRIMARY KEY (account_id, code_hash),
                                    CONSTRAINT fk_mfa_recovery_codes_enrollment
                                        FOREIGN KEY (account_id) REFERENCES mfa_enrollments(account_id)
);

CREATE INDEX idx_mfa_enrollments_state
    ON mfa_enrollments(state);

CREATE INDEX idx_mfa_totp_factors_account
    ON mfa_totp_factors(account_id);

CREATE INDEX idx_mfa_recovery_codes_account_used
    ON mfa_recovery_codes(account_id, used);


INSERT INTO accounts (
    id,
    username,
    persistence_version,
    version,
    password_hash,
    password_change_required,
    password_last_changed_at,
    mfa_required,
    scopes,
    failed_login_attempts,
    created_at,
    updated_at,
    deleted_at,
    locked_until
) VALUES (
             0,
             'root',
             0,
             0,
             '$argon2id$v=19$m=16384,t=2,p=1$0Qx8z6VszTGCoSiU9Vt/kw$eE14dCtVY24faHbYkA3PtIPRcL1enwnqW/RZDBesbLg',
             TRUE,
             NOW(),
             FALSE,
             127,
             0,
             NOW(),
             NOW(),
             NULL,
             NULL
         )
ON CONFLICT (username) DO NOTHING;
