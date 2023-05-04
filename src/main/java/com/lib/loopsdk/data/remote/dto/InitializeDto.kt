package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class InitializeDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("brandTheme")
        val brandTheme: BrandTheme,
        @SerializedName("defaultCurrency")
        val defaultCurrency: DefaultCurrency,
        @SerializedName("instanceId")
        val instanceId: String,
        @SerializedName("isCurrencyAdded")
        val isCurrencyAdded: Int,
        @SerializedName("isReferalAdded")
        val isReferalAdded: Int,
        @SerializedName("isSignupConsentRequired")
        val isSignupConsentRequired: Int,
        @SerializedName("isThemeAdded")
        val isThemeAdded: Int,
        @SerializedName("isTierAdded")
        val isTierAdded: Int,
        @SerializedName("pointsCreditTime")
        val pointsCreditTime: Int,
        @SerializedName("pointsIdentifier")
        val pointsIdentifier: PointsIdentifier,
        @SerializedName("primaryUserId")
        val primaryUserId: Int
    ) {
        data class BrandTheme(
            @SerializedName("launcherDesktop")
            val launcherDesktop: LauncherDesktop,
            @SerializedName("launcherDesktopIcon")
            val launcherDesktopIcon: String,
            @SerializedName("launcherMobile")
            val launcherMobile: LauncherMobile,
            @SerializedName("launcherMobileIcon")
            val launcherMobileIcon: String,
            @SerializedName("loyaltyProgramName")
            val loyaltyProgramName: String,
            @SerializedName("panelSpacing")
            val panelSpacing: PanelSpacing,
            @SerializedName("themeButtons")
            val themeButtons: ThemeButtons,
            @SerializedName("themeColors")
            val themeColors: ThemeColors,
            @SerializedName("themeFont")
            val themeFont: ThemeFont,
            @SerializedName("themeShapes")
            val themeShapes: ThemeShapes
        ) {
            data class LauncherDesktop(
                @SerializedName("defaultIconId")
                val defaultIconId: String,
                @SerializedName("display")
                val display: Display,
                @SerializedName("isIconDefault")
                val isIconDefault: Int,
                @SerializedName("label")
                val label: String,
                @SerializedName("placement")
                val placement: Placement
            ) {
                data class Display(
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("label")
                    val label: String
                )

                data class Placement(
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("label")
                    val label: String
                )
            }

            data class LauncherMobile(
                @SerializedName("defaultIconId")
                val defaultIconId: String,
                @SerializedName("display")
                val display: Display,
                @SerializedName("isIconDefault")
                val isIconDefault: Int,
                @SerializedName("label")
                val label: String,
                @SerializedName("placement")
                val placement: Placement
            ) {
                data class Display(
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("label")
                    val label: String
                )

                data class Placement(
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("label")
                    val label: String
                )
            }

            data class PanelSpacing(
                @SerializedName("bottom")
                val bottom: String,
                @SerializedName("side")
                val side: String,
                @SerializedName("unit")
                val unit: String
            )

            data class ThemeButtons(
                @SerializedName("id")
                val id: Int,
                @SerializedName("style")
                val style: String
            )

            data class ThemeColors(
                @SerializedName("fontIconColor")
                val fontIconColor: FontIconColor,
                @SerializedName("primary")
                val primary: Primary,
                @SerializedName("secondary")
                val secondary: Secondary
            ) {
                data class FontIconColor(
                    @SerializedName("hexCode")
                    val hexCode: String
                )

                data class Primary(
                    @SerializedName("hexCode")
                    val hexCode: String
                )

                data class Secondary(
                    @SerializedName("hexCode")
                    val hexCode: String
                )
            }

            data class ThemeFont(
                @SerializedName("id")
                val id: Int,
                @SerializedName("style")
                val style: String
            )

            data class ThemeShapes(
                @SerializedName("buttons")
                val buttons: Buttons,
                @SerializedName("container")
                val container: Container
            ) {
                data class Buttons(
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("style")
                    val style: String
                )

                data class Container(
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("style")
                    val style: String
                )
            }
        }

        data class DefaultCurrency(
            @SerializedName("code")
            val code: String,
            @SerializedName("flag")
            val flag: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("name_plural")
            val namePlural: String,
            @SerializedName("symbol")
            val symbol: String
        )

        data class PointsIdentifier(
            @SerializedName("pointsLabelPlural")
            val pointsLabelPlural: String,
            @SerializedName("pointsLabelSingular")
            val pointsLabelSingular: String,
            @SerializedName("currencyIconPng")
            val currencyIconPng: String,
            @SerializedName("currencyIconSvg")
            val currencyIconSvg: String,
            @SerializedName("currencyIcon")
            val currencyIcon: String,
            @SerializedName("isDefaultCurrencyIcon")
            val isDefaultCurrencyIcon: Int,
            @SerializedName("currencyRatio")
            val currencyRatio: Float,

        )
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}