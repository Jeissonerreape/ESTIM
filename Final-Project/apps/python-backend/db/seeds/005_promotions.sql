-- ============================================================
-- Promotions + promotion_games seeds
-- ============================================================

---------------------------------------------------------------
-- 1) Launch10 – 10% off Cube Quest
---------------------------------------------------------------
INSERT INTO promotions (
  name, description, discount_type, discount_value,
  coupon_code, is_active, valid_from, valid_until,
  usage_limit
)
SELECT
  'Launch10',
  '10% off Cube Quest during launch window',
  'percentage',
  10.00,
  'LAUNCH10',
  TRUE,
  NOW() - INTERVAL '1 day',
  NOW() + INTERVAL '14 days',
  1000
WHERE NOT EXISTS (
  SELECT 1 FROM promotions WHERE name = 'Launch10'
);

-- Link Launch10 to Cube Quest
INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Cube Quest'
WHERE p.name = 'Launch10'
  AND NOT EXISTS (
    SELECT 1 FROM promotion_games pg
    WHERE pg.promotion_id = p.id
      AND pg.game_id = g.id
  );

---------------------------------------------------------------
-- 2) COZY25 – 25% off cozy games (Cozy Leaf Studios)
--    Applies to: Fantasy Quest Online, Space Odyssey
---------------------------------------------------------------
INSERT INTO promotions (
  name, description, discount_type, discount_value,
  coupon_code, is_active, valid_from, valid_until,
  usage_limit
)
SELECT
  'Cozy25',
  '25% off selected cozy & simulation games',
  'percentage',
  25.00,
  'COZY25',
  TRUE,
  NOW() - INTERVAL '3 days',
  NOW() + INTERVAL '10 days',
  500
WHERE NOT EXISTS (
  SELECT 1 FROM promotions WHERE name = 'Cozy25'
);

-- Link Cozy25 to Fantasy Quest Online
INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Fantasy Quest Online'
WHERE p.name = 'Cozy25'
  AND NOT EXISTS (
    SELECT 1 FROM promotion_games pg
    WHERE pg.promotion_id = p.id
      AND pg.game_id = g.id
  );

-- Link Cozy25 to Space Odyssey
INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Space Odyssey'
WHERE p.name = 'Cozy25'
  AND NOT EXISTS (
    SELECT 1 FROM promotion_games pg
    WHERE pg.promotion_id = p.id
      AND pg.game_id = g.id
  );

---------------------------------------------------------------
-- 3) ACTION5 – $5 off selected action titles
--    Applies to: Cyberpunk Legends, Zombie Survival
---------------------------------------------------------------
INSERT INTO promotions (
  name, description, discount_type, discount_value,
  coupon_code, is_active, valid_from, valid_until,
  usage_limit
)
SELECT
  'Action5',
  'Get $5 off selected action games',
  'fixed_amount',
  5.00,
  'ACTION5',
  TRUE,
  NOW() - INTERVAL '2 days',
  NOW() + INTERVAL '7 days',
  300
WHERE NOT EXISTS (
  SELECT 1 FROM promotions WHERE name = 'Action5'
);

-- Link Action5 to Cyberpunk Legends
INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Cyberpunk Legends'
WHERE p.name = 'Action5'
  AND NOT EXISTS (
    SELECT 1 FROM promotion_games pg
    WHERE pg.promotion_id = p.id
      AND pg.game_id = g.id
  );

-- Link Action5 to Zombie Survival
INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Zombie Survival'
WHERE p.name = 'Action5'
  AND NOT EXISTS (
    SELECT 1 FROM promotion_games pg
    WHERE pg.promotion_id = p.id
      AND pg.game_id = g.id
  );

---------------------------------------------------------------
-- 4) STRATWEEKEND – 50% weekend sale on strategy games
--    No coupon (automatic sale)
--    Applies to: Tactical Warfare, Medieval Kingdoms
---------------------------------------------------------------
INSERT INTO promotions (
  name, description, discount_type, discount_value,
  coupon_code, is_active, valid_from, valid_until,
  usage_limit
)
SELECT
  'StratWeekend',
  'Weekend sale: 50% off selected strategy games',
  'percentage',
  50.00,
  NULL,              -- no coupon, automatic discount
  TRUE,
  NOW() - INTERVAL '1 day',
  NOW() + INTERVAL '2 days',
  NULL               -- no explicit usage limit
WHERE NOT EXISTS (
  SELECT 1 FROM promotions WHERE name = 'StratWeekend'
);

-- Link StratWeekend to Tactical Warfare
INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Tactical Warfare'
WHERE p.name = 'StratWeekend'
  AND NOT EXISTS (
    SELECT 1 FROM promotion_games pg
    WHERE pg.promotion_id = p.id
      AND pg.game_id = g.id
  );

-- Link StratWeekend to Medieval Kingdoms
INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Medieval Kingdoms'
WHERE p.name = 'StratWeekend'
  AND NOT EXISTS (
    SELECT 1 FROM promotion_games pg
    WHERE pg.promotion_id = p.id
      AND pg.game_id = g.id
  );

-- ============================================================
-- End of promotions + promotion_games seeds
-- ============================================================
