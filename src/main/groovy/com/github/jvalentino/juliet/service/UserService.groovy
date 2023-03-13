package com.github.jvalentino.juliet.service

import com.github.jvalentino.juliet.entity.AuthUser
import com.github.jvalentino.juliet.repo.AuthUserRepo
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.apache.commons.codec.digest.B64
import org.apache.commons.codec.digest.Md5Crypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

/**
 * A general service used for dealing with users
 * @author john.valentino
 */
@CompileDynamic
@Service
@Slf4j
@SuppressWarnings([
        'UnnecessaryToString',
        'DuplicateStringLiteral',
        'ThrowException',
        'UnnecessarySetter',
        'UnnecessaryGetter',
])
class UserService {

    @Autowired
    AuthUserRepo authUserRepo

    protected UserService instance = this

    @PostConstruct
    void init() {
        log.info('Checking to see if we need to create a default admin user...')
        List<AuthUser> users = authUserRepo.findAdminUser('admin')

        if (users.size() != 0) {
            return
        }

        log.info('There are zero admin users, so we are going to now create one')

        String generatedPassword = UUID.randomUUID().toString()
        AuthUser user = instance.saveNewUser('admin', 'admin', 'admin', generatedPassword)

        log.info('===========================================================')
        log.info('New User Created')
        log.info("Email: ${user.email}")
        log.info("Password: ${generatedPassword}")
        // For example: 0421908e-2285-4142-93ed-b5c060e4fcc4
        log.info('===========================================================')
    }

    AuthUser saveNewUser(String email, String firstName, String lastName, String plaintextPassword) {
        String randomSalt = instance.generateSalt()

        AuthUser user = new AuthUser(email:email, firstName:firstName, lastName:lastName)
        user.with {
            salt = randomSalt
            password = Md5Crypt.md5Crypt(plaintextPassword.bytes, randomSalt)
        }

        authUserRepo.save(user)
    }

    AuthUser isValidUser(String email, String password) {
        log.info("Checking to see if ${email} is a valid user with its given password...")
        List<AuthUser> users = authUserRepo.findAdminUser(email)

        if (users.size() == 0) {
            log.info("${email} doesn't have any email matches, so not valid")
            return null
        }

        AuthUser user = users.first()
        String expected = Md5Crypt.md5Crypt(password.bytes, user.salt)

        if (expected == user.password) {
            log.info("Email ${email} gave a password that matches the salted MD5 hash")
            return user
        }

        log.info("Email ${email} gave a passowrd that doesn't match the salted MD5 hash")
        null
    }

    List<AuthUser> findUsers(List<Long> ids) {
        authUserRepo.findAllById(ids)
    }

    int countCurrentUsers() {
        authUserRepo.count()
    }

    protected String generateSalt() {
        '$1$' + B64.getRandomSalt(8)
    }

}
