package com.github.jvalentino.juliet.config

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Used for requiring a valid X-Auth-Token for every request.
 */
@Service
@Configurable
@CompileDynamic
@Slf4j
@SuppressWarnings(['UnnecessaryGetter'])
class SecurityFilter extends GenericFilterBean {

    @Value('${management.apikey}')
    String apikey

    @Value('${management.securePath}')
    String securePath

    void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        // pull the token out of the header
        HttpServletRequest httpRequest = (HttpServletRequest) request
        String token = httpRequest.getHeader('x-auth-token')
        String pathInfo = httpRequest.getRequestURI()

        if (!pathInfo.startsWith(securePath)) {
            log.info("${pathInfo} is not secured")
            chain.doFilter(request, response)
            return
        }

        if (token != apikey) {
            log.info("${token} token does not match the api key for ${pathInfo}")
            HttpServletResponse res = (HttpServletResponse) response
            res.status = 401
            return
        }

        chain.doFilter(request, response)
    }

}
