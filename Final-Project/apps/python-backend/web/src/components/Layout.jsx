import { Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";

export default function Layout() {
  return (
    <div style={styles.layout}>
      <Header />
      <main style={styles.main}>
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}

const styles = {
  layout: {
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Arial, sans-serif',
    background: 'linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 100%)',
    minHeight: '100vh',
    color: '#e0e0e0',
    display: 'flex',
    flexDirection: 'column'
  },
  main: {
    flex: 1
  }
};
