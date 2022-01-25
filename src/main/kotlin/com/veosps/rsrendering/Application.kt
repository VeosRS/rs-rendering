package com.veosps.rsrendering

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}