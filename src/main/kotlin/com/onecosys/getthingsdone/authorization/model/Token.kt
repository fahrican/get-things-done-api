package com.onecosys.getthingsdone.authorization.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "token")
class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_sequence")
    @SequenceGenerator(name = "token_sequence", sequenceName = "token_sequence", allocationSize = 1)
    val id: Long = 0


    @Column(unique = true)
    var token: String? = null

    @Enumerated(EnumType.STRING)
    var tokenType: TokenType = TokenType.BEARER

    var revoked = false

    var expired = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null
}