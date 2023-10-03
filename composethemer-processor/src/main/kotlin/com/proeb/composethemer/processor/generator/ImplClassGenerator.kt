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

package com.proeb.composethemer.processor.generator

import com.proeb.composethemer.processor.ksp.overrideAnnotations
import com.proeb.composethemer.processor.ksp.overrideModifiers
import com.proeb.composethemer.processor.ksp.overridePrimaryConstructor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName

/**
 * Created by emre bahadir on 9/24/2023
 */
internal class ImplClassGenerator(
    private val declaration: KSClassDeclaration,
    private val className: ClassName
) {

    fun generate(): TypeSpec {
        return when (declaration.classKind) {
            ClassKind.INTERFACE -> {
                buildComponentThemeImplClassTypeSpec()
            }
            else -> error("The class annotated with `ComponentTheme` must be a interface.")
        }
    }

    fun buildComponentThemeImplClassTypeSpec(): TypeSpec {
        val propertyGenerator = ImplClassConstructorPropertiesGenerator(
            declaration = declaration
        )

        val functionsGenerator = ImplClassFunctionsGenerator(
            declaration = declaration
        )

        return TypeSpec.classBuilder(className)
            .overridePrimaryConstructor(propertyGenerator.generate())
            .addThemeSpecs()
            .addSuperinterface(declaration.toClassName())
            .addFunctions(functionsGenerator.generate())
            .build()
    }

    private fun TypeSpec.Builder.addThemeSpecs(): TypeSpec.Builder = apply {
        overrideAnnotations(declaration)
        overrideModifiers(declaration)
        addKdoc("A theme default class ${declaration.classKind.type} by [${declaration.toClassName()}].")
    }
}
