-- ============================================================
-- Chat Messages Seeds (Idempotent)
-- ============================================================

---------------------------------------------------------------
-- 1) Alice → Bob (text, read)
---------------------------------------------------------------
INSERT INTO chat_messages (sender_id, receiver_id, message_type, content, is_read, read_at, sent_at)
SELECT
  u1.id,
  u2.id,
  'text',
  'Hey Bob! Love your game — the drifting feels amazing!',
  TRUE,
  NOW(),
  NOW() - INTERVAL '5 minutes'
FROM users u1, users u2
WHERE u1.email = 'alice@example.com'
  AND u2.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM chat_messages m
    WHERE m.sender_id = u1.id AND m.receiver_id = u2.id
          AND m.content LIKE 'Hey Bob%'
  );

---------------------------------------------------------------
-- 2) Bob → Alice (text, unread)
---------------------------------------------------------------
INSERT INTO chat_messages (sender_id, receiver_id, message_type, content, is_read, sent_at)
SELECT
  u1.id,
  u2.id,
  'text',
  'Thanks Alice! Really glad you liked it — big update coming soon!',
  FALSE,
  NOW() - INTERVAL '2 minutes'
FROM users u1, users u2
WHERE u1.email = 'bob@example.com'
  AND u2.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM chat_messages m
    WHERE m.sender_id = u1.id AND m.receiver_id = u2.id
          AND m.content LIKE 'Thanks Alice%'
  );

---------------------------------------------------------------
-- 3) Alice → Charlie (read)
---------------------------------------------------------------
INSERT INTO chat_messages (sender_id, receiver_id, message_type, content, is_read, read_at, sent_at)
SELECT
  u1.id,
  u2.id,
  'text',
  'Hey Charlie, are you joining the tournament tonight?',
  TRUE,
  NOW(),
  NOW() - INTERVAL '30 minutes'
FROM users u1, users u2
WHERE u1.email = 'alice@example.com'
  AND u2.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM chat_messages m
    WHERE m.sender_id = u1.id AND m.receiver_id = u2.id
          AND m.content LIKE 'Hey Charlie%'
  );

---------------------------------------------------------------
-- 4) Charlie → Alice (read reply)
---------------------------------------------------------------
INSERT INTO chat_messages (sender_id, receiver_id, message_type, content, is_read, read_at, sent_at)
SELECT
  u1.id,
  u2.id,
  'text',
  'Yeah! I''ll be online — practicing my build already.',
  TRUE,
  NOW(),
  NOW() - INTERVAL '20 minutes'
FROM users u1, users u2
WHERE u1.email = 'charlie@example.com'
  AND u2.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM chat_messages m
    WHERE m.sender_id = u1.id AND m.receiver_id = u2.id
          AND m.content LIKE 'Yeah! I''ll be online%'
  );

---------------------------------------------------------------
-- 5) Bob → Diana (image, unread)
---------------------------------------------------------------
INSERT INTO chat_messages (sender_id, receiver_id, message_type, content, is_read, sent_at)
SELECT
  u1.id,
  u2.id,
  'image',
  'https://images.example.com/screenshots/starforge_update_1.png',
  FALSE,
  NOW() - INTERVAL '3 hours'
FROM users u1, users u2
WHERE u1.email = 'bob@example.com'
  AND u2.email = 'diana@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM chat_messages m
    WHERE m.sender_id = u1.id AND m.receiver_id = u2.id
          AND m.message_type = 'image'
  );

---------------------------------------------------------------
-- 6) Diana → Bob (text, read)
---------------------------------------------------------------
INSERT INTO chat_messages (sender_id, receiver_id, message_type, content, is_read, read_at, sent_at)
SELECT
  u1.id,
  u2.id,
  'text',
  'Whoa! The visuals look amazing. Is this for the next patch?',
  TRUE,
  NOW(),
  NOW() - INTERVAL '2 hours'
FROM users u1, users u2
WHERE u1.email = 'diana@example.com'
  AND u2.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM chat_messages m
    WHERE m.sender_id = u1.id AND m.receiver_id = u2.id
          AND m.content LIKE 'Whoa! The visuals%'
  );

-- ============================================================
-- End of Chat Message Seeds
-- ============================================================
