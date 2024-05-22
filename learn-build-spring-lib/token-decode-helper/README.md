# Spring boot security filter library.

### Test API
Start app `SpringSecurityApplication`

`GET /v1/secure/inquire` - Secured by authority `INQUIRE`.

`GET /v1/secure/generate` - Secured by role `ROLE_GENERATE`.

`GET /v1/secure/count` - Secured by role `ROLE_COUNT`.

`GET /v1/secure/sum` - Unsecured.

### JWT Token for test
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdGFmZl9pZCI6IjYxNjQzIiwicGVybWlzc2lvbnMiOlsiSU5RVUlSRSIsIlJPTEVfR0VORVJBVEUiXX0.HqQ-NGw3b6DZ8FhMoLwvblRNgziUdLe6CAKgzTqjqFI
```
Payload
```JSON
{
  "staff_id": "61643",
  "permissions": ["INQUIRE", "ROLE_GENERATE"]
}
```