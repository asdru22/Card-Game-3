package com.asdru.cardgame3.data

import com.asdru.cardgame3.R

object ShopDataProvider {

    fun getShopItems(): List<ShopItem> {
        return listOf(
            ShopItem(
                name = "Potion",
                cost = 5,
                iconRes = R.drawable.icon_health,
                onApply = { entity ->
                    entity.health = (entity.health + 30f).coerceAtMost(entity.maxHealth)
                }
            ),
            ShopItem(
                name = "Steroids",
                cost = 10,
                iconRes = R.drawable.icon_health,
                onApply = { entity ->
                    entity.damage += 10f
                }
            ),
            ShopItem(
                name = "Shield",
                cost = 8,
                iconRes = R.drawable.icon_health,
                onApply = { entity ->
                    entity.maxHealth += 20f
                    entity.health += 20f
                }
            )
        )
    }
}