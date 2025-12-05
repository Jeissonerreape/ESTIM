// src/features/auth/authService.js
import { apiPost } from "../../api/apiClient";
import { ENDPOINTS } from "../../api/endpoints";

const ACCESS_TOKEN_KEY = "accessToken";
const CURRENT_USER_KEY = "currentUser";

export function registerUser(data) {
  return apiPost(ENDPOINTS.REGISTER, data);
}

export async function loginUser(data) {
  const result = await apiPost(ENDPOINTS.LOGIN, data);

  // Save token for authenticated requests like /me/library
  if (result && result.accessToken) {
    localStorage.setItem(ACCESS_TOKEN_KEY, result.accessToken);
  }

  // Try to grab user info from backend response
  const backendUser =
    result?.user ||
    result?.currentUser ||
    result?.profile ||
    null;

  const userToStore =
    backendUser ||
    // fallback: at least store the email that logged in
    { email: data.email };

  localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(userToStore));

  // Notify rest of the app
  window.dispatchEvent(new Event("auth:login"));

  return result;
}

export function logoutUser() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(CURRENT_USER_KEY);

  // Notify rest of the app
  window.dispatchEvent(new Event("auth:logout"));
}

export function getCurrentUser() {
  const raw = localStorage.getItem(CURRENT_USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function isLoggedIn() {
  return !!localStorage.getItem(ACCESS_TOKEN_KEY);
}
