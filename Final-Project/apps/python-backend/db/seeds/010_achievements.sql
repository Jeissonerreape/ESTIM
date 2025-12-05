-- db/seeds/010_achievements.sql
-- ============================================================
-- Achievements & User Achievement Seeds (Idempotent)
-- ============================================================

---------------------------------------------------------------
-- ACHIEVEMENTS FOR CUBE QUEST
---------------------------------------------------------------

-- 1) First Cube
INSERT INTO achievements (game_id, name, description, achievement_type, criteria, display_order)
SELECT 
  g.id,
  'First Cube',
  'Complete the first level',
  'bronze',
  '{"level":1}'::jsonb,
  1
FROM games g
WHERE g.title = 'Cube Quest'
  AND NOT EXISTS (
    SELECT 1
    FROM achievements a
    WHERE a.name = 'First Cube'
      AND a.game_id = g.id
  );

-- 2) Cube Master
INSERT INTO achievements (game_id, name, description, achievement_type, criteria, display_order)
SELECT 
  g.id,
  'Cube Master',
  'Finish the game with all secret challenges completed',
  'gold',
  '{"completion":100,"secrets":true}'::jsonb,
  2
FROM games g
WHERE g.title = 'Cube Quest'
  AND NOT EXISTS (
    SELECT 1
    FROM achievements a
    WHERE a.name = 'Cube Master'
      AND a.game_id = g.id
  );


---------------------------------------------------------------
-- ACHIEVEMENTS FOR FANTASY QUEST ONLINE
---------------------------------------------------------------

-- 3) First Harvest
INSERT INTO achievements (game_id, name, description, achievement_type, criteria, display_order)
SELECT 
  g.id,
  'First Harvest',
  'Harvest your first crop',
  'bronze',
  '{"action":"harvest"}'::jsonb,
  1
FROM games g
WHERE g.title = 'Fantasy Quest Online'
  AND NOT EXISTS (
    SELECT 1
    FROM achievements a
    WHERE a.name = 'First Harvest'
      AND a.game_id = g.id
  );

-- 4) Master Farmer
INSERT INTO achievements (game_id, name, description, achievement_type, criteria, display_order)
SELECT 
  g.id,
  'Master Farmer',
  'Reach crop mastery level 10',
  'silver',
  '{"mastery_level":10}'::jsonb,
  2
FROM games g
WHERE g.title = 'Fantasy Quest Online'
  AND NOT EXISTS (
    SELECT 1
    FROM achievements a
    WHERE a.name = 'Master Farmer'
      AND a.game_id = g.id
  );


---------------------------------------------------------------
-- ACHIEVEMENTS FOR CYBERPUNK LEGENDS
---------------------------------------------------------------

-- 5) Chrome Initiate
INSERT INTO achievements (game_id, name, description, achievement_type, criteria, display_order)
SELECT 
  g.id,
  'Chrome Initiate',
  'Install your first cybernetic upgrade',
  'bronze',
  '{"upgrades_installed":1}'::jsonb,
  1
FROM games g
WHERE g.title = 'Cyberpunk Legends'
  AND NOT EXISTS (
    SELECT 1
    FROM achievements a
    WHERE a.name = 'Chrome Initiate'
      AND a.game_id = g.id
  );

-- 6) Street Legend
INSERT INTO achievements (game_id, name, description, achievement_type, criteria, display_order)
SELECT 
  g.id,
  'Street Legend',
  'Reach reputation level 20 in the Neon District',
  'gold',
  '{"reputation":20}'::jsonb,
  2
FROM games g
WHERE g.title = 'Cyberpunk Legends'
  AND NOT EXISTS (
    SELECT 1
    FROM achievements a
    WHERE a.name = 'Street Legend'
      AND a.game_id = g.id
  );



-- ============================================================
-- USER ACHIEVEMENTS
-- ============================================================

---------------------------------------------------------------
-- Alice → Cube Quest achievements
---------------------------------------------------------------

-- First Cube (unlocked)
INSERT INTO user_achievements (user_id, achievement_id, game_id, progress, is_unlocked, unlocked_at)
SELECT 
  u.id,
  a.id,
  g.id,
  100,
  TRUE,
  NOW()
FROM users u
JOIN games g ON g.title = 'Cube Quest'
JOIN achievements a ON a.name = 'First Cube' AND a.game_id = g.id
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1
    FROM user_achievements ua
    WHERE ua.user_id = u.id
      AND ua.achievement_id = a.id
  );

-- Cube Master (in progress)
INSERT INTO user_achievements (user_id, achievement_id, game_id, progress, is_unlocked)
SELECT 
  u.id,
  a.id,
  g.id,
  40,
  FALSE
FROM users u
JOIN games g ON g.title = 'Cube Quest'
JOIN achievements a ON a.name = 'Cube Master' AND a.game_id = g.id
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1
    FROM user_achievements ua
    WHERE ua.user_id = u.id
      AND ua.achievement_id = a.id
  );


---------------------------------------------------------------
-- Bob → Fantasy Quest Online
---------------------------------------------------------------

-- First Harvest (unlocked)
INSERT INTO user_achievements (user_id, achievement_id, game_id, progress, is_unlocked, unlocked_at)
SELECT
  u.id,
  a.id,
  g.id,
  100,
  TRUE,
  NOW()
FROM users u
JOIN games g ON g.title = 'Fantasy Quest Online'
JOIN achievements a ON a.name = 'First Harvest' AND a.game_id = g.id
WHERE u.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1
    FROM user_achievements ua
    WHERE ua.user_id = u.id
      AND ua.achievement_id = a.id
  );


---------------------------------------------------------------
-- Charlie → Cyberpunk Legends
---------------------------------------------------------------

-- Chrome Initiate (partial progress)
INSERT INTO user_achievements (user_id, achievement_id, game_id, progress, is_unlocked)
SELECT
  u.id,
  a.id,
  g.id,
  30,
  FALSE
FROM users u
JOIN games g ON g.title = 'Cyberpunk Legends'
JOIN achievements a ON a.name = 'Chrome Initiate' AND a.game_id = g.id
WHERE u.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1
    FROM user_achievements ua
    WHERE ua.user_id = u.id
      AND ua.achievement_id = a.id
  );

-- ============================================================
-- End of Achievements Seeds
-- ============================================================
