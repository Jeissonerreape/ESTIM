-- ============================================================
-- Refund Request Seeds (Idempotent)
-- ============================================================

-- Alice → Refund request for EST-100001 (Cube Quest)
INSERT INTO refund_requests (
  user_id,
  order_id,
  ticket_id,
  reason,
  status,
  amount_requested,
  amount_approved,
  description,
  created_at,
  processed_at,
  processed_by
)
SELECT
  u.id,
  o.id,
  t.id,
  'accidental_purchase',   -- must exist in refund_reason enum
  'rejected',              -- must exist in refund_status enum
  8.99,
  8.99,                    -- amount_approved = requested (policy-based rejection)
  'Playtime exceeds policy.',
  NOW() - INTERVAL '2 days',
  NOW() - INTERVAL '1 day',
  admin_user.id
FROM users u
JOIN orders o
  ON o.order_number = 'EST-100001'
JOIN support_tickets t
  ON t.subject = 'Refund request for Cube Quest'
JOIN users admin_user
  ON admin_user.email = 'admin@example.com'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1
    FROM refund_requests rr
    WHERE rr.user_id = u.id
      AND rr.order_id = o.id
  );

-- ============================================================
-- End of Refund Request Seeds
-- ============================================================
