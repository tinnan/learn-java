db = connect('mongodb://localhost/admin_panel')

db.createUser({
    user: "admpanelusr",
    pwd: "Passw0rd",
    roles: [
        {
            role: "readWrite",
            db: "admin_panel"
        }
    ]
});