package com.richards275.wareneingang.domain.dto

import com.richards275.wareneingang.domain.AnzeigeFrontend
import com.richards275.wareneingang.domain.Zustand

class WareDtoAnFrontend @JvmOverloads constructor(
    var name: String,
    var nummer: String,
    var menge: Int,
    var mengeeditiert: Int?,
    var bemerkung: String,
    var zustand: Zustand? = null
) : AnzeigeFrontend