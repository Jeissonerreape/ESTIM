-- db/seeds/004_game_binaries.sql
-- ============================================================
-- Game Binary Seeds
-- Assumes:
--   - games table already seeded
--   - platform_type enum includes: 'windows', 'linux', 'mac'
-- ============================================================


---------------------------------------------------------------
-- 1) Speed Racing Ultimate (Windows build)
---------------------------------------------------------------
INSERT INTO game_binaries (game_id, platform, version, download_url, file_size, checksum, is_active)
SELECT
  g.id,
  'windows',
  '1.0.0',
  'https://cdn.example/sru/sru-win.zip',
  150000000,
  'sha256-sru-win-0001',
  TRUE
FROM games g
WHERE g.title = 'Speed Racing Ultimate'
  AND NOT EXISTS (
    SELECT 1 FROM game_binaries b
    WHERE b.game_id = g.id
      AND b.platform = 'windows'
      AND b.version = '1.0.0'
  );


---------------------------------------------------------------
-- 2) Pixel Adventure (Windows)
---------------------------------------------------------------
INSERT INTO game_binaries (game_id, platform, version, download_url, file_size, checksum, is_active)
SELECT
  g.id,
  'windows',
  '1.0.0',
  'https://cdn.example/pixel/pixel-win.zip',
  90000000,
  'sha256-pixel-win-0001',
  TRUE
FROM games g
WHERE g.title = 'Pixel Adventure'
  AND NOT EXISTS (
    SELECT 1 FROM game_binaries b
    WHERE b.game_id = g.id
      AND b.platform = 'windows'
      AND b.version = '1.0.0'
  );


---------------------------------------------------------------
-- 3) Dungeon Master (Windows)
---------------------------------------------------------------
INSERT INTO game_binaries (game_id, platform, version, download_url, file_size, checksum, is_active)
SELECT
  g.id,
  'windows',
  '1.0.0',
  'https://cdn.example/dmaster/dmaster-win.zip',
  120000000,
  'sha256-dmaster-win-0001',
  TRUE
FROM games g
WHERE g.title = 'Dungeon Master'
  AND NOT EXISTS (
    SELECT 1 FROM game_binaries b
    WHERE b.game_id = g.id
      AND b.platform = 'windows'
      AND b.version = '1.0.0'
  );


---------------------------------------------------------------
-- 4) Tactical Warfare (Windows)
---------------------------------------------------------------
INSERT INTO game_binaries (game_id, platform, version, download_url, file_size, checksum, is_active)
SELECT
  g.id,
  'windows',
  '1.0.0',
  'https://cdn.example/tw/tw-win.zip',
  80000000,
  'sha256-tw-win-0001',
  TRUE
FROM games g
WHERE g.title = 'Tactical Warfare'
  AND NOT EXISTS (
    SELECT 1 FROM game_binaries b
    WHERE b.game_id = g.id
      AND b.platform = 'windows'
      AND b.version = '1.0.0'
  );


-- ============================================================
-- END OF SEED
-- ============================================================
