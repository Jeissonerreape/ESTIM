
# STEAM / GAMESTORE — Frontend (React + Vite)

## Summary
React + Vite frontend for the (Steam/GAMESTORE) platform. This README covers installation, structure, workflow, testing, and common commands.

---

## Requirements
- Node.js (v16+ recommended)
- Code editor (VSCode, etc.)
- Modern browser

---

## Quick Installation

```bash
# Install dependencies
npm install
npm install react-router-dom

# Development server
npm run dev

# Production build

<!--
README mejorado para la carpeta `web` (frontend) — en español.
-->

<!--
Comprehensive English README for the `web` frontend (React + Vite).
-->

# Frontend — `web` (React + Vite)

Overview
--------
This folder contains the web client for the ESTIM project — a small game store UI implemented with React (v19) and Vite. The frontend is organized into feature folders and reusable components and communicates with the backend via the centralized API endpoints in `src/api/endpoints.js`.

If you are exploring the repository, start here to learn how to run, build, test, and deploy the frontend.

Prerequisites
-------------
- Node.js 16+ (Node 18 or 20 recommended).
- npm (v8+) or yarn.
- Docker (optional, for containerized runs).
- Modern browser for local testing.

Quick start
-----------
From the `Final-Project/web` directory:

```powershell
# Install dependencies
npm install

# Start development server (HMR)
npm run dev

# Create production build
npm run build

# Preview production build locally
npm run preview
```

Available scripts (in `package.json`)
-----------------------------------
- `npm run dev` — Run Vite dev server (hot module reloading).
- `npm run build` — Build the app for production.
- `npm run preview` — Preview the production build locally.
- `npm run test` — Run tests with Jest.
- `npm run lint` — Run ESLint (note: current lint script targets tests and uses `|| true` to avoid failing the script).

Docker
------
A `Dockerfile` is included for quick containerized development. The container runs the Vite dev server and exposes port 5173.

Build and run example:

```powershell
# Build image
docker build -t estim-web:local .

# Run container and map port 5173
docker run --rm -p 5173:5173 estim-web:local
```

Note: the Dockerfile runs `npm run dev`. For production containers you may prefer to build and serve static files from an nginx image.

Project structure (high level)
------------------------------

```
web/
├── public/                 # static assets
├── src/
│   ├── app/                # app bootstrap and routes (main.jsx, App.jsx, routes.jsx)
│   ├── api/                # api client and endpoints (apiClient.js, endpoints.js)
│   ├── components/         # shared UI components (Header, Button, GameCard...)
│   ├── features/           # feature folders (games, cart, auth, library, etc.)
│   ├── styles/             # global CSS and style utilities
│   └── setupTests.js       # testing setup
├── jest.config.js
├── babel.config.cjs
├── vite.config.js
├── Dockerfile
└── package.json
```

How the app works (data flow)
-----------------------------
1. `src/app/main.jsx` mounts the React app to the DOM and initializes providers (if any).
2. `src/app/App.jsx` configures global layout and shared providers (theme, auth, etc.).
3. `src/app/routes.jsx` defines client routes and lazy-loaded pages.
4. Pages live under `src/features/<feature>/` and are responsible for the page UI and coordinating actions.
5. Feature pages call feature services (e.g., `gamesService.js`) which prepare requests and call the API client.
6. `src/api/apiClient.js` is the thin HTTP layer that performs fetch/axios calls, attaches tokens, and handles common errors.
7. `src/api/endpoints.js` centralizes backend routes and makes it easy to point the frontend to different backends or environments.

Suggested environment and API configuration
-------------------------------------------
- The codebase currently centralizes endpoints in `src/api/endpoints.js`. To support multiple environments, expose the backend base URL via a Vite environment variable, for example `VITE_API_BASE_URL`.

Example `.env` (create in `Final-Project/web`):

```text
VITE_API_BASE_URL=http://localhost:8080/api
```

Then, in `src/api/endpoints.js` or `apiClient.js`, use `import.meta.env.VITE_API_BASE_URL` to build full URLs.

Testing
-------
This project uses Jest and @testing-library/react for unit tests of components.

Run tests:

```powershell
npm test

# watch mode
npm test -- --watch

# coverage
npm test -- --coverage
```

Tests are located in `src/components/__test__/` and test utilities are configured in `src/setupTests.js`.

Linting and code quality
------------------------
- ESLint is included as a dev dependency. Run `npm run lint` to run the configured linters.
- You can add Husky / pre-commit hooks to run lint and tests before commits (recommended for teams).

Deployment notes
----------------
- For static hosting: run `npm run build` and upload the contents of `dist/` to a static host (Netlify, Vercel, GitHub Pages, S3 + CloudFront, etc.).
- If deploying to a subpath (GitHub Pages or a subdirectory), keep or adjust `base` in `vite.config.js` (currently `base: "/ESTIM/"`).
- For server-based deployment, you can build the static files and serve them with nginx or any static file server.

Troubleshooting
---------------
- If dependency installation fails: remove `node_modules` and `package-lock.json`, then run `npm install` again.

```powershell
Remove-Item -Recurse -Force node_modules,package-lock.json
npm install
```

- If Jest complains about jsdom environment, ensure `jest-environment-jsdom` is installed as a dev dependency.
- If CORS errors occur when the frontend talks to the backend, add CORS headers on the backend during development or enable a proxy in Vite config.

Recommendations and next steps
------------------------------
- Document environment variables and required backend endpoints in `src/api/endpoints.js`.
- Add a `README_COMPONENTS.md` or Storybook to document and visually test shared components.
- Add CI (GitHub Actions) to run lint and tests on PRs.
- Consider a production-ready Dockerfile that builds the app and serves `dist/` via nginx for production images.

Contributing
------------
- Fork the repository, create a feature branch, implement changes and tests, then open a Pull Request to `main`.
- Add tests for new components and follow the existing folder conventions.

License
-------
See the top-level `LICENSE` file in the repository for licensing details.

Contact / Help
--------------
If you want, I can:
- Add environment variable documentation and example `.env` files.
- Create a production Dockerfile and nginx config for static serving.
- Add a small CI workflow that runs tests and lint on PRs.

Tell me which of the above you want next and I will implement it.


## Development & lint

