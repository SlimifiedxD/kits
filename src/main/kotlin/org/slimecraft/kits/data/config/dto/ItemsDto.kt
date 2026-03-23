package org.slimecraft.kits.data.config.dto

data class ItemsDto(val minItems: Int = 1, val maxItems: Int = 1, val items: List<ItemDto>)