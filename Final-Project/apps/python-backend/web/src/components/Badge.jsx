export default function Badge({ 
  children, 
  variant = "default", 
  icon,
  size = "medium"
}) {
  return (
    <span style={{ 
      ...styles.base, 
      ...styles.variants[variant],
      ...styles.sizes[size]
    }}>
      {icon && <span style={styles.icon}>{icon}</span>}
      {children}
    </span>
  );
}

const styles = {
  base: {
    borderRadius: '4px',
    fontWeight: '600',
    display: 'inline-flex',
    alignItems: 'center',
    gap: '4px',
    transition: 'all 0.3s'
  },
  variants: {
    // Badge gris por defecto
    default: {
      background: 'rgba(255, 255, 255, 0.05)',
      color: '#888',
      border: '1px solid rgba(255, 255, 255, 0.1)'
    },
    // Badge de categoría (naranja transparente)
    category: {
      background: 'rgba(255, 107, 53, 0.2)',
      border: '1px solid rgba(255, 107, 53, 0.5)',
      color: '#ff6b35'
    },
    // Badge de descuento (naranja sólido)
    discount: {
      background: '#ff6b35',
      color: '#000',
      border: 'none'
    },
    // Badge de título (para sidebars)
    title: {
      color: '#888',
      textTransform: 'uppercase',
      letterSpacing: '1px',
      border: 'none',
      background: 'transparent'
    },
    // Badge de éxito (verde)
    success: {
      background: '#4ade80',
      color: '#000',
      border: 'none'
    },
    // Badge de advertencia (amarillo)
    warning: {
      background: '#fbbf24',
      color: '#000',
      border: 'none'
    },
    // Badge de error (rojo)
    danger: {
      background: '#ef4444',
      color: '#fff',
      border: 'none'
    }
  },
  sizes: {
    small: {
      padding: '3px 8px',
      fontSize: '10px'
    },
    medium: {
      padding: '4px 12px',
      fontSize: '11px'
    },
    large: {
      padding: '8px 16px',
      fontSize: '20px'
    }
  },
  icon: {
    fontSize: '1em',
    display: 'flex',
    alignItems: 'center'
  }
};
