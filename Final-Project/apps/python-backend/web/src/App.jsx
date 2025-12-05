// src/app/App.jsx
import React, { useEffect, useState } from "react";

export default function App() {
  const [javaStatus, setJavaStatus] = useState("checking…");
  const [pyStatus, setPyStatus] = useState("checking…");

  useEffect(() => {
    const javaBase =
      import.meta.env.VITE_JAVA_API_BASE || "http://localhost:8080";
    const pyBase =
      import.meta.env.VITE_PY_API_BASE || "http://localhost:8000";

    fetch(`${javaBase}/health`)
      .then((r) => r.text())
      .then((t) => setJavaStatus(t))
      .catch(() => setJavaStatus("down"));

    fetch(`${pyBase}/health`)
      .then((r) => r.json())
      .then((d) => setPyStatus(d.status || "ok"))
      .catch(() => setPyStatus("down"));
  }, []);

  return (
    <main style={{ padding: 24, fontFamily: "system-ui", color: "#fff", background: "#050505", minHeight: "100vh" }}>
      {/* 🔐 Auth status + logout */}
      <AuthStatusBar />

      {/* Existing content */}
      <h1>ESTIM</h1>
      <ul>
        <li>Java API: {javaStatus}</li>
        <li>Python API: {pyStatus}</li>
      </ul>
    </main>
  );
}
