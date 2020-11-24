package com.richards275.wareneingang.utils

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority


class TestAuthentication(
    var auth: MutableCollection<out GrantedAuthority>,
    var prince: Any

) : Authentication {

  override fun getName(): String {
    TODO("Not yet implemented")
  }

  override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
    return auth
  }

  override fun getCredentials(): Any {
    TODO("Not yet implemented")
  }

  override fun getDetails(): Any {
    TODO("Not yet implemented")
  }

  override fun getPrincipal(): Any {
    return prince
  }

  override fun isAuthenticated(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setAuthenticated(isAuthenticated: Boolean) {
    TODO("Not yet implemented")
  }
}