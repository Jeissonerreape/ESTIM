-- db/seeds/013_cloud_saves.sql
-- ============================================================
-- Cloud Save Seeds (Idempotent)
-- ============================================================

---------------------------------------------------------------
-- 1) Alice → Cube Quest (Slot 1)
---------------------------------------------------------------
INSERT INTO cloud_saves (user_id, game_id, save_slot, save_name, file_path, file_size, metadata)
SELECT
  u.id,
  g.id,
  1,
  'Level 1',
  '/saves/alice/cube-quest/slot1.sav',
  4096,
  '{"version":"1.0.0","level":1}'::jsonb
FROM users u
JOIN games g ON g.title = 'Cube Quest'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM cloud_saves cs 
    WHERE cs.user_id = u.id AND cs.game_id = g.id AND cs.save_slot = 1
  );

---------------------------------------------------------------
-- 2) Alice → Cube Quest (Slot 2 — deeper progress)
---------------------------------------------------------------
INSERT INTO cloud_saves (user_id, game_id, save_slot, save_name, file_path, file_size, metadata)
SELECT
  u.id,
  g.id,
  2,
  'Deep Run',
  '/saves/alice/cube-quest/slot2.sav',
  8192,
  '{"version":"1.0.0","level":7,"secrets_found":3}'::jsonb
FROM users u
JOIN games g ON g.title = 'Cube Quest'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM cloud_saves cs 
    WHERE cs.user_id = u.id AND cs.game_id = g.id AND cs.save_slot = 2
  );

---------------------------------------------------------------
-- 3) Bob → Fantasy Quest Online
---------------------------------------------------------------
INSERT INTO cloud_saves (user_id, game_id, save_slot, save_name, file_path, file_size, metadata)
SELECT
  u.id,
  g.id,
  1,
  'Farm Base',
  '/saves/bob/fantasy-quest-online/slot1.sav',
  20480,
  '{"version":"2.1.0","location":"Meadow Fields","crops":["corn","berries"]}'::jsonb
FROM users u
JOIN games g ON g.title = 'Fantasy Quest Online'
WHERE u.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM cloud_saves cs 
    WHERE cs.user_id = u.id AND cs.game_id = g.id AND cs.save_slot = 1
  );

---------------------------------------------------------------
-- 4) Charlie → Cyberpunk Legends
---------------------------------------------------------------
INSERT INTO cloud_saves (user_id, game_id, save_slot, save_name, file_path, file_size, metadata)
SELECT
  u.id,
  g.id,
  1,
  'Neon District Start',
  '/saves/charlie/cyberpunk-legends/slot1.sav',
  16384,
  '{"version":"1.3.2","reputation":5,"upgrades":{"cyberarm":true}}'::jsonb
FROM users u
JOIN games g ON g.title = 'Cyberpunk Legends'
WHERE u.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM cloud_saves cs 
    WHERE cs.user_id = u.id AND cs.game_id = g.id AND cs.save_slot = 1
  );

-- ============================================================
-- End of Cloud Save Seeds
-- ============================================================
