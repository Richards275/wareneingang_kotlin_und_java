package com.richards275.wareneingang.domain

abstract class CSV {
  abstract fun toCsvUeberschrift(): List<String?>?
  abstract fun toCsvZeile(): List<String?>?
}