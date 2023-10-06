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

/**
 * Created by emre bahadir on 9/27/2023
 */
public object CTDefaultImports {
    public val COMPOSABLE: Pair<String, String> = Pair("androidx.compose.runtime", "Composable")

    public val DEFAULT_IMPORTS: List<Pair<String, String>> = listOf(
        Pair("androidx.compose.material3", "MaterialTheme"),
        Pair("androidx.compose.material3", "surfaceColorAtElevation"),
        Pair("androidx.compose.material3", "contentColorFor"),
        Pair("androidx.compose.material3", "LocalContentColor"),
        Pair("androidx.compose.ui.graphics", "Color"),
        Pair("androidx.compose.ui.graphics", "compositeOver"),
        Pair("androidx.compose.ui.unit", "DpSize"),
        Pair("androidx.compose.ui.unit", "dp"),
        Pair("androidx.compose.foundation.layout", "PaddingValues"),
        Pair("androidx.compose.foundation", "BorderStroke")
    )
}
