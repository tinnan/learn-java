# Spring batch learning
Collections of example application for Spring batch program. Each example is activated separately by active profile.

## Running
Start the app with program arguments `--spring.profiles.active=<profile1>,<profile2>,...`

## Examples
1. Profile `tut1` - Simple batch program with 1 job and 1 step to read from CSV file, transform the 
   data and write to database.

## API
Start app with profile `api` will expose the following APIs.
1. List all job names.
   ```
   GET http://localhost:8080/api/v1/job
   ```
2. Run job.
   ```
   POST http://localhost:8080/api/v1/job/run/:jobName
   ```