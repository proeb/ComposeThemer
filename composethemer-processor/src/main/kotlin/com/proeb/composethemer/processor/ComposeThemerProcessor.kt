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
import com.proeb.composethemer.processor.generator.ComponentThemeFileGenerator
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * Created by emre bahadir on 9/24/2023
 */
public class ComposeThemerProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val declarations = getDeclarationsAnnotatedWithComponentTheme(resolver)

        if (!declarations.iterator().hasNext()) return emptyList()

        declarations
            .filter { it.validate() && it.validateModifierContainsInterface(logger) }
            .forEach {
                processClassComponentTheme(it)
            }

        return emptyList()
    }

    private fun processClassComponentTheme(declaration: KSClassDeclaration) {
        logger.info("Processing ${declaration.simpleName.asString()}", declaration)

        val dependencySource = declaration.containingFile
        val sources = if (dependencySource != null) arrayOf(dependencySource) else arrayOf()

        val componentThemeFileSpec = generateProcessComponentTheme(declaration)
        componentThemeFileSpec.forEach {
            it.writeTo(
                codeGenerator = codeGenerator,
                dependencies = Dependencies(aggregating = true, sources = sources)
            )
        }
    }

    private fun generateProcessComponentTheme(declaration: KSClassDeclaration): List<FileSpec> {
        return ComponentThemeFileGenerator(
            declaration
        ).generate()
    }

    private fun getDeclarationsAnnotatedWithComponentTheme(resolver: Resolver): Sequence<KSClassDeclaration> =
        resolver.getSymbolsWithAnnotation(ComponentTheme::class.java.name)
            .filterIsInstance<KSClassDeclaration>()
            .distinct()
}
