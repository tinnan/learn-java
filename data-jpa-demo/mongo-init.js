db = connect('mongodb://localhost/admin');

db.createUser({
    user: "product_user",
    pwd: "password",
    roles: [
        {
            role: "readWrite",
            db: "product"
        }
    ]
});
