package com.github.jvalentino.juliet.repo

import com.github.jvalentino.juliet.entity.AuthUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * Repository interface for the AuthUser entity
 * @author john.valentino
 */
interface AuthUserRepo extends JpaRepository<AuthUser, Long> {

    @Query('select distinct u from AuthUser u where u.email = ?1')
    List<AuthUser> findAdminUser(String email)

}
