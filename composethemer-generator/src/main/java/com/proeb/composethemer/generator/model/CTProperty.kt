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

package com.proeb.composethemer.generator.model

import org.gradle.internal.impldep.com.google.gson.annotations.SerializedName

/**
 * Created by emre bahadir on 9/26/2023
 */
public data class CTProperty(
    @SerializedName("key")
    val key: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("color")
    val color: CTColor? = null,

    @SerializedName("textStyle")
    val textStyle: String? = null,

    @SerializedName("padding")
    val padding: CTPadding? = null,

    @SerializedName("border")
    val border: CTBorder? = null,

    @SerializedName("size")
    val size: CTDpSize? = null,

    @SerializedName("dp")
    val dp: Int? = null,

    @SerializedName("viewTheme")
    val viewTheme: CTViewTheme? = null
)
