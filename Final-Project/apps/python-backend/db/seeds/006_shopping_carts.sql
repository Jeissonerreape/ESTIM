-- db/seeds/006_shopping_carts.sql
-- ============================================================
-- Shopping carts + initial cart_items
-- ============================================================

---------------------------------------------------------------
-- 1) Shopping cart for Alice
---------------------------------------------------------------
INSERT INTO shopping_carts (user_id)
SELECT u.id
FROM users u
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM shopping_carts sc
    WHERE sc.user_id = u.id
  );

---------------------------------------------------------------
-- 2) Alice has Cube Quest in cart with Launch10 promo
---------------------------------------------------------------
INSERT INTO cart_items (cart_id, user_id, game_id, promotion_id, quantity)
SELECT
  sc.id,
  u.id,
  g.id,
  p.id,
  1
FROM users u
JOIN shopping_carts sc ON sc.user_id = u.id
JOIN games g ON g.title = 'Cube Quest'
LEFT JOIN promotions p ON p.name = 'Launch10'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1
    FROM cart_items ci
    WHERE ci.cart_id = sc.id
      AND ci.game_id = g.id
      AND ci.promotion_id IS NOT DISTINCT FROM p.id
  );

-- ============================================================
-- End of shopping cart seeds
-- ============================================================
