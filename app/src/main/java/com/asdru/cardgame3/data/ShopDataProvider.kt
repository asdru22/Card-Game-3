package com.asdru.cardgame3.data

import com.asdru.cardgame3.R

object ShopDataProvider {

  fun getShopItems(): List<ShopItem> {
    return listOf(
      ShopItem(
        id = "potion_small",
        nameRes = R.string.game_forsaken, // Value: "Health Potion"
        // Value: "Drink to receive %1$s."
        descriptionRes = R.string.game_forsaken,
        cost = 5,
        iconRes = R.drawable.icon_health,
        formatArgs = listOf(
          // This creates the clickable [[Heal]] text
          EffectPlaceholder(
            name = "Heal",
            description = "Restores 30 Health instantly.",
            isPositive = true
          )
        ),
        onApply = { entity ->
          entity.health = (entity.health + 30f).coerceAtMost(entity.maxHealth)
        }
      ),
      ShopItem(
        id = "steroids",
        nameRes = R.string.game_forsaken, // Value: "Steroids"
        // Value: "Permanently grants %1$s."
        descriptionRes = R.string.game_forsaken,
        cost = 10,
        iconRes = R.drawable.icon_attack_damage, // Changed icon to match effect
        formatArgs = listOf(
          EffectPlaceholder(
            name = "Strength",
            description = "Increases Damage by 10.",
            isPositive = true
          )
        ),
        onApply = { entity ->
          entity.damage += 10f
        }
      ),
      ShopItem(
        id = "shield_upgrade",
        nameRes = R.string.game_forsaken, // Value: "Iron Plating"
        // Value: "Grants %1$s and restores HP."
        descriptionRes = R.string.game_forsaken,
        cost = 8,
        iconRes = R.drawable.icon_health,
        formatArgs = listOf(
          EffectPlaceholder(
            name = "Vitality",
            description = "Increases Max Health by 20.",
            isPositive = true
          )
        ),
        onApply = { entity ->
          entity.maxHealth += 20f
          entity.health += 20f
        }
      )
    )
  }
}