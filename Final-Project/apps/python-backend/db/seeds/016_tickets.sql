-- ============================================================
-- Support Ticket Seeds (Idempotent)
-- ============================================================

---------------------------------------------------------------
-- Ticket 1 — Alice: Refund request for Cube Quest
---------------------------------------------------------------
INSERT INTO support_tickets (user_id, subject, description, category, status, priority, assigned_to, created_at)
SELECT
  u.id,
  'Refund request for Cube Quest',
  'I purchased by mistake.',
  'refund',
  'open',
  'medium',
  admin.id,
  NOW() - INTERVAL '2 days'
FROM users u
JOIN users admin ON admin.email = 'admin@example.com'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM support_tickets t
    WHERE t.subject = 'Refund request for Cube Quest'
  );

-- Alice's message
INSERT INTO ticket_messages (ticket_id, author_id, message, created_at)
SELECT
  t.id,
  u.id,
  'Please help.',
  NOW() - INTERVAL '2 days'
FROM support_tickets t
JOIN users u ON u.email = 'alice@example.com'
WHERE t.subject = 'Refund request for Cube Quest'
  AND NOT EXISTS (
    SELECT 1 FROM ticket_messages m
    WHERE m.ticket_id = t.id AND m.message = 'Please help.'
  );

-- Admin reply
INSERT INTO ticket_messages (ticket_id, author_id, message, is_internal, created_at)
SELECT
  t.id,
  a.id,
  'We are reviewing your refund request now.',
  FALSE,
  NOW() - INTERVAL '1 day'
FROM support_tickets t
JOIN users a ON a.email = 'admin@example.com'
WHERE t.subject = 'Refund request for Cube Quest'
  AND NOT EXISTS (
    SELECT 1 FROM ticket_messages m
    WHERE m.ticket_id = t.id
      AND m.message LIKE 'We are reviewing%'
  );


---------------------------------------------------------------
-- Ticket 2 — Charlie: Bug report in Cyberpunk Legends
---------------------------------------------------------------
INSERT INTO support_tickets (user_id, subject, description, category, status, priority, created_at)
SELECT
  u.id,
  'Game crashes on startup',
  'Cyberpunk Legends crashes immediately after launching.',
  'technical',
  'open',
  'high',
  NOW() - INTERVAL '3 hours'
FROM users u
WHERE u.email = 'charlie@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM support_tickets t
    WHERE t.subject = 'Game crashes on startup'
  );

-- Charlie initial message
INSERT INTO ticket_messages (ticket_id, author_id, message, created_at)
SELECT
  t.id,
  u.id,
  'It crashes every time I click Play.',
  NOW() - INTERVAL '3 hours'
FROM support_tickets t
JOIN users u ON u.email = 'charlie@example.com'
WHERE t.subject = 'Game crashes on startup'
  AND NOT EXISTS (
    SELECT 1 FROM ticket_messages m
    WHERE m.ticket_id = t.id AND m.message LIKE 'It crashes%'
  );

-- Admin internal note
INSERT INTO ticket_messages (ticket_id, author_id, message, is_internal, created_at)
SELECT
  t.id,
  a.id,
  'Possible GPU driver issue. Investigate crash logs.',
  TRUE,
  NOW() - INTERVAL '2 hours'
FROM support_tickets t
JOIN users a ON a.email = 'admin@example.com'
WHERE t.subject = 'Game crashes on startup'
  AND NOT EXISTS (
    SELECT 1 FROM ticket_messages m
    WHERE m.ticket_id = t.id AND m.is_internal = TRUE
  );


---------------------------------------------------------------
-- Ticket 3 — Diana: Account help (Closed)
---------------------------------------------------------------
INSERT INTO support_tickets (user_id, subject, description, category, status, priority, assigned_to, created_at, closed_at)
SELECT
  u.id,
  'Cannot change my email address',
  'The email change option gives an error.',
  'account',
  'closed',
  'low',
  admin.id,
  NOW() - INTERVAL '10 days',
  NOW() - INTERVAL '9 days'
FROM users u
JOIN users admin ON admin.email = 'admin@example.com'
WHERE u.email = 'diana@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM support_tickets t
    WHERE t.subject = 'Cannot change my email address'
  );

-- Diana initial message
INSERT INTO ticket_messages (ticket_id, author_id, message, created_at)
SELECT
  t.id,
  u.id,
  'Hi, I keep getting an error when trying to update my email.',
  NOW() - INTERVAL '10 days'
FROM support_tickets t
JOIN users u ON u.email = 'diana@example.com'
WHERE t.subject = 'Cannot change my email address'
  AND NOT EXISTS (
    SELECT 1 FROM ticket_messages m
    WHERE m.ticket_id = t.id AND m.message LIKE 'Hi, I keep%'
  );

-- Admin response
INSERT INTO ticket_messages (ticket_id, author_id, message, created_at)
SELECT
  t.id,
  a.id,
  'Issue fixed. You can update your email now.',
  NOW() - INTERVAL '9 days'
FROM support_tickets t
JOIN users a ON a.email = 'admin@example.com'
WHERE t.subject = 'Cannot change my email address'
  AND NOT EXISTS (
    SELECT 1 FROM ticket_messages m
    WHERE m.ticket_id = t.id
      AND m.message LIKE 'Issue fixed%'
  );

-- ============================================================
-- End of Support Ticket Seeds
-- ============================================================
