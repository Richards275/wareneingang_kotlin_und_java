package com.richards275.wareneingang.domain.dto

import java.util.*

class CSVRequestBodyDto(
    var userId: Long? = null,
    var lieferantId: Long? = null,
    var lieferdatum: Date? = null,
    var bemerkung: String? = null
)