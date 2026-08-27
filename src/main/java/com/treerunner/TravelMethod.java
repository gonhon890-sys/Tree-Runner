package com.treerunner;

public enum TravelMethod
{
    // Standard teleports
    LUMBRIDGE_TELEPORT("Lumbridge Teleport"),
    VARROCK_TELEPORT("Varrock Teleport"),
    FALADOR_TELEPORT("Falador Teleport"),
    TAVERLEY_TELEPORT("Taverley Teleport"),
    CAMELOT_TELEPORT("Camelot Teleport"),
    CATHERBY_TELEPORT("Catherby Teleport"),
    ARDOUGNE_TELEPORT("Ardougne Teleport"),
    BATTLEFRONT_TELEPORT("Battlefront Teleport"),

    // Jewellery / items
    RING_OF_WEALTH_FALADOR("Ring of Wealth - Falador"),
    RING_OF_WEALTH_GE("Ring of Wealth - Grand Exchange"),
    SKILLS_NECKLACE_FARMING_GUILD("Skills Necklace - Farming Guild"),
    SKILLS_NECKLACE_COOKING_GUILD("Skills Necklace - Cooking Guild"),
    SKILLS_NECKLACE_MINING_GUILD("Skills Necklace - Mining Guild"),
    GAMES_NECKLACE_BURTHORPE("Games Necklace - Burthorpe"),
    SLAYER_RING_STRONGHOLD("Slayer Ring - Stronghold"),
    DIGSITE_PENDANT("Digsite Pendant"),
    CRYSTAL_TELEPORT_SEED("Crystal Teleport Seed"),
    SEED_POD("Seed Pod"),
    NECKLACE_OF_PASSAGE("Necklace of Passage"),

    // Pendant of Ates
    PENDANT_OF_ATES_NEMUS("Pendant of Ates - Nemus Retreat"),
    PENDANT_OF_ATES_KASTORI("Pendant of Ates - Kastori"),

    // Spirit trees
    SPIRIT_TREE_GRAND_EXCHANGE("Spirit Tree - Grand Exchange"),
    SPIRIT_TREE_GNOME_STRONGHOLD("Spirit Tree - Gnome Stronghold"),
    SPIRIT_TREE_GNOME_VILLAGE("Spirit Tree - Gnome Village"),
    SPIRIT_TREE_FARMING_GUILD("Spirit Tree - Farming Guild"),

    // Fairy rings
    FAIRY_RING_CIR("CIR"),
    FAIRY_RING_CKR("CKR"),
    FAIRY_RING_CJQ("CJQ"),
    FAIRY_RING_AJP("AJP"),
    FAIRY_RING_AIS("AIS"),
    FAIRY_RING_CIQ("CIQ"),

    // Quetzal transport
    QUETZAL_QUETZACALLI_GORGE("Quetzacalli Gorge Quetzal"),
    QUETZAL_AUBURNVALE("Auburnvale Quetzal"),
    QUETZAL_KASTORI("Kastori Quetzal"),
    QUETZAL_TAL_TEKLAN("Tal Teklan Quetzal"),
    QUETZAL_COLOSSAL_WYRM("Colossal Wyrm Remains Quetzal"),

    // Other transport
    GNOME_GLIDER("Gnome Glider"),
    BALLOON("Balloon"),
    CHARTER_SHIP("Charter Ship"),
    MINECART("Minecart"),
    BOAT("Boat"),
    POH_TAVERLEY("POH - Taverley"),

    // Sailing / rowboat
    CORSAIR_COVE_ROWBOAT("Corsair Cove Rowboat"),
    SAIL_FROM_CORSAIR_COVE("Sail from Corsair Cove"),

    // Capes / miscellaneous
    FARMING_CAPE("Farming Cape"),
    ACHIEVEMENT_DIARY_CAPE("Diary Cape"),

    WALK("Walk");

    private final String displayName;

    TravelMethod(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}