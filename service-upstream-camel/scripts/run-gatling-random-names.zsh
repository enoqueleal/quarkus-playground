#!/usr/bin/env zsh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
APP_PORT="${APP_PORT:-8080}"
APP_BASE_URL="${APP_BASE_URL:-http://localhost:${APP_PORT}}"
SIMULATION_CLASS="br.com.playground.performance.RandomNamesSimulation"
APP_LOG_FILE="${APP_LOG_FILE:-/tmp/quarkus-random-names-app.log}"

cd "$PROJECT_DIR"

export MAVEN_OPTS="${MAVEN_OPTS:-} --add-opens=java.base/java.lang=ALL-UNNAMED"
export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} --add-opens=java.base/java.lang=ALL-UNNAMED"

./mvnw -q -DskipTests package

java \
  -Dquarkus.http.port="$APP_PORT" \
  -Dquarkus.datasource.devservices.enabled=false \
  -jar "$PROJECT_DIR/target/quarkus-app/quarkus-run.jar" > "$APP_LOG_FILE" 2>&1 &
APP_PID=$!

cleanup() {
  kill "$APP_PID" >/dev/null 2>&1 || true
}
trap cleanup EXIT

for _ in {1..60}; do
  if curl -sf "$APP_BASE_URL/api/random-names" >/dev/null; then
    break
  fi
  sleep 2
done

curl -sf "$APP_BASE_URL/api/random-names" >/dev/null

./mvnw -q gatling:test \
  -Dgatling.simulationClass="$SIMULATION_CLASS" \
  -Dgatling.baseUrl="$APP_BASE_URL" \
  "$@"

