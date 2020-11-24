package com.richards275.wareneingang.domain

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "lieferung")
class Lieferung @JvmOverloads constructor(

    @OneToOne
    var lieferant: Lieferant? = null,

    @Column(name = "lieferdatum")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    var lieferDatum: LocalDate?,

    @Column(name = "bemerkung")
    var bemerkung: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "lieferungsstatus")
    var lieferungsStatus: LieferungsStatus?,

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "lieferung")
    var wareSet: MutableSet<Ware> = HashSet(),

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "lieferung")
    var csvFehlerList: MutableList<CSVFehler>? = mutableListOf<CSVFehler>(),

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    ) {

  constructor(lieferDate: Date, lieferant: Lieferant?,
              bemerkung: String?, status: LieferungsStatus) : this(
      lieferant,
      lieferDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
      bemerkung, status)

  fun addWare(ware: Ware): Lieferung {
    ware.lieferung = this
    wareSet.add(ware)
    return this
  }

  fun darfBearbeitetWerden(): Boolean {
    return setOf(LieferungsStatus.INBEARBEITUNG, LieferungsStatus.BESTAETIGT)
        .contains(lieferungsStatus)
  }

  fun getBearbeiteteWareSet() {
    wareSet.filter { obj: Ware -> obj.istInSpalteBearbeitet() }
  }


}