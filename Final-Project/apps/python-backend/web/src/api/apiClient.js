const BASE_URL =
  import.meta.env.VITE_JAVA_API_BASE || "http://localhost:8080";
const BASE_URL_PY =
  import.meta.env.VITE_PY_API_BASE || "http://localhost:8000";

function authHeader() {
  const token = localStorage.getItem("accessToken");
  if (!token) return {};
  return { Authorization: `Bearer ${token}` };
}

async function handleResponse(res) {
  const text = await res.text();
  let data = null;

  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }

  if (!res.ok) {
    const error = new Error(`HTTP ${res.status}`);
    error.status = res.status;
    error.data = data;
    throw error;
  }

  return data;
}

// ---------- Java backend helpers ----------

export async function apiGet(endpoint) {
  const res = await fetch(BASE_URL + endpoint, {
    method: "GET",
    headers: {
      Accept: "application/json",
      ...authHeader(),
    },
  });
  return handleResponse(res);
}

export async function apiPost(endpoint, data) {
  const res = await fetch(BASE_URL + endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...authHeader(),
    },
    body: JSON.stringify(data),
  });
  return handleResponse(res);
}

export async function apiPut(endpoint, data) {
  const res = await fetch(BASE_URL + endpoint, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...authHeader(),
    },
    body: JSON.stringify(data),
  });
  return handleResponse(res);
}

export async function apiPatch(endpoint, data) {
  const res = await fetch(BASE_URL + endpoint, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...authHeader(),
    },
    body: JSON.stringify(data),
  });
  return handleResponse(res);
}

export async function apiDelete(endpoint, data = null) {
  const options = {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...authHeader(),
    },
  };
  if (data) {
    options.body = JSON.stringify(data);
  }

  const res = await fetch(BASE_URL + endpoint, options);
  return handleResponse(res);
}

// ---------- Python backend helpers ----------

export async function pyGet(endpoint) {
  const res = await fetch(BASE_URL_PY + endpoint, {
    method: "GET",
    headers: {
      Accept: "application/json",
      ...authHeader(),
    },
  });
  return handleResponse(res);
}

export async function pyPost(endpoint, data) {
  const res = await fetch(BASE_URL_PY + endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...authHeader(),
    },
    body: JSON.stringify(data),
  });
  return handleResponse(res);
}

export async function pyDelete(endpoint, data = null) {
  const options = {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...authHeader(),
    },
  };
  if (data) {
    options.body = JSON.stringify(data);
  }

  const res = await fetch(BASE_URL_PY + endpoint, options);
  return handleResponse(res);
}

// Simpsons helper: takes a full URL, not BASE_URL + endpoint
export async function apiSimpsons(url) {
  const res = await fetch(url, {
    method: "GET",
    headers: { Accept: "application/json" },
  });
  return handleResponse(res);
}
