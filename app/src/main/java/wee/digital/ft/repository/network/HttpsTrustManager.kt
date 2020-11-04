package wee.digital.ft.repository.network

import android.content.Context
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

class HttpsTrustManager : X509TrustManager {
    @Throws(java.security.cert.CertificateException::class)
    override fun checkClientTrusted(
            x509Certificates: Array<X509Certificate>, s: String
    ) {

    }

    @Throws(java.security.cert.CertificateException::class)
    override fun checkServerTrusted(
            x509Certificates: Array<X509Certificate>, s: String
    ) {

    }

    fun isClientTrusted(chain: Array<X509Certificate>): Boolean {
        return true
    }

    fun isServerTrusted(chain: Array<X509Certificate>): Boolean {
        return true
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return _AcceptedIssuers
    }

    companion object {
        private var trustManagers: Array<TrustManager>? = null
        var trustManager: X509TrustManager? = null
        var sslContext: SSLContext? = null
        var _AcceptedIssuers = arrayOf<X509Certificate>()
        fun allowAllSSL(context: Context, crtFile: Int) {
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
            val cf = CertificateFactory.getInstance("X.509")
            val inStream = context.resources.openRawResource(crtFile)
            val ca: Certificate
            ca = cf.generateCertificate(inStream)
            val kStore = KeyStore.getInstance(KeyStore.getDefaultType())
            kStore.load(null, null)
            kStore.setCertificateEntry("ca", ca)
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(kStore)
            val trustCerts = tmf.trustManagers
            trustManager = trustCerts[0] as X509TrustManager
            try {
                sslContext = SSLContext.getInstance("SSL")
                sslContext!!.init(null, trustCerts, SecureRandom())
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    sslContext!!
                            .socketFactory
            )
        }
        fun allowAllSSL() {
            HttpsURLConnection.setDefaultHostnameVerifier { arg0, arg1 -> true }
            if (trustManagers == null) {
                trustManagers = arrayOf(
                    HttpsTrustManager()
                )
            }
            try {
                sslContext = SSLContext.getInstance("TLS")
                sslContext!!.init(null, trustManagers, SecureRandom())
                trustManager = trustManagers!![0] as X509TrustManager
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(
                sslContext!!
                    .socketFactory
            )
        }
    }
}