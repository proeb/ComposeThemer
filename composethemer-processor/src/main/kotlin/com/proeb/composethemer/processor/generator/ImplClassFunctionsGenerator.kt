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

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.proeb.composethemer.processor.ksp.overrideAnnotations
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * Created by emre bahadir on 9/24/2023
 */
internal class ImplClassFunctionsGenerator(
    private val declaration: KSClassDeclaration
) {

    fun generate(): List<FunSpec> {
        val functions: MutableList<FunSpec> = mutableListOf()
        declaration.getDeclaredFunctions()
            .toSet()
            .map { it }
            .forEach { function ->
                function.returnType?.resolve()?.let { type ->
                    val functionSpec = FunSpec.builder(function.simpleName.asString())
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(type.toTypeName())
                        .addStatement(type.returnType, function.simpleName.asString())
                        .addAnnotations(function.overrideAnnotations())
                        .build()
                    functions.add(functionSpec)
                }
            }
        return functions
    }
}

private inline val KSType.returnType: String
    get() = if (declaration.simpleName.asString() == "State") {
        "return rememberUpdatedState(%N)"
    } else {
        "return %N"
    }
