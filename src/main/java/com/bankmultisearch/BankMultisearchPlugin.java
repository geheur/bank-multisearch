package com.bankmultisearch;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.http.api.item.ItemStats;

@Slf4j
@PluginDescriptor(
	name = "Bank slot/multisearch",
	tags = {"search", "utility", "utilties"}
)
public class BankMultisearchPlugin extends Plugin
{
	@Inject private Client client;
	@Inject private BankMultisearchConfig config;
	@Inject private ItemManager itemManager;
	@Inject private ConfigManager configManager;

	@Subscribe(priority = -1)
	public void onScriptCallbackEvent(ScriptCallbackEvent e) {
		if (!"bankSearchFilter".equals(e.getEventName())) return;

		int[] intStack = client.getIntStack();
		String[] stringStack = client.getStringStack();
		int intStackSize = client.getIntStackSize();
		int stringStackSize = client.getStringStackSize();

		String search = stringStack[stringStackSize - 1];
		if (search.isEmpty()) return;
		updateSearch(search);

		int itemId = intStack[intStackSize - 1];
		if (slotsBitfield != 0) { // any slot filters exist.
			ItemStats itemStats = itemManager.getItemStats(itemId, false);
			boolean wrongSlot =
				itemStats == null ||
				!itemStats.isEquipable() ||
				(slotsBitfield & 1 << itemStats.getEquipment().getSlot()) == 0 ||
				(
					itemStats.getEquipment().getSlot() == EquipmentInventorySlot.WEAPON.getSlotIdx() &&
					twohand != null &&
					itemStats.getEquipment().isTwoHanded() != twohand
				)
			;
			intStack[intStackSize - 2] = wrongSlot ? 0 : 1;
			if (wrongSlot) return; // Filtered out items should not be included in the later search.
		}

		if (split != null)
		{
			String itemName = itemManager.getItemComposition(itemId).getName().toLowerCase();
			for (String s : split)
			{
				if (itemName.contains(s))
				{
					// return true
					intStack[intStackSize - 2] = 1;
					return;
				}
			}
			if (slotsBitfield != 0) intStack[intStackSize - 2] = 0; // remove anything we added from slot search that doesn't match.
		}
	}

	private String lastSearch = "";
	private List<String> split = Collections.emptyList();
	private int slotsBitfield = 0;
	private Boolean twohand = null;

	private void updateSearch(String search) {
		// use old values if possible.
		if (search.equals(lastSearch)) return;
		lastSearch = search;

		split = null;
		slotsBitfield = 0;
		twohand = null;

		// detect bank value searching, we don't want to search for "ge" or "ha" accidentally. <>= are not used in any item names so this should not interfere with people's use of the plugin.
		if (search.matches(".*[<>=].*")) {
			return;
		}

		split = new ArrayList<>(Arrays.asList(search.toLowerCase().split("\\s+")));

		// find and record any slot filters and remove them from the search terms.
		if (config.slotSearchEnabled())
		{
			for (int i = 0; i < split.size(); i++)
			{
				String s = split.get(i);
				if (s.startsWith("slot:")) {
					s = s.substring("slot:".length());
				} else if (s.endsWith("slot")) {
					s = s.substring(0, s.length() - "slot".length());
				} else {
					if (config.slotSearchRequireSlot()) continue;
				}
				boolean found = true;
				switch (s)
				{
					case "leg":
					case "legs":
						slotsBitfield |= 1 << EquipmentInventorySlot.LEGS.getSlotIdx();
						break;
					case "chest":
					case "body":
					case "torso":
						slotsBitfield |= 1 << EquipmentInventorySlot.BODY.getSlotIdx();
						break;
					case "glove":
					case "gloves":
					case "hand":
					case "hands":
						slotsBitfield |= 1 << EquipmentInventorySlot.GLOVES.getSlotIdx();
						break;
					case "head":
					case "helm":
					case "hat":
						slotsBitfield |= 1 << EquipmentInventorySlot.HEAD.getSlotIdx();
						break;
					case "boot":
					case "boots":
					case "foot":
					case "feet":
						slotsBitfield |= 1 << EquipmentInventorySlot.BOOTS.getSlotIdx();
						break;
					case "amulet":
					case "necklace":
					case "neck":
						slotsBitfield |= 1 << EquipmentInventorySlot.AMULET.getSlotIdx();
						break;
					case "ring":
						slotsBitfield |= 1 << EquipmentInventorySlot.RING.getSlotIdx();
						break;
					case "shield":
					case "offhand":
						slotsBitfield |= 1 << EquipmentInventorySlot.SHIELD.getSlotIdx();
						break;
					case "cape":
					case "cloak":
					case "back":
						slotsBitfield |= 1 << EquipmentInventorySlot.CAPE.getSlotIdx();
						break;
					case "ammo":
						slotsBitfield |= 1 << EquipmentInventorySlot.AMMO.getSlotIdx();
						break;
					case "weapon":
						slotsBitfield |= 1 << EquipmentInventorySlot.WEAPON.getSlotIdx();
						break;
					case "1h":
					case "mainhand":
					case "1hweapon":
					case "weaponslot1h":
						slotsBitfield |= 1 << EquipmentInventorySlot.WEAPON.getSlotIdx();
						twohand = Boolean.FALSE;
						break;
					case "2h":
					case "2hweapon":
					case "weaponslot2h":
						slotsBitfield |= 1 << EquipmentInventorySlot.WEAPON.getSlotIdx();
						twohand = Boolean.TRUE;
						break;
					default:
						found = false;
				}
				if (found)
				{
					split.remove(i);
					i--;
				}
			}
		}

		// disable multisearch if multisearch is disabled
		// do not disable multisearch if slot search is in use. This is convenient for queries that contain both a slot filter and other search text.
		// disable multisearch when there are no search terms left. This is necessary for queries that only contain slot filters.
		if (
			!config.multiSearchEnabled() && slotsBitfield == 0 ||
			split.size() == 0
		) {
			split = null;
		}
	}

	@Override
	public void startUp() {
		migrate();
	}

	@Subscribe
	public void onProfileChanged(ProfileChanged e) {
		migrate();
	}

	public void migrate() {
		configManager.setConfiguration("bankmultisearch", "serialVersion", 0);
	}

	@Provides
	BankMultisearchConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankMultisearchConfig.class);
	}
}
