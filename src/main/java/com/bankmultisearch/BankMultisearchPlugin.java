package com.bankmultisearch;

import com.google.gson.Gson;
import com.google.inject.Provides;
import java.util.*;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
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
	@Inject private ConfigManager configManager;
	@Inject private Gson gson;
	@Inject private EventBus eventBus;

	@Override
	protected void startUp()
	{
	}

	@Override
	protected void shutDown()
	{
	}

	@Provides
	BankMultisearchConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankMultisearchConfig.class);
	}

	private final Map<String, Object> vars = new HashMap<>();

	@Subscribe
	public void onAccountHashChanged(AccountHashChanged e) {
	}

	@Subscribe
	public void onActorDeath(ActorDeath e) {
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged e) {
	}

	@Subscribe
	public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed e) {
	}

	@Subscribe
	public void onBeforeMenuRender(BeforeMenuRender e) {
	}

	@Subscribe
	public void onBeforeRender(BeforeRender e) {
	}

	@Subscribe
	public void onCanvasSizeChanged(CanvasSizeChanged e) {
	}

	@Subscribe
	public void onChatMessage(ChatMessage e) {
	}

	@Subscribe
	public void onClanChannelChanged(ClanChannelChanged e) {
	}

	@Subscribe
	public void onClanMemberJoined(ClanMemberJoined e) {
	}

	@Subscribe
	public void onClanMemberLeft(ClanMemberLeft e) {
	}

	@Subscribe
	public void onClientTick(ClientTick e) {
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted e) {
		if ("update".equals(e.getCommand())) {
		}
	}

	@Subscribe
	public void onDecorativeObjectDespawned(DecorativeObjectDespawned e) {
	}

	@Subscribe
	public void onDecorativeObjectSpawned(DecorativeObjectSpawned e) {
	}

	@Subscribe
	public void onDraggingWidgetChanged(DraggingWidgetChanged e) {
	}

	@Subscribe
	public void onFakeXpDrop(FakeXpDrop e) {
	}

	@Subscribe
	public void onFocusChanged(FocusChanged e) {
	}

	@Subscribe
	public void onFriendsChatChanged(FriendsChatChanged e) {
	}

	@Subscribe
	public void onFriendsChatMemberJoined(FriendsChatMemberJoined e) {
	}

	@Subscribe
	public void onFriendsChatMemberLeft(FriendsChatMemberLeft e) {
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned e) {
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned e) {
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e) {
	}

	@Subscribe
	public void onGameTick(GameTick e) {
	}

	@Subscribe
	public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged e) {
	}

	@Subscribe
	public void onGrandExchangeSearched(GrandExchangeSearched e) {
	}

	@Subscribe
	public void onGraphicChanged(GraphicChanged e) {
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated e) {
	}

	@Subscribe
	public void onGroundObjectDespawned(GroundObjectDespawned e) {
	}

	@Subscribe
	public void onGroundObjectSpawned(GroundObjectSpawned e) {
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied e) {
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged e) {
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged e) {
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned e) {
	}

	@Subscribe
	public void onItemQuantityChanged(ItemQuantityChanged e) {
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned e) {
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded e) {
	}

	@Subscribe
	public void onMenuOpened(MenuOpened e) {
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked e) {
	}

	@Subscribe
	public void onMenuShouldLeftClick(MenuShouldLeftClick e) {
	}

	@Subscribe
	public void onNameableNameChanged(NameableNameChanged e) {
	}

	@Subscribe
	public void onNpcChanged(NpcChanged e) {
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned e) {
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned e) {
	}

	@Subscribe
	public void onOverheadTextChanged(OverheadTextChanged e) {
	}

	@Subscribe
	public void onPlayerChanged(PlayerChanged e) {
	}

	@Subscribe
	public void onPlayerDespawned(PlayerDespawned e) {
	}

	@Subscribe
	public void onPlayerMenuOptionsChanged(PlayerMenuOptionsChanged e) {
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned e) {
	}

	@Subscribe
	public void onPostHealthBar(PostHealthBar e) {
	}

	@Subscribe
	public void onPostItemComposition(PostItemComposition e) {
	}

	@Subscribe
	public void onPostObjectComposition(PostObjectComposition e) {
	}

	@Subscribe
	public void onPostStructComposition(PostStructComposition e) {
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved e) {
	}

	@Subscribe
	public void onRemovedFriend(RemovedFriend e) {
	}

	@Subscribe
	public void onResizeableChanged(ResizeableChanged e) {
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent e) {
		int[] intStack = client.getIntStack();
		String[] stringStack = client.getStringStack();
		int intStackSize = client.getIntStackSize();
		int stringStackSize = client.getStringStackSize();

		if ("bankSearchFilter".equals(e.getEventName()))
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

	@Subscribe
	public void onScriptPostFired(ScriptPostFired e) {
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired e) {
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed e) {
	}

	@Subscribe
	public void onStatChanged(StatChanged e) {
	}

	@Subscribe
	public void onUsernameChanged(UsernameChanged e) {
	}

	@Subscribe
	public void onVarClientIntChanged(VarClientIntChanged e) {
	}

	@Subscribe
	public void onVarClientStrChanged(VarClientStrChanged e) {
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged e) {
	}

	@Subscribe
	public void onVolumeChanged(VolumeChanged e) {
	}

	@Subscribe
	public void onWallObjectDespawned(WallObjectDespawned e) {
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned e) {
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed e) {
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded e) {
	}

	@Subscribe
	public void onWorldChanged(WorldChanged e) {
	}

	@Subscribe
	public void onWorldListLoad(WorldListLoad e) {
	}
}
