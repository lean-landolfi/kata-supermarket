Feature: Testing health check

  Testing health check

Scenario: Testing health check
  When a consumer makes an API GET request "/health-check"
  Then the API returns an "OK" status code