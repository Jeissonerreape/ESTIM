-- ============================================================
-- User Seed File (Base + New Users + New Admin)
-- ============================================================

--------------------------
-- Helper: function to avoid duplication
--------------------------
-- Not required; using WHERE NOT EXISTS in each insert.

---------------------------------------------------------------
-- 1) Existing Users (safe re-seed)
---------------------------------------------------------------
INSERT INTO users (email, password_hash, display_name, role)
SELECT 'alice@example.com', '$2y$dummy', 'Alice', 'user'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@example.com');

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'bob@example.com', '$2y$dummy', 'Bob', 'publisher'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob@example.com');

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'admin@example.com', '$2y$dummy', 'Admin', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@example.com');

-- Ensure admin@example.com is marked as administrator
INSERT INTO administrators (user_id)
SELECT id FROM users WHERE email = 'admin@example.com'
ON CONFLICT DO NOTHING;

---------------------------------------------------------------
-- 2) New Publisher Owners
---------------------------------------------------------------

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'starforge@example.com', '$2y$dummy', 'StarforgeOwner', 'publisher'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'starforge@example.com');

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'cozyleaf@example.com', '$2y$dummy', 'CozyLeafOwner', 'publisher'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'cozyleaf@example.com');

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'neonbyte@example.com', '$2y$dummy', 'NeonByteOwner', 'publisher'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'neonbyte@example.com');

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'sevenseas@example.com', '$2y$dummy', 'SevenSeasOwner', 'publisher'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'sevenseas@example.com');

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'quantumember@example.com', '$2y$dummy', 'QuantumEmberOwner', 'publisher'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'quantumember@example.com');


---------------------------------------------------------------
-- 3) New Regular Users
---------------------------------------------------------------

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'charlie@example.com', '$2y$dummy', 'Charlie', 'user'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'charlie@example.com');

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'diana@example.com', '$2y$dummy', 'Diana', 'user'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'diana@example.com');

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'eve@example.com', '$2y$dummy', 'Eve', 'user'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'eve@example.com');

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'frank@example.com', '$2y$dummy', 'Frank', 'user'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'frank@example.com');

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'grace@example.com', '$2y$dummy', 'Grace', 'user'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'grace@example.com');


---------------------------------------------------------------
-- 4) New Admin User
---------------------------------------------------------------

INSERT INTO users (email, password_hash, display_name, role)
SELECT 'moderator@example.com', '$2y$dummy', 'Moderator', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'moderator@example.com');

-- Mark as administrator
INSERT INTO administrators (user_id)
SELECT id FROM users WHERE email = 'moderator@example.com'
ON CONFLICT DO NOTHING;

-- ============================================================
-- End of User Seeds
-- ============================================================
