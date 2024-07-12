#!/bin/bash

echo "Starting replica set initialization"
until mongosh --host mongo --eval "print(\"waited for connection\")"
do
    echo "Not ready. Waiting..."
    sleep 2
done
echo "Connected"
echo "Creating replica set"
mongosh --host mongo <<EOF
try {
  rs.status();
} catch (err) {
  rs.initiate({
    _id: 'rs0',
    members: [
      { _id:0, host:'mongo:27017', priority:1 }
    ]
  });
}
EOF
echo "Replica set created"
