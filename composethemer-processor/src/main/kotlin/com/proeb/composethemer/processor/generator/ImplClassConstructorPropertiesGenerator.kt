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

import com.proeb.composethemer.processor.ksp.themeTypeName
import com.proeb.composethemer.processor.model.ConstructorProperty
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec

/**
 * Created by emre bahadir on 9/24/2023
 */
internal class ImplClassConstructorPropertiesGenerator(
    private val declaration: KSClassDeclaration
) {

    private val functions = declaration.getDeclaredFunctions()
        .toSet()
        .map { it }

    fun generate(): List<ConstructorProperty> {
        val constructorProperties: MutableList<ConstructorProperty> = mutableListOf()
        functions.forEach { function ->
            function.returnType?.resolve()?.let { type ->
                val parameterSpec = ParameterSpec.Companion.builder(
                    name = function.simpleName.asString(),
                    type = type.themeTypeName
                ).build()

                val property = PropertySpec.Companion.builder(
                    parameterSpec.name,
                    parameterSpec.type,
                    KModifier.PRIVATE
                )
                    .initializer("%N", parameterSpec)
                    .mutable(false)
                    .build()

                val constructorProperty = ConstructorProperty(parameterSpec, property)
                constructorProperties.add(constructorProperty)
            }
        }
        return constructorProperties
    }
}
