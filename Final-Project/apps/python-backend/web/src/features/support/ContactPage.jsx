import React from 'react';

const styles = {
  container: {
    background: '#000',
    minHeight: '100vh',
    color: '#fff',
    padding: '40px',
    maxWidth: '600px',
    margin: '0 auto',
  },
  title: {
    fontSize: '36px',
    fontWeight: '700',
    marginBottom: '30px',
  },
  infoBlock: {
    marginBottom: '20px',
  },
  label: {
    fontWeight: '700',
    fontSize: '16px',
    color: '#ff6b35',
    marginBottom: '6px',
  },
  text: {
    fontSize: '14px',
    color: '#ccc',
    margin: '4px 0',
  },
  link: {
    color: '#ff6b35',
    textDecoration: 'none',
  }
};

export default function ContactPage() {
  return (
    <div style={styles.container}>
      <h1 style={styles.title}>Contacto - Universidad Nacional de Colombia</h1>

      <div style={styles.infoBlock}>
        <div style={styles.label}>Dirección:</div>
        <div style={styles.text}>Carrera 30 #45-03, Bogotá, Colombia</div>
      </div>

      <div style={styles.infoBlock}>
        <div style={styles.label}>Teléfonos:</div>
        <div style={styles.text}>+57 1 3165000</div>
        <div style={styles.text}>+57 1 3165252</div>
      </div>

      <div style={styles.infoBlock}>
        <div style={styles.label}>Correos Electrónicos:</div>
        <div style={styles.text}>
          <a href="mailto:info@unal.edu.co" style={styles.link}>info@unal.edu.co</a>
        </div>
        <div style={styles.text}>
          <a href="mailto:contacto@unal.edu.co" style={styles.link}>contacto@unal.edu.co</a>
        </div>
      </div>

      <div style={styles.infoBlock}>
        <div style={styles.label}>Sitio Web:</div>
        <div style={styles.text}>
          <a href="https://unal.edu.co" target="_blank" rel="noopener noreferrer" style={styles.link}>
            https://unal.edu.co
          </a>
        </div>
      </div>
    </div>
  );
}
