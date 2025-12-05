import { useNavigate } from "react-router-dom";
import Button from "../../components/Button";

export default function PurchaseSuccessPage() {
  const navigate = useNavigate();

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.icon}>🎉</div>

        <h1 style={styles.title}>¡Compra completada!</h1>
        <p style={styles.text}>
          Gracias por tu compra. Tus juegos ya están disponibles en tu biblioteca.
        </p>

        <div style={styles.buttons}>
          <Button onClick={() => navigate("/library")}>
            📚 Ir a mi biblioteca
          </Button>

          <button 
            style={styles.backButton}
            onClick={() => navigate("/store")}
          >
            🛒 Seguir comprando
          </button>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Arial, sans-serif',
    background: 'linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 100%)',
    minHeight: '100vh',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    padding: '40px',
    color: '#e0e0e0'
  },
  card: {
    background: 'rgba(30, 30, 30, 0.8)',
    border: '1px solid #2a2a2a',
    borderRadius: '12px',
    padding: '40px',
    width: '100%',
    maxWidth: '480px',
    textAlign: 'center',
    boxShadow: '0 0 30px rgba(0,0,0,0.3)'
  },
  icon: {
    fontSize: '60px',
    marginBottom: '20px'
  },
  title: {
    fontSize: '32px',
    fontWeight: '700',
    color: '#fff',
    marginBottom: '10px'
  },
  text: {
    fontSize: '16px',
    color: '#aaa',
    marginBottom: '30px',
    lineHeight: 1.5
  },
  buttons: {
    display: 'flex',
    flexDirection: 'column',
    gap: '15px'
  },
  backButton: {
    background: 'rgba(255, 255, 255, 0.05)',
    border: '1px solid #333',
    color: '#ccc',
    fontSize: '14px',
    padding: '12px',
    borderRadius: '6px',
    cursor: 'pointer',
    transition: 'all 0.3s'
  }
};
