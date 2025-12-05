-- db/seeds/012_user_preferences.sql
-- ============================================================
-- User Preferences Seeds (Idempotent)
-- ============================================================

---------------------------------------------------------------
-- 1) Alice → Puzzle & Indie games
---------------------------------------------------------------
INSERT INTO user_preferences (user_id, favorite_genres, preferred_price_range)
SELECT
  u.id,
  ARRAY['puzzle','indie'],
  '{"min":0,"max":15}'::jsonb
FROM users u
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM user_preferences p WHERE p.user_id = u.id
  );

---------------------------------------------------------------
-- 2) Bob → Farming & RPG
---------------------------------------------------------------
INSERT INTO user_preferences (user_id, favorite_genres, preferred_price_range)
SELECT
  u.id,
  ARRAY['simulation','rpg','farming'],
  '{"min":10,"max":40}'::jsonb
FROM users u
WHERE u.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM user_preferences p WHERE p.user_id = u.id
  );

---------------------------------------------------------------
-- 3) Charlie → Shooters & Action
---------------------------------------------------------------
INSERT INTO user_preferences (user_id, favorite_genres, preferred_price_range)
SELECT
  u.id,
  ARRAY['action','fps','strategy'],
  '{"min":0,"max":60}'::jsonb
FROM users u
WHERE u.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM user_preferences p WHERE p.user_id = u.id
  );

---------------------------------------------------------------
-- 4) Admin → Strategy & Management
---------------------------------------------------------------
INSERT INTO user_preferences (user_id, favorite_genres, preferred_price_range)
SELECT
  u.id,
  ARRAY['strategy','management','city-builder'],
  '{"min":0,"max":999}'::jsonb
FROM users u
WHERE u.email = 'admin@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM user_preferences p WHERE p.user_id = u.id
  );

-- ============================================================
-- End of User Preferences Seeds
-- ============================================================
