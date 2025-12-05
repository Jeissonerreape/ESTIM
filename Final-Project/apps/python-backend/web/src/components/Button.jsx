export default function Button({ 
  children, 
  onClick, 
  variant = "primary", 
  size = "medium",
  icon,
  iconPosition = "left",
  fullWidth = false,
  disabled = false,
  loading = false,
  type = "button",
  style = {}
}) {
  return (
    <button 
      type={type}
      style={{
        ...styles.base,
        ...styles.variants[variant],
        ...styles.sizes[size],
        ...(fullWidth && styles.fullWidth),
        ...(disabled && styles.disabled),
        ...(loading && styles.loading),
        ...style
      }}
      onClick={onClick}
      disabled={disabled || loading}
    >
      {loading ? (
        <span style={styles.spinner}>⏳</span>
      ) : (
        <>
          {icon && iconPosition === "left" && (
            <span style={styles.icon}>{icon}</span>
          )}
          {children}
          {icon && iconPosition === "right" && (
            <span style={styles.icon}>{icon}</span>
          )}
        </>
      )}
    </button>
  );
}

const styles = {
  base: {
    border: 'none',
    borderRadius: '6px',
    fontWeight: '700',
    cursor: 'pointer',
    transition: 'all 0.3s',
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    fontFamily: 'inherit',
    outline: 'none',
    userSelect: 'none'
  },
  
  variants: {
    primary: {
      background: '#ff6b35',
      color: '#000',
      boxShadow: '0 4px 20px rgba(255, 107, 53, 0.4)',
      border: 'none'
    },
    secondary: {
      background: 'transparent',
      border: '1px solid #ff6b35',
      color: '#ff6b35',
      boxShadow: 'none'
    },
    ghost: {
      background: 'rgba(255, 255, 255, 0.05)',
      border: '1px solid #333',
      color: '#fff',
      boxShadow: 'none'
    },
    icon: {
      background: 'rgba(0, 0, 0, 0.5)',
      border: 'none',
      color: '#fff',
      borderRadius: '50%',
      padding: '0'
    }
  },
  
  sizes: {
    small: {
      padding: '6px 16px',
      fontSize: '12px',
      minHeight: '32px'
    },
    medium: {
      padding: '12px 24px',
      fontSize: '14px',
      minHeight: '40px'
    },
    large: {
      padding: '14px 40px',
      fontSize: '16px',
      minHeight: '50px'
    },
    icon: {
      width: '50px',
      height: '50px',
      minWidth: '50px',
      minHeight: '50px',
      padding: '0'
    }
  },
  
  fullWidth: {
    width: '100%'
  },
  disabled: {
    opacity: 0.5,
    cursor: 'not-allowed',
    pointerEvents: 'none'
  },
  loading: {
    opacity: 0.7,
    cursor: 'wait'
  },
  
  icon: {
    fontSize: '16px',
    display: 'flex',
    alignItems: 'center'
  },
  spinner: {
    fontSize: '16px',
    animation: 'spin 1s linear infinite'
  }
};
