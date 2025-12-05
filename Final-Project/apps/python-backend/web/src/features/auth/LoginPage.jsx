import { useState } from "react";
import { loginUser } from "./authService";
import { useNavigate } from "react-router-dom";
import Button from "../../components/Button";
import Input from "../../components/Input";

export default function LoginPage() {
  const [form, setForm] = useState({ email: "", password: "" });
  const navigate = useNavigate();

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      const result = await loginUser(form);
      console.log("Logged in:", result);
      alert("¡Inicio de sesión exitoso!");
      navigate("/library");
    } catch (error) {
      alert("Error al iniciar sesión");
    }
  }

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <h1 style={styles.title}>Iniciar sesión</h1>
          <p style={styles.subtitle}>Bienvenido de vuelta</p>
        </div>

        <form onSubmit={handleSubmit} style={styles.form}>
          <Input
            name="email"
            type="email"
            placeholder="Correo electrónico"
            value={form.email}
            onChange={handleChange}
          />

          <Input
            name="password"
            type="password"
            placeholder="Contraseña"
            value={form.password}
            onChange={handleChange}
          />

          <Button type="submit">Iniciar sesión</Button>
        </form>

        <div style={styles.footer}>
          <span style={styles.footerText}>¿No tienes cuenta? </span>
          <a href="/register" style={styles.link}>Regístrate</a>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '20px'
  },
  card: {
    background: 'rgba(20, 20, 20, 0.8)',
    backdropFilter: 'blur(10px)',
    border: '1px solid #2a2a2a',
    borderRadius: '12px',
    padding: '40px',
    width: '100%',
    maxWidth: '400px',
    boxShadow: '0 10px 40px rgba(0, 0, 0, 0.5)'
  },
  header: {
    textAlign: 'center',
    marginBottom: '30px'
  },
  title: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#fff',
    marginBottom: '8px'
  },
  subtitle: {
    fontSize: '14px',
    color: '#888'
  },
  form: {
    marginBottom: '20px'
  },
  footer: {
    textAlign: 'center',
    paddingTop: '20px',
    borderTop: '1px solid #2a2a2a'
  },
  footerText: {
    color: '#888',
    fontSize: '14px'
  },
  link: {
    color: '#ff6b35',
    textDecoration: 'none',
    fontWeight: '600',
    fontSize: '14px'
  }
};