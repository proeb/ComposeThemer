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

package com.proeb.composethemer.processor.ksp

import com.proeb.composethemer.core.annotation.ComponentTheme
import com.proeb.composethemer.processor.model.ConstructorProperty
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * Created by emre bahadir on 9/24/2023
 */
internal fun TypeSpec.Builder.overrideModifiers(declaration: KSClassDeclaration): TypeSpec.Builder =
    apply {
        addModifiers(declaration.modifiers.mapNotNull { it.toKModifier() })
    }

internal fun TypeSpec.Builder.overridePrimaryConstructor(
    propertyList: List<ConstructorProperty>
): TypeSpec.Builder = apply {
    primaryConstructor(
        FunSpec.constructorBuilder()
            .addParameters(propertyList.map { it.parameterSpec })
            .build()
    )
    addProperties(propertyList.map { it.propertySpec })
}

internal val KSType.themeTypeName: TypeName
    get() = if (declaration.simpleName.asString() == "State") {
        arguments.firstOrNull()?.toTypeName()?.let { it } ?: toTypeName()
    } else {
        toTypeName()
    }

internal fun TypeSpec.Builder.overrideAnnotations(declaration: KSClassDeclaration): TypeSpec.Builder =
    apply {
        declaration.annotations
            .filter {
                it.shortName.asString() != ComponentTheme::class.simpleName
            }
            .forEach {
                if (it.shortName.asString() == "OptIn") {
                    val annotationBuiler = AnnotationSpec.builder(
                        ClassName("kotlin", "OptIn")
                    )

                    it.arguments.forEach { argument ->
                        val member = CodeBlock.builder()
                        addValueToBlock(argument.value!!, member)
                        annotationBuiler.addMember(member.build())
                    }

                    addAnnotation(annotationBuiler.build())
                } else {
                    addAnnotation(it.toAnnotationSpec())
                }
            }
    }

internal fun KSFunctionDeclaration.overrideAnnotations(): List<AnnotationSpec> {
    return annotations
        .map {
            if (it.shortName.asString() == "OptIn") {
                val annotationBuiler = AnnotationSpec.builder(
                    ClassName("kotlin", "OptIn")
                )

                it.arguments.forEach { argument ->
                    val member = CodeBlock.builder()
                    addValueToBlock(argument.value!!, member)
                    annotationBuiler.addMember(member.build())
                }
                annotationBuiler.build()
            } else {
                it.toAnnotationSpec()
            }
        }.toList()
}

private fun addValueToBlock(value: Any, member: CodeBlock.Builder) {
    when (value) {
        is List<*> -> {
            // Array type
            value.forEachIndexed { index, innerValue ->
                if (index > 0) member.add(", ")
                addValueToBlock(innerValue!!, member)
            }
        }
        is KSType -> {
            val unwrapped = value.unwrapTypeAlias()
            val isEnum = (unwrapped.declaration as KSClassDeclaration).classKind == ClassKind.ENUM_ENTRY
            if (isEnum) {
                val parent = unwrapped.declaration.parentDeclaration as KSClassDeclaration
                val entry = unwrapped.declaration.simpleName.getShortName()
                member.add("%T.%L", parent.toClassName(), entry)
            } else {
                member.add("%T::class", unwrapped.toClassName())
            }
        }
        is KSName ->
            member.add(
                "%T.%L",
                ClassName.bestGuess(value.getQualifier()),
                value.getShortName()
            )
        is KSAnnotation -> member.add("%L", value.toAnnotationSpec())
        else -> member.add(memberForValue(value))
    }
}

internal fun KSType.unwrapTypeAlias(): KSType {
    return if (this.declaration is KSTypeAlias) {
        (this.declaration as KSTypeAlias).type.resolve()
    } else {
        this
    }
}

internal fun memberForValue(value: Any) = when (value) {
    is Class<*> -> CodeBlock.of("%T::class", value)
    is Enum<*> -> CodeBlock.of("%T.%L", value.javaClass, value.name)
    is String -> CodeBlock.of("%S", value)
    is Float -> CodeBlock.of("%Lf", value)
    is Double -> CodeBlock.of("%L", value)
    is Char -> CodeBlock.of("$value.toChar()")
    is Byte -> CodeBlock.of("$value.toByte()")
    is Short -> CodeBlock.of("$value.toShort()")
    // Int or Boolean
    else -> CodeBlock.of("%L", value)
}
