package com.aguiar.expense_tracking2.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var email: String,

    @Column (nullable = false)
    var password: String

)