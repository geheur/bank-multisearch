package com.bankmultisearch;

import com.google.gson.Gson;
import com.google.inject.Provides;
import java.util.*;
import java.util.function.Consumer;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.EventBus.Subscriber;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.http.api.item.ItemStats;

@Slf4j
@PluginDescriptor(
	name = "Bank search utilities",
	tags = {"slot"}
)
public class BankMultisearchPlugin extends Plugin
{
	@Inject private Client client;
	@Inject private BankMultisearchConfig config;
	@Inject private ItemManager itemManager;
	@Inject private ClientThread clientThread;
	@Inject private KeyManager keyManager;
	@Inject private OverlayManager overlayManager;
	@Inject private MouseManager mouseManager;
	@Inject private ConfigManager configManager;
	@Inject private Gson gson;
	@Inject private EventBus eventBus;

	@Override
	protected void startUp()
	{
		updateSubscribers();
	}

	@Override
	protected void shutDown()
	{
		removeSubscribers();
	}

	@Provides
	BankMultisearchConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankMultisearchConfig.class);
	}

	private final List<Subscriber> subscribers = new ArrayList<>();

	@Subscribe
	public void onGameTick(GameTick e) {
		updateSubscribers();
	}

	private void updateSubscribers() {
		removeSubscribers();

		register(GameTick.class, e -> {
//			System.out.println("in subscriber");
		});
	}

	private <T> void register(Class<T> event, Consumer<T> eventHandler) {
		register(event, eventHandler, 1f);
	}

	private <T> void register(Class<T> event, Consumer<T> eventHandler, float priority) {
		Subscriber subscriber = eventBus.register(event, eventHandler, priority);
		subscribers.add(subscriber);
	}

	private void removeSubscribers() {
		for (Subscriber subscriber : subscribers)
		{
			eventBus.unregister(subscriber);
		}
		subscribers.clear();
	}

	private final Map<String, Object> vars = new HashMap<>();

	@Subscribe
	public void onCommandExecuted(CommandExecuted e) {
		if ("update".equals(e.getCommand())) {

		}
	}

	@Subscribe(priority = -1)
	public void onScriptCallbackEvent(ScriptCallbackEvent e) {
		if (!"bankSearchFilter".equals(e.getEventName())) return;

		int[] intStack = client.getIntStack();
		String[] stringStack = client.getStringStack();
		int intStackSize = client.getIntStackSize();
		int stringStackSize = client.getStringStackSize();

		String search = stringStack[stringStackSize - 1];
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
			intStack[intStackSize - 2] = 0;
			String itemName = itemManager.getItemComposition(itemId).getName().toLowerCase();
			for (String s : split)
			{
				if (itemName.contains(s))
				{
					// return true
					intStack[intStackSize - 2] = 1;
					break;
				}
			}
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
				if (s.endsWith("slot"))
				{
					s = s.substring(0, s.length() - "slot".length());
					System.out.println(s);
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
		System.out.println("slots bitfield " + slotsBitfield + " split " + split);
	}
}
