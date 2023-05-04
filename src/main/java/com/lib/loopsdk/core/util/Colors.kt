package com.lib.loopsdk.core.util

import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.data.remote.dto.InitializeDto

object Colors {
    private val brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
    var PRIMARY_BRAND_COLOR: String = brandTheme.themeColors.primary.hexCode
    var SECONDARY_BRAND_COLOR: String = brandTheme.themeColors.secondary.hexCode
    var FONT_COLOR: String = brandTheme.themeColors.fontIconColor.hexCode
}