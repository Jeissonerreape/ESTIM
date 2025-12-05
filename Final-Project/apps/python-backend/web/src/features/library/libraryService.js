import { apiGet, apiPost, apiPatch } from "../../api/apiClient";
import { ENDPOINTS } from "../../api/endpoints";

export function fetchMyLibrary() {
  return apiGet(ENDPOINTS.MY_LIBRARY);
}

export function addGameToMyLibrary({ gameId, source }) {
  return apiPost(ENDPOINTS.MY_LIBRARY, { gameId, source });
}

export function updateMyLibraryEntry(gameId, payload) {
  return apiPatch(ENDPOINTS.myLibraryEntry(gameId), payload);
}