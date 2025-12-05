-- db/seeds/007_orders.sql
-- ============================================================
-- Order Flow Seeds: orders, order_items, order_promotions
-- (promotion_usage and licenses are seeded elsewhere)
-- ============================================================


---------------------------------------------------------------
-- 1) ALICE — Order EST-100001 — Cube Quest + Launch10
---------------------------------------------------------------

-- Order
INSERT INTO orders (
  user_id, order_number, status, subtotal, tax_amount, discount_amount,
  total_amount, currency, payment_gateway, payment_gateway_id, paid_at
)
SELECT
  u.id, 'EST-100001', 'completed',
  9.99, 0.00, 1.00, 8.99,
  'USD', 'mock', 'PAY-123', NOW()
FROM users u
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM orders WHERE order_number = 'EST-100001'
  );

-- Order item
INSERT INTO order_items (order_id, game_id, quantity, unit_price, total_price)
SELECT
  o.id,
  g.id,
  1,
  9.99,
  9.99
FROM orders o
JOIN games g ON g.title = 'Cube Quest'
WHERE o.order_number = 'EST-100001'
  AND NOT EXISTS (
    SELECT 1 FROM order_items oi
    WHERE oi.order_id = o.id AND oi.game_id = g.id
  );

-- Order promotion
INSERT INTO order_promotions (order_id, promotion_id, discount_amount)
SELECT
  o.id,
  p.id,
  1.00
FROM orders o
JOIN promotions p ON p.name = 'Launch10'
WHERE o.order_number = 'EST-100001'
  AND NOT EXISTS (
    SELECT 1 FROM order_promotions op
    WHERE op.order_id = o.id AND op.promotion_id = p.id
  );



---------------------------------------------------------------
-- 2) BOB — Order EST-100002 — Fantasy Quest Online + Cozy25
---------------------------------------------------------------

-- Order
INSERT INTO orders (
  user_id, order_number, status, subtotal, tax_amount, discount_amount,
  total_amount, currency, payment_gateway, payment_gateway_id, paid_at
)
SELECT
  u.id, 'EST-100002', 'completed',
  12.99, 0.00, 3.25, 9.74,
  'USD', 'mock', 'PAY-456', NOW()
FROM users u
WHERE u.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM orders WHERE order_number = 'EST-100002'
  );

-- Order item
INSERT INTO order_items (order_id, game_id, quantity, unit_price, total_price)
SELECT
  o.id,
  g.id,
  1,
  12.99,
  12.99
FROM orders o
JOIN games g ON g.title = 'Fantasy Quest Online'
WHERE o.order_number = 'EST-100002'
  AND NOT EXISTS (
    SELECT 1 FROM order_items oi
    WHERE oi.order_id = o.id AND oi.game_id = g.id
  );

-- Order promotion
INSERT INTO order_promotions (order_id, promotion_id, discount_amount)
SELECT
  o.id,
  p.id,
  3.25
FROM orders o
JOIN promotions p ON p.name = 'Cozy25'
WHERE o.order_number = 'EST-100002'
  AND NOT EXISTS (
    SELECT 1 FROM order_promotions op
    WHERE op.order_id = o.id AND op.promotion_id = p.id
  );



---------------------------------------------------------------
-- 3) CHARLIE — Order EST-100003 — Cyberpunk Legends + Action5
---------------------------------------------------------------

-- Order
INSERT INTO orders (
  user_id, order_number, status, subtotal, tax_amount, discount_amount,
  total_amount, currency, payment_gateway, payment_gateway_id, paid_at
)
SELECT
  u.id, 'EST-100003', 'completed',
  39.99, 0.00, 5.00, 34.99,
  'USD', 'mock', 'PAY-789', NOW()
FROM users u
WHERE u.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM orders WHERE order_number = 'EST-100003'
  );

-- Order item
INSERT INTO order_items (order_id, game_id, quantity, unit_price, total_price)
SELECT
  o.id,
  g.id,
  1,
  39.99,
  39.99
FROM orders o
JOIN games g ON g.title = 'Cyberpunk Legends'
WHERE o.order_number = 'EST-100003'
  AND NOT EXISTS (
    SELECT 1 FROM order_items oi
    WHERE oi.order_id = o.id AND oi.game_id = g.id
  );

-- Order promotion
INSERT INTO order_promotions (order_id, promotion_id, discount_amount)
SELECT
  o.id,
  p.id,
  5.00
FROM orders o
JOIN promotions p ON p.name = 'Action5'
WHERE o.order_number = 'EST-100003'
  AND NOT EXISTS (
    SELECT 1 FROM order_promotions op
    WHERE op.order_id = o.id AND op.promotion_id = p.id
  );

-- ============================================================
-- End of Order Flow Seeds (orders + items + order_promotions)
-- ============================================================
