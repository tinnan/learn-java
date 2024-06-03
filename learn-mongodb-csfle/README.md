# MongoDB Client Side Field Level Encryption example

## Before start app
Start docker compose
```shell
docker compose up -d
```

## Manual encrypt/decrypt app
1. Open `ClientSideFieldLevelEncryptionManualTutorial` and comment/uncomment code that you want to run.
2. Run main class `ClientSideFieldLevelEncryptionManualTutorial`.

## Automatic encrypt/decrypt app
### Pre-requisite
Download and place Automatic Encryption Shared Library `mongo_crypt_v1.dylib` into `lib` directory 
in project root and set `SHARED_LIB_PATH` in `.env` to `lib/mongo_crypt_v1.dylib`.

### Run
1. Open `ClientSideFieldLevelEncryptionAutomaticTutorial` and comment/uncomment code that you want to run.
2. Run main class `ClientSideFieldLevelEncryptionAutomaticTutorial`.
