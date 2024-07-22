package com.bankmultisearch;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bankmultisearch")
public interface BankMultisearchConfig extends Config
{
	@ConfigItem(
		keyName = "multiSearchEnabled",
		name = "Multisearch",
		description = "Search for all words in the search separately, e.g. search \"chisel sapphire\" to show all items that contain EITHER \"chisel\" or \"sapphire\".",
		position = 10
	)
	default boolean multiSearchEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "slotSearchEnabled",
		name = "Slot search",
		description = "Including a slot name in the search to only show items that fit in that slot. You can include multiple slots. Remember that new items may not show up because they have not been marked with a slot by the runelite wiki scraper.",
		position = 20
	)
	default boolean slotSearchEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "bla",
		name = "<html>Available slot search terms: leg, legs, chest, body, torso, glove, gloves, hand, hands, head, helm, hat, boot, boots, foot, feet, amulet, necklace, neck, ring, shield, offhand, cape, cloak, back, ammo, weapon, 1h, mainhand, 1hweapon, weaponslot1h, 2h, 2hweapon, weaponslot2h.</html>",
		description = "",
		position = 30
	)
	default void label1() {

	}

	@ConfigItem(
		keyName = "slotSearchRequireSlot",
		name = "Require \"slot\"",
		description = "e.g. you must type \"ringslot\" or \"slot:ring\" instead of \"ring\" to search for rings.",
		position = 40
	)
	default boolean slotSearchRequireSlot() {
		return false;
	}
}
