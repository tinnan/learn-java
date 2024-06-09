# Learn Spring + Mongo DB Transaction

## Requirement
1. MongoDB Version 4.0+ (Community, Enterprise, Atlas)
2. Transaction only works in cluster mode. (Not supported in standalone mode)

## Configuration
Configure transaction manager. See example in
```
com.example.demo.config.MongoConfig
```

## Implementation
Use `org.springframework.transaction.annotation.Transactional` annotation to mark your transaction method.
