ALTER TABLE accounts
ADD hierarchy_level INTEGER NOT NULL DEFAULT 0;

UPDATE accounts SET hierarchy_level = 100
WHERE id=0;