import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer style={styles.footer}>
      <div style={styles.footerContent}>
        <div style={styles.footerSection}>
          <div style={styles.footerLogo}>GAMESTORE</div>
          <p style={styles.footerDescription}>
            Your favorite video game store. The best titles at the best prices.
          </p>
        </div>

        <div style={styles.footerSection}>
          <h4 style={styles.footerTitle}>Support</h4>
          <Link to="/help" style={styles.footerLink}>Help Center</Link>
          <Link to="/contact" style={styles.footerLink}>Contact</Link>
        </div>
        <div style={styles.footerSection}>
          <h4 style={styles.footerTitle}>Follow Us</h4>
          <a href="https://twitter.com" style={styles.footerLink} target="_blank" rel="noopener noreferrer">Twitter</a>
          <a href="https://facebook.com" style={styles.footerLink} target="_blank" rel="noopener noreferrer">Facebook</a>
          <a href="https://instagram.com" style={styles.footerLink} target="_blank" rel="noopener noreferrer">Instagram</a>
          <a href="https://discord.com" style={styles.footerLink} target="_blank" rel="noopener noreferrer">Discord</a>
        </div>

      </div>
    </footer>
  );
}

const styles = {
  footer: {
    background: '#0a0a0a',
    borderTop: '1px solid #1a1a1a',
    padding: '60px 40px 30px'
  },
  footerContent: {
    display: 'grid',
    gridTemplateColumns: '2fr 1fr 1fr 1fr',
    gap: '40px',
    maxWidth: '1400px',
    margin: '0 auto'
  },
  footerSection: {
    display: 'flex',
    flexDirection: 'column',
    gap: '12px'
  },
  footerLogo: {
    fontSize: '20px',
    fontWeight: '700',
    color: '#ff6b35',
    marginBottom: '8px',
    letterSpacing: '1px'
  },
  footerDescription: {
    fontSize: '14px',
    color: '#666',
    lineHeight: '1.6',
    margin: 0
  },
  footerTitle: {
    fontSize: '14px',
    fontWeight: '700',
    color: '#fff',
    marginBottom: '8px'
  },
  footerLink: {
    fontSize: '14px',
    color: '#888',
    cursor: 'pointer',
    transition: 'color 0.3s'
  }
};
