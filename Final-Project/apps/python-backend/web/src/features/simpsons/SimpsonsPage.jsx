//simpsons
import { useState, useEffect } from "react";
import { getSimpsonCharacter } from "./simpsonsService"; //next

export default function SimpsonsPage() {
  const [name, setName] = useState("");

  useEffect(() => {
    getSimpsonCharacter().then(setName);
  }, []);

  return (
    <div>
      {name ? <p>Personaje: {name}</p> : <p>Cargando...</p>}
    </div>
  );
}
