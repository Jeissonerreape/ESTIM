//simpsons
import { apiSimpsons } from "../../api/apiClient"; //next
import { ENDPOINTS } from "../../api/endpoints";

export const getSimpsonCharacter = async () => {
    const data = await apiSimpsons(ENDPOINTS.SIMPSON_CHARACTER);
    return data.name; // solo devolvemos el nombre 
};
