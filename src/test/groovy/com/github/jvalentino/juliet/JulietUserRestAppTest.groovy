package com.github.jvalentino.juliet


import org.springframework.boot.SpringApplication
import spock.lang.Specification

class JulietUserRestAppTest extends Specification {

    def setup() {
        GroovyMock(SpringApplication, global:true)
    }

    def "test main"() {
        when:
        JulietUserRestApp.main(null)

        then:
        1 * SpringApplication.run(JulietUserRestApp, null)
    }

}
