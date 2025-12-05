-- Attach updated_at trigger to tables that have it
DO $$
DECLARE
  r RECORD;
BEGIN
  FOR r IN
    SELECT tablename
    FROM pg_tables
    WHERE schemaname = 'public'
      AND tablename IN (
        'users','user_profiles','publishers','games','shopping_carts','orders',
        'reviews','user_achievements','user_preferences','support_tickets'
      )
  LOOP
    EXECUTE format($f$
      DROP TRIGGER IF EXISTS set_%1$s_updated_at ON %1$s;
      CREATE TRIGGER set_%1$s_updated_at
      BEFORE UPDATE ON %1$s
      FOR EACH ROW
      EXECUTE FUNCTION set_updated_at();
    $f$, r.tablename);
  END LOOP;
END $$;
