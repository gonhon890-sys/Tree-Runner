package com.treerunner;

import java.time.Instant;

public class TrackedTree
{
    private final TreePatch patch;
    private final TreeSpecies species;
    private final Instant plantedAt;

    public TrackedTree(TreePatch patch, TreeSpecies species, Instant plantedAt)
    {
        this.patch = patch;
        this.species = species;
        this.plantedAt = plantedAt;
    }

    public TreePatch getPatch()
    {
        return patch;
    }

    public TreeSpecies getSpecies()
    {
        return species;
    }

    public Instant getPlantedAt()
    {
        return plantedAt;
    }

    public Instant getExpectedReadyAt()
    {
        return plantedAt.plus(species.getGrowthTime());
    }

    public boolean isExpectedReady()
    {
        return Instant.now().isAfter(getExpectedReadyAt())
                || Instant.now().equals(getExpectedReadyAt());
    }
}