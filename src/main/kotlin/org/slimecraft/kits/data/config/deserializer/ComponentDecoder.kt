package org.slimecraft.kits.data.config.deserializer

import com.sksamuel.hoplite.ConfigFailure
import com.sksamuel.hoplite.ConfigResult
import com.sksamuel.hoplite.DecoderContext
import com.sksamuel.hoplite.Node
import com.sksamuel.hoplite.StringNode
import com.sksamuel.hoplite.decoder.Decoder
import com.sksamuel.hoplite.fp.Validated
import com.sksamuel.hoplite.fp.invalid
import com.sksamuel.hoplite.fp.valid
import net.kyori.adventure.text.Component
import org.slimecraft.bedrock.kt.extensions.component
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class ComponentDecoder : Decoder<Component> {
    override fun supports(type: KType): Boolean {
        return type.classifier == Component::class
    }

    override fun decode(
        node: Node,
        type: KType,
        context: DecoderContext
    ): ConfigResult<Component> {
        if (node !is StringNode) return Validated.Invalid(ConfigFailure.Generic("$node is not a string node"))
        return Validated.Valid(node.value.component())
    }
}