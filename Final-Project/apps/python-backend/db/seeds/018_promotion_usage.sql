-- db/seeds/018_promotion_usage.sql
-- ============================================================
-- Promotion Usage Seeds
-- Requires:
--   - promotions already inserted (005_promotions.sql)
--   - users already inserted (000_users.sql)
--   - orders already inserted (007_orders.sql)
-- ============================================================

-- 1) Alice used Launch10 on order EST-100001
INSERT INTO promotion_usage (promotion_id, user_id, order_id)
SELECT 
  p.id,
  u.id,
  o.id
FROM promotions p
JOIN users u  ON u.email = 'alice@example.com'
JOIN orders o ON o.order_number = 'EST-100001'
WHERE p.name = 'Launch10'
  AND NOT EXISTS (
    SELECT 1 FROM promotion_usage pu
    WHERE pu.promotion_id = p.id AND pu.user_id = u.id
  );

-- 2) Bob used Cozy25 on order EST-100002
INSERT INTO promotion_usage (promotion_id, user_id, order_id)
SELECT 
  p.id,
  u.id,
  o.id
FROM promotions p
JOIN users u  ON u.email = 'bob@example.com'
JOIN orders o ON o.order_number = 'EST-100002'
WHERE p.name = 'Cozy25'
  AND NOT EXISTS (
    SELECT 1 FROM promotion_usage pu
    WHERE pu.promotion_id = p.id AND pu.user_id = u.id
  );

-- 3) Charlie used Action5 on order EST-100003
INSERT INTO promotion_usage (promotion_id, user_id, order_id)
SELECT 
  p.id,
  u.id,
  o.id
FROM promotions p
JOIN users u  ON u.email = 'charlie@example.com'
JOIN orders o ON o.order_number = 'EST-100003'
WHERE p.name = 'Action5'
  AND NOT EXISTS (
    SELECT 1 FROM promotion_usage pu
    WHERE pu.promotion_id = p.id AND pu.user_id = u.id
  );

-- ============================================================
-- End of promotion_usage seeds
-- ============================================================
