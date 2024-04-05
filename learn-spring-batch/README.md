# Spring batch learning
Collections of example application for Spring batch program. Each example is activated separately by active profile.

## Running
Start the app with program arguments `--spring.profiles.active=<profile1>,<profile2>,...`

## Examples
1. Profiles `tut1`, `tut1-simple` - Simple batch program with 1 job and 1 step to read from CSV file, transform the 
   data and write to database.
2. Profiles `tut1`, `tut1-api` - Simple batch program, same as no.1, but starting from API call.
   ```
   POST http://localhost:8080/api/v1/job/tut1
   ```