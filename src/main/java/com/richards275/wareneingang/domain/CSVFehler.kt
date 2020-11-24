package com.richards275.wareneingang.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*


@Entity
@Table(name = "csvfehler")
class CSVFehler @JvmOverloads constructor(
    @Column(name = "zeile")
    var zeile: Long,

    @Column(name = "fehlermeldung")
    var fehlermeldung: String,

    @Column(name = "feld")
    var feld: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    var lieferung: Lieferung? = null,

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
) : CSV(), AnzeigeFrontend {

  override fun toCsvUeberschrift(): List<String?>? {
    return listOf("Zeilennummer", "Fehlermeldung", "Feld mit Fehler")
  }

  override fun toCsvZeile(): List<String?>? {
    return listOf(this.zeile.toString(),
        this.fehlermeldung,
        this.feld
    )
  }
}