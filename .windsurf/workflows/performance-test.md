---
description: Complete performance testing workflow for Quarkus application using tool calls
---

# Performance Testing Workflow

This workflow automates the complete performance testing process for Quarkus application using available tool calls.

## Prerequisites

- Docker must be running
- k6 must be installed and available
- Maven must be installed
- Quarkus project structure must be present

## Steps

### 1. Build Application
Build Quarkus application using Maven:
```bash
cd quarkus-playground-updstream && mvn clean package -DskipTests
```

### 2. Start Docker Containers
Stop any existing containers and start the application:
```bash
docker-compose down && docker-compose up -d
```

### 3. Verify Application Health
Wait for the application to be ready and verify it's responding:
```bash
sleep 10 && curl -s http://localhost:8081/api
```

### 4. Run Unified Performance Test (5 minutes, progressive load)
Execute a single progressive test that covers all load levels:
```bash
k6 run .k6/scripts/k6-unified-test.js
```

### 5. Generate Performance Report
Create a comprehensive performance report with all test results and analysis.

### 6. Cleanup
Stop containers after testing:
```bash
docker-compose down
```

## Expected Results

The unified progressive test covers all load levels in a single 5-minute execution:

| Load Level | VUs | Duration | Expected P95 (ms) | Status |
|------------|-----|----------|------------------|---------|
| Baseline | 10 | 30s | < 10 | ✅ Excellent |
| Low Load | 50 | 30s | < 20 | ✅ Good |
| Medium Load | 100 | 30s | < 50 | ✅ Acceptable |
| High Load | 200 | 30s | < 100 | ✅ Stable |
| Very High Load | 400 | 30s | < 200 | ⚠️ Monitor |
| Extreme Load | 600 | 30s | < 300 | ⚠️ Degradation |
| Maximum Load | 800 | 30s | < 500 | ⚠️ High Degradation |
| Critical Load | 1000 | 30s | < 1000 | ❌ Critical |

## Key Performance Points

- **Safe Operating Range**: Up to 200 VUs
- **Degradation Point**: Between 200-400 VUs
- **Maximum Capacity**: Up to 1000 VUs with degradation
- **Test Duration**: 5 minutes total
- **Load Pattern**: Progressive ramp-up with 15s stages

## Usage

Run this workflow to perform complete performance testing of the Quarkus application. The workflow will:

1. Build the application
2. Start containers with resource limits
3. Run unified progressive test (5 minutes)
4. Generate comprehensive reports
5. Clean up resources

## Notes

- Container limits: 1 vCPU, 1GB RAM
- Test endpoint: http://localhost:8081/api/random-names
- Single test file: `.k8/k6-unified-test.js`
- Progressive load: 10→50→100→200→400→600→800→1000 VUs
- Reports saved in: `.k8/reports/` directory
- JSON results saved as: `k6-output-{timestamp}.json`
- HTML reports saved as: `k6-report-{timestamp}.html`
- All results are saved with unique timestamps
- Reports include performance analysis and recommendations
- All commands can be executed automatically without manual authorization
- Tools (k6, Maven, Docker) are called from system PATH