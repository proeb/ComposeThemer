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

package com.proeb.composethemer.generator

import com.proeb.composethemer.generator.model.CTComponentTheme
import com.proeb.composethemer.generator.model.CTTheme
import com.proeb.composethemer.generator.util.CTDefaultImports
import com.proeb.composethemer.generator.util.getReturnValue
import com.google.gson.Gson
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Created by emre bahadir on 9/26/2023
 */
public open class ThemeGenerator : DefaultTask() {

    public companion object {
        public const val THEMES_CLASS_NAME: String = "Themes"

        public const val THEMES_PACKAGE: String = "com.composethemer"

        public const val SOURCE_DIR: String = "src"
        public const val GENERATE_DIR: String = "/generated"

        public const val THEME_BASE_NAME_POSTFIX: String = "Themes"
        public const val THEME_DEFAULTS_POSTFIX: String = "Defaults"
        public const val THEME_VALUES_PREFIX: String = "set"
        public const val THEME_VALUES_POSTFIX: String = "Values"

        public const val VALUES_PARAMETER_NAME: String = "theme"

        public const val RETURN_KEY: String = "return"
        public const val RETURN_TYPE: String = "%T"
        public const val TYPE_VALUE: String = "%L"

        public val composableAnnotation: ClassName = ClassName(CTDefaultImports.COMPOSABLE.first, CTDefaultImports.COMPOSABLE.second)
    }

    @TaskAction
    public fun generateThemeClasses() {
        println("Themes are generating...")
        val themeFile = FileSpec.builder(THEMES_PACKAGE, THEMES_CLASS_NAME)

        CTDefaultImports.DEFAULT_IMPORTS.forEach {
            themeFile.addImport(it.first, it.second)
        }

        project.fileTree(SOURCE_DIR).files.filter {
            it.name.equals("theme.json")
        }.forEach { file ->
            val gson = Gson()

            val componentTheme = gson.fromJson<CTComponentTheme>(file.readText(), CTComponentTheme::class.java)

            val componentName = componentTheme.componentName.orEmpty()
            val packageName = componentTheme.componentPackage.orEmpty()
            val themeClassName = componentTheme.themeName.orEmpty()
            val themeBaseName = "${componentName}$THEME_BASE_NAME_POSTFIX"

            themeFile.addImport(packageName, themeClassName)
            themeFile.addImport("$packageName.$themeClassName$THEME_DEFAULTS_POSTFIX", "$THEME_VALUES_PREFIX$themeClassName$THEME_VALUES_POSTFIX")

            val sealedClassBuilder = TypeSpec.classBuilder(themeBaseName)
                .addModifiers(KModifier.SEALED)
                .addProperty(
                    PropertySpec.builder(
                        VALUES_PARAMETER_NAME,
                        TypeVariableName(themeClassName)
                    )
                        .addAnnotation(
                            AnnotationSpec.builder(composableAnnotation)
                                .useSiteTarget(AnnotationSpec.UseSiteTarget.GET)
                                .build()
                        )
                        .addModifiers(KModifier.ABSTRACT)
                        .build()
                )

            componentTheme.themes?.forEach { themeMap ->
                val themeName = themeMap.keys.first()

                val theme = themeMap[themeName]
                val hasBaseTheme = !theme?.baseTheme.isNullOrEmpty()
                var returnType = ""
                val returnValues = arrayListOf<Any>()
                val addedKeys = arrayListOf<String>()

                theme?.properties?.forEach { property ->
                    val returnValue = getReturnValue(property)
                    if (returnValue.isNotEmpty()) {
                        val propertyKey = property.key
                        if (!propertyKey.isNullOrEmpty()) {
                            returnType += "$propertyKey = $TYPE_VALUE, "
                            returnValues.add(getReturnValue(property))
                            addedKeys.add(propertyKey)
                        }
                    }
                }

                if (hasBaseTheme) {
                    returnType = checkBaseThemeValues(
                        componentTheme,
                        theme,
                        returnType,
                        returnValues,
                        addedKeys
                    )
                }

                returnValues.lastOrNull()?.let {
                    returnType = returnType.removeSuffix(", ")
                }

                val objectBuilder = TypeSpec.objectBuilder(themeName)
                    .superclass(ClassName(THEMES_PACKAGE, themeBaseName))
                    .addProperty(
                        PropertySpec.builder(
                            VALUES_PARAMETER_NAME,
                            TypeVariableName(themeClassName)
                        )
                            .addModifiers(KModifier.OVERRIDE)
                            .getter(
                                FunSpec.getterBuilder()
                                    .addStatement(
                                        "$RETURN_KEY $RETURN_TYPE.$THEME_VALUES_PREFIX$themeClassName$THEME_VALUES_POSTFIX($returnType)",
                                        ClassName(packageName, "$themeClassName$THEME_DEFAULTS_POSTFIX"),
                                        *returnValues.toTypedArray()
                                    )
                                    .addAnnotation(composableAnnotation)
                                    .build()
                            )
                            .build()
                    )
                sealedClassBuilder.addType(objectBuilder.build())
            }

            themeFile.addType(sealedClassBuilder.build()).build()
        }

        themeFile.build().writeTo(File("${project.buildDir}$GENERATE_DIR"))
        println("Themes are generated...")
    }

    private fun checkBaseThemeValues(
        componentTheme: CTComponentTheme,
        theme: CTTheme?,
        returnType: String,
        returnValues: ArrayList<Any>,
        addedKeys: ArrayList<String>
    ): String {
        var returnTypeStr = returnType
        val baseThemeMap = componentTheme.themes?.firstOrNull { it.keys.first() == theme?.baseTheme }
        val baseTheme = baseThemeMap?.get(theme?.baseTheme)
        val hasBaseTheme = !baseTheme?.baseTheme.isNullOrEmpty()
        baseTheme?.properties?.forEach { property ->
            addedKeys.any { it == property.key }.let { hasProperty ->
                val returnValue = getReturnValue(property)
                if (hasProperty == false && returnValue.isNotEmpty()) {
                    val propertyKey = property.key
                    if (!propertyKey.isNullOrEmpty()) {
                        returnTypeStr += "$propertyKey = $TYPE_VALUE, "
                        returnValues.add(getReturnValue(property))
                        addedKeys.add(propertyKey)
                    }
                }
            }
        }

        if (hasBaseTheme) {
            return checkBaseThemeValues(
                componentTheme,
                baseTheme,
                returnTypeStr,
                returnValues,
                addedKeys
            )
        } else {
            return returnTypeStr
        }
    }
}
