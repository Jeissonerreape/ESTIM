-- db/seeds/002_games.sql
-- Extra seed games for ESTIM catalog
-- Assumes these publishers already exist:
--   - Bob Games Studio
--   - Starforge Interactive
--   - Cozy Leaf Studios
--   - Neon Byte Labs
--   - Quantum Ember Studios

---------------------------------------------------------------
-- 0) Cube Quest (Bob Games Studio)
---------------------------------------------------------------
INSERT INTO games (
  publisher_id, title, description, short_description,
  price, base_price, is_published, release_date, age_rating,
  system_requirements, metadata
)
SELECT
  p.id,
  'Cube Quest',
  'A minimalist puzzle-adventure with cubes.',
  'Minimalist puzzle-adventure.',
  9.99,
  14.99,
  TRUE,
  CURRENT_DATE - INTERVAL '30 days',
  'E',
  '{
    "minimum": {
      "os": "Windows 10 64-bit",
      "cpu": "Intel i3 or equivalent",
      "ram_gb": 4,
      "gpu": "Integrated",
      "storage_gb": 2
    }
  }'::jsonb,
  '{
    "tags": ["puzzle","minimalist"],
    "languages": ["en","es"],
    "genres": ["Puzzle"],
    "features": ["singleplayer"]
  }'::jsonb
FROM publishers p
WHERE p.company_name = 'Bob Games Studio'
  AND NOT EXISTS (
    SELECT 1 FROM games g WHERE g.title = 'Cube Quest'
  );

---------------------------------------------------------------
-- 1) Speed Racing Ultimate — arcade racing (Starforge Interactive)
---------------------------------------------------------------
INSERT INTO games (
  publisher_id, title, description, short_description,
  price, base_price, is_published, release_date, age_rating,
  system_requirements, metadata
)
SELECT
  p.id,
  'Speed Racing Ultimate',
  'High-speed arcade racer focused on stylish drifts, neon-lit cities and online leaderboards.',
  'Neon-soaked arcade drifting.',
  19.99, 29.99, TRUE,
  CURRENT_DATE - INTERVAL '90 days',
  'T',
  '{
    "minimum": {
      "os": "Windows 10 64-bit",
      "cpu": "Intel i5 or equivalent",
      "ram_gb": 8,
      "gpu": "GTX 960 or equivalent",
      "storage_gb": 15
    },
    "recommended": {
      "os": "Windows 11 64-bit",
      "cpu": "Intel i7 or equivalent",
      "ram_gb": 16,
      "gpu": "GTX 1660 or equivalent",
      "storage_gb": 15
    }
  }'::jsonb,
  '{
    "tags": ["racing", "arcade", "drift"],
    "languages": ["en", "es", "pt"],
    "genres": ["Racing", "Arcade"],
    "features": ["online-multiplayer", "controller-support"]
  }'::jsonb
FROM publishers p
WHERE p.company_name = 'Starforge Interactive'
  AND NOT EXISTS (
    SELECT 1 FROM games g WHERE g.title = 'Speed Racing Ultimate'
  );

---------------------------------------------------------------
-- 2) Space Odyssey — city builder (Cozy Leaf Studios)
---------------------------------------------------------------
INSERT INTO games (
  publisher_id, title, description, short_description,
  price, base_price, is_published, release_date, age_rating,
  system_requirements, metadata
)
SELECT
  p.id,
  'Space Odyssey',
  'A relaxed city-builder where you construct floating islands, balance resources and keep citizens happy in the sky.',
  'Cozy floating-islands city builder.',
  24.99, 39.99, TRUE,
  CURRENT_DATE - INTERVAL '200 days',
  'E',
  '{
    "minimum": {
      "os": "Windows 10 64-bit",
      "cpu": "Intel i3 or equivalent",
      "ram_gb": 4,
      "gpu": "Intel UHD or equivalent",
      "storage_gb": 4
    },
    "recommended": {
      "os": "Windows 10 64-bit",
      "cpu": "Intel i5 or equivalent",
      "ram_gb": 8,
      "gpu": "GTX 970 or equivalent",
      "storage_gb": 4
    }
  }'::jsonb,
  '{
    "tags": ["city-builder", "relaxing", "strategy"],
    "languages": ["en", "es", "fr"],
    "genres": ["Simulation", "Strategy"],
    "features": ["singleplayer", "cloud-saves"]
  }'::jsonb
FROM publishers p
WHERE p.company_name = 'Cozy Leaf Studios'
  AND NOT EXISTS (
    SELECT 1 FROM games g WHERE g.title = 'Space Odyssey'
  );

---------------------------------------------------------------
-- 3) Pixel Adventure — hacking roguelite (Neon Byte Labs)
---------------------------------------------------------------
INSERT INTO games (
  publisher_id, title, description, short_description,
  price, base_price, is_published, release_date, age_rating,
  system_requirements, metadata
)
SELECT
  p.id,
  'Pixel Adventure',
  'Tactical hacking roguelite where every run is a different corporate network. Chain exploits, dodge trace attempts, and escape with the data.',
  'Terminal-style hacking roguelite.',
  14.99, 19.99, TRUE,
  CURRENT_DATE - INTERVAL '60 days',
  'T',
  '{
    "minimum": {
      "os": "Windows 10 64-bit",
      "cpu": "Any dual-core",
      "ram_gb": 4,
      "gpu": "Integrated",
      "storage_gb": 2
    }
  }'::jsonb,
  '{
    "tags": ["roguelite", "hacking", "strategy"],
    "languages": ["en"],
    "genres": ["Roguelike", "Strategy"],
    "features": ["singleplayer", "steam-cloud"]
  }'::jsonb
FROM publishers p
WHERE p.company_name = 'Neon Byte Labs'
  AND NOT EXISTS (
    SELECT 1 FROM games g WHERE g.title = 'Pixel Adventure'
  );

---------------------------------------------------------------
-- 4) Fantasy Quest Online — cozy farming sim (Cozy Leaf Studios)
---------------------------------------------------------------
INSERT INTO games (
  publisher_id, title, description, short_description,
  price, base_price, is_published, release_date, age_rating,
  system_requirements, metadata
)
SELECT
  p.id,
  'Fantasy Quest Online',
  'Cozy farming sim on a tiny asteroid. Grow alien crops, befriend space animals, and decorate your homestead.',
  'Cozy farming on a tiny asteroid.',
  12.99, 19.99, TRUE,
  CURRENT_DATE - INTERVAL '15 days',
  'E',
  '{
    "minimum": {
      "os": "Windows 10 64-bit",
      "cpu": "Intel i3 or equivalent",
      "ram_gb": 4,
      "gpu": "Intel HD 4000 or equivalent",
      "storage_gb": 3
    }
  }'::jsonb,
  '{
    "tags": ["farming", "cozy", "simulation"],
    "languages": ["en", "es", "de"],
    "genres": ["Simulation"],
    "features": ["singleplayer", "controller-support"]
  }'::jsonb
FROM publishers p
WHERE p.company_name = 'Cozy Leaf Studios'
  AND NOT EXISTS (
    SELECT 1 FROM games g WHERE g.title = 'Fantasy Quest Online'
  );

---------------------------------------------------------------
-- 5) Cyberpunk Legends — tactical shooter (Starforge Interactive)
---------------------------------------------------------------
INSERT INTO games (
  publisher_id, title, description, short_description,
  price, base_price, is_published, release_date, age_rating,
  system_requirements, metadata
)
SELECT
  p.id,
  'Cyberpunk Legends',
  'Tactical first-person shooter with round-based objectives, destructible cover, and small-team competitive play.',
  'Round-based tactical FPS.',
  39.99, 59.99, TRUE,
  CURRENT_DATE - INTERVAL '365 days',
  'M',
  '{
    "minimum": {
      "os": "Windows 10 64-bit",
      "cpu": "Intel i5 or equivalent",
      "ram_gb": 8,
      "gpu": "GTX 970 or equivalent",
      "storage_gb": 50
    },
    "recommended": {
      "os": "Windows 11 64-bit",
      "cpu": "Intel i7 or equivalent",
      "ram_gb": 16,
      "gpu": "RTX 2060 or equivalent",
      "storage_gb": 50
    }
  }'::jsonb,
  '{
    "tags": ["fps", "tactical", "multiplayer"],
    "languages": ["en", "es"],
    "genres": ["Action"],
    "features": ["online-multiplayer", "ranked", "voice-chat"]
  }'::jsonb
FROM publishers p
WHERE p.company_name = 'Starforge Interactive'
  AND NOT EXISTS (
    SELECT 1 FROM games g WHERE g.title = 'Cyberpunk Legends'
  );

---------------------------------------------------------------
-- 6) Dark Chronicles — puzzle, unreleased (Quantum Ember Studios)
---------------------------------------------------------------
INSERT INTO games (
  publisher_id, title, description, short_description,
  price, base_price, is_published, release_date, age_rating,
  system_requirements, metadata
)
SELECT
  p.id,
  'Dark Chronicles',
  'Grid-based puzzle game where you program tiny training bots with simple commands to solve classroom challenges.',
  'Teach tiny robots to solve puzzles.',
  4.99, 9.99, FALSE,
  CURRENT_DATE + INTERVAL '45 days',
  'E',
  '{
    "minimum": {
      "os": "Windows 10 64-bit",
      "cpu": "Any dual-core",
      "ram_gb": 2,
      "gpu": "Integrated",
      "storage_gb": 1
    }
  }'::jsonb,
  '{
    "tags": ["puzzle", "programming", "educational"],
    "languages": ["en", "es"],
    "genres": ["Puzzle"],
    "features": ["singleplayer"]
  }'::jsonb
FROM publishers p
WHERE p.company_name = 'Quantum Ember Studios'
  AND NOT EXISTS (
    SELECT 1 FROM games g WHERE g.title = 'Dark Chronicles'
  );

---------------------------------------------------------------
-- 7) Dungeon Master — rhythm roguelite (Neon Byte Labs)
---------------------------------------------------------------
INSERT INTO games (
  publisher_id, title, description, short_description,
  price, base_price, is_published, release_date, age_rating,
  system_requirements, metadata
)
SELECT
  p.id,
  'Dungeon Master',
  'Rhythm-based dungeon crawler where your attacks, dodges and spells sync with an ever-changing soundtrack.',
  'Rhythm-driven dungeon crawler.',
  11.99, 14.99, TRUE,
  CURRENT_DATE - INTERVAL '10 days',
  'T',
  '{
    "minimum": {
      "os": "Windows 10 64-bit",
      "cpu": "Intel i3 or equivalent",
      "ram_gb": 4,
      "gpu": "GTX 750Ti or equivalent",
      "storage_gb": 5
    }
  }'::jsonb,
  '{
    "tags": ["rhythm", "roguelite", "action"],
    "languages": ["en", "es", "ja"],
    "genres": ["Action", "Music"],
    "features": ["singleplayer", "leaderboards"]
  }'::jsonb
FROM publishers p
WHERE p.company_name = 'Neon Byte Labs'
  AND NOT EXISTS (
    SELECT 1 FROM games g WHERE g.title = 'Dungeon Master'
  );

---------------------------------------------------------------
-- 8) Medieval Kingdoms (Starforge Interactive)
---------------------------------------------------------------
INSERT INTO games (
  publisher_id, title, description, short_description,
  price, base_price, is_published, release_date, age_rating,
  system_requirements, metadata
)
SELECT
  p.id,
  'Medieval Kingdoms',
  'Epic medieval strategy game with kingdom building, diplomacy, and large-scale battles.',
  'Epic medieval strategy.',
  29.99, 39.99, TRUE,
  CURRENT_DATE - INTERVAL '120 days',
  'T',
  '{
    "minimum": {
      "os": "Windows 10 64-bit",
      "cpu": "Intel i5 or equivalent",
      "ram_gb": 8,
      "gpu": "GTX 1060 or equivalent",
      "storage_gb": 10,
      "vr_headset": true
    }
  }'::jsonb,
  '{
    "tags": ["vr", "rhythm", "fitness"],
    "languages": ["en"],
    "genres": ["Music", "Sports"],
    "features": ["vr-only", "leaderboards"]
  }'::jsonb
FROM publishers p
WHERE p.company_name = 'Starforge Interactive'
  AND NOT EXISTS (
    SELECT 1 FROM games g WHERE g.title = 'Medieval Kingdoms'
  );

---------------------------------------------------------------
-- 9) Tactical Warfare (Quantum Ember Studios)
---------------------------------------------------------------
INSERT INTO games (
  publisher_id, title, description, short_description,
  price, base_price, is_published, release_date, age_rating,
  system_requirements, metadata
)
SELECT
  p.id,
  'Tactical Warfare',
  'Online strategy game inspired by chess variants, with asymmetric factions and seasonal ladders.',
  'Asymmetric online chess-like.',
  0.00, 4.99, TRUE,
  CURRENT_DATE - INTERVAL '7 days',
  'E',
  '{
    "minimum": {
      "os": "Windows 10 64-bit",
      "cpu": "Any dual-core",
      "ram_gb": 2,
      "gpu": "Integrated",
      "storage_gb": 1
    }
  }'::jsonb,
  '{
    "tags": ["strategy", "board-game", "online"],
    "languages": ["en", "es", "ru"],
    "genres": ["Strategy"],
    "features": ["online-multiplayer", "ranked", "f2p"]
  }'::jsonb
FROM publishers p
WHERE p.company_name = 'Quantum Ember Studios'
  AND NOT EXISTS (
    SELECT 1 FROM games g WHERE g.title = 'Tactical Warfare'
  );

---------------------------------------------------------------
-- 10) Zombie Survival (Bob Games Studio)
---------------------------------------------------------------
INSERT INTO games (
  publisher_id, title, description, short_description,
  price, base_price, is_published, release_date, age_rating,
  system_requirements, metadata
)
SELECT
  p.id,
  'Zombie Survival',
  'Post-apocalyptic survival game with crafting, base building, and cooperative multiplayer.',
  'Post-apocalyptic survival.',
  14.99, 19.99, TRUE,
  CURRENT_DATE - INTERVAL '30 days',
  'E',
  '{
    "minimum": {
      "os": "Windows 10 64-bit",
      "cpu": "Any dual-core",
      "ram_gb": 2,
      "gpu": "Integrated",
      "storage_gb": 1
    }
  }'::jsonb,
  '{
    "tags": ["strategy", "board-game", "online"],
    "languages": ["en", "es", "ru"],
    "genres": ["Strategy"],
    "features": ["online-multiplayer", "ranked", "f2p"]
  }'::jsonb
FROM publishers p
WHERE p.company_name = 'Bob Games Studio'
  AND NOT EXISTS (
    SELECT 1 FROM games g WHERE g.title = 'Zombie Survival'
  );
