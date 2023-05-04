package com.lib.loopsdk.ui.feature_landing_home.presentation

data class Game(
    val gameName: String,
    val isEnabled: Int,
    val isUnlockGameEnabled: Int,
    val pointsStartAt: Int,
    val resume: Int = -1
)
