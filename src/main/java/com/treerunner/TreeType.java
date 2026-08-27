package com.treerunner;

public enum TreeType
{
    NORMAL("Tree"),
    FRUIT("Fruit Tree"),
    HARDWOOD("Hardwood Tree"),
    CALQUAT("Calquat"),
    CELASTRUS("Celastrus"),
    REDWOOD("Redwood"),
    CRYSTAL("Crystal Tree");

    private final String displayName;

    TreeType(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}