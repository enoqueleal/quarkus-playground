---
name: performance-test
description: Run complete performance testing for Quarkus application
argument-hint: "[project-dir]"
allowed-tools:
  - read
  - exec
  - grep
  - glob
permissions:
  allow:
    - Exec(mvn)
    - Exec(docker-compose)
    - Exec(k6)
    - Exec(curl)
    - Exec(sleep)
---

Execute the complete performance testing workflow for the Quarkus application.

## Setup
Navigate to the project directory: `cd quarkus-playground-updstream` (or use $1 if provided)

## Execution Steps

1. **Build the application**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Start containers**
   ```bash
   docker-compose down && docker-compose up -d
   ```

3. **Verify application health**
   ```bash
   sleep 10 && curl -s http://localhost:8081/api
   ```

4. **Run performance test** (5-minute progressive load test)
   ```bash
   k6 run k6/scripts/k6-unified-test.js
   ```

5. **Stop containers**
   ```bash
   docker-compose down
   ```

## Expected Output

After completing all steps, provide a summary including:

- **Build status**: Success/failure
- **Application health**: Response from health check
- **Test results**: Key metrics from k6 output (P95, P99, throughput, error rate)
- **Performance analysis**: Compare actual P95 against expected thresholds:
  - Baseline (10 VUs): < 10ms
  - Low (50 VUs): < 20ms
  - Medium (100 VUs): < 50ms
  - High (200 VUs): < 100ms
  - Very High (400 VUs): < 200ms
  - Extreme (600 VUs): < 300ms
  - Maximum (800 VUs): < 500ms
  - Critical (1000 VUs): < 1000ms
- **Issues encountered**: Any errors or failures
- **Recommendations**: Performance improvements or concerns

If any step fails, stop and report the specific error with context.