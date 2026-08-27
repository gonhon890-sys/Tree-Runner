package com.treerunner;

import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

@Singleton
public class PatchTracker
{
    private static final String CONFIG_GROUP = "treerunnerTrackedTrees";

    private final Map<TreePatch, TrackedTree> trackedTrees =
            new EnumMap<>(TreePatch.class);

    private final ConfigManager configManager;

    @Inject
    public PatchTracker(ConfigManager configManager)
    {
        this.configManager = configManager;
        loadTrackedTrees();
    }

    public void recordPlanting(
            TreePatch patch,
            TreeSpecies species,
            Instant plantedAt
    )
    {
        TrackedTree trackedTree =
                new TrackedTree(
                        patch,
                        species,
                        plantedAt
                );

        trackedTrees.put(
                patch,
                trackedTree
        );

        saveTrackedTree(trackedTree);
    }

    public TrackedTree getTrackedTree(
            TreePatch patch
    )
    {
        return trackedTrees.get(patch);
    }

    public Collection<TrackedTree> getAllTrackedTrees()
    {
        return trackedTrees.values();
    }

    public TrackedTree getLastPlantedTree(
            TreeType treeType
    )
    {
        TrackedTree latest = null;

        for (TrackedTree tree : trackedTrees.values())
        {
            if (
                    tree.getSpecies().getTreeType()
                            != treeType
            )
            {
                continue;
            }

            if (
                    latest == null
                            || tree.getPlantedAt()
                            .isAfter(
                                    latest.getPlantedAt()
                            )
            )
            {
                latest = tree;
            }
        }

        return latest;
    }

    public boolean isTreeTypeReady(
            TreeType treeType
    )
    {
        TrackedTree lastPlanted =
                getLastPlantedTree(treeType);

        return lastPlanted != null
                && lastPlanted.isExpectedReady();
    }

    public void clearPatch(
            TreePatch patch
    )
    {
        trackedTrees.remove(patch);

        configManager.unsetConfiguration(
                CONFIG_GROUP,
                patch.name()
        );
    }

    public void clearAll()
    {
        for (TreePatch patch : TreePatch.values())
        {
            configManager.unsetConfiguration(
                    CONFIG_GROUP,
                    patch.name()
            );
        }

        trackedTrees.clear();
    }

    private void saveTrackedTree(
            TrackedTree tree
    )
    {
        String value =
                tree.getSpecies().name()
                        + "|"
                        + tree.getPlantedAt()
                        .toEpochMilli();

        configManager.setConfiguration(
                CONFIG_GROUP,
                tree.getPatch().name(),
                value
        );
    }

    private void loadTrackedTrees()
    {
        for (TreePatch patch : TreePatch.values())
        {
            String value =
                    configManager.getConfiguration(
                            CONFIG_GROUP,
                            patch.name()
                    );

            if (
                    value == null
                            || value.isEmpty()
            )
            {
                continue;
            }

            try
            {
                String[] parts =
                        value.split("\\|");

                TreeSpecies species =
                        TreeSpecies.valueOf(
                                parts[0]
                        );

                long plantedAtMillis =
                        Long.parseLong(
                                parts[1]
                        );

                Instant plantedAt =
                        Instant.ofEpochMilli(
                                plantedAtMillis
                        );

                trackedTrees.put(
                        patch,
                        new TrackedTree(
                                patch,
                                species,
                                plantedAt
                        )
                );
            }
            catch (Exception ignored)
            {
                configManager.unsetConfiguration(
                        CONFIG_GROUP,
                        patch.name()
                );
            }
        }
    }
}