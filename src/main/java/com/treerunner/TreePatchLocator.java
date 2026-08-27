package com.treerunner;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Singleton;
import java.util.EnumMap;
import java.util.Map;

@Singleton
public class TreePatchLocator
{
    private final Map<TreePatch, WorldArea> patchAreas =
            new EnumMap<>(TreePatch.class);

    public TreePatchLocator()
    {
        // =========================
        // NORMAL TREE PATCHES
        // =========================

        patchAreas.put(
                TreePatch.LUMBRIDGE,
                createArea(
                        3192, 3230,
                        3194, 3232
                )
        );

        patchAreas.put(
                TreePatch.FALADOR,
                createArea(
                        3003, 3372,
                        3005, 3374
                )
        );

        patchAreas.put(
                TreePatch.VARROCK,
                createArea(
                        3228, 3458,
                        3230, 3460
                )
        );

        patchAreas.put(
                TreePatch.TAVERLEY,
                createArea(
                        2935, 3437,
                        2937, 3439
                )
        );

        patchAreas.put(
                TreePatch.GNOME_STRONGHOLD_TREE,
                createArea(
                        2435, 3414,
                        2437, 3416
                )
        );

        patchAreas.put(
                TreePatch.FARMING_GUILD_TREE,
                createArea(
                        1231, 3735,
                        1233, 3737
                )
        );

        patchAreas.put(
                TreePatch.NEMUS_RETREAT,
                createArea(
                        1365, 3320,
                        1367, 3322
                )
        );

        // =========================
        // FRUIT TREE PATCHES
        // =========================

        patchAreas.put(
                TreePatch.GNOME_STRONGHOLD_FRUIT,
                createArea(
                        2475, 3445,
                        2476, 3446
                )
        );

        patchAreas.put(
                TreePatch.CATHERBY,
                createArea(
                        2860, 3433,
                        2861, 3434
                )
        );

        patchAreas.put(
                TreePatch.TREE_GNOME_MAZE,
                createArea(
                        2489, 3179,
                        2490, 3180
                )
        );

        patchAreas.put(
                TreePatch.BRIMHAVEN,
                createArea(
                        2764, 3212,
                        2765, 3213
                )
        );

        patchAreas.put(
                TreePatch.LLETYA,
                createArea(
                        2346, 3161,
                        2347, 3162
                )
        );

        patchAreas.put(
                TreePatch.FARMING_GUILD_FRUIT,
                createArea(
                        1242, 3758,
                        1243, 3759
                )
        );

        patchAreas.put(
                TreePatch.KASTORI_FRUIT,
                createArea(
                        1349, 3056,
                        1350, 3057
                )
        );

        // =========================
        // HARDWOOD PATCHES
        // =========================

        patchAreas.put(
                TreePatch.FOSSIL_ISLAND_HARDWOOD_1,
                createArea(
                        3700, 3835,
                        3704, 3839
                )
        );

        patchAreas.put(
                TreePatch.FOSSIL_ISLAND_HARDWOOD_2,
                createArea(
                        3707, 3832,
                        3709, 3834
                )
        );

        patchAreas.put(
                TreePatch.FOSSIL_ISLAND_HARDWOOD_3,
                createArea(
                        3714, 3834,
                        3716, 3836
                )
        );

        patchAreas.put(
                TreePatch.LOCUS_OASIS,
                createArea(
                        1686, 2971,
                        1688, 2973
                )
        );

        patchAreas.put(
                TreePatch.ANGLERS_RETREAT,
                createArea(
                        2470, 2702,
                        2472, 2704
                )
        );

        // =========================
        // CALQUAT PATCHES
        // =========================

        patchAreas.put(
                TreePatch.TAI_BWO_WANNAI,
                createArea(
                        2795, 3100,
                        2797, 3102
                )
        );

        patchAreas.put(
                TreePatch.SUMMER_SHORE,
                createArea(
                        3127, 2404,
                        3129, 2406
                )
        );

        patchAreas.put(
                TreePatch.KASTORI_CALQUAT,
                createArea(
                        1366, 3031,
                        1368, 3033
                )
        );

        // =========================
        // CELASTRUS
        // =========================

        patchAreas.put(
                TreePatch.FARMING_GUILD_CELASTRUS,
                createArea(
                        1243, 3749,
                        1245, 3751
                )
        );

        // =========================
        // REDWOOD
        // =========================

        patchAreas.put(
                TreePatch.FARMING_GUILD_REDWOOD,
                createArea(
                        1224, 3751,
                        1232, 3758
                )
        );

        // =========================
        // CRYSTAL TREE
        // =========================

        patchAreas.put(
                TreePatch.PRIFDDINAS_CRYSTAL,
                createArea(
                        3291, 6118,
                        3292, 6119
                )
        );
    }

    private WorldArea createArea(
            int x1,
            int y1,
            int x2,
            int y2
    )
    {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);

        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);

        int width =
                maxX - minX + 1;

        int height =
                maxY - minY + 1;

        return new WorldArea(
                minX,
                minY,
                width,
                height,
                0
        );
    }

    public TreePatch findPatch(
            WorldPoint location
    )
    {
        for (
                Map.Entry<TreePatch, WorldArea> entry :
                patchAreas.entrySet()
        )
        {
            if (
                    entry.getValue()
                            .contains(location)
            )
            {
                return entry.getKey();
            }
        }

        return null;
    }

    public WorldArea getPatchArea(
            TreePatch patch
    )
    {
        return patchAreas.get(patch);
    }
    public TreePatch findNearestPatch(
            WorldPoint location,
            int maxDistance
    )
    {
        TreePatch closestPatch = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Map.Entry<TreePatch, WorldArea> entry :
                patchAreas.entrySet())
        {
            WorldArea area = entry.getValue();

            if (area.getPlane() != location.getPlane())
            {
                continue;
            }

            int distance =
                    area.distanceTo(location);

            if (distance <= maxDistance
                    && distance < closestDistance)
            {
                closestPatch = entry.getKey();
                closestDistance = distance;
            }
        }

        return closestPatch;
    }
}