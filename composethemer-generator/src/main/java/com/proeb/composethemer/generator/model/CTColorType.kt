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

/**
 * Created by emre bahadir on 9/26/2023
 */
public enum class CTColorType(public val key: String) {
    MaterialColor("MaterialColor"),
    MaterialContentColor("MaterialContentColor"),
    HexColor("HexColor"),
    LocalContentColor("LocalContentColor");

    public companion object {
        public fun fromKey(key: String): CTColorType? {
            return values().find { it.key == key }
        }
    }
}
