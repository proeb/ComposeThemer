package com.proeb.composethemer.generator.model

/**
 * Created by emre bahadir on 10/16/2023
 */
public enum class CTTonalElevationType(public val key: String) {
    MaterialColor("MaterialColor"),
    HexColor("HexColor");

    public companion object {
        public fun fromKey(key: String): CTTonalElevationType? {
            return values().find { it.key == key }
        }
    }
}
