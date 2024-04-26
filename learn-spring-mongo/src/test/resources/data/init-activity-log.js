db.activity_log.insertMany(
    [
        {
            'tx_datetime': Date('2024-04-15T03:41:33.024Z'),
            'staff_id': '52134',
            'branch_code': '001',
            'channel': 'Branch',
            'rmid_ec': Int32(77318491),
            'id_type': 'CID',
            'id_no': '1123900091841',
            'service_type': 'Create RM',
            'activity_type': 'Dip Chip',
            'activity_status': 'Failed',
            'detail': '{"errorCode":"400","errorMsg":"Generic Server Error"}'
        },
        {
            'tx_datetime': Date('2024-04-15T06:03:12.672Z'),
            'staff_id': '62007',
            'branch_code': '002',
            'channel': 'Branch',
            'rmid_ec': Int32(88714120),
            'id_type': 'PASSPORT',
            'id_no': 'AA99481250',
            'service_type': 'Apply AL',
            'activity_type': 'Phone No. Input',
            'activity_status': 'Pass'
        }
    ]
);