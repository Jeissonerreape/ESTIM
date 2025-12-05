export default function LoadingSpinner({ message = "Cargando..." }) {
  return (
    <div style={styles.loadingContainer}>
      <div style={styles.spinner}>⏳</div>
      <p style={styles.loadingText}>{message}</p>
    </div>
  );
}

const styles = {
  loadingContainer: {
    minHeight: '100vh',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    background: '#000'
  },
  spinner: {
    fontSize: '48px',
    animation: 'spin 2s linear infinite'
  },
  loadingText: {
    color: '#888',
    fontSize: '16px',
    marginTop: '20px'
  }
};
