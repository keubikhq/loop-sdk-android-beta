package com.lib.loopsdk.core.util

import com.jakewharton.rxrelay3.PublishRelay
import com.lib.loopsdk.data.remote.dto.InitializeDto

val RxBusOnboardingRelay by lazy { PublishRelay.create<Int>() }

val RxBusInitializeRelay by lazy { PublishRelay.create<String>() }

