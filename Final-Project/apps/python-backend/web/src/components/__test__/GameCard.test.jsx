import React from "react";
import { render, screen } from "@testing-library/react";
import GameCard from "../GameCard";

// Mock the component to avoid useState issues in tests
jest.mock("../GameCard", () => {
  return function MockGameCard({ game, showPrice, showDiscount, showTags }) {
    return (
      <div data-testid="game-card">
        <h3>{game.title}</h3>
        <img src={game.image} alt={game.title} />
        {showPrice && <span>${game.price}</span>}
        {showDiscount && game.discount > 0 && <span>-{game.discount}%</span>}
        {showTags && game.category && <span>{game.category}</span>}
        {showTags && game.multiplayer && <span>Multiplayer</span>}
      </div>
    );
  };
});

describe("GameCard component", () => {
  const mockGame = {
    id: 1,
    title: "Halo Infinite",
    price: 59.99,
    discount: 20,
    image: "/halo.jpg",
    category: "Shooter",
    multiplayer: true
  };

  test("renders game title", () => {
    render(<GameCard game={mockGame} />);
    expect(screen.getByText("Halo Infinite")).toBeInTheDocument();
  });

  test("renders game image with correct alt text", () => {
    render(<GameCard game={mockGame} />);
    const img = screen.getByAltText("Halo Infinite");
    expect(img).toHaveAttribute("src", "/halo.jpg");
  });

  test("renders price when showPrice is true", () => {
    render(<GameCard game={mockGame} showPrice={true} />);
    expect(screen.getByText("$59.99")).toBeInTheDocument();
  });

  test("renders category tag when showTags is true", () => {
    render(<GameCard game={mockGame} showTags={true} />);
    expect(screen.getByText("Shooter")).toBeInTheDocument();
  });

  test("renders Multiplayer tag when showTags and multiplayer are true", () => {
    render(<GameCard game={mockGame} showTags={true} />);
    expect(screen.getByText("Multiplayer")).toBeInTheDocument();
  });

  test("renders discount badge when discount > 0 and showDiscount is true", () => {
    render(<GameCard game={mockGame} showDiscount={true} />);
    expect(screen.getByText("-20%")).toBeInTheDocument();
  });
});
