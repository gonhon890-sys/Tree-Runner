package com.treerunner;

import com.google.inject.Provides;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;

@PluginDescriptor(
		name = "Tree Runner",
		description = "Helps plan and track tree farming runs",
		tags = {"farming", "tree", "trees"}
)
public class TreeRunnerPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TreeRunManager treeRunManager;

	@Inject
	private TreeRunnerOverlay overlay;

	@Inject
	private TreeRunnerPatchOverlay patchOverlay;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private TreeRunnerPanel panel;

	@Inject
	private Client client;

	@Inject
	private TravelAvailabilityManager travelAvailabilityManager;

	@Inject
	private TreePatchLocator treePatchLocator;

	@Inject
	private ItemManager itemManager;

	@Inject
	private PatchTracker patchTracker;

	private WorldPoint lastPatchClicked;
	private int lastPatchObjectId = -1;
	private TreeSpecies pendingPlantingSpecies;
	private TreePatch pendingPlantingPatch;
	private int pendingPlantingItemId = -1;
	private int pendingPlantingInitialCount = 0;
	private int pendingPlantingTicksRemaining = 0;
	private NavigationButton navigationButton;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(patchOverlay);

		BufferedImage icon = ImageUtil.loadImageResource(
				TreeRunnerPlugin.class,
				"/farming_icon.png"
		);

		navigationButton = NavigationButton.builder()
				.tooltip("Tree Runner")
				.icon(icon)
				.priority(5)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navigationButton);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(patchOverlay);

		panel.stopRefreshTimer();

		if (navigationButton != null)
		{
			clientToolbar.removeNavigation(navigationButton);
			navigationButton = null;
		}
	}
	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		MenuAction action =
				event.getMenuAction();

		boolean plantingSapling =
				action
						== MenuAction
						.WIDGET_TARGET_ON_GAME_OBJECT;

		boolean clearingPatch =
				event.getMenuOption()
						.equalsIgnoreCase("Clear")
						|| event.getMenuOption()
						.equalsIgnoreCase("Chop down");

		if (!plantingSapling
				&& !clearingPatch)
		{
			return;
		}

		if (client.getLocalPlayer() == null)
		{
			return;
		}

		TreeSpecies plantedSpecies =
				null;

		int plantingItemId =
				-1;

		if (plantingSapling)
		{
			Widget selectedWidget =
					client.getSelectedWidget();

			if (selectedWidget != null)
			{
				plantingItemId =
						selectedWidget.getItemId();

				if (plantingItemId > 0)
				{
					String itemName =
							itemManager
									.getItemComposition(
											plantingItemId
									)
									.getName();

					plantedSpecies =
							TreeSpecies
									.fromSaplingName(
											itemName
									);


					;
				}
			}
		}

		lastPatchClicked =
				WorldPoint.fromScene(
						client,
						event.getParam0(),
						event.getParam1(),
						client.getPlane()
				);

		lastPatchObjectId =
				event.getId();

		WorldPoint playerLocation =
				client.getLocalPlayer()
						.getWorldLocation();

		TreePatch detectedPatch =
				treePatchLocator
						.findNearestPatch(
								playerLocation,
								10
						);

		if (detectedPatch == null)
		{

			return;
		}

		/*
		 * Do not record the tree yet.
		 *
		 * Store it as pending and wait until
		 * the sapling actually leaves inventory.
		 */
		if (plantingSapling
				&& plantedSpecies != null
				&& plantingItemId > 0)
		{
			pendingPlantingSpecies =
					plantedSpecies;

			pendingPlantingPatch =
					detectedPatch;

			pendingPlantingItemId =
					plantingItemId;

			pendingPlantingInitialCount =
					countInventoryItem(
							plantingItemId
					);

			pendingPlantingTicksRemaining =
					5;

		}

		if (clearingPatch) {
		}
	}
	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (event.getType() != ChatMessageType.SPAM
				&& event.getType() != ChatMessageType.GAMEMESSAGE) {
			return;
		}

		String message = event.getMessage();

		if (message == null) {
			return;
		}

		String lowerMessage = message.toLowerCase();

		if (!lowerMessage.startsWith("you plant the ")
				|| !lowerMessage.contains(" sapling in the tree patch")) {
			return;
		}

		String speciesName = lowerMessage
				.replace("you plant the ", "")
				.replace(" sapling in the tree patch.", "")
				.trim();

		TreeSpecies plantedSpecies =
				TreeSpecies.fromSaplingName(
						speciesName + " sapling"
				);

		if (plantedSpecies == null) {

			return;
		}

		if (client.getLocalPlayer() == null) {
			return;
		}

		WorldPoint playerLocation =
				client.getLocalPlayer()
						.getWorldLocation();

		TreePatch detectedPatch =
				treePatchLocator.findNearestPatch(
						playerLocation,
						5
				);

		if (detectedPatch == null) {

			;

			return;
		}

		patchTracker.recordPlanting(
				detectedPatch,
				plantedSpecies,
				java.time.Instant.now()
		);

		panel.refreshPatchTimes();

		treeRunManager.markPatchCompleted(
				detectedPatch
		);


	}
	private int countInventoryItem(
			int itemId
	)
	{
		ItemContainer inventory =
				client.getItemContainer(
						InventoryID.INVENTORY
				);

		if (inventory == null)
		{
			return 0;
		}

		int count = 0;

		for (Item item :
				inventory.getItems())
		{
			if (item != null
					&& item.getId() == itemId)
			{
				count += item.getQuantity();
			}
		}

		return count;
	}

	private void confirmPendingPlanting()
	{
		if (pendingPlantingSpecies == null
				|| pendingPlantingPatch == null)
		{
			return;
		}

		patchTracker.recordPlanting(
				pendingPlantingPatch,
				pendingPlantingSpecies,
				java.time.Instant.now()
		);

		panel.refreshPatchTimes();

		treeRunManager.markPatchCompleted(
				pendingPlantingPatch
		);

		panel.refreshCurrentRoute();

		clearPendingPlanting();
	}

	private void clearPendingPlanting()
	{
		pendingPlantingSpecies = null;
		pendingPlantingPatch = null;
		pendingPlantingItemId = -1;
		pendingPlantingInitialCount = 0;
		pendingPlantingTicksRemaining = 0;
	}
	@Subscribe
	public void onGameTick(GameTick event)
	{
		travelAvailabilityManager.refreshCarriedItems();

		if (pendingPlantingSpecies != null
				&& pendingPlantingItemId > 0)
		{
			int currentCount =
					countInventoryItem(
							pendingPlantingItemId
					);

			/*
			 * Successful planting consumes one sapling.
			 */
			if (currentCount
					< pendingPlantingInitialCount)
			{
				confirmPendingPlanting();
			}
			else
			{
				pendingPlantingTicksRemaining--;

				/*
				 * Nothing was consumed, so assume the
				 * interaction failed or was a misclick.
				 */
				if (pendingPlantingTicksRemaining <= 0)
				{
					clearPendingPlanting();
				}
			}
		}

		treeRunManager.checkPrimeRun();
	}
	@Provides
	TreeRunnerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TreeRunnerConfig.class);
	}
}