package testconfig

import testutils.FileServerConstants
import testutils.SslUtils
import groovy.util.logging.Slf4j
import redis.embedded.RedisServer
import redis.embedded.core.ExecutableProvider
import redis.embedded.model.OsArchitecture

import java.nio.file.Path
import java.nio.file.Paths

import static java.net.HttpURLConnection.HTTP_OK
import static java.nio.file.Files.*
import static java.nio.file.StandardOpenOption.*

@Slf4j
final class EmbeddedRedisUtils {
    private EmbeddedRedisUtils() {}

    static RedisServer createServer(int port) {
        final OsArchitecture osArch = OsArchitecture.detectOSandArchitecture()
        if (OsArchitecture.UNIX_x86_64 == osArch) {
            // Configure the library to download custom Redis binary version.
            SslUtils.setTrustAll()
            log.info("Use Redis server without TLS support version for testing on build pipeline.")
            final Path cacheLocation = Paths.get(System.getProperty("java.io.tmpdir"), "redis-binary");
            return RedisServer.newRedisServer()
                    .port(port)
                    .executableProvider(newCachedUrlProvider(
                            cacheLocation, URI.create(FileServerConstants.REDIS_6_2_6_LINUX_X86_64_NO_TLS)))
                    .build()
        }

        return new RedisServer(port)
    }

    static ExecutableProvider newCachedUrlProvider(final Path cachedLocation, final URI uri) {
        return () -> {
            if (isRegularFile(cachedLocation)) {
                return cachedLocation.toFile()
            }

            final HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection()
            try {
                String basicAuth = FileServerConstants.FILE_SERVICE_USER + ":" + FileServerConstants.FILE_SERVER_PASSWORD
                connection.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder().encode(basicAuth.getBytes())))
                if (connection.getResponseCode() != HTTP_OK) {
                    throw new IOException("Failed to download redis binary from " + uri + ", status code is " + connection.getResponseCode())
                }

                createDirectories(cachedLocation.getParent())
                try (
                        final OutputStream out = newOutputStream(cachedLocation, CREATE, WRITE, TRUNCATE_EXISTING)
                        final InputStream ins = connection.getInputStream()
                ) {

                    final byte[] buffer = new byte[8192]
                    int length
                    while ((length = ins.read(buffer)) != -1) {
                        out.write(buffer, 0, length)
                    }
                }
                cachedLocation.toFile().setExecutable(true)

                return cachedLocation.toFile()
            } finally {
                connection.disconnect()
            }
        }
    }
}
