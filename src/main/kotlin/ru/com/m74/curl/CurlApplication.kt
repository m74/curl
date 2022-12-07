package ru.com.m74.curl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CurlApplication

fun main(args: Array<String>) {
	runApplication<CurlApplication>(*args)
}
