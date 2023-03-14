package com.github.jvalentino.juliet.rest

import com.github.jvalentino.juliet.dto.CountDto
import com.github.jvalentino.juliet.dto.ListDto
import com.github.jvalentino.juliet.dto.ResultDto
import com.github.jvalentino.juliet.dto.UserDto
import com.github.jvalentino.juliet.entity.AuthUser
import com.github.jvalentino.juliet.service.UserService
import com.github.jvalentino.juliet.util.BaseIntg
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserRestIntgTest extends BaseIntg {

    @Autowired
    UserService userService

    void "test performCount"() {
        when:
        MvcResult response = mvc.perform(
                get("/user/count").header('X-Auth-Token', '123'))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        CountDto result = this.toObject(response, CountDto)
        result.value == 1L
    }

    void "test performCount when not authorized"() {
        when:
        MvcResult response = mvc.perform(
                get("/user/count"))
                .andDo(print())
                .andExpect(status().is(401))
                .andReturn()

        then:
        true
    }

    void "test login"() {
        given:
        AuthUser user = userService.saveNewUser(
                'test_email', 'first', 'last', 'test_password')

        UserDto input = new UserDto()
        input.with {
            email = user.email
            password = 'test_password'
        }

        when:
        MvcResult response = mvc.perform(
                post("/user/login")
                        .header('X-Auth-Token', '123')
                        .content(toJson(input))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        AuthUser result = this.toObject(response, AuthUser)
        result.email == user.email
    }

    void "test login invalid"() {
        given:
        UserDto input = new UserDto()
        input.with {
            email = 'test_email'
            password = 'test_password'
        }

        when:
        MvcResult response = mvc.perform(
                post("/user/login")
                        .header('X-Auth-Token', '123')
                        .content(toJson(input))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().is(401))
                .andReturn()

        then:
        true
    }

    void "Test listUsers"() {
        given:
        ListDto input = new ListDto(values:[1L])

        when:
        MvcResult response = mvc.perform(
                post("/user/list")
                        .header('X-Auth-Token', '123')
                        .content(toJson(input))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        List<AuthUser> results = this.toObject(response, List<AuthUser>)
        results.size() == 1
        results.first().email == 'admin'
    }

}
