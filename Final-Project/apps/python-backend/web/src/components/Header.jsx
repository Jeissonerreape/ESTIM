import React from "react";
import { useState, useEffect, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import {
  getCurrentUser,
  isLoggedIn,
  logoutUser,
} from "../features/auth/authService";

export default function Header() {
  const [searchQuery, setSearchQuery] = useState("");
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [user, setUser] = useState(() =>
    isLoggedIn() ? getCurrentUser() : null
  );

  const navigate = useNavigate();
  const location = useLocation();
  const dropdownRef = useRef(null);

  // Close dropdown on outside click
  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
    }

    if (isDropdownOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isDropdownOpen]);

  // Listen to login / logout events
  useEffect(() => {
    function syncAuthState() {
      setUser(isLoggedIn() ? getCurrentUser() : null);
    }

    window.addEventListener("auth:login", syncAuthState);
    window.addEventListener("auth:logout", syncAuthState);

    return () => {
      window.removeEventListener("auth:login", syncAuthState);
      window.removeEventListener("auth:logout", syncAuthState);
    };
  }, []);

  function toggleDropdown() {
    setIsDropdownOpen(!isDropdownOpen);
  }

  function handleNavigate(path) {
    navigate(path);
    setIsDropdownOpen(false);
  }

  function isActive(path) {
    if (path === "/") {
      return location.pathname === "/" || location.pathname === "/store";
    }
    return location.pathname === path;
  }

  function handleLogout() {
    logoutUser();
    setIsDropdownOpen(false);
    window.location.reload();
  }

  // Just the username, no "Logged in as"
  const username =
    user?.profile?.displayName ||
    user?.name ||
    user?.email ||
    "Unknown user";

  return (
    <header style={styles.header}>
      <div style={styles.navLeft}>
        <div style={styles.logo} onClick={() => navigate("/")}>
          GAMESTORE
        </div>

        {/* Store */}
        <div
          style={{
            ...styles.navItem,
            ...(isActive("/") && styles.navItemActive),
          }}
          onClick={() => navigate("/")}
        >
          Store
        </div>

        {/* Library */}
        <div
          style={{
            ...styles.navItem,
            ...(isActive("/library") && styles.navItemActive),
          }}
          onClick={() => navigate("/library")}
        >
          Library
        </div>

        {/* Wishlist */}
        <div
          style={{
            ...styles.navItem,
            ...(isActive("/wishlist") && styles.navItemActive),
          }}
          onClick={() => navigate("/wishlist")}
        >
          💝 Wishlist
        </div>

        {/* Community */}
        <div style={styles.navItem}>Community</div>

        {/* Deals */}
        <div style={styles.navItem}>Deals</div>
      </div>

      <div style={styles.navRight}>
        <div style={styles.searchContainer}>
          <input
            type="text"
            placeholder="Search games..."
            style={styles.searchBox}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <span style={styles.searchIcon}>🔍</span>
        </div>
        <div style={styles.iconBtn} onClick={() => navigate("/cart")}>
          🛒
        </div>

        {/* User Dropdown */}
        <div style={styles.dropdownContainer} ref={dropdownRef}>
          <div style={styles.iconBtn} onClick={toggleDropdown}>
            👤
          </div>

          {isDropdownOpen && (
            <div style={styles.dropdownMenu}>
              {/* 1) HEADER ROW: Not logged in OR username */}
              <div style={styles.dropdownHeader}>
                {user ? username : "Not logged in"}
              </div>

              {/* 2) All Games */}
              <div
                style={{
                  ...styles.dropdownItem,
                  borderTop: "1px solid #333",
                }}
                onMouseEnter={(e) =>
                  (e.currentTarget.style.background =
                    "rgba(255, 107, 53, 0.1)")
                }
                onMouseLeave={(e) =>
                  (e.currentTarget.style.background = "transparent")
                }
                onClick={() => handleNavigate("/games")}
              >
                🎮 All Games
              </div>

              {/* 3) Divider */}
              <div style={styles.dropdownDivider} />

              {/* 4) Auth actions */}
              {user ? (
                // LOGGED IN → Logout only
                <div
                  style={styles.dropdownItem}
                  onMouseEnter={(e) =>
                    (e.currentTarget.style.background =
                      "rgba(255, 107, 53, 0.1)")
                  }
                  onMouseLeave={(e) =>
                    (e.currentTarget.style.background = "transparent")
                  }
                  onClick={handleLogout}
                >
                  🚪 Logout
                </div>
              ) : (
                // NOT LOGGED IN → Login + Sign Up
                <>
                  <div
                    style={styles.dropdownItem}
                    onMouseEnter={(e) =>
                      (e.currentTarget.style.background =
                        "rgba(255, 107, 53, 0.1)")
                    }
                    onMouseLeave={(e) =>
                      (e.currentTarget.style.background = "transparent")
                    }
                    onClick={() => handleNavigate("/login")}
                  >
                    🔑 Login
                  </div>

                  <div
                    style={styles.dropdownItem}
                    onMouseEnter={(e) =>
                      (e.currentTarget.style.background =
                        "rgba(255, 107, 53, 0.1)")
                    }
                    onMouseLeave={(e) =>
                      (e.currentTarget.style.background = "transparent")
                    }
                    onClick={() => handleNavigate("/register")}
                  >
                    ✍️ Sign Up
                  </div>
                </>
              )}
            </div>
          )}
        </div>
      </div>
    </header>
  );
}

const styles = {
  header: {
    background: "rgba(0, 0, 0, 0.95)",
    padding: "12px 40px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    borderBottom: "1px solid #222",
    position: "sticky",
    top: 0,
    zIndex: 1000,
  },
  navLeft: {
    display: "flex",
    gap: "30px",
    alignItems: "center",
  },
  logo: {
    fontSize: "18px",
    fontWeight: "700",
    color: "#ff6b35",
    letterSpacing: "1px",
    cursor: "pointer",
  },
  navItem: {
    color: "#aaa",
    fontSize: "14px",
    cursor: "pointer",
    transition: "all 0.3s",
    fontWeight: "500",
    position: "relative",
    padding: "8px 0",
  },
  navItemActive: {
    color: "#ff6b35",
    fontWeight: "600",
    borderBottom: "2px solid #ff6b35",
  },
  navRight: {
    display: "flex",
    gap: "20px",
    alignItems: "center",
  },
  searchContainer: {
    position: "relative",
    display: "flex",
    alignItems: "center",
  },
  searchBox: {
    background: "#1a1a1a",
    border: "1px solid #333",
    borderRadius: "20px",
    padding: "8px 40px 8px 16px",
    color: "#fff",
    fontSize: "13px",
    width: "280px",
    outline: "none",
  },
  searchIcon: {
    position: "absolute",
    right: "15px",
    fontSize: "14px",
    color: "#666",
  },
  iconBtn: {
    fontSize: "18px",
    cursor: "pointer",
    transition: "transform 0.2s",
    color: "#fff",
  },
  dropdownContainer: {
    position: "relative",
  },
  dropdownMenu: {
    position: "absolute",
    top: "100%",
    right: 0,
    marginTop: "12px",
    background: "#1a1a1a",
    border: "1px solid #333",
    borderRadius: "8px",
    minWidth: "220px",
    boxShadow: "0 8px 24px rgba(0, 0, 0, 0.6)",
    padding: "8px 0",
    zIndex: 1000,
  },
  dropdownHeader: {
    padding: "8px 20px",
    fontSize: "13px",
    color: "#ccc",
    fontWeight: 600,
  },
  dropdownItem: {
    padding: "12px 20px",
    color: "#ccc",
    fontSize: "14px",
    cursor: "pointer",
    transition: "all 0.2s",
    display: "flex",
    alignItems: "center",
    gap: "10px",
    background: "transparent",
  },
  dropdownDivider: {
    height: "1px",
    background: "#333",
    margin: "8px 0",
  },
};
