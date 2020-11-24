package com.richards275.wareneingang.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank


@Entity
@Table(name = "lieferant")
class Lieferant @JvmOverloads constructor(

    @Column(name = "name")
    @NotBlank
    var name: String = "",

    @Column(name = "istaktiv")
    var istAktiv: Boolean = false,

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

)