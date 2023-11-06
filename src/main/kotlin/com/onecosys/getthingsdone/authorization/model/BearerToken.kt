package com.onecosys.getthingsdone.authorization.model

import com.onecosys.getthingsdone.user.model.entity.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "bearer_token")
class BearerToken(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bearer_token_sequence")
    @SequenceGenerator(name = "bearer_token_sequence", sequenceName = "bearer_token_sequence", allocationSize = 1)
    val id: Long = 0,

    @Column(unique = true)
    var token: String? = null,

    var isRevoked: Boolean = false,

    var isExpired: Boolean = false,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null
)