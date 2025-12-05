-- ============================================================
-- Friendship Seeds (Idempotent)
-- ============================================================

---------------------------------------------------------------
-- 1) Alice ↔ Bob (accepted)
---------------------------------------------------------------
INSERT INTO friendships (user_id, friend_id, status, accepted_at)
SELECT
  u1.id,
  u2.id,
  'accepted',
  NOW()
FROM users u1, users u2
WHERE u1.email = 'alice@example.com'
  AND u2.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM friendships f
    WHERE f.user_id = u1.id AND f.friend_id = u2.id
  );

---------------------------------------------------------------
-- 2) Alice ↔ Charlie (pending)
---------------------------------------------------------------
INSERT INTO friendships (user_id, friend_id, status)
SELECT
  u1.id,
  u2.id,
  'pending'
FROM users u1, users u2
WHERE u1.email = 'alice@example.com'
  AND u2.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM friendships f
    WHERE f.user_id = u1.id AND f.friend_id = u2.id
  );

---------------------------------------------------------------
-- 3) Bob ↔ Diana (accepted)
---------------------------------------------------------------
INSERT INTO friendships (user_id, friend_id, status, accepted_at)
SELECT
  u1.id,
  u2.id,
  'accepted',
  NOW()
FROM users u1, users u2
WHERE u1.email = 'bob@example.com'
  AND u2.email = 'diana@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM friendships f
    WHERE f.user_id = u1.id AND f.friend_id = u2.id
  );

---------------------------------------------------------------
-- 4) Charlie ↔ Bob (blocked)
---------------------------------------------------------------
INSERT INTO friendships (user_id, friend_id, status)
SELECT
  u1.id,
  u2.id,
  'blocked'    -- FIXED: was 'rejected'
FROM users u1, users u2
WHERE u1.email = 'charlie@example.com'
  AND u2.email = 'bob@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM friendships f
    WHERE f.user_id = u1.id AND f.friend_id = u2.id
  );

-- ============================================================
-- End of Friendship Seeds
-- ============================================================
