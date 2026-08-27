package com.treerunner;

import java.util.List;

public class TreeRunStep
{
    private final String displayName;
    private final TreeType treeType;
    private final List<TreePatch> patches;
    private TravelMethod travelMethod;

    public TreeRunStep(
            String displayName,
            TreeType treeType,
            List<TreePatch> patches,
            TravelMethod travelMethod
    )
    {
        this.displayName = displayName;
        this.treeType = treeType;
        this.patches = patches;
        this.travelMethod = travelMethod;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public TreeType getTreeType()
    {
        return treeType;
    }

    public List<TreePatch> getPatches()
    {
        return patches;
    }

    public TravelMethod getTravelMethod()
    {
        return travelMethod;
    }

    public void setTravelMethod(TravelMethod travelMethod)
    {
        this.travelMethod = travelMethod;
    }
}