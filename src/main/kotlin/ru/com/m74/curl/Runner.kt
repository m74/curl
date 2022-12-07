package ru.com.m74.curl

import org.springframework.boot.CommandLineRunner
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import ru.com.m74.curl.dto.Arguments
import ru.com.m74.curl.dto.Keystore

@Component
class Runner : CommandLineRunner {

    override fun run(vararg args: String?) = try {
        arguments(*args).run {
            if (help) {
                showHelp()
            } else if (url == null) {
                println("curl: try 'curl --help' for more information")
            } else {
                println(this)

                val resp = restTemplate(this).exchange(
                    url ?: throw RuntimeException("URL required"),
                    method,
                    HttpEntity(body, headers),
                    String::class.java
                )

                println("response: ${resp.body}")
            }
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}


fun showHelp() {
    println(
        """
        Usage: curl [options...] <url>
         -X, --request <data> HTTP PROTOCOL (GET|POST|PUT|DELETE)
         -H, --header <header>   HTTP header
         -d, --data <data>   HTTP POST data
         -k, --insecure  Disable SSL cert validation
         -E, --cert <certificate[:password]> TLS client certificate file (for mTLS)
         --cert-type <type> (PEM, DER, ENG, P12) If not specified, P12 is assumed.
         -h, --help <category>  Get help for commands
         -v, --verbose       Make the operation more talkative
    """.trimIndent()
    )
}

fun arguments(vararg args: String?): Arguments {
    var url: String? = null
    var method: HttpMethod = HttpMethod.GET
    val headers: MutableList<String> = mutableListOf()
    var insecure = false
    var verbose = false
    var help = false
    var keystoreType: String? = null
    var keystore: String? = null
    var body: String? = null

    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "-X", "--request" -> method = HttpMethod.resolve(args[++i])!!
            "-H", "--header" -> {
                val header = args[++i]
                if (header != null) headers.add(header)
            }

            "-d", "--data" -> body = args[++i]
            "-k", "--insecure" -> insecure = true
            "-E", "--cert" -> keystore = args[++i]
            "--cert-type" -> keystoreType = args[++i]
            "-v", "--verbose" -> verbose = true
            "-h", "--help" -> help = true
            else -> {
                if (url != null) throw RuntimeException("Command line format error")
                url = args[i]
            }
        }
        i++
    }

    return Arguments(
        url,
        url?.startsWith("https://") ?: false,
        method,
        headers(headers),
        insecure,
        keystore(keystore, keystoreType),
        body,
        verbose,
        help
    )
}

fun headers(list: MutableList<String>) = LinkedMultiValueMap<String, String>().apply {
    list.forEach {
        val arr = it.split(":")
        add(arr[0].trim(), arr[1].trim())
    }
}

fun keystore(path: String?, type: String?): Keystore? = if (path != null) {
    val arr = path.split(':')
    Keystore(type(type ?: "P12"), arr[0], arr[1], arr[1])
} else {
    null
}

fun type(type: String): String = when (type) {
    "P12" -> "PKCS12"
    else -> type
}
