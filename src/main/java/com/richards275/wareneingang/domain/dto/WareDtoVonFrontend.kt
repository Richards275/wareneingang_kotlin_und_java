package com.richards275.wareneingang.domain.dto

import com.richards275.wareneingang.domain.Zustand

class WareDtoVonFrontend(
    var name: String = "",
    var nummer: String = "",
    var mengeeditiert: Int = 0,
    var bemerkung: String = "",
    var zustand: Zustand? = null,
    var lieferungId: Long = 0,
    lieferantId: Long = 0
) : BasisDto() {
}
