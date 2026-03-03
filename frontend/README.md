# Forex Converter — Frontend

Vue 3 single-page application for the Forex Converter service.

## Tech Stack

- **Vue 3** with Composition API
- **TypeScript** — strict type safety across the codebase
- **Vite** — fast dev server and optimised production builds
- **Axios** — HTTP client for backend API communication
- **neverthrow** — result-based error handling instead of thrown exceptions
- **Vitest** — unit testing

## Environment Setup

Copy the example environment file:

```bash
cp .env.example .env
```

> **Tip:** If you remove the `VITE_API_URL` variable from your `.env` file entirely, the application will default to using the standard Vite proxy preset (which routes `/api/*` requests directly to `http://localhost:8080`).

## Installation

```bash
npm install
```

## Development

| Command           | Description                                    |
|------------------|------------------------------------------------|
| `npm run dev`     | Start Vite dev server on http://localhost:5173 |
| `npm run build`   | Type-check and build for production            |
| `npm run preview` | Preview the production build locally           |

API requests to `/api/*` are automatically proxied to `http://localhost:8080` during development. Make sure the backend is running before starting the dev server.

## Testing

| Command                | Description                    |
|------------------------|--------------------------------|
| `npm run test`         | Run unit tests                 |
| `npm run test:ui`      | Run tests with the Vitest UI   |
| `npm run test:coverage`| Run tests with coverage report |

Tests live alongside their source files using the `*.spec.ts` naming convention.

## Code Quality

| Command              | Description                               |
|---------------------|-------------------------------------------|
| `npm run lint`       | Lint with ESLint                          |
| `npm run format`     | Format with Prettier                      |
| `npm run type-check` | TypeScript type checking without emitting |
| `npm run check`      | Run lint + type-check + tests             |

> **Note:** `eslint-config-prettier` is included to turn off conflicting ESLint style rules so Prettier remains the single source of truth for formatting.

Run `npm run check` before pushing to ensure the branch is clean.

## Troubleshooting

- **API calls fail during local dev:** Ensure the backend is actively running on `localhost:8080` before you start `npm run dev`.
- **Port 5173 is already in use:** Vite will fail to start. Stop the conflicting process or override the port by running `npm run dev -- --port 5174`.

## Key Decisions

- **neverthrow** is used for error handling — API call results are typed as `Result<T, E>`, avoiding uncaught promise rejections.
- **`@` alias** resolves to `./src` for clean, absolute-style imports throughout the project.
- Production-like setup is served as a **pre-built static bundle via Caddy** inside Docker — no Node.js runtime required.
