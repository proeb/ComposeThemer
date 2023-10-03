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

package com.proeb.composethemer.processor

import com.proeb.composethemer.core.annotation.ComponentTheme
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration

/**
 * Created by emre bahadir on 9/24/2023
 */
internal fun KSClassDeclaration.validateModifierContainsInterface(logger: KSPLogger): Boolean {
    // check classKind is interface
    if (classKind != ClassKind.INTERFACE) {
        logger.error(
            "${ComponentTheme::class.simpleName} can't be attached to ${classKind.type}. " +
                "You can only attach to the interface "
        )
        return false
    }
    return true
}

public fun KSPLogger.anothers() {
}
