-- Users
INSERT INTO users (id, email, password_hash, display_name, role)
VALUES
  (gen_random_uuid(), 'alice@example.com',  '$2y$dummy', 'Alice', 'user'),
  (gen_random_uuid(), 'bob@example.com',    '$2y$dummy', 'Bob',   'publisher'),
  (gen_random_uuid(), 'admin@example.com',  '$2y$dummy', 'Admin', 'admin');

-- Mark admin as administrator
INSERT INTO administrators (user_id)
SELECT id FROM users WHERE email = 'admin@example.com';

-- Publisher linked to Bob
INSERT INTO publishers (user_id, company_name, description, website_url, contact_email, is_verified, approved_by, approved_at)
VALUES (
  (SELECT id FROM users WHERE email = 'bob@example.com'),
  'Bob Games Studio',
  'Indie studio making tiny bangers.',
  'https://bobgames.example',
  'contact@bobgames.example',
  TRUE,
  (SELECT a.id FROM administrators a JOIN users u ON a.user_id = u.id WHERE u.email = 'admin@example.com'),
  NOW()
);

-- A Game
INSERT INTO games (publisher_id, title, description, short_description, price, base_price, is_published, release_date, age_rating, metadata)
VALUES (
  (SELECT id FROM publishers WHERE company_name = 'Bob Games Studio'),
  'Cube Quest',
  'A minimalist puzzle-adventure with cubes.',
  'Minimalist puzzle-adventure.',
  9.99, 14.99, TRUE, CURRENT_DATE - INTERVAL '30 days', 'E',
  '{"tags": ["puzzle","minimalist"], "languages": ["en","es"]}'::jsonb
);

-- Media and binaries
INSERT INTO game_media (game_id, media_type, url, caption, display_order)
VALUES (
  (SELECT id FROM games WHERE title = 'Cube Quest'), 'screenshot',
  'https://cdn.example/cube-quest-1.png', 'First look', 1
);

INSERT INTO game_binaries (game_id, platform, version, download_url, file_size, checksum, is_active)
VALUES (
  (SELECT id FROM games WHERE title = 'Cube Quest'),
  'windows', '1.0.0', 'https://cdn.example/cube-quest-win.zip', 150000000, 'abc123', TRUE
);

-- Promotion (10% off)
INSERT INTO promotions (name, description, discount_type, discount_value, coupon_code, is_active, valid_from, valid_until, usage_limit)
VALUES ('Launch10', '10% off launch window', 'percentage', 10.00, 'LAUNCH10', TRUE, NOW() - INTERVAL '1 day', NOW() + INTERVAL '14 days', 1000);

-- Link promo to game
INSERT INTO promotion_games (promotion_id, game_id)
VALUES (
  (SELECT id FROM promotions WHERE name = 'Launch10'),
  (SELECT id FROM games WHERE title = 'Cube Quest')
);

-- Alice gets a cart and adds the game with promo
INSERT INTO shopping_carts (user_id)
SELECT id FROM users WHERE email = 'alice@example.com';

INSERT INTO cart_items (cart_id, user_id, game_id, promotion_id, quantity)
VALUES (
  (SELECT id FROM shopping_carts WHERE user_id = (SELECT id FROM users WHERE email = 'alice@example.com')),
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  (SELECT id FROM games WHERE title = 'Cube Quest'),
  (SELECT id FROM promotions WHERE name = 'Launch10'),
  1
);

-- Alice places order
INSERT INTO orders (user_id, order_number, status, subtotal, tax_amount, discount_amount, total_amount, currency, payment_gateway, payment_gateway_id, paid_at)
VALUES (
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  'EST-100001', 'completed', 9.99, 0.00, 1.00, 8.99, 'USD', 'mock', 'PAY-123', NOW()
);

INSERT INTO order_items (order_id, game_id, quantity, unit_price, total_price)
VALUES (
  (SELECT id FROM orders WHERE order_number = 'EST-100001'),
  (SELECT id FROM games WHERE title = 'Cube Quest'),
  1, 9.99, 9.99
);

-- Order promotions
INSERT INTO order_promotions (order_id, promotion_id, discount_amount)
VALUES (
  (SELECT id FROM orders WHERE order_number = 'EST-100001'),
  (SELECT id FROM promotions WHERE name = 'Launch10'),
  1.00
);

-- Promotion usage record
INSERT INTO promotion_usage (promotion_id, user_id, order_id)
VALUES (
  (SELECT id FROM promotions WHERE name = 'Launch10'),
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  (SELECT id FROM orders WHERE order_number = 'EST-100001')
);

-- Library + License for Alice after purchase
INSERT INTO libraries (user_id, game_id, source)
VALUES (
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  (SELECT id FROM games WHERE title = 'Cube Quest'),
  'purchase'
);

INSERT INTO licenses (library_id, user_id, game_id, license_key)
VALUES (
  (SELECT id FROM libraries WHERE user_id = (SELECT id FROM users WHERE email = 'alice@example.com')
                         AND game_id = (SELECT id FROM games WHERE title = 'Cube Quest')),
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  (SELECT id FROM games WHERE title = 'Cube Quest'),
  'CQ-ALICE-0001'
);

-- Review by Alice
INSERT INTO reviews (user_id, game_id, rating, title, body, is_verified_owner)
VALUES (
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  (SELECT id FROM games WHERE title = 'Cube Quest'),
  5, 'Delightful!', 'Short, sweet, smart puzzles.', TRUE
);

-- Achievement + user_achievement
INSERT INTO achievements (game_id, name, description, achievement_type, criteria, display_order)
VALUES (
  (SELECT id FROM games WHERE title = 'Cube Quest'),
  'First Cube', 'Complete the first level', 'bronze', '{"level":1}', 1
);

INSERT INTO user_achievements (user_id, achievement_id, game_id, progress, is_unlocked, unlocked_at)
VALUES (
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  (SELECT id FROM achievements WHERE name = 'First Cube'),
  (SELECT id FROM games WHERE title = 'Cube Quest'),
  100, TRUE, NOW()
);

-- Wishlist
INSERT INTO wishlists (user_id, game_id, promotion_id, notes)
VALUES (
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  (SELECT id FROM games WHERE title = 'Cube Quest'),
  (SELECT id FROM promotions WHERE name = 'Launch10'),
  'If a deeper discount appears, buy a gift copy.'
);

-- Preferences
INSERT INTO user_preferences (user_id, favorite_genres, preferred_price_range)
VALUES (
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  ARRAY['puzzle','indie'],
  '{"min":0,"max":15}'::jsonb
);

-- Cloud save
INSERT INTO cloud_saves (user_id, game_id, save_slot, save_name, file_path, file_size, metadata)
VALUES (
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  (SELECT id FROM games WHERE title = 'Cube Quest'),
  1, 'Level 1', '/saves/alice/cq/slot1.sav', 4096, '{"version":"1.0.0"}'
);

-- Social
INSERT INTO friendships (user_id, friend_id, status, accepted_at)
VALUES (
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  (SELECT id FROM users WHERE email = 'bob@example.com'),
  'accepted', NOW()
);

INSERT INTO chat_messages (sender_id, receiver_id, message_type, content, is_read, read_at)
VALUES (
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  (SELECT id FROM users WHERE email = 'bob@example.com'),
  'text', 'Hey, love your game!', TRUE, NOW()
);

-- Support & Refund (example opened then rejected)
INSERT INTO support_tickets (user_id, subject, description, category, status, priority, assigned_to)
VALUES (
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  'Refund request for Cube Quest',
  'I purchased by mistake.',
  'refund', 'open', 'medium',
  (SELECT id FROM users WHERE email = 'admin@example.com')
);

INSERT INTO ticket_messages (ticket_id, author_id, message)
VALUES (
  (SELECT id FROM support_tickets WHERE subject = 'Refund request for Cube Quest'),
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  'Please help.'
);

INSERT INTO refund_requests (user_id, order_id, ticket_id, reason, status, amount_requested, description)
VALUES (
  (SELECT id FROM users WHERE email = 'alice@example.com'),
  (SELECT id FROM orders WHERE order_number = 'EST-100001'),
  (SELECT id FROM support_tickets WHERE subject = 'Refund request for Cube Quest'),
  'accidental_purchase', 'rejected', 8.99, 'Playtime exceeds policy.'
);
