CREATE TABLE accounts (
                          id BIGINT PRIMARY KEY,
                          username TEXT NOT NULL UNIQUE,
                          version BIGINT NOT NULL,
                          persistence_version BIGINT,

                          password_hash TEXT NOT NULL,
                          password_change_required BOOLEAN NOT NULL,
                          password_last_changed_at TIMESTAMP NOT NULL,

                          mfa_required BOOLEAN DEFAULT FALSE,
                          failed_login_attempts INTEGER,

                          created_at TIMESTAMP,
                          updated_at TIMESTAMP,
                          deleted_at TIMESTAMP,
                          locked_until TIMESTAMP,

                          role VARCHAR(16) NOT NULL
);

CREATE TABLE devices (
                         id TEXT PRIMARY KEY,
                         spot_id TEXT NOT NULL,
                         secret_hash TEXT NOT NULL,

                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL,
                         last_seen_at TIMESTAMP,
                         deleted_at TIMESTAMP
);

CREATE TABLE mfa_enrollments (
                                 account_id BIGINT PRIMARY KEY,

                                 state VARCHAR(32) NOT NULL,
                                 enrolled_at TIMESTAMP,
                                 last_verified_at TIMESTAMP,

                                 failed_attempts INTEGER NOT NULL DEFAULT 0,
                                 locked_until TIMESTAMP,

                                 policy_max_failures INTEGER NOT NULL,
                                 policy_lock_duration_seconds BIGINT NOT NULL,

                                 CONSTRAINT fk_mfa_enrollments_account
                                     FOREIGN KEY (account_id)
                                         REFERENCES accounts(id)
);

CREATE TABLE mfa_totp_factors (
                                  account_id BIGINT PRIMARY KEY,

                                  secret TEXT NOT NULL,
                                  issuer TEXT NOT NULL,
                                  label TEXT NOT NULL,

                                  activated_at TIMESTAMP,
                                  last_used_at TIMESTAMP,

                                  CONSTRAINT fk_mfa_totp_factors_enrollment
                                      FOREIGN KEY (account_id)
                                          REFERENCES mfa_enrollments(account_id)
);

CREATE TABLE mfa_recovery_codes (
                                    account_id BIGINT NOT NULL,
                                    code_hash TEXT NOT NULL,
                                    used BOOLEAN NOT NULL DEFAULT FALSE,

                                    CONSTRAINT pk_mfa_recovery_codes
                                        PRIMARY KEY (account_id, code_hash),

                                    CONSTRAINT fk_mfa_recovery_codes_enrollment
                                        FOREIGN KEY (account_id)
                                            REFERENCES mfa_enrollments(account_id)
);

CREATE TABLE sessions (
                          id BIGINT PRIMARY KEY,

                          account_id BIGINT NOT NULL,
                          device_id TEXT NOT NULL,

                          version_snapshot BIGINT NOT NULL,
                          refresh_token TEXT NOT NULL UNIQUE,

                          created_at TIMESTAMP NOT NULL,
                          expires_at TIMESTAMP NOT NULL,
                          revoked_at TIMESTAMP
);

CREATE TABLE transition_tokens (
                                   id BIGINT PRIMARY KEY,

                                   account_id BIGINT NOT NULL,
                                   device_id TEXT NOT NULL,

                                   purpose TEXT NOT NULL,
                                   token_hash TEXT NOT NULL UNIQUE,

                                   created_at TIMESTAMP NOT NULL,
                                   expires_at TIMESTAMP NOT NULL,

                                   consumed_at TIMESTAMP,
                                   revoked_at TIMESTAMP
);

-- Accounts
CREATE INDEX idx_accounts_deleted
    ON accounts (deleted_at);

CREATE INDEX idx_accounts_locked
    ON accounts (locked_until);

CREATE INDEX idx_accounts_version
    ON accounts (id, version);

CREATE INDEX idx_accounts_username_active
    ON accounts (username)
    WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX uq_accounts_single_owner_active
    ON accounts (role)
    WHERE role = 'OWNER'
        AND deleted_at IS NULL;

-- Devices
CREATE INDEX idx_devices_deleted
    ON devices (deleted_at);

CREATE INDEX idx_devices_updated
    ON devices (updated_at);

CREATE UNIQUE INDEX idx_devices_spot_unique
    ON devices (spot_id)
    WHERE deleted_at IS NULL;

-- MFA
CREATE INDEX idx_mfa_enrollments_state
    ON mfa_enrollments (state);

CREATE INDEX idx_mfa_recovery_codes_account_used
    ON mfa_recovery_codes (account_id, used);

CREATE INDEX idx_mfa_totp_factors_account
    ON mfa_totp_factors (account_id);

-- Sessions
CREATE INDEX idx_sessions_account
    ON sessions (account_id);

CREATE INDEX idx_sessions_expires
    ON sessions (expires_at);

CREATE INDEX idx_sessions_revoked
    ON sessions (revoked_at);

CREATE INDEX idx_sessions_refresh_active
    ON sessions (refresh_token)
    WHERE revoked_at IS NULL;

-- Transition tokens
CREATE INDEX idx_transition_tokens_account
    ON transition_tokens (account_id);

CREATE INDEX idx_transition_tokens_device
    ON transition_tokens (device_id);

CREATE INDEX idx_transition_tokens_expires
    ON transition_tokens (expires_at);

CREATE INDEX idx_transition_tokens_purpose
    ON transition_tokens (purpose);

CREATE INDEX idx_transition_tokens_active
    ON transition_tokens (token_hash)
    WHERE consumed_at IS NULL
        AND revoked_at IS NULL;