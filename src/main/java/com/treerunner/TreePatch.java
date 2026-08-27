package com.treerunner;

public enum TreePatch
{
    // =========================
    // NORMAL TREE PATCHES
    // =========================

    LUMBRIDGE(
            "Lumbridge",
            TreeType.NORMAL,
            TreePatchGroup.LUMBRIDGE,
            TravelMethod.LUMBRIDGE_TELEPORT,
            TravelMethod.ACHIEVEMENT_DIARY_CAPE
    ),

    VARROCK(
            "Varrock",
            TreeType.NORMAL,
            TreePatchGroup.VARROCK,
            TravelMethod.VARROCK_TELEPORT,
            TravelMethod.ACHIEVEMENT_DIARY_CAPE,
            TravelMethod.SPIRIT_TREE_GRAND_EXCHANGE,
            TravelMethod.RING_OF_WEALTH_GE,
            TravelMethod.SKILLS_NECKLACE_COOKING_GUILD
    ),

    FALADOR(
            "Falador",
            TreeType.NORMAL,
            TreePatchGroup.FALADOR,
            TravelMethod.RING_OF_WEALTH_FALADOR,
            TravelMethod.FALADOR_TELEPORT,
            TravelMethod.SKILLS_NECKLACE_MINING_GUILD
    ),

    TAVERLEY(
            "Taverley",
            TreeType.NORMAL,
            TreePatchGroup.TAVERLEY,
            TravelMethod.BALLOON,
            TravelMethod.POH_TAVERLEY,
            TravelMethod.TAVERLEY_TELEPORT,
            TravelMethod.GAMES_NECKLACE_BURTHORPE
    ),

    GNOME_STRONGHOLD_TREE(
            "Gnome Stronghold",
            TreeType.NORMAL,
            TreePatchGroup.GNOME_STRONGHOLD,
            TravelMethod.SLAYER_RING_STRONGHOLD,
            TravelMethod.SPIRIT_TREE_GNOME_STRONGHOLD,
            TravelMethod.BALLOON,
            TravelMethod.SEED_POD,
            TravelMethod.GNOME_GLIDER,
            TravelMethod.NECKLACE_OF_PASSAGE
    ),

    FARMING_GUILD_TREE(
            "Farming Guild",
            TreeType.NORMAL,
            TreePatchGroup.FARMING_GUILD,
            TravelMethod.SKILLS_NECKLACE_FARMING_GUILD,
            TravelMethod.FARMING_CAPE,
            TravelMethod.SPIRIT_TREE_FARMING_GUILD,
            TravelMethod.MINECART,
            TravelMethod.FAIRY_RING_CIR,
            TravelMethod.BATTLEFRONT_TELEPORT
    ),

    NEMUS_RETREAT(
            "Nemus Retreat",
            TreeType.NORMAL,
            TreePatchGroup.NEMUS_RETREAT,
            TravelMethod.QUETZAL_QUETZACALLI_GORGE,
            TravelMethod.PENDANT_OF_ATES_NEMUS,
            TravelMethod.QUETZAL_AUBURNVALE,
            TravelMethod.FAIRY_RING_AIS
    ),

    // =========================
    // FRUIT TREE PATCHES
    // =========================

    GNOME_STRONGHOLD_FRUIT(
            "Tree Gnome Stronghold",
            TreeType.FRUIT,
            TreePatchGroup.GNOME_STRONGHOLD,
            TravelMethod.SPIRIT_TREE_GNOME_STRONGHOLD,
            TravelMethod.SLAYER_RING_STRONGHOLD,
            TravelMethod.GNOME_GLIDER,
            TravelMethod.SEED_POD
    ),

    CATHERBY(
            "Catherby",
            TreeType.FRUIT,
            TreePatchGroup.CATHERBY,
            TravelMethod.CATHERBY_TELEPORT,
            TravelMethod.CAMELOT_TELEPORT
    ),

    TREE_GNOME_MAZE(
            "Tree Gnome Maze",
            TreeType.FRUIT,
            TreePatchGroup.TREE_GNOME_MAZE,
            TravelMethod.SPIRIT_TREE_GNOME_VILLAGE,
            TravelMethod.FAIRY_RING_CIQ
    ),

    BRIMHAVEN(
            "Brimhaven",
            TreeType.FRUIT,
            TreePatchGroup.BRIMHAVEN,
            TravelMethod.CHARTER_SHIP
    ),

    LLETYA(
            "Lletya",
            TreeType.FRUIT,
            TreePatchGroup.LLETYA,
            TravelMethod.CRYSTAL_TELEPORT_SEED
    ),

    FARMING_GUILD_FRUIT(
            "Farming Guild",
            TreeType.FRUIT,
            TreePatchGroup.FARMING_GUILD,
            TravelMethod.SKILLS_NECKLACE_FARMING_GUILD,
            TravelMethod.FARMING_CAPE,
            TravelMethod.SPIRIT_TREE_FARMING_GUILD,
            TravelMethod.MINECART,
            TravelMethod.FAIRY_RING_CIR,
            TravelMethod.BATTLEFRONT_TELEPORT
    ),

    KASTORI_FRUIT(
            "Kastori",
            TreeType.FRUIT,
            TreePatchGroup.KASTORI,
            TravelMethod.PENDANT_OF_ATES_KASTORI,
            TravelMethod.QUETZAL_KASTORI,
            TravelMethod.QUETZAL_TAL_TEKLAN
    ),

    // =========================
    // HARDWOOD
    // =========================

    FOSSIL_ISLAND_HARDWOOD_1(
            "Fossil Island",
            TreeType.HARDWOOD,
            TreePatchGroup.FOSSIL_ISLAND,
            TravelMethod.DIGSITE_PENDANT
    ),

    FOSSIL_ISLAND_HARDWOOD_2(
            "Fossil Island",
            TreeType.HARDWOOD,
            TreePatchGroup.FOSSIL_ISLAND,
            TravelMethod.DIGSITE_PENDANT
    ),

    FOSSIL_ISLAND_HARDWOOD_3(
            "Fossil Island",
            TreeType.HARDWOOD,
            TreePatchGroup.FOSSIL_ISLAND,
            TravelMethod.DIGSITE_PENDANT
    ),

    LOCUS_OASIS(
            "Locus Oasis",
            TreeType.HARDWOOD,
            TreePatchGroup.LOCUS_OASIS,
            TravelMethod.QUETZAL_COLOSSAL_WYRM,
            TravelMethod.FAIRY_RING_AJP
    ),

    ANGLERS_RETREAT(
            "Anglers' Retreat",
            TreeType.HARDWOOD,
            TreePatchGroup.ANGLERS_RETREAT,
            TravelMethod.CORSAIR_COVE_ROWBOAT,
            TravelMethod.SAIL_FROM_CORSAIR_COVE
    ),

    // =========================
    // CALQUAT
    // =========================

    TAI_BWO_WANNAI(
            "Tai Bwo Wannai",
            TreeType.CALQUAT,
            TreePatchGroup.TAI_BWO_WANNAI,
            TravelMethod.FAIRY_RING_CKR
    ),

    SUMMER_SHORE(
            "Summer Shore",
            TreeType.CALQUAT,
            TreePatchGroup.SUMMER_SHORE,
            TravelMethod.FAIRY_RING_CJQ
    ),

    KASTORI_CALQUAT(
            "Kastori",
            TreeType.CALQUAT,
            TreePatchGroup.KASTORI,
            TravelMethod.PENDANT_OF_ATES_KASTORI,
            TravelMethod.QUETZAL_KASTORI,
            TravelMethod.QUETZAL_TAL_TEKLAN
    ),

    // =========================
    // CELASTRUS
    // =========================

    FARMING_GUILD_CELASTRUS(
            "Farming Guild",
            TreeType.CELASTRUS,
            TreePatchGroup.FARMING_GUILD,
            TravelMethod.SKILLS_NECKLACE_FARMING_GUILD,
            TravelMethod.FARMING_CAPE,
            TravelMethod.SPIRIT_TREE_FARMING_GUILD,
            TravelMethod.MINECART,
            TravelMethod.FAIRY_RING_CIR,
            TravelMethod.BATTLEFRONT_TELEPORT
    ),

    // =========================
    // REDWOOD
    // =========================

    FARMING_GUILD_REDWOOD(
            "Farming Guild",
            TreeType.REDWOOD,
            TreePatchGroup.FARMING_GUILD,
            TravelMethod.SKILLS_NECKLACE_FARMING_GUILD,
            TravelMethod.FARMING_CAPE,
            TravelMethod.SPIRIT_TREE_FARMING_GUILD,
            TravelMethod.MINECART,
            TravelMethod.FAIRY_RING_CIR,
            TravelMethod.BATTLEFRONT_TELEPORT
    ),

    // =========================
    // CRYSTAL TREE
    // =========================

    PRIFDDINAS_CRYSTAL(
            "Prifddinas",
            TreeType.CRYSTAL,
            TreePatchGroup.PRIFDDINAS,
            TravelMethod.CRYSTAL_TELEPORT_SEED
    );

    private final String displayName;
    private final TreeType treeType;
    private final TreePatchGroup patchGroup;
    private final TravelMethod[] travelMethods;

    TreePatch(
            String displayName,
            TreeType treeType,
            TreePatchGroup patchGroup,
            TravelMethod... travelMethods
    )
    {
        this.displayName = displayName;
        this.treeType = treeType;
        this.patchGroup = patchGroup;
        this.travelMethods = travelMethods;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public TreeType getTreeType()
    {
        return treeType;
    }

    public TreePatchGroup getPatchGroup()
    {
        return patchGroup;
    }

    public TravelMethod[] getTravelMethods()
    {
        return travelMethods;
    }
}