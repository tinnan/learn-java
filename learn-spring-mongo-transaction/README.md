# Learn Spring + Mongo DB

## Generate Querydsl Q class

Run `./gradlew build`

## MongoDB indices and execution plan

Index creation example command.

```javascript
db.activity_log.createIndex({tx_datetime: 1});
db.activity_log.createIndex({tx_datetime: 1, branch_code: 1});
```

Query execution plan explain command example.

```javascript
db.activity_log.find({ $and: [ { $and: [{ "tx_datetime": { $gte: ISODate("2024-04-30T00:00:00") } }, { "tx_datetime": { $lte: ISODate("2024-05-05T10:11:47") } }] }, { "branch_code": "00145" } ] }).explain();
db.activity_log.find({ $and: [ { $and: [{ "tx_datetime": { $gte: ISODate("2024-04-30T00:00:00") } }, { "tx_datetime": { $lte: ISODate("2024-05-05T10:11:47") } }] }, { "staff_id": "52143" } ] }).explain()
db.activity_log.find({ "branch_code": "00145" }).explain();
db.activity_log.find({ "staff_id": "52134" }).explain();
```