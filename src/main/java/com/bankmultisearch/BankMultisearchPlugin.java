package com.bankmultisearch;

import com.google.gson.Gson;
import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.VarClientInt;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.bank.BankConfig;
import net.runelite.client.plugins.bank.BankSearch;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Bank Multisearch"
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
	@Inject public ConfigManager configManager;
	@Inject public Gson gson;

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Provides
	BankMultisearchConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankMultisearchConfig.class);
	}

	private final Map<String, Object> vars = new HashMap<>();

	@Subscribe
	public void onObject(Object o) {
		if (o instanceof ScriptCallbackEvent) {
			ScriptCallbackEvent e = (ScriptCallbackEvent) o;
			System.out.println("script callback event " + e.getEventName());
		}
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		int[] intStack = client.getIntStack();
		String[] stringStack = client.getStringStack();
		int intStackSize = client.getIntStackSize();
		int stringStackSize = client.getStringStackSize();

		if ("bankSearchFilter".equals(event.getEventName()))
		{
			int itemId = intStack[intStackSize - 1];
			String search = stringStack[stringStackSize - 1];

			// TODO check if value search is happening. Same for regex search maybe?
			String[] split = search.toLowerCase().split("\\s+");
			for (String s : split)
			{
				if (itemManager.getItemComposition(itemId).getName().toLowerCase().contains(s)) {
					// return true
					intStack[intStackSize - 2] = 1;
					break;
				}
			}
		}
	}
}
