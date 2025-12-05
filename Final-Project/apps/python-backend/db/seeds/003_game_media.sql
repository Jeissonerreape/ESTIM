-- db/seeds/003_game_media.sql
-- ============================================================
-- Game media seeds: cover images matching EXAMPLE_GAMES in StorePage.jsx
-- Assumes:
--   - games with these titles already exist in the `games` table
--   - media_type enum contains 'cover_art' for main cover images
-- ============================================================

-- 1) Cyberpunk Legends
INSERT INTO game_media (game_id, media_type, url, thumbnail_url, caption, display_order)
SELECT
  g.id,
  'cover_art',
  'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400&h=300&fit=crop',
  'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400&h=300&fit=crop',
  'Cyberpunk Legends cover',
  0
FROM games g
WHERE g.title = 'Cyberpunk Legends'
  AND NOT EXISTS (
    SELECT 1 FROM game_media gm
    WHERE gm.game_id = g.id
      AND gm.media_type = 'cover_art'
      AND gm.display_order = 0
  );

-- 2) Fantasy Quest Online
INSERT INTO game_media (game_id, media_type, url, thumbnail_url, caption, display_order)
SELECT
  g.id,
  'cover_art',
  'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400&h=300&fit=crop',
  'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400&h=300&fit=crop',
  'Fantasy Quest Online cover',
  0
FROM games g
WHERE g.title = 'Fantasy Quest Online'
  AND NOT EXISTS (
    SELECT 1 FROM game_media gm
    WHERE gm.game_id = g.id
      AND gm.media_type = 'cover_art'
      AND gm.display_order = 0
  );

-- 3) Speed Racing Ultimate
INSERT INTO game_media (game_id, media_type, url, thumbnail_url, caption, display_order)
SELECT
  g.id,
  'cover_art',
  'https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=400&h=300&fit=crop',
  'https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=400&h=300&fit=crop',
  'Speed Racing Ultimate cover',
  0
FROM games g
WHERE g.title = 'Speed Racing Ultimate'
  AND NOT EXISTS (
    SELECT 1 FROM game_media gm
    WHERE gm.game_id = g.id
      AND gm.media_type = 'cover_art'
      AND gm.display_order = 0
  );

-- 4) Dark Chronicles
INSERT INTO game_media (game_id, media_type, url, thumbnail_url, caption, display_order)
SELECT
  g.id,
  'cover_art',
  'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&h=300&fit=crop',
  'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&h=300&fit=crop',
  'Dark Chronicles cover',
  0
FROM games g
WHERE g.title = 'Dark Chronicles'
  AND NOT EXISTS (
    SELECT 1 FROM game_media gm
    WHERE gm.game_id = g.id
      AND gm.media_type = 'cover_art'
      AND gm.display_order = 0
  );

-- 5) Tactical Warfare
INSERT INTO game_media (game_id, media_type, url, thumbnail_url, caption, display_order)
SELECT
  g.id,
  'cover_art',
  'https://images.unsplash.com/photo-1526506118085-60ce8714f8c5?w=400&h=300&fit=crop',
  'https://images.unsplash.com/photo-1526506118085-60ce8714f8c5?w=400&h=300&fit=crop',
  'Tactical Warfare cover',
  0
FROM games g
WHERE g.title = 'Tactical Warfare'
  AND NOT EXISTS (
    SELECT 1 FROM game_media gm
    WHERE gm.game_id = g.id
      AND gm.media_type = 'cover_art'
      AND gm.display_order = 0
  );

-- 6) Medieval Kingdoms
INSERT INTO game_media (game_id, media_type, url, thumbnail_url, caption, display_order)
SELECT
  g.id,
  'cover_art',
  'https://images.unsplash.com/photo-1518709414768-a88981a4515d?w=400&h=300&fit=crop',
  'https://images.unsplash.com/photo-1518709414768-a88981a4515d?w=400&h=300&fit=crop',
  'Medieval Kingdoms cover',
  0
FROM games g
WHERE g.title = 'Medieval Kingdoms'
  AND NOT EXISTS (
    SELECT 1 FROM game_media gm
    WHERE gm.game_id = g.id
      AND gm.media_type = 'cover_art'
      AND gm.display_order = 0
  );

-- 7) Pixel Adventure
INSERT INTO game_media (game_id, media_type, url, thumbnail_url, caption, display_order)
SELECT
  g.id,
  'cover_art',
  'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400&h=300&fit=crop',
  'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400&h=300&fit=crop',
  'Pixel Adventure cover',
  0
FROM games g
WHERE g.title = 'Pixel Adventure'
  AND NOT EXISTS (
    SELECT 1 FROM game_media gm
    WHERE gm.game_id = g.id
      AND gm.media_type = 'cover_art'
      AND gm.display_order = 0
  );

-- 8) Space Odyssey
INSERT INTO game_media (game_id, media_type, url, thumbnail_url, caption, display_order)
SELECT
  g.id,
  'cover_art',
  'https://images.unsplash.com/photo-1614732414444-096e5f1122d5?w=400&h=300&fit=crop',
  'https://images.unsplash.com/photo-1614732414444-096e5f1122d5?w=400&h=300&fit=crop',
  'Space Odyssey cover',
  0
FROM games g
WHERE g.title = 'Space Odyssey'
  AND NOT EXISTS (
    SELECT 1 FROM game_media gm
    WHERE gm.game_id = g.id
      AND gm.media_type = 'cover_art'
      AND gm.display_order = 0
  );

-- 9) Dungeon Master
INSERT INTO game_media (game_id, media_type, url, thumbnail_url, caption, display_order)
SELECT
  g.id,
  'cover_art',
  'https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=400&h=300&fit=crop',
  'https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=400&h=300&fit=crop',
  'Dungeon Master cover',
  0
FROM games g
WHERE g.title = 'Dungeon Master'
  AND NOT EXISTS (
    SELECT 1 FROM game_media gm
    WHERE gm.game_id = g.id
      AND gm.media_type = 'cover_art'
      AND gm.display_order = 0
  );

-- 10) Zombie Survival
INSERT INTO game_media (game_id, media_type, url, thumbnail_url, caption, display_order)
SELECT
  g.id,
  'cover_art',
  'https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?w=400&h=300&fit=crop',
  'https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?w=400&h=300&fit=crop',
  'Zombie Survival cover',
  0
FROM games g
WHERE g.title = 'Zombie Survival'
  AND NOT EXISTS (
    SELECT 1 FROM game_media gm
    WHERE gm.game_id = g.id
      AND gm.media_type = 'cover_art'
      AND gm.display_order = 0
  );

-- ============================================================
-- End of game_media seeds
-- ============================================================
