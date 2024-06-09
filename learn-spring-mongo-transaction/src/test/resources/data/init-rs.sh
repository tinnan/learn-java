set -e # Exit immediately if any command returns non-zero status.

mongosh --port 27017 <<EOF
try {
  rs.status();
} catch (err) {
  rs.initiate({
    _id: 'rs0',
    members: [
      {
        _id:0,
        host:'host.docker.internal:27017',
        priority:1
      }
    ]
  });
}
EOF
