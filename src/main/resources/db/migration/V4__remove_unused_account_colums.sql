ALTER TABLE accounts
    DROP COLUMN IF EXISTS hierarchy_level,
    DROP COLUMN IF EXISTS scopes;