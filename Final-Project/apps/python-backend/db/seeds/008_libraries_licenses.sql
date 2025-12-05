-- ============================================================
-- Library & License Seeds (Standalone)
-- Requires:
--   - 000_users.sql   (alice, bob, charlie...)
--   - 002_games.sql   (Cube Quest, Fantasy Quest Online, Cyberpunk Legends)
-- ============================================================

---------------------------------------------------------------
-- 1) Alice → Cube Quest
---------------------------------------------------------------

-- Library entry
INSERT INTO libraries (user_id, game_id, source)
SELECT
  u.id,
  g.id,
  'purchase'
FROM users u
JOIN games g ON g.title = 'Cube Quest'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1
    FROM libraries l
    WHERE l.user_id = u.id
      AND l.game_id = g.id
  );

-- License entry
INSERT INTO licenses (library_id, user_id, game_id, license_key, is_active, purchase_date)
SELECT
  l.id,
  l.user_id,
  l.game_id,
  'CQ-ALICE-0001',
  TRUE,
  NOW()
FROM libraries l
JOIN users u ON u.email = 'alice@example.com'
JOIN games g ON g.title = 'Cube Quest'
WHERE l.user_id = u.id
  AND l.game_id = g.id
  AND NOT EXISTS (
    SELECT 1
    FROM licenses li
    WHERE li.user_id = u.id
      AND li.game_id = g.id
  );



---------------------------------------------------------------
-- 2) Bob → Fantasy Quest Online
---------------------------------------------------------------

INSERT INTO libraries (user_id, game_id, source)
SELECT
  u.id,
  g.id,
  'purchase'
FROM users u
JOIN games g ON g.title = 'Fantasy Quest Online'
WHERE u.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1
    FROM libraries l
    WHERE l.user_id = u.id
      AND l.game_id = g.id
  );

INSERT INTO licenses (library_id, user_id, game_id, license_key, is_active, purchase_date)
SELECT
  l.id,
  l.user_id,
  l.game_id,
  'FQO-BOB-0001',
  TRUE,
  NOW()
FROM libraries l
JOIN users u ON u.email = 'bob@example.com'
JOIN games g ON g.title = 'Fantasy Quest Online'
WHERE l.user_id = u.id
  AND l.game_id = g.id
  AND NOT EXISTS (
    SELECT 1
    FROM licenses li
    WHERE li.user_id = u.id
      AND li.game_id = g.id
  );



---------------------------------------------------------------
-- 3) Charlie → Cyberpunk Legends
---------------------------------------------------------------

INSERT INTO libraries (user_id, game_id, source)
SELECT
  u.id,
  g.id,
  'purchase'
FROM users u
JOIN games g ON g.title = 'Cyberpunk Legends'
WHERE u.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1
    FROM libraries l
    WHERE l.user_id = u.id
      AND l.game_id = g.id
  );

INSERT INTO licenses (library_id, user_id, game_id, license_key, is_active, purchase_date)
SELECT
  l.id,
  l.user_id,
  l.game_id,
  'CP-CHARLIE-0001',
  TRUE,
  NOW()
FROM libraries l
JOIN users u ON u.email = 'charlie@example.com'
JOIN games g ON g.title = 'Cyberpunk Legends'
WHERE l.user_id = u.id
  AND l.game_id = g.id
  AND NOT EXISTS (
    SELECT 1
    FROM licenses li
    WHERE li.user_id = u.id
      AND li.game_id = g.id
  );

-- ============================================================
-- End of Library & License Seeds
-- ============================================================
