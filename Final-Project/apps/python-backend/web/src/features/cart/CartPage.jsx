import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Button from "../../components/Button";
import { getCart, removeFromCart, clearCart } from "./cartService";

export default function CartPage() {
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadCart();
  }, []);

  async function loadCart() {
    try {
      const data = await getCart();
      setCartItems(data);
    } catch (error) {
      console.error("Error loading cart:", error);
    } finally {
      setLoading(false);
    }
  }

  async function handleRemove(gameId) {
    try {
      await removeFromCart(gameId);
      setCartItems(cartItems.filter(item => item.id !== gameId));
    } catch (error) {
      alert("Error al eliminar del carrito");
    }
  }

  async function handleCheckout() {
    try {
      await clearCart();
      navigate("/purchase-success");
    } catch (error) {
      alert("Error al procesar la compra");
    }
  }

  // Asegurarnos de que siempre trabajamos con un array
const safeCartItems = Array.isArray(cartItems) ? cartItems : [];

const subtotal = safeCartItems.reduce((sum, item) => {
  const price =
    item.discount > 0
      ? item.price * (1 - item.discount / 100)
      : item.price;
  return sum + price;
}, 0);

const tax = subtotal * 0.19;
const total = subtotal + tax;

  if (loading) {
    return (
      <div style={styles.loadingContainer}>
        <div style={styles.spinner}>⏳</div>
        <p style={styles.loadingText}>Cargando carrito...</p>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <button style={styles.backButton} onClick={() => navigate(-1)}>
          ← Volver
        </button>
        <h1 style={styles.title}>🛒 Mi Carrito</h1>
      </div>

      {safeCartItems.length === 0 ? (
        <EmptyCart navigate={navigate} />
      ) : (
        <div style={styles.content}>
          <div style={styles.itemsSection}>
            <div style={styles.sectionHeader}>
              <h2 style={styles.sectionTitle}>Artículos ({safeCartItems.length})</h2>
              <button 
                style={styles.clearButton}
                onClick={() => {
                  if (window.confirm("¿Vaciar todo el carrito?")) {
                    setCartItems([]);
                  }
                }}
              >
                🗑️ Vaciar carrito
              </button>
            </div>

            <div style={styles.itemsList}>
              {safeCartItems.map((item) => (
                <CartItem 
                  key={item.id} 
                  item={item} 
                  onRemove={handleRemove}
                />
              ))}
            </div>
          </div>

          <div style={styles.summarySection}>
            <div style={styles.summaryCard}>
              <h2 style={styles.summaryTitle}>Resumen de compra</h2>
              
              <div style={styles.summaryDetails}>
                <div style={styles.summaryRow}>
                  <span style={styles.summaryLabel}>Subtotal</span>
                  <span style={styles.summaryValue}>${subtotal.toFixed(2)}</span>
                </div>
                
                <div style={styles.summaryRow}>
                  <span style={styles.summaryLabel}>Impuestos (19%)</span>
                  <span style={styles.summaryValue}>${tax.toFixed(2)}</span>
                </div>

                <div style={styles.divider}></div>

                <div style={styles.summaryRow}>
                  <span style={styles.totalLabel}>Total</span>
                  <span style={styles.totalValue}>${total.toFixed(2)}</span>
                </div>
              </div>

              <Button onClick={handleCheckout}>
                💳 Proceder al pago
              </Button>

              <div style={styles.securityBadge}>
                <span style={styles.securityIcon}>🔒</span>
                <span style={styles.securityText}>Pago 100% seguro</span>
              </div>
            </div>

            <div style={styles.infoCard}>
              <h3 style={styles.infoTitle}>💡 Información</h3>
              <ul style={styles.infoList}>
                <li style={styles.infoItem}>✓ Descarga instantánea</li>
                <li style={styles.infoItem}>✓ Actualizaciones automáticas</li>
                <li style={styles.infoItem}>✓ Soporte 24/7</li>
                <li style={styles.infoItem}>✓ Garantía de reembolso</li>
              </ul>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

// Componente: CartItem
function CartItem({ item, onRemove }) {
  const [isHovered, setIsHovered] = useState(false);

  const discountedPrice = item.discount > 0 
    ? (item.price * (1 - item.discount / 100)).toFixed(2)
    : null;

  const finalPrice = discountedPrice || item.price.toFixed(2);

  return (
    <div 
      style={{
        ...styles.cartItem,
        ...(isHovered && styles.cartItemHover)
      }}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <img src={item.image} alt={item.title} style={styles.itemImage} />
      
      <div style={styles.itemInfo}>
        <h3 style={styles.itemTitle}>{item.title}</h3>
        <p style={styles.itemDescription}>{item.description || "Acción y aventura"}</p>
        
        <div style={styles.itemTags}>
          <span style={styles.tag}>🎮 Acción</span>
          <span style={styles.tag}>🌍 Multijugador</span>
        </div>
      </div>

      <div style={styles.itemPricing}>
        {item.discount > 0 && (
          <div style={styles.discountBadge}>-{item.discount}%</div>
        )}
        <div style={styles.priceWrapper}>
          {item.discount > 0 && (
            <span style={styles.originalPrice}>${item.price}</span>
          )}
          <span style={styles.itemPrice}>${finalPrice}</span>
        </div>
      </div>

      <button 
        style={styles.removeButton}
        onClick={() => onRemove(item.id)}
      >
        ✕
      </button>
    </div>
  );
}

// Componente: EmptyCart
function EmptyCart({ navigate }) {
  return (
    <div style={styles.emptyCart}>
      <div style={styles.emptyIcon}>🛒</div>
      <h2 style={styles.emptyTitle}>Tu carrito está vacío</h2>
      <p style={styles.emptyText}>
        Explora nuestra tienda y encuentra juegos increíbles
      </p>
      
      <div style={styles.buttonGroup}>
        <Button 
          variant="primary"
          size="large"
          icon="🎮"
          onClick={() => navigate("/store")}
        >
          Ir a la tienda
        </Button>
        
        <Button 
          variant="secondary"
          size="large"
          onClick={() => navigate("/cart/success")}
        >
          Compra completada
        </Button>
      </div>
    </div>
  );
}

// Estilos
const styles = {
  container: {
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Arial, sans-serif',
    background: 'linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 100%)',
    minHeight: '100vh',
    color: '#e0e0e0',
    padding: '40px'
  },
  loadingContainer: {
    minHeight: '100vh',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 100%)'
  },
  spinner: {
    fontSize: '48px',
    animation: 'spin 2s linear infinite'
  },
  loadingText: {
    color: '#888',
    fontSize: '16px',
    marginTop: '20px'
  },
  header: {
    marginBottom: '30px',
    display: 'flex',
    alignItems: 'center',
    gap: '20px'
  },
  backButton: {
    background: 'rgba(255, 255, 255, 0.05)',
    border: '1px solid #333',
    borderRadius: '6px',
    padding: '10px 20px',
    color: '#ccc',
    fontSize: '14px',
    cursor: 'pointer',
    transition: 'all 0.3s',
    fontFamily: 'inherit'
  },
  title: {
    fontSize: '32px',
    fontWeight: '700',
    color: '#fff',
    margin: 0
  },
  content: {
    display: 'grid',
    gridTemplateColumns: '1fr 400px',
    gap: '30px',
    maxWidth: '1400px',
    margin: '0 auto'
  },
  itemsSection: {
    minHeight: '400px'
  },
  sectionHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '20px'
  },
  sectionTitle: {
    fontSize: '20px',
    fontWeight: '600',
    color: '#fff',
    margin: 0
  },
  clearButton: {
    background: 'transparent',
    border: 'none',
    color: '#ff6b35',
    fontSize: '14px',
    cursor: 'pointer',
    transition: 'all 0.3s',
    fontFamily: 'inherit'
  },
  itemsList: {
    display: 'flex',
    flexDirection: 'column',
    gap: '15px'
  },
  cartItem: {
    background: 'rgba(30, 30, 30, 0.6)',
    border: '1px solid #2a2a2a',
    borderRadius: '10px',
    padding: '20px',
    display: 'flex',
    gap: '20px',
    alignItems: 'center',
    transition: 'all 0.3s',
    position: 'relative'
  },
  cartItemHover: {
    borderColor: '#ff6b35',
    transform: 'translateX(5px)',
    boxShadow: '0 5px 20px rgba(255, 107, 53, 0.2)'
  },
  itemImage: {
    width: '120px',
    height: '80px',
    objectFit: 'cover',
    borderRadius: '8px'
  },
  itemInfo: {
    flex: 1,
    minWidth: 0
  },
  itemTitle: {
    fontSize: '18px',
    fontWeight: '600',
    color: '#fff',
    margin: '0 0 5px 0'
  },
  itemDescription: {
    fontSize: '13px',
    color: '#888',
    margin: '0 0 10px 0'
  },
  itemTags: {
    display: 'flex',
    gap: '8px',
    flexWrap: 'wrap'
  },
  tag: {
    background: 'rgba(255, 255, 255, 0.05)',
    padding: '4px 10px',
    borderRadius: '4px',
    fontSize: '11px',
    color: '#aaa'
  },
  itemPricing: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-end',
    gap: '8px'
  },
  discountBadge: {
    background: '#4ade80',
    color: '#000',
    fontSize: '12px',
    fontWeight: '700',
    padding: '4px 8px',
    borderRadius: '4px'
  },
  priceWrapper: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-end',
    gap: '4px'
  },
  originalPrice: {
    fontSize: '13px',
    color: '#888',
    textDecoration: 'line-through'
  },
  itemPrice: {
    fontSize: '22px',
    fontWeight: '700',
    color: '#fff'
  },
  removeButton: {
    position: 'absolute',
    top: '15px',
    right: '15px',
    background: 'rgba(255, 0, 0, 0.1)',
    border: '1px solid rgba(255, 0, 0, 0.3)',
    borderRadius: '50%',
    width: '30px',
    height: '30px',
    color: '#ff4444',
    fontSize: '16px',
    cursor: 'pointer',
    transition: 'all 0.3s',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  },
  summarySection: {
    display: 'flex',
    flexDirection: 'column',
    gap: '20px'
  },
  summaryCard: {
    background: 'rgba(30, 30, 30, 0.8)',
    backdropFilter: 'blur(10px)',
    border: '1px solid #2a2a2a',
    borderRadius: '12px',
    padding: '25px',
    position: 'sticky',
    top: '20px'
  },
  summaryTitle: {
    fontSize: '20px',
    fontWeight: '600',
    color: '#fff',
    marginBottom: '20px'
  },
  summaryDetails: {
    marginBottom: '25px'
  },
  summaryRow: {
    display: 'flex',
    justifyContent: 'space-between',
    marginBottom: '12px'
  },
  summaryLabel: {
    fontSize: '14px',
    color: '#aaa'
  },
  summaryValue: {
    fontSize: '14px',
    color: '#fff',
    fontWeight: '500'
  },
  divider: {
    height: '1px',
    background: '#2a2a2a',
    margin: '15px 0'
  },
  totalLabel: {
    fontSize: '18px',
    fontWeight: '700',
    color: '#fff'
  },
  totalValue: {
    fontSize: '24px',
    fontWeight: '700',
    color: '#ff6b35'
  },
  securityBadge: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    marginTop: '15px',
    padding: '10px',
    background: 'rgba(74, 222, 128, 0.1)',
    borderRadius: '6px'
  },
  securityIcon: {
    fontSize: '16px'
  },
  securityText: {
    fontSize: '13px',
    color: '#4ade80'
  },
  infoCard: {
    background: 'rgba(30, 30, 30, 0.6)',
    border: '1px solid #2a2a2a',
    borderRadius: '10px',
    padding: '20px'
  },
  infoTitle: {
    fontSize: '16px',
    fontWeight: '600',
    color: '#fff',
    marginBottom: '15px'
  },
  infoList: {
    listStyle: 'none',
    padding: 0,
    margin: 0
  },
  infoItem: {
    fontSize: '13px',
    color: '#aaa',
    marginBottom: '10px',
    paddingLeft: '5px'
  },
  emptyCart: {
    textAlign: 'center',
    padding: '100px 20px',
    maxWidth: '500px',
    margin: '0 auto'
  },
  emptyIcon: {
    fontSize: '80px',
    marginBottom: '20px',
    opacity: 0.5
  },
  emptyTitle: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#fff',
    marginBottom: '10px'
  },
  emptyText: {
    fontSize: '16px',
    color: '#888',
    marginBottom: '30px'
  },
  shopButton: {
    background: 'linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%)',
    border: 'none',
    borderRadius: '6px',
    padding: '15px 40px',
    color: '#fff',
    fontSize: '16px',
    fontWeight: '600',
    cursor: 'pointer',
    transition: 'all 0.3s',
    boxShadow: '0 4px 15px rgba(255, 107, 53, 0.3)'
  }
};