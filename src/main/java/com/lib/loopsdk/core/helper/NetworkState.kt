package com.lib.loopsdk.core.helper

enum class NetworkState(val isConnected : Boolean) {

  CONNECTED(true),

  DISCONNECTED(false),

  UNINITIALIZED(false)

}