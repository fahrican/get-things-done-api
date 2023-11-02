package com.onecosys.getthingsdone.user.entity

import com.onecosys.getthingsdone.authentication.dto.VerificationToken
import com.onecosys.getthingsdone.authorization.model.Role
import com.onecosys.getthingsdone.authorization.model.Token
import com.onecosys.getthingsdone.task.model.entity.Task
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "_user")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    val id: Long = 0,

    @NotBlank
    var firstName: String = "",

    @NotBlank
    var lastName: String = "",

    @NotBlank
    var email: String = "",

    @NotBlank
    var _username: String = "",

    @NotBlank
    var _password: String = "",

    @NotNull
    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER,

    var isVerified: Boolean = false,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = true)
    private val verificationToken: VerificationToken? = null,


    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    private val tokens: List<Token>? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    private val tasks: List<Task>? = null
) : UserDetails {

    override fun getAuthorities(): List<GrantedAuthority> = listOf(SimpleGrantedAuthority(role.name))

    override fun getPassword() = _password

    override fun getUsername() = _username

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = isVerified
}
