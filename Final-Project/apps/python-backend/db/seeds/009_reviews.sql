-- ============================================================
-- Review Seeds (Idempotent)
-- ============================================================

---------------------------------------------------------------
-- 1) Alice → Cube Quest
---------------------------------------------------------------
INSERT INTO reviews (user_id, game_id, rating, title, body, is_verified_owner)
SELECT 
  u.id,
  g.id,
  5,
  'Delightful!',
  'Short, sweet, smart puzzles. Loved every level.',
  TRUE
FROM users u
JOIN games g ON g.title = 'Cube Quest'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM reviews r WHERE r.user_id = u.id AND r.game_id = g.id
  );

---------------------------------------------------------------
-- 2) Bob → Fantasy Quest Online
---------------------------------------------------------------
INSERT INTO reviews (user_id, game_id, rating, title, body, is_verified_owner)
SELECT
  u.id,
  g.id,
  4,
  'Relaxing and charming',
  'Beautiful environments, cozy farming, very wholesome.',
  TRUE
FROM users u
JOIN games g ON g.title = 'Fantasy Quest Online'
WHERE u.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM reviews r WHERE r.user_id = u.id AND r.game_id = g.id
  );

---------------------------------------------------------------
-- 3) Charlie → Cyberpunk Legends
---------------------------------------------------------------
INSERT INTO reviews (user_id, game_id, rating, title, body, is_verified_owner)
SELECT
  u.id,
  g.id,
  3,
  'Fun but uneven',
  'Great combat, but the progression system needs work.',
  TRUE
FROM users u
JOIN games g ON g.title = 'Cyberpunk Legends'
WHERE u.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM reviews r WHERE r.user_id = u.id AND r.game_id = g.id
  );

---------------------------------------------------------------
-- 4) Alice → Space Odyssey
---------------------------------------------------------------
INSERT INTO reviews (user_id, game_id, rating, title, body, is_verified_owner)
SELECT
  u.id,
  g.id,
  4,
  'Chill and beautiful',
  'Loved building cities among the stars. Super relaxing.',
  FALSE   -- she didn't buy it in seeds (not verified)
FROM users u
JOIN games g ON g.title = 'Space Odyssey'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM reviews r WHERE r.user_id = u.id AND r.game_id = g.id
  );

---------------------------------------------------------------
-- 5) Bob → Dungeon Master
---------------------------------------------------------------
INSERT INTO reviews (user_id, game_id, rating, title, body, is_verified_owner)
SELECT
  u.id,
  g.id,
  5,
  'Incredible rhythm gameplay',
  'Combat synced to music is insanely fun. Perfect difficulty curve.',
  FALSE   -- Bob didn’t buy this game in seeds
FROM users u
JOIN games g ON g.title = 'Dungeon Master'
WHERE u.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM reviews r WHERE r.user_id = u.id AND r.game_id = g.id
  );

-- ============================================================
-- End of Review Seeds
-- ============================================================
