package com.github.jvalentino.juliet.service

import com.github.jvalentino.juliet.dto.LoginDto
import com.github.jvalentino.juliet.dto.ResultDto
import com.github.jvalentino.juliet.dto.UserDto
import com.github.jvalentino.juliet.entity.AuthUser
import com.github.jvalentino.juliet.repo.AuthUserRepo
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.http.HttpSession

class UserServiceTest extends Specification {

    @Subject
    UserService subject

    def setup() {
        subject = new UserService()
        subject.with {
            instance = Mock(UserService)
            authUserRepo = Mock(AuthUserRepo)
        }

    }

    def "Test init when already users"() {
        when:
        subject.init()

        then:
        1 * subject.authUserRepo.findAdminUser('admin') >> [new AuthUser()]
        0 * subject.instance.saveNewUser('admin', 'admin', 'admin', _)
    }

    def "Test init when no users"() {
        when:
        subject.init()

        then:
        1 * subject.authUserRepo.findAdminUser('admin') >> []
        1 * subject.instance.saveNewUser(_, _, _, _) >> { String email, String firstName, String lastName, String plaintextPassword ->
            assert email == 'admin'
            assert firstName == 'admin'
            assert lastName == 'admin'
            assert plaintextPassword != null
            new AuthUser()
        }
    }

    def "test saveNewUser"() {
        given:
        String email = 'a'
        String firstName = 'b'
        String lastName = 'c'
        String plaintextPassword = 'd'

        when:
        AuthUser result = subject.saveNewUser(email, firstName, lastName, plaintextPassword)

        then:
        1 * subject.instance.generateSalt() >> '$1$aaa'
        1 * subject.authUserRepo.save(_) >> { AuthUser user ->
            assert user.email == 'a'
            assert user.firstName == 'b'
            assert user.lastName == 'c'
            assert user.salt == '$1$aaa'
            assert user.password == '$1$aaa$.8XK7wl5ryfI3lNEHsEyu/'
            user
        }

        and:
        result.email == 'a'

    }

    def "test isValidUser when no email match"() {
        given:
        String email = 'a'
        String password = 'd'

        when:
        AuthUser result = subject.isValidUser(email, password)

        then:
        1 * subject.authUserRepo.findAdminUser(email) >> []

        and:
        result == null
    }

    def "test isValidUser when password match"() {
        given:
        String email = 'a'
        String password = 'd'

        and:
        AuthUser user = new AuthUser(salt:'$1$aaa', password:'$1$aaa$.8XK7wl5ryfI3lNEHsEyu/')

        when:
        AuthUser result = subject.isValidUser(email, password)

        then:
        1 * subject.authUserRepo.findAdminUser(email) >> [user]

        and:
        result == user
    }

    def "test isValidUser when no password match"() {
        given:
        String email = 'a'
        String password = 'foobar'

        and:
        AuthUser user = new AuthUser(salt:'$1$aaa', password:'$1$aaa$.8XK7wl5ryfI3lNEHsEyu/')

        when:
        AuthUser result = subject.isValidUser(email, password)

        then:
        1 * subject.authUserRepo.findAdminUser(email) >> [user]

        and:
        result == null
    }

}
