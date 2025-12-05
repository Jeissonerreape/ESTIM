-- db/seeds/001_publishers.sql
-- ============================================================
-- Combined Publisher Seed File
-- Inserts all publishers if they do not already exist.
-- Requires that:
--   - Users with the corresponding emails already exist
--   - A user with email 'admin@example.com' is an administrator
-- ============================================================

---------------------------------------------------------------
-- 1) Bob Games Studio
---------------------------------------------------------------
INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  (SELECT id FROM users WHERE email = 'bob@example.com'),
  'Bob Games Studio',
  'Indie studio making tiny bangers.',
  'https://bobgames.example',
  'contact@bobgames.example',
  TRUE,
  (SELECT a.id
   FROM administrators a
   JOIN users u ON a.user_id = u.id
   WHERE u.email = 'admin@example.com'),
  NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM publishers WHERE company_name = 'Bob Games Studio'
);

---------------------------------------------------------------
-- 2) Starforge Interactive
---------------------------------------------------------------
INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  (SELECT id FROM users WHERE email = 'starforge@example.com'),
  'Starforge Interactive',
  'Mid-sized global studio focusing on action and sci-fi titles.',
  'https://starforge.example',
  'contact@starforge.example',
  TRUE,
  (SELECT a.id
   FROM administrators a
   JOIN users u ON a.user_id = u.id
   WHERE u.email = 'admin@example.com'),
  NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM publishers WHERE company_name = 'Starforge Interactive'
);

---------------------------------------------------------------
-- 3) Cozy Leaf Studios
---------------------------------------------------------------
INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  (SELECT id FROM users WHERE email = 'cozyleaf@example.com'),
  'Cozy Leaf Studios',
  'Indie developer focused on cozy farming, slice-of-life, and simulation titles.',
  'https://cozyleaf.example',
  'support@cozyleaf.example',
  TRUE,
  (SELECT a.id
   FROM administrators a
   JOIN users u ON a.user_id = u.id
   WHERE u.email = 'admin@example.com'),
  NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM publishers WHERE company_name = 'Cozy Leaf Studios'
);

---------------------------------------------------------------
-- 4) Neon Byte Labs
---------------------------------------------------------------
INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  (SELECT id FROM users WHERE email = 'neonbyte@example.com'),
  'Neon Byte Labs',
  'Experimental studio creating neon-soaked retro-futuristic experiences.',
  'https://neonbyte.example',
  'hello@neonbyte.example',
  TRUE,
  (SELECT a.id
   FROM administrators a
   JOIN users u ON a.user_id = u.id
   WHERE u.email = 'admin@example.com'),
  NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM publishers WHERE company_name = 'Neon Byte Labs'
);

---------------------------------------------------------------
-- 5) Seven Seas Entertainment
---------------------------------------------------------------
INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  (SELECT id FROM users WHERE email = 'sevenseas@example.com'),
  'Seven Seas Entertainment',
  'Story-focused team developing narrative adventures and visual novels.',
  'https://sevenseas.example',
  'contact@sevenseas.example',
  TRUE,
  (SELECT a.id
   FROM administrators a
   JOIN users u ON a.user_id = u.id
   WHERE u.email = 'admin@example.com'),
  NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM publishers WHERE company_name = 'Seven Seas Entertainment'
);

---------------------------------------------------------------
-- 6) Quantum Ember Studios
---------------------------------------------------------------
INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  (SELECT id FROM users WHERE email = 'quantumember@example.com'),
  'Quantum Ember Studios',
  'Developers of deep simulations and tactical strategy experiences.',
  'https://quantumember.example',
  'support@quantumember.example',
  TRUE,
  (SELECT a.id
   FROM administrators a
   JOIN users u ON a.user_id = u.id
   WHERE u.email = 'admin@example.com'),
  NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM publishers WHERE company_name = 'Quantum Ember Studios'
);

-- ============================================================
-- End of Publisher Seeds
-- ============================================================
