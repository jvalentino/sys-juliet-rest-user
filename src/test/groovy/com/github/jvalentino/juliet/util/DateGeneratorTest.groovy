package com.github.jvalentino.juliet.util

import com.github.jvalentino.juliet.util.DateGenerator
import spock.lang.Specification

class DateGeneratorTest extends Specification {

    def "Test DateGenerator"() {
        when:
        Date result = DateGenerator.date()

        then:
        result
    }
}
