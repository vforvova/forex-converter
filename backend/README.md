# Backend

Java 21 + Spring Boot REST API for currency conversion.

## Requirements

- Java 21 — managed via [`mise`](https://mise.jdx.dev/) (see project root for `.mise.toml`)
- Gradle wrapper is included (`./gradlew`) — no separate installation needed

## Running Locally

```bash
export SWOP_API_KEY=your_api_key_here
./gradlew bootRun
```

The API will be available at `http://localhost:8080`.

> [!TIP]
> Use the `dev` profile for a better local experience — Influx export is disabled
> and logging is more verbose (see [Spring Profiles](#spring-profiles) below).

## Spring Profiles

| Profile     | Activate with                          | Influx export | Log level | Health details |
|-------------|----------------------------------------|---------------|-----------|----------------|
| _(default)_ | —                                      | ✅ Enabled     | `WARN`    | Hidden         |
| `dev`       | `--spring.profiles.active=dev`         | ❌ Disabled    | `DEBUG`   | Full           |

Run with the dev profile:
```bash
export SWOP_API_KEY=your_api_key_here
./gradlew bootRun --args='--spring.profiles.active=dev'
```

> [!NOTE]
> When using the `dev` profile, you do not need a running InfluxDB instance.
> Additional actuator endpoints (`/actuator/info`, `/actuator/env`, `/actuator/beans`)
> are also exposed.

## Commands

| Command                   | Description                      |
|---------------------------|----------------------------------|
| `./gradlew build`         | Compile and assemble the project |
| `./gradlew bootRun`       | Run the application              |
| `./gradlew test`          | Run all tests                    |
| `./gradlew check`         | Tests + lint (run before commit) |
| `./gradlew spotlessApply` | Auto-format Java source          |
| `./gradlew ktlintFormat`  | Auto-format Kotlin DSL           |

> [!IMPORTANT]
> The Docker image build skips tests (`-x test`). Always run `./gradlew test`
> locally or in CI — do not rely on the Docker build to catch test failures.

## Project Structure

```
src/main/java/com/forexconverter/
├── ForexConverterApplication.java   # Entry point
├── conversion/                      # HTTP layer: controller, DTOs, exception handler
├── rate/                            # Rate abstraction: Provider interface, cache config, Swop adapter
└── swop/                            # swop.cx HTTP client and configuration
```

## Secrets & .gitignore

`backend/` maintains its own `.gitignore` alongside the root one.
The `.env` file (containing `SWOP_API_KEY`) is gitignored at the `backend/` level.

> [!WARNING]
> Never commit `.env` or any file containing real credentials. `application.yml`
> uses environment variable placeholders (`${SWOP_API_KEY}`) — keep it that way.
