import http from 'k6/http';
import { check } from 'k6';

const targetBaseUrl = __ENV.TARGET_BASE_URL || 'http://127.0.0.1:18080';
const targetPath = __ENV.TARGET_PATH || '/actuator/health';

export const options = {
  stages: [
    { duration: '30s', target: 40 },
    { duration: '60s', target: 120 },
    { duration: '90s', target: 200 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.1'],
  },
};

export default function () {
  const url = `${targetBaseUrl}${targetPath}`;

  const responses = http.batch([
    ['GET', url],
    ['GET', url],
    ['GET', url],
  ]);

  for (const res of responses) {
    check(res, {
      'status is 2xx/3xx': (r) => r.status >= 200 && r.status < 400,
    });
  }
}