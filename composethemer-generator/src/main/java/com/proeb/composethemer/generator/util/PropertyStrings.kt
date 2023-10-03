/*
 * Designed and developed by 2023 proeb (Emre BAHADIR)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.proeb.composethemer.generator.util

import com.proeb.composethemer.generator.model.CTDpSize
import com.proeb.composethemer.generator.model.CTBorder
import com.proeb.composethemer.generator.model.CTColor
import com.proeb.composethemer.generator.model.CTColorType
import com.proeb.composethemer.generator.model.CTCompositeColor
import com.proeb.composethemer.generator.model.CTCompositeType
import com.proeb.composethemer.generator.model.CTPadding
import com.proeb.composethemer.generator.model.CTProperty
import com.proeb.composethemer.generator.model.CTPropertyType
import com.proeb.composethemer.generator.model.CTViewTheme

/**
 * Created by emre bahadir on 9/27/2023
 */
public fun getReturnValue(property: CTProperty): String = when (CTPropertyType.fromKey(property.type.orEmpty())) {
    CTPropertyType.Color -> getColorValue(property.color ?: getTransparentColor())
    CTPropertyType.TextStyle -> getTextStyle(property.textStyle.orEmpty())
    CTPropertyType.PaddingValues -> getPaddingValue(property.padding ?: CTPadding())
    CTPropertyType.BorderStroke -> getBorder(property.border ?: CTBorder(color = getTransparentColor()))
    CTPropertyType.DpSize -> getDpSizeValue(property.size ?: CTDpSize())
    CTPropertyType.Dp -> getDpValue(property.dp ?: 0)
    CTPropertyType.Theme -> getTheme(property.viewTheme)
    null -> ""
}

private fun getColorValue(color: CTColor) = if (!color.value.isNullOrEmpty()) {
    when (CTColorType.fromKey(color.colorType.orEmpty())) {
        CTColorType.MaterialColor -> "MaterialTheme.colorScheme.${color.value.orEmpty()}${getAlphaValue(color)}${getCompositeColorValue(color)}"
        CTColorType.MaterialContentColor -> "MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.${color.value.orEmpty()})${getAlphaValue(color)}${getCompositeColorValue(color)}"
        CTColorType.HexColor -> "Color(${color.value.orEmpty()})${getAlphaValue(color)}"
        null -> ""
    }
} else {
    ""
}

private fun getAlphaValue(color: CTColor) = if (color.alpha != null) {
    ".copy(alpha = ${color.alpha}f)"
} else {
    ""
}

private fun getCompositeColorValue(color: CTColor) = if (color.compositeColor != null) {
    ".compositeOver(${getCompositeColor(color.compositeColor)})"
} else {
    ""
}

private fun getCompositeColor(compositeColor: CTCompositeColor) = when (CTCompositeType.fromKey(compositeColor.compositeType.orEmpty())) {
    CTCompositeType.SurfaceColorAtElevation -> "MaterialTheme.colorScheme.surfaceColorAtElevation(${getElevationValue(compositeColor.elevation ?: 0)}.dp)"
    CTCompositeType.MaterialColor -> "MaterialTheme.colorScheme.${compositeColor.value.orEmpty()}"
    CTCompositeType.HexColor -> "Color.${compositeColor.value.orEmpty()}"
    null -> ""
}

private fun getElevationValue(elevationLevel: Int) = when (elevationLevel) {
    0 -> CTElevations.Level0
    1 -> CTElevations.Level1
    2 -> CTElevations.Level2
    3 -> CTElevations.Level3
    4 -> CTElevations.Level4
    5 -> CTElevations.Level5
    else -> 0
}

private fun getTextStyle(textStyle: String) = if (textStyle.isNotEmpty()) {
    "MaterialTheme.typography.${textStyle.orEmpty()}"
} else {
    ""
}

private fun getPaddingValue(padding: CTPadding) = "PaddingValues(start = ${padding.start ?: 0}.dp, end = ${padding.end ?: 0}.dp, top = ${padding.top ?: 0}.dp, bottom = ${padding.bottom ?: 0}.dp)"

private fun getBorder(border: CTBorder) = "BorderStroke(width = ${border.width ?: 0}.dp, color = ${getColorValue(border.color ?: CTColor())})"

private fun getDpSizeValue(size: CTDpSize) = "DpSize(width = ${size.width ?: 0}.dp, height = ${size.height ?: 0}.dp)"

private fun getDpValue(dp: Int) = "$dp.dp"

private fun getTheme(theme: CTViewTheme?) = if (theme != null) {
    "${theme.componentName.orEmpty()}Themes.${theme.themeName.orEmpty()}.theme"
} else {
    ""
}

private fun getTransparentColor() = CTColor(
    colorType = CTColorType.HexColor.key,
    value = "0x00000000",
    alpha = 0f
)

public fun getEmptyString(): String = ""
