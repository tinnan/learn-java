db = connect("mongodb://localhost/admin_panel");

db.createUser({
    user: "adminuser",
    pwd: "adminpwd",
    roles: [
        {
            role: "readWrite",
            db: "admin_panel"
        }
    ]
});

db.createCollection('activity_log', {});
db.createView('activity_log_view', 'activity_log',
    [
        {
            $addFields: {
                user_activity: {
                    $concat: ['$service_type', '_', '$activity_status']
                }
            }
        }
    ]
);

db.activity_log.createIndex({tx_datetime: 1});
db.activity_log.createIndex({tx_datetime: 1, branch_code: 1});
db.activity_log.createIndex({staff_id: 1});

const dataSize = 1000000;
const bulkSize = 100000;
const epochSeed = 1714475447000; // 2024-04-30T18:10:47.000+07:00
let dat = [];
for (let i = 1; i <= dataSize; i += 1) {
    const epoch = epochSeed + (i * 1000); // 1 activity log every 1 second.
    const l = {
        '_id': UUID(),
        'tx_datetime': new Date(epoch),
        'staff_id': '52134',
        'branch_code': '0'.repeat(5).concat(Math.random().toFixed(3) * 1000).substr(-5),
        'channel': 'Branch',
        'rmid_ec': Int32(77318491),
        'id_type': 'CID',
        'id_no': '1123900091841',
        'service_type': 'Create RM',
        'activity_type': 'Dip Chip',
        'activity_status': 'Pass',
        'meta_data': Object({'device_id': 'BB00931', 'device_app_version': '1.0.0'})
    };
    if (Math.random() < 0.1) {
        l.activity_status = 'Failed';
        l.detail = Object({"error_code": "400", "error_msg": "Generic Server Error"});
    }
    dat.push(l);
    if (i % bulkSize == 0) {
        db.activity_log.insertMany(dat);
        dat = [];
    }
}