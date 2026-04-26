ALTER TABLE accounts
    ADD COLUMN role VARCHAR(16);

UPDATE accounts
SET role = CASE
    WHEN id = 0 THEN 'OWNER'
    WHEN (scopes & (1::bigint << 6)) <> 0 THEN 'ADMIN'
    ELSE 'USER'
END;

UPDATE accounts
SET mfa_required = TRUE
WHERE role IN ('OWNER', 'ADMIN');

ALTER TABLE accounts
    ALTER COLUMN role SET NOT NULL;

CREATE UNIQUE INDEX uq_accounts_single_owner_active
    ON accounts ((role))
    WHERE role = 'OWNER' AND deleted_at IS NULL;



