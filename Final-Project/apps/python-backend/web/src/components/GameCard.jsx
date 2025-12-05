import React, { useState } from "react";


export default function GameCard({ 
  game, 
  onClick, 
  showPrice = false,
  showDiscount = false,
  showTags = false,
  onAddToCart,
  variant = "simple"
}) {
  const [isHovered, setIsHovered] = useState(false);

  const discountedPrice = game.discount > 0
    ? (game.price * (1 - game.discount / 100)).toFixed(2)
    : null;

  return (
    <div
      style={{
        ...styles.gameCard,
        ...(isHovered ? styles.gameCardHover : styles.gameCardNormal) // ✅ CAMBIO: resetea explícitamente
      }}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      onClick={onClick}
    >
      <div style={styles.imageWrapper}>
        <img 
          src={game.image} 
          alt={game.title} 
          style={styles.gameImage}
        />
        
        {/* Discount Badge */}
        {showDiscount && game.discount > 0 && (
          <div style={styles.discountBadge}>-{game.discount}%</div>
        )}

        {/* Hover Overlay */}
        {variant === "detailed" && isHovered && onAddToCart && (
          <div style={styles.overlay}>
            <button
              style={styles.addToCartBtn}
              onClick={(e) => {
                e.stopPropagation();
                onAddToCart(game.id);
              }}
            >
              🛒 Añadir
            </button>
          </div>
        )}
      </div>

      <div style={styles.gameInfo}>
        <h3 style={styles.gameTitle}>{game.title}</h3>
        
        {/* Tags */}
        {showTags && game.category && (
          <div style={styles.tags}>
            <span style={styles.tag}>{game.category}</span>
            {game.multiplayer && <span style={styles.tag}>Multiplayer</span>}
          </div>
        )}

        {/* Price */}
        {showPrice && game.price !== undefined && (
          <div style={styles.priceContainer}>
            {game.discount > 0 ? (
              <>
                <span style={styles.originalPrice}>${game.price}</span>
                <span style={styles.finalPrice}>${discountedPrice}</span>
              </>
            ) : (
              <span style={styles.price}>${game.price.toFixed(2)}</span>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

const styles = {
  gameCard: {
    background: 'rgba(30, 30, 30, 0.6)',
    borderRadius: '10px',
    overflow: 'hidden',
    cursor: 'pointer',
    transition: 'all 0.3s',
    border: '1px solid transparent'
  },
  gameCardNormal: {
    // ✅ NUEVO: Estado normal explícito
    transform: 'translateY(0)',
    borderColor: 'transparent',
    boxShadow: 'none'
  },
  gameCardHover: {
    transform: 'translateY(-5px)',
    borderColor: '#ff6b35',
    boxShadow: '0 10px 30px rgba(255, 107, 53, 0.3)'
  },
  imageWrapper: {
    position: 'relative',
    width: '100%',
    height: '140px',
    overflow: 'hidden'
  },
  gameImage: {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
    background: 'linear-gradient(135deg, #2a2a2a 0%, #1a1a1a 100%)',
    transition: 'transform 0.3s'
  },
  discountBadge: {
    position: 'absolute',
    top: '10px',
    left: '10px',
    background: '#ff6b35',
    color: '#000',
    fontSize: '12px',
    fontWeight: '700',
    padding: '4px 10px',
    borderRadius: '4px'
  },
  overlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: 'rgba(0, 0, 0, 0.85)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  },
  addToCartBtn: {
    background: '#ff6b35',
    border: 'none',
    borderRadius: '6px',
    padding: '10px 20px',
    color: '#000',
    fontSize: '13px',
    fontWeight: '700',
    cursor: 'pointer',
    fontFamily: 'inherit'
  },
  gameInfo: {
    padding: '15px'
  },
  gameTitle: {
    fontSize: '14px',
    fontWeight: '500',
    color: '#fff',
    margin: '0 0 8px 0'
  },
  tags: {
    display: 'flex',
    gap: '6px',
    marginBottom: '10px',
    flexWrap: 'wrap'
  },
  tag: {
    background: 'rgba(255, 255, 255, 0.05)',
    padding: '3px 8px',
    borderRadius: '4px',
    fontSize: '11px',
    color: '#888',
    border: '1px solid rgba(255, 255, 255, 0.1)'
  },
  priceContainer: {
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    paddingTop: '10px',
    borderTop: '1px solid rgba(255, 255, 255, 0.1)'
  },
  originalPrice: {
    fontSize: '12px',
    color: '#666',
    textDecoration: 'line-through'
  },
  finalPrice: {
    fontSize: '16px',
    fontWeight: '700',
    color: '#ff6b35'
  },
  price: {
    fontSize: '16px',
    fontWeight: '700',
    color: '#fff'
  }
};
