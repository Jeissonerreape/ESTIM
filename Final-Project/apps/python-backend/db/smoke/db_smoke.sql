\t on
\echo '== ENUM check (user_role) =='
SELECT enumlabel
FROM pg_enum e
JOIN pg_type t ON t.oid = e.enumtypid
WHERE t.typname = 'user_role'
ORDER BY 1;

\echo '== Tables exist =='
SELECT COUNT(*) AS tables_created
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name IN
    ('users','publishers','games','orders','order_items','libraries','licenses','reviews','promotions');

\echo '== Seed users =='
SELECT email, role FROM users ORDER BY email;

\echo '== Seed game =='
SELECT title, price, is_published FROM games;

\echo '== Ownership model (library + license) =='
SELECT u.email, g.title, l.license_key
FROM licenses l
JOIN users u ON u.id = l.user_id
JOIN games g ON g.id = l.game_id;

\echo '== Trigger test (updated_at changes) =='
SELECT updated_at AS before FROM games WHERE title = 'Cube Quest';
UPDATE games SET short_description = 'SmokeTest' WHERE title = 'Cube Quest';
SELECT updated_at AS after FROM games WHERE title = 'Cube Quest';

\echo '== Constraint test (rating 1..5; expect ERROR below, this is GOOD) =='
INSERT INTO reviews (user_id, game_id, rating) VALUES
  ((SELECT id FROM users WHERE email = 'alice@example.com'),
   (SELECT id FROM games WHERE title = 'Cube Quest'), 6);
