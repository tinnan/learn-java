#!/bin/bash

mongosh --host mongo <<EOF
use admin;
db.createUser({
  user: '$MONGO_INITDB_USERNAME',
  pwd:'$MONGO_INITDB_PASSWORD',
  roles:[
    { role: 'readWrite', db: 'admin_panel' }
  ]
});
EOF
