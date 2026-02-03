package com.aguiar.expense_tracking2.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

import java.time.LocalDate

@Entity
class Expense (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var category: String,

    @Column(nullable = false)
    var subCategory: String,

    var description: String,

    @Column(nullable = false, precision = 19, scale = 2)
    var amount: BigDecimal,

    @Column(nullable = false)
    var date: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User

)