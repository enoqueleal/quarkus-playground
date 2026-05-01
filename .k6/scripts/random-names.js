import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.K6_BASE_URL || __ENV.BASE_URL || 'http://localhost:8080';
const USERS = Number(__ENV.K6_USERS || __ENV.USERS || 10);
const RAMP_SECONDS = Number(__ENV.K6_RAMP_SECONDS || __ENV.RAMP_SECONDS || 5);
const REPEAT_COUNT = Number(__ENV.K6_REPEAT_COUNT || __ENV.REPEAT_COUNT || 3);
const HOLD_SECONDS = Number(__ENV.K6_HOLD_SECONDS || Math.max(5, REPEAT_COUNT));
const RAMP_DOWN_SECONDS = Number(__ENV.K6_RAMP_DOWN_SECONDS || 1);
const PARK_SECONDS = RAMP_SECONDS + HOLD_SECONDS + RAMP_DOWN_SECONDS + 1;

export const options = {
  scenarios: {
    random_names: {
      executor: 'ramping-vus',
      exec: 'randomNamesScenario',
      startVUs: 0,
      stages: [
        { duration: `${RAMP_SECONDS}s`, target: USERS },
        { duration: `${HOLD_SECONDS}s`, target: USERS },
        { duration: `${RAMP_DOWN_SECONDS}s`, target: 0 },
      ],
      gracefulRampDown: '0s',
      tags: {
        scenario: 'random-names',
      },
    },
  },
  thresholds: {
    checks: ['rate==1.0'],
    http_req_failed: ['rate==0.0'],
  },
};

const requestParams = {
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
  tags: {
    name: 'GET /api/random-names',
    resource: 'random-names',
  },
};

function contentTypeStartsWithJson(response) {
  const contentType = response.headers['Content-Type'] || response.headers['content-type'] || '';
  return String(contentType).startsWith('application/json');
}

function parseJson(response) {
  try {
    return response.json();
  } catch (_error) {
    return null;
  }
}

export function randomNamesScenario() {
  for (let attempt = 0; attempt < REPEAT_COUNT; attempt += 1) {
    const response = http.get(`${BASE_URL}/api/random-names`, requestParams);
    const payload = parseJson(response);

    check(response, {
      'status is 200': (res) => res.status === 200,
      'content-type is application/json': (res) => contentTypeStartsWithJson(res),
      'resource is random-names': () => payload?.resource === 'random-names',
      'status is ready': () => payload?.status === 'ready',
      'total is 5': () => payload?.total === 5,
      'has at least one name': () => Array.isArray(payload?.names) && payload.names.length > 0,
    });
  }

  sleep(PARK_SECONDS);
}

