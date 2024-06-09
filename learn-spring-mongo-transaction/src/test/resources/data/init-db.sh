set -e # Exit immediately if any command returns non-zero status.

mongosh --port 27017 <<EOF
use admin;
db.createUser({
  user: '$MONGO_INITDB_USERNAME',
  pwd:'$MONGO_INITDB_PASSWORD',
  roles:[
    { role: 'readWrite', db: 'admin_panel' }
  ]
});

use admin_panel;
db.createCollection('activity_log');
db.createCollection('activity_message');
EOF
