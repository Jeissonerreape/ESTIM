-- Helper for updated_at
-- (Trigger itself is added in 0004)
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END; $$ LANGUAGE plpgsql;

-- ==================== CORE USER & AUTH ====================
CREATE TABLE users (
  id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email                  VARCHAR(255) NOT NULL UNIQUE,
  password_hash          VARCHAR(255) NOT NULL,
  display_name           VARCHAR(100) NOT NULL,
  avatar_url             TEXT,
  bio                    TEXT,
  role                   user_role NOT NULL DEFAULT 'user',
  is_active              BOOLEAN NOT NULL DEFAULT TRUE,
  email_verified         BOOLEAN NOT NULL DEFAULT FALSE,
  last_login_at          TIMESTAMPTZ,
  location               VARCHAR(100),
  privacy_show_activity  BOOLEAN NOT NULL DEFAULT TRUE,
  privacy_show_profile   BOOLEAN NOT NULL DEFAULT TRUE,
  privacy_show_wishlist  BOOLEAN NOT NULL DEFAULT TRUE,
  status                 VARCHAR(50),
  created_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at             TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_display_name ON users(display_name);
CREATE INDEX idx_users_created_at   ON users(created_at);


CREATE TABLE user_profiles (
  id                        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id                   UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  bio                       TEXT,
  location                  VARCHAR(100),
  website_url               TEXT,
  social_links              JSONB,
  privacy_settings          JSONB,
  notification_preferences  JSONB,
  created_at                TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at                TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ==================== ADMINISTRATORS & ACTIONS ====================
CREATE TABLE administrators (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id     UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_administrators_user_id ON administrators(user_id);

CREATE TABLE admin_actions (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  administrator_id UUID NOT NULL REFERENCES administrators(id) ON DELETE CASCADE,
  target_type      admin_target_type NOT NULL,
  target_id        UUID NOT NULL,
  action           VARCHAR(100) NOT NULL, -- suspend, verify, delete, warn, etc.
  reason           TEXT,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_admin_actions_admin_id ON admin_actions(administrator_id);
CREATE INDEX idx_admin_actions_target_type ON admin_actions(target_type);
CREATE INDEX idx_admin_actions_created_at ON admin_actions(created_at);
CREATE UNIQUE INDEX uq_admin_actions_target_pair ON admin_actions(target_type, target_id);

-- ==================== PUBLISHERS ====================
CREATE TABLE publishers (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id          UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  company_name     VARCHAR(255) NOT NULL,
  description      TEXT,
  website_url      TEXT,
  contact_email    VARCHAR(255),
  tax_id           VARCHAR(100),
  payment_details  JSONB,
  is_verified      BOOLEAN NOT NULL DEFAULT FALSE,
  approved_by      UUID REFERENCES administrators(id),
  approved_at      TIMESTAMPTZ,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ==================== GAMES ====================
CREATE TABLE games (
  id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  publisher_id         UUID NOT NULL REFERENCES publishers(id) ON DELETE CASCADE,
  title                VARCHAR(255) NOT NULL,
  description          TEXT,
  short_description    VARCHAR(500),
  price                NUMERIC(10,2) NOT NULL,
  base_price           NUMERIC(10,2),
  is_published         BOOLEAN NOT NULL DEFAULT FALSE,
  release_date         DATE,
  age_rating           age_rating_type,
  system_requirements  JSONB,
  metadata             JSONB,
  average_rating       NUMERIC(3,2) NOT NULL DEFAULT 0.0,
  review_count         INTEGER NOT NULL DEFAULT 0,
  total_playtime       BIGINT  NOT NULL DEFAULT 0,
  download_count       INTEGER NOT NULL DEFAULT 0,
  created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_games_publisher_id  ON games(publisher_id);
CREATE INDEX idx_games_title         ON games(title);
CREATE INDEX idx_games_release_date  ON games(release_date);
CREATE INDEX idx_games_price         ON games(price);
CREATE INDEX idx_games_avg_rating    ON games(average_rating);
CREATE INDEX idx_games_published     ON games(is_published);

CREATE TABLE game_media (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  media_type    media_type NOT NULL,
  url           TEXT NOT NULL,
  thumbnail_url TEXT,
  caption       VARCHAR(255),
  display_order INTEGER NOT NULL DEFAULT 0,
  uploaded_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_game_media_game_id      ON game_media(game_id);
CREATE INDEX idx_game_media_media_type   ON game_media(media_type);
CREATE INDEX idx_game_media_display_order ON game_media(display_order);

CREATE TABLE game_binaries (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  platform      platform_type NOT NULL,
  version       VARCHAR(50) NOT NULL,
  download_url  TEXT NOT NULL,
  file_size     BIGINT NOT NULL,
  checksum      VARCHAR(64),
  release_notes TEXT,
  is_active     BOOLEAN NOT NULL DEFAULT TRUE,
  uploaded_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_game_binaries_game_id  ON game_binaries(game_id);
CREATE INDEX idx_game_binaries_platform ON game_binaries(platform);
CREATE INDEX idx_game_binaries_active   ON game_binaries(is_active);

-- ==================== LIBRARY & LICENSES ====================
CREATE TABLE libraries (
  id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  game_id   UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  source    VARCHAR(50),
  added_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (user_id, game_id)
);

CREATE INDEX idx_libraries_user_id ON libraries(user_id);
CREATE INDEX idx_libraries_game_id ON libraries(game_id);

CREATE TABLE licenses (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  library_id    UUID NOT NULL UNIQUE REFERENCES libraries(id) ON DELETE CASCADE,
  user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  license_key   VARCHAR(100),
  is_active     BOOLEAN NOT NULL DEFAULT TRUE,
  purchase_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  expires_at    TIMESTAMPTZ,
  UNIQUE (user_id, game_id)
);

CREATE INDEX idx_licenses_user_id ON licenses(user_id);
CREATE INDEX idx_licenses_game_id ON licenses(game_id);

-- ==================== SHOPPING & ORDERS ====================
CREATE TABLE shopping_carts (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE promotions (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name              VARCHAR(255) NOT NULL,
  description       TEXT,
  discount_type     discount_type NOT NULL,
  discount_value    NUMERIC(10,2) NOT NULL,
  coupon_code       VARCHAR(100) UNIQUE,
  is_active         BOOLEAN NOT NULL DEFAULT TRUE,
  valid_from        TIMESTAMPTZ NOT NULL,
  valid_until       TIMESTAMPTZ,
  usage_limit       INTEGER,
  used_count        INTEGER NOT NULL DEFAULT 0,
  min_order_amount  NUMERIC(10,2) NOT NULL DEFAULT 0.00,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_promotions_active     ON promotions(is_active);
CREATE INDEX idx_promotions_valid_from ON promotions(valid_from);
CREATE INDEX idx_promotions_valid_until ON promotions(valid_until);

CREATE TABLE cart_items (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  cart_id       UUID NOT NULL REFERENCES shopping_carts(id) ON DELETE CASCADE,
  user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  promotion_id  UUID REFERENCES promotions(id),
  quantity      INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
  added_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (cart_id, game_id, promotion_id)
);

CREATE INDEX idx_cart_items_cart_id      ON cart_items(cart_id);
CREATE INDEX idx_cart_items_user_id      ON cart_items(user_id);
CREATE INDEX idx_cart_items_game_id      ON cart_items(game_id);
CREATE INDEX idx_cart_items_promotion_id ON cart_items(promotion_id);

CREATE TABLE orders (
  id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id              UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  order_number         VARCHAR(50) NOT NULL UNIQUE,
  status               order_status NOT NULL DEFAULT 'pending',
  subtotal             NUMERIC(10,2) NOT NULL,
  tax_amount           NUMERIC(10,2) NOT NULL DEFAULT 0.00,
  discount_amount      NUMERIC(10,2) NOT NULL DEFAULT 0.00,
  total_amount         NUMERIC(10,2) NOT NULL,
  currency             VARCHAR(3) NOT NULL DEFAULT 'USD',
  billing_address      JSONB,
  payment_gateway      payment_gateway_type,
  payment_gateway_id   VARCHAR(255),
  paid_at              TIMESTAMPTZ,
  created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_user_id   ON orders(user_id);
CREATE INDEX idx_orders_status    ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

CREATE TABLE order_items (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id     UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  game_id      UUID NOT NULL REFERENCES games(id) ON DELETE RESTRICT,
  quantity     INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
  unit_price   NUMERIC(10,2) NOT NULL,
  total_price  NUMERIC(10,2) NOT NULL
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_game_id  ON order_items(game_id);

-- Promotions links
CREATE TABLE promotion_games (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  promotion_id  UUID NOT NULL REFERENCES promotions(id) ON DELETE CASCADE,
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  UNIQUE (promotion_id, game_id)
);

CREATE INDEX idx_promotion_games_promo ON promotion_games(promotion_id);
CREATE INDEX idx_promotion_games_game  ON promotion_games(game_id);

CREATE TABLE order_promotions (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id        UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  promotion_id    UUID NOT NULL REFERENCES promotions(id) ON DELETE RESTRICT,
  discount_amount NUMERIC(10,2) NOT NULL,
  UNIQUE (order_id, promotion_id)
);

CREATE INDEX idx_order_promotions_order ON order_promotions(order_id);
CREATE INDEX idx_order_promotions_promo ON order_promotions(promotion_id);

CREATE TABLE promotion_usage (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  promotion_id  UUID NOT NULL REFERENCES promotions(id) ON DELETE CASCADE,
  user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  order_id      UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  used_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (promotion_id, user_id)
);

-- ==================== REVIEWS ====================
CREATE TABLE reviews (
  id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id            UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  game_id            UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  rating             INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
  title              VARCHAR(255),
  body               TEXT,
  is_verified_owner  BOOLEAN NOT NULL DEFAULT FALSE,
  is_helpful         INTEGER NOT NULL DEFAULT 0,
  is_public          BOOLEAN NOT NULL DEFAULT TRUE,
  created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (user_id, game_id)
);

CREATE INDEX idx_reviews_user_id   ON reviews(user_id);
CREATE INDEX idx_reviews_game_id   ON reviews(game_id);
CREATE INDEX idx_reviews_rating    ON reviews(rating);
CREATE INDEX idx_reviews_created_at ON reviews(created_at);

-- ==================== SOCIAL ====================
CREATE TABLE friendships (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  friend_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  status       friendship_status NOT NULL DEFAULT 'pending',
  created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  accepted_at  TIMESTAMPTZ,
  UNIQUE (user_id, friend_id)
);

CREATE INDEX idx_friendships_user    ON friendships(user_id);
CREATE INDEX idx_friendships_friend  ON friendships(friend_id);
CREATE INDEX idx_friendships_status  ON friendships(status);

CREATE TABLE chat_messages (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  sender_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  receiver_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  message_type  message_type NOT NULL DEFAULT 'text',
  content       TEXT NOT NULL,
  is_read       BOOLEAN NOT NULL DEFAULT FALSE,
  read_at       TIMESTAMPTZ,
  sent_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_sender     ON chat_messages(sender_id);
CREATE INDEX idx_chat_receiver   ON chat_messages(receiver_id);
CREATE INDEX idx_chat_sent_at    ON chat_messages(sent_at);
CREATE INDEX idx_chat_pair_time  ON chat_messages(sender_id, receiver_id, sent_at);

CREATE TABLE user_activity (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  activity_type activity_type NOT NULL,
  target_type   target_type,
  target_id     UUID,
  metadata      JSONB,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_activity_user    ON user_activity(user_id);
CREATE INDEX idx_user_activity_type    ON user_activity(activity_type);
CREATE INDEX idx_user_activity_created ON user_activity(created_at);

-- ==================== ACHIEVEMENTS ====================
CREATE TABLE achievements (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  game_id          UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  name             VARCHAR(255) NOT NULL,
  description      TEXT,
  icon_url         TEXT,
  achievement_type achievement_type NOT NULL,
  criteria         JSONB,
  display_order    INTEGER NOT NULL DEFAULT 0,
  is_secret        BOOLEAN NOT NULL DEFAULT FALSE,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_achievements_game_id  ON achievements(game_id);
CREATE INDEX idx_achievements_type     ON achievements(achievement_type);

CREATE TABLE user_achievements (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  achievement_id UUID NOT NULL REFERENCES achievements(id) ON DELETE CASCADE,
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  progress      INTEGER NOT NULL DEFAULT 0,
  is_unlocked   BOOLEAN NOT NULL DEFAULT FALSE,
  unlocked_at   TIMESTAMPTZ,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (user_id, achievement_id)
);

CREATE INDEX idx_user_achievements_user ON user_achievements(user_id);
CREATE INDEX idx_user_achievements_game ON user_achievements(game_id);

-- ==================== WISHLIST & PREFERENCES ====================
CREATE TABLE wishlists (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  promotion_id  UUID REFERENCES promotions(id),
  added_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  notes         TEXT,
  UNIQUE (user_id, game_id)
);

CREATE INDEX idx_wishlists_user_id      ON wishlists(user_id);
CREATE INDEX idx_wishlists_game_id      ON wishlists(game_id);
CREATE INDEX idx_wishlists_promotion_id ON wishlists(promotion_id);

CREATE TABLE user_preferences (
  id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id                  UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  favorite_genres          TEXT[],
  preferred_price_range    JSONB,
  excluded_tags            TEXT[],
  playstyle_preferences    JSONB,
  created_at               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at               TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ==================== CLOUD SAVES ====================
CREATE TABLE cloud_saves (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  save_slot     INTEGER NOT NULL DEFAULT 1,
  save_name     VARCHAR(255),
  file_path     TEXT NOT NULL,
  file_size     BIGINT NOT NULL,
  metadata      JSONB,
  last_saved_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (user_id, game_id, save_slot)
);

CREATE INDEX idx_cloud_saves_user_id ON cloud_saves(user_id);
CREATE INDEX idx_cloud_saves_game_id ON cloud_saves(game_id);

-- ==================== SUPPORT & REFUNDS ====================
CREATE TABLE support_tickets (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  subject      VARCHAR(255) NOT NULL,
  description  TEXT NOT NULL,
  category     ticket_category NOT NULL,
  status       ticket_status NOT NULL DEFAULT 'open',
  priority     ticket_priority NOT NULL DEFAULT 'medium',
  assigned_to  UUID REFERENCES users(id),
  created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  closed_at    TIMESTAMPTZ
);

CREATE INDEX idx_tickets_user      ON support_tickets(user_id);
CREATE INDEX idx_tickets_status    ON support_tickets(status);
CREATE INDEX idx_tickets_category  ON support_tickets(category);
CREATE INDEX idx_tickets_created   ON support_tickets(created_at);

CREATE TABLE ticket_messages (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ticket_id    UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
  author_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  message      TEXT NOT NULL,
  is_internal  BOOLEAN NOT NULL DEFAULT FALSE,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ticket_messages_ticket  ON ticket_messages(ticket_id);
CREATE INDEX idx_ticket_messages_author  ON ticket_messages(author_id);
CREATE INDEX idx_ticket_messages_created ON ticket_messages(created_at);

CREATE TABLE refund_requests (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id          UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  order_id         UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  ticket_id        UUID REFERENCES support_tickets(id),
  reason           refund_reason NOT NULL,
  status           refund_status NOT NULL DEFAULT 'pending',
  amount_requested NUMERIC(10,2) NOT NULL,
  amount_approved  NUMERIC(10,2),
  description      TEXT,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  processed_at     TIMESTAMPTZ,
  processed_by     UUID REFERENCES users(id)
);

CREATE TABLE password_reset_tokens (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token       VARCHAR(255) NOT NULL UNIQUE,
  expires_at  TIMESTAMPTZ NOT NULL,
  used        BOOLEAN NOT NULL DEFAULT FALSE,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_oauth_accounts (
  id               uuid PRIMARY KEY,
  user_id          uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  provider         varchar(50) NOT NULL,
  email            varchar(255),
  external_user_id varchar(255),
  linked_at        timestamptz DEFAULT now()
);

CREATE TABLE user_payment_methods (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  provider       VARCHAR(50) NOT NULL,
  external_token VARCHAR(255) NOT NULL,
  last4          VARCHAR(4),
  is_default     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_user_payment_methods_user_id
  ON user_payment_methods(user_id);


CREATE INDEX idx_password_reset_tokens_user_id     ON password_reset_tokens(user_id);
CREATE INDEX idx_password_reset_tokens_expires_at  ON password_reset_tokens(expires_at);
CREATE INDEX idx_refunds_user    ON refund_requests(user_id);
CREATE INDEX idx_refunds_order   ON refund_requests(order_id);
CREATE INDEX idx_refunds_status  ON refund_requests(status);
CREATE INDEX idx_refunds_created ON refund_requests(created_at);
CREATE INDEX idx_user_oauth_accounts_user_id ON user_oauth_accounts(user_id);

CREATE TABLE community_posts (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  author_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  title       VARCHAR(255) NOT NULL,
  body        TEXT NOT NULL,
  type        VARCHAR(32) NOT NULL,    -- Java: PostType (BLOG, FORUM, WORKSHOP)
  status      VARCHAR(32) NOT NULL,    -- Java: PostStatus (DRAFT, PUBLISHED, DELETED)
  pinned      BOOLEAN NOT NULL DEFAULT FALSE,
  game_id     UUID REFERENCES games(id) ON DELETE CASCADE,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_community_posts_author_id ON community_posts(author_id);
CREATE INDEX idx_community_posts_game_id   ON community_posts(game_id);
CREATE INDEX idx_community_posts_type      ON community_posts(type);
CREATE INDEX idx_community_posts_status    ON community_posts(status);
CREATE INDEX idx_community_posts_created   ON community_posts(created_at);

-- Optional: keep updated_at in sync via your trigger helper
CREATE TRIGGER trg_community_posts_set_updated_at
BEFORE UPDATE ON community_posts
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();


-- ==================== COMMUNITY POST TAGS ====================

CREATE TABLE community_post_tags (
  post_id UUID NOT NULL REFERENCES community_posts(id) ON DELETE CASCADE,
  tag     TEXT NOT NULL,
  PRIMARY KEY (post_id, tag)
);

CREATE INDEX idx_community_post_tags_tag ON community_post_tags(tag);


-- ==================== COMMUNITY COMMENTS ====================

CREATE TABLE community_comments (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  post_id     UUID NOT NULL REFERENCES community_posts(id) ON DELETE CASCADE,
  author_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  body        TEXT NOT NULL,
  status      VARCHAR(32) NOT NULL,  -- Java: CommentStatus (ACTIVE, DELETED)
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_community_comments_post_id   ON community_comments(post_id);
CREATE INDEX idx_community_comments_author_id ON community_comments(author_id);
CREATE INDEX idx_community_comments_status    ON community_comments(status);
CREATE INDEX idx_community_comments_created   ON community_comments(created_at);

CREATE TRIGGER trg_community_comments_set_updated_at
BEFORE UPDATE ON community_comments
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();


