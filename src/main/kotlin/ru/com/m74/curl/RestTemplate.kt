package ru.com.m74.curl

import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContexts
import org.slf4j.LoggerFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.util.ResourceUtils
import org.springframework.web.client.RestTemplate
import ru.com.m74.curl.dto.Arguments
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.X509Certificate

val log = LoggerFactory.getLogger("ru.com.m74.curl.RestTemplate")

fun restTemplate(arguments: Arguments): RestTemplate {
    val clientBuilder = HttpClients.custom()

    // Setup ssl
    if (arguments.ssl) {
        val sslContextBuilder = SSLContexts.custom();
        // Disable validation server cert
        if (arguments.insecure)
            sslContextBuilder.loadTrustMaterial(null) { _: Array<X509Certificate?>?, s: String? -> true }

        // Setup client certs keystore for mTLS
        val st = arguments.keystore
        if (st != null) {
            log.info("Set keystore(${st.type}): ${st.path}")
            val keystore = KeyStore.getInstance(st.type)

            FileInputStream(ResourceUtils.getFile(st.path)).use { keystore.load(it, st.password.toCharArray()) }
            sslContextBuilder.loadKeyMaterial(keystore, st.keyPassword.toCharArray())
        }

        clientBuilder
            .setSSLSocketFactory(SSLConnectionSocketFactory(sslContextBuilder.build(), NoopHostnameVerifier()))
    }

    val requestFactory = HttpComponentsClientHttpRequestFactory()
    requestFactory.httpClient = clientBuilder.build()

    val restTemplate = RestTemplate(requestFactory)

//    restTemplate.uriTemplateHandler = DefaultUriBuilderFactory(url);

    return restTemplate;
}