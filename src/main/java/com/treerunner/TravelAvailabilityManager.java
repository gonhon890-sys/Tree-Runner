package com.treerunner;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.client.game.ItemManager;
import net.runelite.api.Skill;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Varbits;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TravelAvailabilityManager
{
    private final TreeRunnerConfig config;
    private final Client client;
    private final ItemManager itemManager;

    /*
     * Cached carried items.
     *
     * Key = lower-case item name
     * Value = total quantity carried
     *
     * Refreshed on GameTick from the client thread.
     */
    private final Map<String, Integer> carriedItems =
            new HashMap<>();
    private int magicLevel = 0;
    private int spellbook = 0;

    @Inject
    public TravelAvailabilityManager(
            TreeRunnerConfig config,
            Client client,
            ItemManager itemManager
    )
    {
        this.config = config;
        this.client = client;
        this.itemManager = itemManager;
    }

    // =========================================================
    // REFRESH INVENTORY / EQUIPMENT CACHE
    // =========================================================

    public void refreshCarriedItems()
    {
        magicLevel =
                client.getBoostedSkillLevel(
                        Skill.MAGIC
                );
        spellbook =
                client.getVarbitValue(
                        Varbits.SPELLBOOK
                );
        carriedItems.clear();

        cacheContainer(
                InventoryID.INVENTORY
        );

        cacheContainer(
                InventoryID.EQUIPMENT
        );
    }

    private void cacheContainer(
            InventoryID inventoryID
    )
    {
        ItemContainer container =
                client.getItemContainer(
                        inventoryID
                );

        if (container == null)
        {
            return;
        }

        for (Item item :
                container.getItems())
        {
            if (item == null
                    || item.getId() <= 0)
            {
                continue;
            }

            String itemName =
                    itemManager
                            .getItemComposition(
                                    item.getId()
                            )
                            .getName();

            if (itemName == null)
            {
                continue;
            }

            String lowerName =
                    itemName.toLowerCase();

            int quantity =
                    Math.max(
                            item.getQuantity(),
                            1
                    );

            carriedItems.put(
                    lowerName,
                    carriedItems.getOrDefault(
                            lowerName,
                            0
                    ) + quantity
            );
        }
    }

    // =========================================================
    // CONFIG ENABLED
    // =========================================================

    public boolean isEnabled(
            TravelMethod method
    )
    {
        switch (method)
        {
            case RING_OF_WEALTH_FALADOR:
            case RING_OF_WEALTH_GE:
                return config.useRingOfWealth();

            case SLAYER_RING_STRONGHOLD:
                return config.useSlayerRing();

            case GNOME_GLIDER:
                return config.useGnomeGlider();

            case BALLOON:
                return config.useBalloon();

            case MINECART:
                return config.useMinecart();

            case GAMES_NECKLACE_BURTHORPE:
                return config.useGamesNecklace();

            case ACHIEVEMENT_DIARY_CAPE:
                return config.useDiaryCape();

            case SPIRIT_TREE_GRAND_EXCHANGE:
            case SPIRIT_TREE_GNOME_STRONGHOLD:
            case SPIRIT_TREE_GNOME_VILLAGE:
            case SPIRIT_TREE_FARMING_GUILD:
                return config.useSpiritTrees();

            case FAIRY_RING_CIR:
            case FAIRY_RING_CIQ:
            case FAIRY_RING_CKR:
            case FAIRY_RING_CJQ:
            case FAIRY_RING_AJP:
            case FAIRY_RING_AIS:
                return config.useFairyRings();

            case QUETZAL_QUETZACALLI_GORGE:
            case QUETZAL_AUBURNVALE:
            case QUETZAL_KASTORI:
            case QUETZAL_TAL_TEKLAN:
            case QUETZAL_COLOSSAL_WYRM:
                return config.useQuetzal();

            case PENDANT_OF_ATES_NEMUS:
            case PENDANT_OF_ATES_KASTORI:
                return config.usePendantOfAtes();

            case FARMING_CAPE:
                return config.useFarmingCape();

            case CORSAIR_COVE_ROWBOAT:
                return config.useRowboat();

            case SAIL_FROM_CORSAIR_COVE:
                return config.useSailing();

            default:
                return true;
        }
    }

    // =========================================================
    // ACTUAL AVAILABILITY
    // =========================================================

    public boolean isAvailable(
            TravelMethod method
    )
    {
        if (!isEnabled(method))
        {
            return false;
        }

        switch (method)
        {

            // =========================
            // SPELL TELEPORTS
            // =========================

            case VARROCK_TELEPORT:
                return hasTeleportTablet(
                        "varrock teleport"
                )
                        || (
                        isStandardSpellbook()
                                && magicLevel >= 25
                                && getRuneCount("law rune") >= 1
                                && getEffectiveAirRunes() >= 3
                                && getEffectiveFireRunes() >= 1
                );

            case LUMBRIDGE_TELEPORT:
                return hasTeleportTablet(
                        "lumbridge teleport"
                )
                        || (
                        isStandardSpellbook()
                                && magicLevel >= 31
                                && getRuneCount("law rune") >= 1
                                && getEffectiveAirRunes() >= 3
                                && getEffectiveEarthRunes() >= 1
                );

            case FALADOR_TELEPORT:
                return hasTeleportTablet(
                        "falador teleport"
                )
                        || (
                        isStandardSpellbook()
                                && magicLevel >= 37
                                && getRuneCount("law rune") >= 1
                                && getEffectiveAirRunes() >= 3
                                && getEffectiveWaterRunes() >= 1
                );

            case CAMELOT_TELEPORT:
                return hasTeleportTablet(
                        "camelot teleport"
                )
                        || (
                        isStandardSpellbook()
                                && magicLevel >= 45
                                && getRuneCount("law rune") >= 1
                                && getEffectiveAirRunes() >= 5
                );

            case ARDOUGNE_TELEPORT:
                return hasTeleportTablet(
                        "ardougne teleport"
                )
                        || (
                        isStandardSpellbook()
                                && magicLevel >= 51
                                && getRuneCount("law rune") >= 2
                                && getEffectiveWaterRunes() >= 2
                );

            case CATHERBY_TELEPORT:
                return hasTeleportTablet(
                        "catherby teleport"
                )
                        || (
                        isLunarSpellbook()
                                && magicLevel >= 87
                                && getRuneCount("law rune") >= 3
                                && getRuneCount("astral rune") >= 3
                                && getEffectiveWaterRunes() >= 10
                );

            case BATTLEFRONT_TELEPORT:
                return hasTeleportTablet(
                        "battlefront teleport"
                )
                        || (
                        isArceuusSpellbook()
                                && magicLevel >= 23
                                && getRuneCount("law rune") >= 1
                                && getEffectiveEarthRunes() >= 1
                                && getEffectiveFireRunes() >= 1
                );

            case TAVERLEY_TELEPORT:
                /*
                 * Taverley isn't a normal spellbook teleport.
                 * For now Tree Runner only considers this method
                 * available when the player is carrying the
                 * appropriate teleport item/tablet.
                 */
                return hasTeleportTablet(
                        "taverley teleport"
                );

            // =========================
            // JEWELLERY
            // =========================

            case RING_OF_WEALTH_FALADOR:
            case RING_OF_WEALTH_GE:
                return hasItemContaining(
                        "ring of wealth"
                );

            case SLAYER_RING_STRONGHOLD:
                return hasItemContaining(
                        "slayer ring"
                )
                        || hasItemContaining(
                        "eternal slayer ring"
                );

            case GAMES_NECKLACE_BURTHORPE:
                return hasItemContaining(
                        "games necklace"
                );

            case SKILLS_NECKLACE_FARMING_GUILD:
            case SKILLS_NECKLACE_COOKING_GUILD:
            case SKILLS_NECKLACE_MINING_GUILD:
                return hasItemContaining(
                        "skills necklace"
                );

            // =========================
            // CAPES
            // =========================

            case FARMING_CAPE:
                return hasItemContaining(
                        "farming cape"
                );

            case ACHIEVEMENT_DIARY_CAPE:
                return hasItemContaining(
                        "achievement diary cape"
                )
                        || hasItemContaining(
                        "diary cape"
                );

            // =========================
            // OTHER ITEMS
            // =========================

            case SEED_POD:
                return hasItemContaining(
                        "royal seed pod"
                );

            case NECKLACE_OF_PASSAGE:
                return hasItemContaining(
                        "necklace of passage"
                );

            case DIGSITE_PENDANT:
                return hasItemContaining(
                        "digsite pendant"
                );

            case CRYSTAL_TELEPORT_SEED:
                return hasItemContaining(
                        "crystal teleport seed"
                )
                        || hasItemContaining(
                        "eternal teleport crystal"
                );

            case PENDANT_OF_ATES_NEMUS:
            case PENDANT_OF_ATES_KASTORI:
                return hasItemContaining(
                        "pendant of ates"
                );

            // =========================
            // NON-ITEM TRANSPORT
            // =========================

            default:
                return true;
        }
    }


    // =========================================================
    // SELECT BEST METHOD
    // =========================================================

    public TravelMethod getClosestAvailable(
            TreePatch patch
    )
    {
        /*
         * First find the best travel method the player
         * has selected in Tree Runner settings.
         *
         * We intentionally DO NOT require the item/runes
         * to currently be carried.
         *
         * This means Tree Runner can still tell the player
         * what they need while preparing at the bank.
         */
        TravelMethod preferredMethod =
                getPreferredMethod(
                        patch
                );

        /*
         * If the player has not selected any usable
         * preference for this patch, fall back to the
         * best method that is actually available.
         */
        if (preferredMethod == null)
        {
            return getBestActuallyAvailable(
                    patch
            );
        }

        /*
         * Look only at travel options that appear BEFORE
         * the preferred option in the patch's Wiki order.
         *
         * If one of those better options happens to be
         * available right now, use it temporarily.
         */
        for (TravelMethod method :
                patch.getTravelMethods())
        {
            if (method == preferredMethod)
            {
                break;
            }

            if (isActuallyAvailable(method))
            {
                return method;
            }
        }

        /*
         * Otherwise keep the selected/preferred travel
         * method even if the player hasn't withdrawn the
         * item or runes yet.
         */
        return preferredMethod;
    }
    private TravelMethod getPreferredMethod(
            TreePatch patch
    )
    {
        for (TravelMethod method :
                patch.getTravelMethods())
        {
            if (isEnabled(method))
            {
                return method;
            }
        }

        return null;
    }

    private TravelMethod getBestActuallyAvailable(
            TreePatch patch
    )
    {
        for (TravelMethod method :
                patch.getTravelMethods())
        {
            if (isActuallyAvailable(method))
            {
                return method;
            }
        }

        return null;
    }

    private boolean isActuallyAvailable(
            TravelMethod method
    )
    {
        /*
         * This deliberately ignores the Tree Runner
         * preference checkbox.
         *
         * It answers only:
         *
         * "Can the player genuinely use this right now?"
         */

        switch (method)
        {
            // =========================
            // SPELL TELEPORTS
            // =========================

            case VARROCK_TELEPORT:
                return hasTeleportTablet(
                        "varrock teleport"
                )
                        || (
                        isStandardSpellbook()
                                && magicLevel >= 25
                                && getRuneCount("law rune") >= 1
                                && getEffectiveAirRunes() >= 3
                                && getEffectiveFireRunes() >= 1
                );

            case LUMBRIDGE_TELEPORT:
                return hasTeleportTablet(
                        "lumbridge teleport"
                )
                        || (
                        isStandardSpellbook()
                                && magicLevel >= 31
                                && getRuneCount("law rune") >= 1
                                && getEffectiveAirRunes() >= 3
                                && getEffectiveEarthRunes() >= 1
                );

            case FALADOR_TELEPORT:
                return hasTeleportTablet(
                        "falador teleport"
                )
                        || (
                        isStandardSpellbook()
                                && magicLevel >= 37
                                && getRuneCount("law rune") >= 1
                                && getEffectiveAirRunes() >= 3
                                && getEffectiveWaterRunes() >= 1
                );

            case CAMELOT_TELEPORT:
                return hasTeleportTablet(
                        "camelot teleport"
                )
                        || (
                        isStandardSpellbook()
                                && magicLevel >= 45
                                && getRuneCount("law rune") >= 1
                                && getEffectiveAirRunes() >= 5
                );

            case ARDOUGNE_TELEPORT:
                return hasTeleportTablet(
                        "ardougne teleport"
                )
                        || (
                        isStandardSpellbook()
                                && magicLevel >= 51
                                && getRuneCount("law rune") >= 2
                                && getEffectiveWaterRunes() >= 2
                );

            case CATHERBY_TELEPORT:
                return hasTeleportTablet(
                        "catherby teleport"
                )
                        || (
                        isLunarSpellbook()
                                && magicLevel >= 87
                                && getRuneCount("law rune") >= 3
                                && getRuneCount("astral rune") >= 3
                                && getEffectiveWaterRunes() >= 10
                );

            case BATTLEFRONT_TELEPORT:
                return hasTeleportTablet(
                        "battlefront teleport"
                )
                        || (
                        isArceuusSpellbook()
                                && magicLevel >= 23
                                && getRuneCount("law rune") >= 1
                                && getEffectiveEarthRunes() >= 1
                                && getEffectiveFireRunes() >= 1
                );

            case TAVERLEY_TELEPORT:
                return hasTeleportTablet(
                        "taverley teleport"
                );

            // =========================
            // JEWELLERY
            // =========================

            case RING_OF_WEALTH_FALADOR:
            case RING_OF_WEALTH_GE:
                return hasItemContaining(
                        "ring of wealth"
                );

            case SLAYER_RING_STRONGHOLD:
                return hasItemContaining(
                        "slayer ring"
                )
                        || hasItemContaining(
                        "eternal slayer ring"
                );

            case GAMES_NECKLACE_BURTHORPE:
                return hasItemContaining(
                        "games necklace"
                );

            case SKILLS_NECKLACE_FARMING_GUILD:
            case SKILLS_NECKLACE_COOKING_GUILD:
            case SKILLS_NECKLACE_MINING_GUILD:
                return hasItemContaining(
                        "skills necklace"
                );

            // =========================
            // CAPES
            // =========================

            case FARMING_CAPE:
                return hasItemContaining(
                        "farming cape"
                );

            case ACHIEVEMENT_DIARY_CAPE:
                return hasItemContaining(
                        "achievement diary cape"
                )
                        || hasItemContaining(
                        "diary cape"
                );

            // =========================
            // OTHER ITEMS
            // =========================

            case SEED_POD:
                return hasItemContaining(
                        "royal seed pod"
                );

            case NECKLACE_OF_PASSAGE:
                return hasItemContaining(
                        "necklace of passage"
                );

            case DIGSITE_PENDANT:
                return hasItemContaining(
                        "digsite pendant"
                );

            case CRYSTAL_TELEPORT_SEED:
                return hasItemContaining(
                        "crystal teleport seed"
                )
                        || hasItemContaining(
                        "eternal teleport crystal"
                );

            case PENDANT_OF_ATES_NEMUS:
            case PENDANT_OF_ATES_KASTORI:
                return hasItemContaining(
                        "pendant of ates"
                );

            /*
             * We cannot automatically verify unlocks
             * for these yet.
             *
             * So don't let an unselected Fairy Ring,
             * Spirit Tree, Quetzal, etc. override the
             * player's planned method merely because
             * we assume it exists.
             */
            default:
                return false;
        }
    }
    // =========================================================
    // TELEPORT TABLETS
    // =========================================================

    private boolean hasTeleportTablet(
            String teleportName
    )
    {
        /*
         * Item names are normally:
         *
         * "Lumbridge teleport"
         * "Varrock teleport"
         * etc.
         */
        return hasItemContaining(
                teleportName
        );
    }

// =========================================================
// SPELLBOOK
// =========================================================

    private boolean isStandardSpellbook()
    {
        return spellbook == 0;
    }

    private boolean isAncientSpellbook()
    {
        return spellbook == 1;
    }

    private boolean isLunarSpellbook()
    {
        return spellbook == 2;
    }

    private boolean isArceuusSpellbook()
    {
        return spellbook == 3;
    }

// =========================================================
// RUNE COUNTS
// =========================================================

    private int getRuneCount(
            String runeName
    )
    {
        return carriedItems.getOrDefault(
                runeName,
                0
        );
    }

// =========================================================
// EFFECTIVE ELEMENTAL RUNES
// Includes combination runes and equipped staves
// =========================================================

    private int getEffectiveAirRunes()
    {
        if (providesAirRunes())
        {
            return Integer.MAX_VALUE;
        }

        return getRuneCount("air rune")
                + getRuneCount("dust rune")
                + getRuneCount("mist rune")
                + getRuneCount("smoke rune");
    }

    private int getEffectiveWaterRunes()
    {
        if (providesWaterRunes())
        {
            return Integer.MAX_VALUE;
        }

        return getRuneCount("water rune")
                + getRuneCount("mist rune")
                + getRuneCount("mud rune")
                + getRuneCount("steam rune");
    }

    private int getEffectiveEarthRunes()
    {
        if (providesEarthRunes())
        {
            return Integer.MAX_VALUE;
        }

        return getRuneCount("earth rune")
                + getRuneCount("dust rune")
                + getRuneCount("mud rune")
                + getRuneCount("lava rune");
    }

    private int getEffectiveFireRunes()
    {
        if (providesFireRunes())
        {
            return Integer.MAX_VALUE;
        }

        return getRuneCount("fire rune")
                + getRuneCount("smoke rune")
                + getRuneCount("steam rune")
                + getRuneCount("lava rune");
    }

// =========================================================
// AIR-RUNE STAVES
// =========================================================

    private boolean providesAirRunes()
    {
        return hasItemContaining(
                "staff of air"
        )
                || hasItemContaining(
                "air battlestaff"
        )
                || hasItemContaining(
                "mystic air staff"
        )
                || hasItemContaining(
                "dust battlestaff"
        )
                || hasItemContaining(
                "mist battlestaff"
        )
                || hasItemContaining(
                "smoke battlestaff"
        );
    }

// =========================================================
// WATER-RUNE STAVES
// =========================================================

    private boolean providesWaterRunes()
    {
        return hasItemContaining(
                "staff of water"
        )
                || hasItemContaining(
                "water battlestaff"
        )
                || hasItemContaining(
                "mystic water staff"
        )
                || hasItemContaining(
                "mist battlestaff"
        )
                || hasItemContaining(
                "mud battlestaff"
        )
                || hasItemContaining(
                "steam battlestaff"
        );
    }

// =========================================================
// EARTH-RUNE STAVES
// =========================================================

    private boolean providesEarthRunes()
    {
        return hasItemContaining(
                "staff of earth"
        )
                || hasItemContaining(
                "earth battlestaff"
        )
                || hasItemContaining(
                "mystic earth staff"
        )
                || hasItemContaining(
                "dust battlestaff"
        )
                || hasItemContaining(
                "mud battlestaff"
        )
                || hasItemContaining(
                "lava battlestaff"
        );
    }

// =========================================================
// FIRE-RUNE STAVES
// =========================================================

    private boolean providesFireRunes()
    {
        return hasItemContaining(
                "staff of fire"
        )
                || hasItemContaining(
                "fire battlestaff"
        )
                || hasItemContaining(
                "mystic fire staff"
        )
                || hasItemContaining(
                "smoke battlestaff"
        )
                || hasItemContaining(
                "lava battlestaff"
        )
                || hasItemContaining(
                "steam battlestaff"
        );
    }
    // =========================================================
    // CACHED ITEM SEARCH
    // =========================================================

    private boolean hasItemContaining(
            String searchText
    )
    {
        String wanted =
                searchText.toLowerCase();

        for (String itemName :
                carriedItems.keySet())
        {
            if (itemName.contains(
                    wanted
            ))
            {
                return true;
            }
        }

        return false;
    }
}