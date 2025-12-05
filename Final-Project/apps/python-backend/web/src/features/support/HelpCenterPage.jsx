import React from 'react';

const styles = {
  container: {
    background: '#000',
    minHeight: '100vh',
    color: '#fff',
    padding: '40px',
    maxWidth: '800px',
    margin: '0 auto',
  },
  title: {
    fontSize: '36px',
    fontWeight: '700',
    marginBottom: '20px',
  },
  paragraph: {
    fontSize: '16px',
    color: '#888',
    marginBottom: '20px',
  },
  list: {
    paddingLeft: '20px',
    color: '#ccc',
  },
  listItem: {
    marginBottom: '12px',
    fontSize: '14px',
  },
  strongText: {
    color: '#fff',
  },
};

export default function HelpCenterPage() {
  return (
    <div style={styles.container}>
      <h1 style={styles.title}>Help Center</h1>
      <p style={styles.paragraph}>
        Welcome to the Help Center. Here are some frequently asked questions:
      </p>
      <ul style={styles.list}>
        <li style={styles.listItem}>
          <strong style={styles.strongText}>How to buy a game?</strong> Select the game and add it to your cart, then proceed to checkout.
        </li>
        <li style={styles.listItem}>
          <strong style={styles.strongText}>What payment methods do you accept?</strong> We accept credit cards, PayPal, and other popular payment options.
        </li>
        <li style={styles.listItem}>
          <strong style={styles.strongText}>How to contact support?</strong> Use the Contact page for any questions or issues.
        </li>
      </ul>
    </div>
  );
}
