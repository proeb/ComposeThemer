package com.proeb.composethemer.generator.model

import org.gradle.internal.impldep.com.google.gson.annotations.SerializedName

/**
 * Created by emre bahadir on 10/16/2023
 */
public data class CTTonalElevationColor(
    @SerializedName("tonalElevationType")
    val tonalElevationType: String? = null,

    @SerializedName("value")
    val backgroundColor: String? = null,

    @SerializedName("elevation")
    val elevation: Int? = null
)
