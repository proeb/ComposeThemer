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
import com.proeb.composethemer.processor.ksp.themeTypeName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toClassName

/**
 * Created by emre bahadir on 9/24/2023
 */
internal class DefaultObjectFunctionsGenerator(
    private val declaration: KSClassDeclaration,
    private val implClassName: ClassName
) {

    fun generate(): FunSpec {
        val funSpec = FunSpec.builder("set${declaration.simpleName.asString()}Values")
            .returns(declaration.toClassName())
            .addAnnotation(
                AnnotationSpec.builder(
                    ClassName("androidx.compose.runtime", "Composable")
                ).build()
            )

        var returnType = " return ${implClassName.simpleName}("
        val returnValues = arrayListOf<Any>()

        declaration.getDeclaredFunctions().forEach { function ->
            function.returnType?.resolve()?.let { type ->
                funSpec.addParameter(
                    function.simpleName.asString(),
                    type.themeTypeName
                )
                returnType += "%N, "
                returnValues.add(function.simpleName.asString())
            }
        }

        // remove last comma
        returnType = returnType.substring(0, returnType.length - 2)
        returnType += ")"

        funSpec.addStatement(returnType, *returnValues.toTypedArray())

        return funSpec.build()
    }
}
