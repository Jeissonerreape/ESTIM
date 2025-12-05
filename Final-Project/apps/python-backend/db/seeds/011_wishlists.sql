-- db/seeds/011_wishlists.sql
-- ============================================================
-- Wishlist Seeds (Idempotent)
-- ============================================================

---------------------------------------------------------------
-- 1) Alice → Cube Quest (with Launch10 promo)
---------------------------------------------------------------
INSERT INTO wishlists (user_id, game_id, promotion_id, notes)
SELECT
  u.id,
  g.id,
  p.id,
  'If a deeper discount appears, buy a gift copy.'
FROM users u
JOIN games g ON g.title = 'Cube Quest'
JOIN promotions p ON p.name = 'Launch10'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM wishlists w
    WHERE w.user_id = u.id AND w.game_id = g.id
  );

---------------------------------------------------------------
-- 2) Alice → Space Odyssey
---------------------------------------------------------------
INSERT INTO wishlists (user_id, game_id, notes)
SELECT
  u.id,
  g.id,
  'Looks very relaxing. Wishlist for later.'
FROM users u
JOIN games g ON g.title = 'Space Odyssey'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM wishlists w
    WHERE w.user_id = u.id AND w.game_id = g.id
  );

---------------------------------------------------------------
-- 3) Bob → Cyberpunk Legends
---------------------------------------------------------------
INSERT INTO wishlists (user_id, game_id, notes)
SELECT
  u.id,
  g.id,
  'Try whenever a sale hits 30% or more.'
FROM users u
JOIN games g ON g.title = 'Cyberpunk Legends'
WHERE u.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM wishlists w
    WHERE w.user_id = u.id AND w.game_id = g.id
  );

---------------------------------------------------------------
-- 4) Charlie → Fantasy Quest Online
---------------------------------------------------------------
INSERT INTO wishlists (user_id, game_id)
SELECT
  u.id,
  g.id
FROM users u
JOIN games g ON g.title = 'Fantasy Quest Online'
WHERE u.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM wishlists w
    WHERE w.user_id = u.id AND w.game_id = g.id
  );

---------------------------------------------------------------
-- 5) Charlie → Dungeon Master
---------------------------------------------------------------
INSERT INTO wishlists (user_id, game_id, notes)
SELECT
  u.id,
  g.id,
  'Looks fun and fast-paced. Maybe next paycheck.'
FROM users u
JOIN games g ON g.title = 'Dungeon Master'
WHERE u.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM wishlists w
    WHERE w.user_id = u.id AND w.game_id = g.id
  );

-- ============================================================
-- End of Wishlist Seeds
-- ============================================================
