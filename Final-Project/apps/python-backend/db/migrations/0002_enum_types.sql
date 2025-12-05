-- ===== ENUMS =====
CREATE TYPE user_role AS ENUM ('user','publisher','admin','moderator');

CREATE TYPE age_rating_type AS ENUM ('EC','E','E10','T','M','AO','RP');

CREATE TYPE media_type AS ENUM ('screenshot','trailer','cover_art','banner');

CREATE TYPE platform_type AS ENUM ('windows','mac','linux','android','ios');

CREATE TYPE order_status AS ENUM ('pending','processing','completed','cancelled','refunded');

CREATE TYPE payment_gateway_type AS ENUM ('pagseguro','stripe','paypal','mock');

CREATE TYPE friendship_status AS ENUM ('pending','accepted','blocked');

CREATE TYPE message_type AS ENUM ('text','image','system');

CREATE TYPE activity_type AS ENUM ('game_purchased','achievement_unlocked','review_posted','friend_added','game_played');

CREATE TYPE target_type AS ENUM ('game','user','review','achievement');

CREATE TYPE achievement_type AS ENUM ('bronze','silver','gold','platinum','hidden');

CREATE TYPE ticket_category AS ENUM ('technical','billing','account','game_issue','refund','other');

CREATE TYPE ticket_status AS ENUM ('open','in_progress','waiting_customer','resolved','closed');

CREATE TYPE ticket_priority AS ENUM ('low','medium','high','urgent');

CREATE TYPE refund_reason AS ENUM ('technical_issues','accidental_purchase','unsatisfactory','wrong_game','other');

CREATE TYPE refund_status AS ENUM ('pending','approved','rejected','processed');

CREATE TYPE discount_type AS ENUM ('percentage','fixed_amount','free_shipping');

-- Admin-specific
CREATE TYPE admin_target_type AS ENUM ('publisher','user','review','support_ticket');
