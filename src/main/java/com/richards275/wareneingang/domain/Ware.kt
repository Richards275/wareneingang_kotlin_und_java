package com.richards275.wareneingang.domain


import com.fasterxml.jackson.annotation.JsonIgnore
import com.richards275.wareneingang.domain.dto.WareDtoAnFrontend
import com.richards275.wareneingang.domain.dto.WareDtoVonFrontend
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.Digits

@Entity
@Table(name = "ware",
    uniqueConstraints = [
      UniqueConstraint(
          columnNames = ["name", "lieferung_id", "nummer"]
      )
    ]
)
class Ware @JvmOverloads constructor(

    @Column(name = "name")
    var name: String,

    @Column(name = "nummer")
    var nummer: String,

    @Column(name = "menge")
    var menge: Int,


    @get:Digits(integer = 3, fraction = 2)
    @Column(name = "preis")
    var preis: BigDecimal,

    @Column(name = "bemerkung")
    var bemerkung: String,

    @JoinColumn(name = "lieferung_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    var lieferung: Lieferung? = null,

    @Column(name = "mengeeditiert")
    var mengeeditiert: Int? = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "zustand")
    var zustand: Zustand = Zustand.NEU,

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

) : CSV() {

  fun verschiebeAusBearbeitetInEingang(): Ware {
    if (this.lieferung?.darfBearbeitetWerden()!!) {
      bemerkung = ""
      mengeeditiert = 0
      zustand = Zustand.NEU
    }
    return this
  }

  // beim Umbau: WareDtoVonFrontend fields zwangsläufig public, da Lombok nicht erkannt
  fun editOderVerschiebeAusEingangWare(wareDtoVonFrontend: WareDtoVonFrontend): Ware {
    if (this.lieferung?.darfBearbeitetWerden()!!) {
      mengeeditiert = wareDtoVonFrontend.mengeeditiert
      zustand = wareDtoVonFrontend.zustand!!
      bemerkung = wareDtoVonFrontend.bemerkung
      if (wareDtoVonFrontend.zustand == Zustand.FEHLT) {
        mengeeditiert = 0
      }
    }
    return this
  }

  fun istInSpalteBearbeitet(): Boolean {
    return zustand != Zustand.NEU
  }

  // beim Umbau: zwangsläufig ohne Builder, da Lombok nicht erkannt
  // WareDtoAnFrontend fields zwangsläufig public, da Lombok nicht erkannt
  fun zuWareDtoAnFrontend(): WareDtoAnFrontend {
    return WareDtoAnFrontend(
        this.name,
        this.nummer,
        this.menge,
        this.mengeeditiert,
        this.bemerkung,
        this.zustand);
  }

  override fun toCsvUeberschrift(): List<String?>? {
    return listOf("Name", "gemeldete Menge", "gelieferte Menge", "Nummer", "Bemerkung")
  }

  override fun toCsvZeile(): List<String?>? {
    return listOf(
        this.name, this.menge.toString(),
        this.mengeeditiert.toString(), this.nummer,
        this.bemerkung)
  }

  // equal und hashCode() implementiert, damit auch im Set
  // beim Einlesen der csv als doppelt erkannt

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Ware) return false

    if (name != other.name) return false
    if (nummer != other.nummer) return false
    if (lieferung != other.lieferung) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + nummer.hashCode()
    result = 31 * result + lieferung.hashCode()
    return result
  }

  override fun toString(): String {
    return "Ware(name='$name', nummer='$nummer', menge=$menge, preis=$preis, bemerkung='$bemerkung', mengeeditiert=$mengeeditiert, zustand=$zustand, id=$id)"
  }

}