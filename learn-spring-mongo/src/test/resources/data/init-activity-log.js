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
        'branch_code': '001',
        'channel': 'Branch',
        'rmid_ec': Int32(77318491),
        'id_type': 'CID',
        'id_no': '1123900091841',
        'service_type': 'Create RM',
        'activity_type': 'Dip Chip',
        'activity_status': 'Failed',
    };
    if (Math.random() < 0.1) {
        l.detail = Object({"errorCode":"400","errorMsg":"Generic Server Error"});
    }
    dat.push(l);
    if (i % bulkSize == 0) {
        db.activity_log.insertMany(dat);
        dat = [];
    } 
}