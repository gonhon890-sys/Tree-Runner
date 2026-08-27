package com.treerunner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.Color;

@ConfigGroup("treerunner")
public interface TreeRunnerConfig extends Config
{
    // =========================================================
    // SECTIONS
    // =========================================================

    @ConfigSection(
            name = "Run Trigger",
            description = "Choose which tree category determines when a new run is ready",
            position = 0
    )
    String runTriggerSection =
            "runTriggerSection";

    @ConfigSection(
            name = "Travel Selection",
            description = "Choose how Tree Runner selects travel methods",
            position = 1
    )
    String travelSelectionSection =
            "travelSelectionSection";

    @ConfigSection(
            name = "Travel Availability",
            description = "Disable transport methods you do not want Tree Runner to use",
            position = 2
    )
    String travelAvailabilitySection =
            "travelAvailabilitySection";

    @ConfigSection(
            name = "Patch Highlighting",
            description = "Configure current patch highlighting",
            position = 3
    )
    String patchHighlightingSection =
            "patchHighlightingSection";

    // =========================================================
    // RUN TRIGGER
    // =========================================================

    @ConfigItem(
            keyName = "runTriggerType",
            name = "Prime Run From",
            description = "Tree type used to determine when the next tree run is ready",
            section = runTriggerSection,
            position = 0
    )
    default TreeType runTriggerType()
    {
        return TreeType.NORMAL;
    }

    // =========================================================
    // TRAVEL SELECTION
    // =========================================================

    @ConfigItem(
            keyName = "useClosestAvailable",
            name = "Use Closest Available Option",
            description = "Choose the closest enabled travel method based on Wiki order",
            section = travelSelectionSection,
            position = 0
    )
    default boolean useClosestAvailable()
    {
        return true;
    }

    // =========================================================
    // TRAVEL AVAILABILITY
    // =========================================================

    @ConfigItem(
            keyName = "useRingOfWealth",
            name = "Ring of Wealth",
            description = "Allow Ring of Wealth travel methods",
            section = travelAvailabilitySection,
            position = 0
    )
    default boolean useRingOfWealth()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useSlayerRing",
            name = "Slayer Ring",
            description = "Allow Slayer Ring travel methods",
            section = travelAvailabilitySection,
            position = 1
    )
    default boolean useSlayerRing()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useGnomeGlider",
            name = "Gnome Glider",
            description = "Allow Gnome Glider travel methods",
            section = travelAvailabilitySection,
            position = 2
    )
    default boolean useGnomeGlider()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useBalloon",
            name = "Balloon",
            description = "Allow Balloon travel methods",
            section = travelAvailabilitySection,
            position = 3
    )
    default boolean useBalloon()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useMinecart",
            name = "Minecart",
            description = "Allow Minecart travel methods",
            section = travelAvailabilitySection,
            position = 4
    )
    default boolean useMinecart()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useGamesNecklace",
            name = "Games Necklace",
            description = "Allow Games Necklace travel methods",
            section = travelAvailabilitySection,
            position = 5
    )
    default boolean useGamesNecklace()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useDiaryCape",
            name = "Diary Cape",
            description = "Allow Achievement Diary Cape travel methods",
            section = travelAvailabilitySection,
            position = 6
    )
    default boolean useDiaryCape()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useSpiritTrees",
            name = "Spirit Trees",
            description = "Allow Spirit Tree travel methods",
            section = travelAvailabilitySection,
            position = 7
    )
    default boolean useSpiritTrees()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useFairyRings",
            name = "Fairy Rings",
            description = "Allow Fairy Ring travel methods",
            section = travelAvailabilitySection,
            position = 8
    )
    default boolean useFairyRings()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useQuetzal",
            name = "Quetzal",
            description = "Allow Quetzal travel methods",
            section = travelAvailabilitySection,
            position = 9
    )
    default boolean useQuetzal()
    {
        return true;
    }

    @ConfigItem(
            keyName = "usePendantOfAtes",
            name = "Pendant of Ates",
            description = "Allow Pendant of Ates travel methods",
            section = travelAvailabilitySection,
            position = 10
    )
    default boolean usePendantOfAtes()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useFarmingCape",
            name = "Farming Cape",
            description = "Allow Farming Cape travel methods",
            section = travelAvailabilitySection,
            position = 11
    )
    default boolean useFarmingCape()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useRowboat",
            name = "Rowboat",
            description = "Allow rowboat travel methods",
            section = travelAvailabilitySection,
            position = 12
    )
    default boolean useRowboat()
    {
        return true;
    }

    @ConfigItem(
            keyName = "useSailing",
            name = "Sailing",
            description = "Allow Sailing travel methods",
            section = travelAvailabilitySection,
            position = 13
    )
    default boolean useSailing()
    {
        return true;
    }

    // =========================================================
    // PATCH HIGHLIGHTING
    // =========================================================

    @ConfigItem(
            keyName = "highlightCurrentPatch",
            name = "Highlight Current Patch",
            description = "Highlight the current tree patch in the game world",
            section = patchHighlightingSection,
            position = 0
    )
    default boolean highlightCurrentPatch()
    {
        return true;
    }

    @ConfigItem(
            keyName = "highlightColor",
            name = "Highlight Colour",
            description = "Choose the colour used to highlight the current tree patch",
            section = patchHighlightingSection,
            position = 1
    )
    default Color highlightColor()
    {
        return new Color(
                255,
                152,
                31
        );
    }
}