package com.example.demo.utils

final class FileServerConstants {
    private FileServerConstants() {}

    public static final String FILE_SERVER_HOST = "file.server.com"
    public static final String FILE_SERVICE_USER = "dist_user"
    public static final String FILE_SERVER_PASSWORD = "dist_pwd"
    public static final String MONGO_DIST_BASE_URL = "https://${FILE_SERVER_HOST}/repository/mongodb"
    public static final String REDIS_6_2_6_LINUX_X86_64_NO_TLS = "https://${FILE_SERVER_HOST}/repository/redis/redis-server-6.2.6-linux-amd64-no-tls"
}
