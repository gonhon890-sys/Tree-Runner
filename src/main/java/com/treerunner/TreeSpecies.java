package com.treerunner;

import java.time.Duration;

public enum TreeSpecies
{
    OAK("Oak", TreeType.NORMAL, Duration.ofMinutes(160)),
    WILLOW("Willow", TreeType.NORMAL, Duration.ofHours(4)),
    MAPLE("Maple", TreeType.NORMAL, Duration.ofMinutes(320)),
    YEW("Yew", TreeType.NORMAL, Duration.ofMinutes(400)),
    MAGIC("Magic", TreeType.NORMAL, Duration.ofHours(8)),

    APPLE("Apple", TreeType.FRUIT, Duration.ofHours(16)),
    BANANA("Banana", TreeType.FRUIT, Duration.ofHours(16)),
    ORANGE("Orange", TreeType.FRUIT, Duration.ofHours(16)),
    CURRY("Curry", TreeType.FRUIT, Duration.ofHours(16)),
    PINEAPPLE("Pineapple", TreeType.FRUIT, Duration.ofHours(16)),
    PAPAYA("Papaya", TreeType.FRUIT, Duration.ofHours(16)),
    PALM("Palm", TreeType.FRUIT, Duration.ofHours(16)),
    DRAGONFRUIT("Dragonfruit", TreeType.FRUIT, Duration.ofHours(16)),

    TEAK("Teak", TreeType.HARDWOOD, Duration.ofMinutes(4480)),
    MAHOGANY("Mahogany", TreeType.HARDWOOD, Duration.ofMinutes(5120)),
    CAMPHOR("Camphor", TreeType.HARDWOOD, Duration.ofMinutes(5120)),
    IRONWOOD("Ironwood", TreeType.HARDWOOD, Duration.ofMinutes(5120)),
    ROSEWOOD("Rosewood", TreeType.HARDWOOD, Duration.ofMinutes(6400)),

    CALQUAT("Calquat", TreeType.CALQUAT, Duration.ofMinutes(1280)),
    CELASTRUS("Celastrus", TreeType.CELASTRUS, Duration.ofMinutes(800)),
    REDWOOD("Redwood", TreeType.REDWOOD, Duration.ofMinutes(6400)),
    CRYSTAL("Crystal", TreeType.CRYSTAL, Duration.ofMinutes(480));

    private final String displayName;
    private final TreeType treeType;
    private final Duration growthTime;

    TreeSpecies(
            String displayName,
            TreeType treeType,
            Duration growthTime
    )
    {
        this.displayName = displayName;
        this.treeType = treeType;
        this.growthTime = growthTime;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public TreeType getTreeType()
    {
        return treeType;
    }

    public Duration getGrowthTime()
    {
        return growthTime;
    }

    // =========================================================
    // TREE ICON
    // =========================================================

    public String getIconResourcePath()
    {
        /*
         * Fruit tree resources use names like:
         *
         * Papaya_tree(grown).png
         * Palm_tree(grown).png
         *
         * Other tree resources use names like:
         *
         * Willow_tree.png
         * Yew_tree.png
         * Teak_tree.png
         */
        if (treeType == TreeType.FRUIT)
        {
            return "/"
                    + displayName
                    + "_tree(grown).png";
        }

        return "/"
                + displayName
                + "_tree.png";
    }

    public static TreeSpecies fromSaplingName(
            String itemName
    )
    {
        if (itemName == null)
        {
            return null;
        }

        String normalized =
                itemName.toLowerCase();

        for (TreeSpecies species :
                values())
        {
            String speciesName =
                    species.getDisplayName()
                            .toLowerCase();

            if (normalized.contains(speciesName)
                    && normalized.contains("sapling"))
            {
                return species;
            }
        }

        return null;
    }
}