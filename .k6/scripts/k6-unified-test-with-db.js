import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
let errorRate = new Rate('errors');
let p95Metric = new Rate('p95_violations');

export let options = {
  stages: [
    // Teste progressivo completo em 5 minutos
    // Baseline - 30 segundos
    { duration: '15s', target: 10 },   // Ramp up para 10 VUs
    { duration: '15s', target: 10 },  // Mantém 10 VUs
    
    // Carga baixa - 45 segundos
    { duration: '15s', target: 50 },   // Ramp up para 50 VUs
    { duration: '15s', target: 50 },   // Mantém 50 VUs
    
    // Carga média - 1 minuto 15 segundos
    { duration: '15s', target: 100 },  // Ramp up para 100 VUs
    { duration: '15s', target: 100 },  // Mantém 100 VUs
    
    // Carga alta - 1 minuto 45 segundos
    { duration: '15s', target: 200 },  // Ramp up para 200 VUs
    { duration: '15s', target: 200 },  // Mantém 200 VUs
    
    // Carga muito alta - 2 minutos 15 segundos
    { duration: '15s', target: 400 },  // Ramp up para 400 VUs
    { duration: '15s', target: 400 },  // Mantém 400 VUs
    
    // Carga extrema - 2 minutos 45 segundos
    { duration: '15s', target: 600 },  // Ramp up para 600 VUs
    { duration: '15s', target: 600 },  // Mantém 600 VUs
    
    // Teste de limite - 3 minutos 15 segundos
    { duration: '15s', target: 800 },  // Ramp up para 800 VUs
    { duration: '15s', target: 800 },  // Mantém 800 VUs
    
    // Carga máxima - 3 minutos 45 segundos
    { duration: '15s', target: 1000 }, // Ramp up para 1000 VUs
    { duration: '15s', target: 1000 }, // Mantém 1000 VUs
    
    // Cool down - 4 minutos
    { duration: '30s', target: 0 },    // Ramp down para 0 VUs
  ],
  thresholds: {
    // Thresholds para detectar diferentes níveis de performance
    http_req_duration: ['p(95)<1000'],  // 95% abaixo de 1s
    http_req_failed: ['rate<0.2'],      // Taxa de erro abaixo de 20%
    errors: ['rate<0.2'],               // Custom error rate abaixo de 20%
  },
};

export default function () {
  let baseUrl = 'http://localhost:8081';
  let endpoint = '/api/people';
  
  let response = http.get(`${baseUrl}${endpoint}`, {
    headers: {
      'Accept': 'application/json',
    },
  });

  let success = check(response, {
    'status is 200': (r) => r.status === 200,
    'response time < 1000ms': (r) => r.timings.duration < 1000,
    'response has content': (r) => r.body && r.body.length > 0,
  });

  errorRate.add(!success);
  
  // Track P95 violations (threshold de 500ms para análise)
  if (response.timings.duration > 500) {
    p95Metric.add(1);
    console.log(`P95 VIOLATION: ${response.timings.duration}ms - Current VUs: ${__VU}`);
  } else {
    p95Metric.add(0);
  }

  // Track severe violations (>1000ms)
  if (response.timings.duration > 1000) {
    console.log(`SEVERE VIOLATION: ${response.timings.duration}ms - Current VUs: ${__VU}`);
  }

  // Log detalhado quando há falhas críticas
  if (!success) {
    console.log(`CRITICAL FAILURE: Status ${response.status}, Duration: ${response.timings.duration}ms, VUs: ${__VU}`);
  }

  // Sleep mínimo para maximizar carga
  sleep(0.01); // 10ms entre requests
}

export function handleSummary(data) {
  // Generate timestamp for unique filenames
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, -5);
  const jsonFilename = `k6-output-${timestamp}.json`;
  const htmlFilename = `k6-report-${timestamp}.html`;
  
  return {
    'stdout': unifiedSummary(data, { indent: ' ', enableColors: true }),
    [jsonFilename]: JSON.stringify(data, null, 2),
    [htmlFilename]: htmlReport(data),
  };
}

function unifiedSummary(data, options) {
  const totalRequests = data.metrics.http_reqs ? data.metrics.http_reqs.count : 0;
  const totalDuration = data.metrics.test_duration ? data.metrics.test_duration.values.max / 1000 : 0;
  const tps = totalDuration > 0 ? (totalRequests / totalDuration).toFixed(2) : 0;
  const errorRate = data.metrics.http_req_failed ? (data.metrics.http_req_failed.rate * 100).toFixed(2) : 0;
  const p95 = data.metrics.http_req_duration ? data.metrics.http_req_duration.values['p(95)'] : 0;
  const maxResponse = data.metrics.http_req_duration ? data.metrics.http_req_duration.values.max : 0;
  const p95Violations = data.metrics.p95_violations ? (data.metrics.p95_violations.rate * 100).toFixed(2) : 0;
  
  // Determinar ponto de degradação
  let degradationPoint = "Nenhuma degradação detectada";
  let status = "✅ ESTÁVEL";
  let performanceLevel = "ÓTIMA";
  
  if (p95 > 1000) {
    degradationPoint = `P95 de ${p95.toFixed(2)}ms ultrapassou 1s`;
    status = "❌ DEGRADAÇÃO CRÍTICA";
    performanceLevel = "CRÍTICA";
  } else if (p95 > 500) {
    degradationPoint = `P95 de ${p95.toFixed(2)}ms ultrapassou 500ms`;
    status = "⚠️ DEGRADAÇÃO MODERADA";
    performanceLevel = "DEGRADADA";
  } else if (p95 > 100) {
    degradationPoint = `P95 de ${p95.toFixed(2)}ms ultrapassou 100ms`;
    status = "⚠️ DEGRADAÇÃO LEVE";
    performanceLevel = "ACEITÁVEL";
  }
  
  if (parseFloat(errorRate) > 20) {
    degradationPoint = `Taxa de erro de ${errorRate}% ultrapassou 20%`;
    status = "❌ FALHA CRÍTICA";
    performanceLevel = "CRÍTICA";
  }
  
  return `
UNIFIED PERFORMANCE TEST SUMMARY - People API with Database
================================================

Test Duration: ${totalDuration.toFixed(2)}s
Total Requests: ${totalRequests}
Average TPS: ${tps}
Max VUs Reached: ${data.metrics.vus ? data.metrics.vus.values.max : 'N/A'}

PERFORMANCE METRICS:
- Average Response Time: ${data.metrics.http_req_duration ? data.metrics.http_req_duration.values.avg.toFixed(2) : 'N/A'}ms
- 95th Percentile: ${p95.toFixed(2)}ms
- Max Response Time: ${maxResponse.toFixed(2)}ms
- Error Rate: ${errorRate}%

DEGRADATION ANALYSIS:
- P95 Violations (>500ms): ${p95Violations}%
- Status: ${status}
- Performance Level: ${performanceLevel}
- Degradation Point: ${degradationPoint}

THRESHOLD ANALYSIS:
- P95 < 100ms: ${p95 < 100 ? '✅ EXCELENTE' : '❌ FALHOU'}
- P95 < 500ms: ${p95 < 500 ? '✅ PASS' : '❌ FALHOU'}
- P95 < 1000ms: ${p95 < 1000 ? '✅ PASS' : '❌ FALHOU'}
- Error Rate < 20%: ${parseFloat(errorRate) < 20 ? '✅ PASS' : '❌ FALHOU'}

RESOURCE UTILIZATION:
- Container Limits: 1 vCPU, 1GB RAM
- Test Load: Up to ${data.metrics.vus ? data.metrics.vus.values.max : 'N/A'} VUs
- Load Pattern: Progressive 10→50→100→200→400→600→800→1000 VUs
  `;
}

function htmlReport(data) {
  const totalRequests = data.metrics.http_reqs ? data.metrics.http_reqs.count : 0;
  const totalDuration = data.metrics.test_duration ? data.metrics.test_duration.values.max / 1000 : 0;
  const tps = totalDuration > 0 ? (totalRequests / totalDuration).toFixed(2) : 0;
  const errorRate = data.metrics.http_req_failed ? (data.metrics.http_req_failed.rate * 100).toFixed(2) : 0;
  const p95 = data.metrics.http_req_duration ? data.metrics.http_req_duration.values['p(95)'] : 0;
  const maxResponse = data.metrics.http_req_duration ? data.metrics.http_req_duration.values.max : 0;
  const p95Violations = data.metrics.p95_violations ? (data.metrics.p95_violations.rate * 100).toFixed(2) : 0;
  
  let status = "✅ ESTÁVEL";
  let statusColor = "#28a745";
  
  if (p95 > 1000) {
    status = "❌ DEGRADAÇÃO CRÍTICA";
    statusColor = "#dc3545";
  } else if (p95 > 500) {
    status = "⚠️ DEGRADAÇÃO MODERADA";
    statusColor = "#ffc107";
  } else if (p95 > 100) {
    status = "⚠️ DEGRADAÇÃO LEVE";
    statusColor = "#ffc107";
  }
  
  if (parseFloat(errorRate) > 20) {
    status = "❌ FALHA CRÍTICA";
    statusColor = "#dc3545";
  }
  
  return `
<!DOCTYPE html>
<html>
<head>
    <title>Unified Performance Test Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { background: #f4f4f4; padding: 20px; border-radius: 5px; margin-bottom: 20px; }
        .metric { margin: 10px 0; padding: 15px; background: #f9f9f9; border-left: 4px solid #007cba; }
        .success { border-left-color: #28a745; }
        .warning { border-left-color: #ffc107; }
        .error { border-left-color: #dc3545; }
        .critical { border-left-color: #dc3545; background: #fff5f5; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #f2f2f2; }
        .highlight { font-size: 1.2em; font-weight: bold; }
        .status { font-size: 1.4em; font-weight: bold; color: ${statusColor}; }
        .progress-bar { width: 100%; height: 20px; background: #e0e0e0; border-radius: 10px; margin: 10px 0; }
        .progress-fill { height: 100%; background: linear-gradient(90deg, #28a745, #ffc107, #dc3545); border-radius: 10px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Unified Performance Test Report</h1>
        <h2>People API with Database - Progressive Load Test</h2>
        <p>Container: 1 vCPU, 1GB RAM</p>
        <p>Test Duration: ${totalDuration.toFixed(2)} seconds</p>
        <div class="status">Status: ${status}</div>
    </div>

    <div class="metric ${parseFloat(errorRate) < 20 ? 'success' : 'critical'}">
        <h3>Error Rate</h3>
        <p class="highlight">${errorRate}%</p>
    </div>

    <div class="metric ${p95 < 100 ? 'success' : p95 < 500 ? 'warning' : 'error'}">
        <h3>95th Percentile Response Time</h3>
        <p class="highlight">${p95.toFixed(2)}ms</p>
        <p>Thresholds: &lt;100ms (Excelente), &lt;500ms (Bom), &lt;1000ms (Aceitável)</p>
    </div>

    <div class="metric">
        <h3>Average TPS</h3>
        <p class="highlight">${tps}</p>
    </div>

    <div class="metric ${p95Violations < 5 ? 'success' : 'warning'}">
        <h3>P95 Violations (&gt;500ms)</h3>
        <p class="highlight">${p95Violations}%</p>
    </div>

    <div class="metric">
        <h3>Max Response Time</h3>
        <p class="highlight">${maxResponse.toFixed(2)}ms</p>
    </div>

    <h2>Performance Analysis</h2>
    <table>
        <tr><th>Metric</th><th>Value</th><th>Threshold</th><th>Status</th></tr>
        <tr><td>P95 Response Time</td><td>${p95.toFixed(2)}ms</td><td>&lt;500ms</td><td>${p95 < 500 ? '✅ PASS' : '❌ FAIL'}</td></tr>
        <tr><td>P95 Response Time</td><td>${p95.toFixed(2)}ms</td><td>&lt;1000ms</td><td>${p95 < 1000 ? '✅ PASS' : '❌ FAIL'}</td></tr>
        <tr><td>Error Rate</td><td>${errorRate}%</td><td>&lt;20%</td><td>${parseFloat(errorRate) < 20 ? '✅ PASS' : '❌ FAIL'}</td></tr>
        <tr><td>P95 Violations</td><td>${p95Violations}%</td><td>&lt;5%</td><td>${p95Violations < 5 ? '✅ PASS' : '❌ FAIL'}</td></tr>
    </table>

    <h2>Load Progression</h2>
    <div class="progress-bar">
        <div class="progress-fill" style="width: ${Math.min(p95, 1000) / 10}%"></div>
    </div>
    <p>Load progression: 10 → 50 → 100 → 200 → 400 → 600 → 800 → 1000 VUs</p>
    <p>Each stage: 15s ramp-up + 15s hold</p>

    <h2>Test Configuration</h2>
    <table>
        <tr><th>Setting</th><th>Value</th></tr>
        <tr><td>Max Virtual Users</td><td>${data.metrics.vus ? data.metrics.vus.values.max : 'N/A'}</td></tr>
        <tr><td>Test Duration</td><td>${totalDuration.toFixed(2)}s</td></tr>
        <tr><td>Container CPU Limit</td><td>1 vCPU</td></tr>
        <tr><td>Container Memory Limit</td><td>1GB</td></tr>
        <tr><td>Target Endpoint</td><td>http://localhost:8082/api/people</td></tr>
        <tr><th>Test Pattern</th><th>Progressive load with 5-minute duration</th></tr>
    </table>

    <h2>Performance Metrics</h2>
    <table>
        <tr><th>Metric</th><th>Value</th></tr>
        <tr><td>Total Requests</td><td>${totalRequests}</td></tr>
        <tr><td>Average Response Time</td><td>${data.metrics.http_req_duration ? data.metrics.http_req_duration.values.avg.toFixed(2) : 'N/A'}ms</td></tr>
        <tr><td>Min Response Time</td><td>${data.metrics.http_req_duration ? data.metrics.http_req_duration.values.min.toFixed(2) : 'N/A'}ms</td></tr>
        <tr><td>50th Percentile</td><td>${data.metrics.http_req_duration && data.metrics.http_req_duration.values['p(50)'] ? data.metrics.http_req_duration.values['p(50)'].toFixed(2) : 'N/A'}ms</td></tr>
        <tr><td>90th Percentile</td><td>${data.metrics.http_req_duration && data.metrics.http_req_duration.values['p(90)'] ? data.metrics.http_req_duration.values['p(90)'].toFixed(2) : 'N/A'}ms</td></tr>
        <tr><td>99th Percentile</td><td>${data.metrics.http_req_duration && data.metrics.http_req_duration.values['p(99)'] ? data.metrics.http_req_duration.values['p(99)'].toFixed(2) : 'N/A'}ms</td></tr>
    </table>
</body>
</html>
  `;
}
