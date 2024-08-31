package testutils

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate

final class SslUtils {
    private SslUtils() {}

    static void setTrustAll() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    X509Certificate[] getAcceptedIssuers() {
                        return null
                    }

                    void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        }

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, new SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            boolean verify(String hostname, SSLSession session) {
                return true
            }
        }

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
    }
}
