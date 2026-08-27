package com.treerunner;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class TreeRunnerOverlay extends OverlayPanel
{
    /*
     * How close the player can be to any patch in the
     * location group before Tree Runner considers them
     * to already be at that destination.
     *
     * This is deliberately generous because locations
     * such as Gnome Stronghold contain patches that are
     * some distance apart.
     */
    private static final int LOCATION_DISTANCE = 50;

    private final Client client;
    private final TreeRunManager treeRunManager;
    private final TravelAvailabilityManager travelAvailabilityManager;
    private final TreePatchLocator treePatchLocator;

    @Inject
    private TreeRunnerOverlay(
            TreeRunnerPlugin plugin,
            Client client,
            TreeRunManager treeRunManager,
            TravelAvailabilityManager travelAvailabilityManager,
            TreePatchLocator treePatchLocator
    )
    {
        super(plugin);

        this.client = client;
        this.treeRunManager = treeRunManager;
        this.travelAvailabilityManager =
                travelAvailabilityManager;
        this.treePatchLocator =
                treePatchLocator;

        setPosition(
                OverlayPosition.TOP_LEFT
        );
    }

    @Override
    public Dimension render(
            Graphics2D graphics
    )
    {
        TreeRunStep currentStep =
                treeRunManager.getCurrentStep();

        if (currentStep == null)
        {
            return null;
        }

        // =========================
        // TITLE
        // =========================

        panelComponent.getChildren().add(
                TitleComponent.builder()
                        .text("Tree Run")
                        .build()
        );

        // =========================
        // PATCH NAME
        // =========================

        String patchName =
                getStepDisplayName(
                        currentStep
                );

        panelComponent.getChildren().add(
                LineComponent.builder()
                        .left(patchName)
                        .build()
        );

        // =========================
        // TRAVEL INSTRUCTION
        // =========================

        /*
         * Don't show a travel method when:
         *
         * 1. The previous route step was at the
         *    same location, OR
         *
         * 2. The player's actual position shows
         *    that they are already at the location.
         */
        boolean alreadyThere =
                isSameLocationAsPreviousStep()
                        || isPlayerAtCurrentLocation(
                        currentStep
                );

        if (!alreadyThere)
        {
            TravelMethod travelMethod =
                    getCurrentTravelMethod(
                            currentStep
                    );

            if (travelMethod != null)
            {
                panelComponent
                        .getChildren()
                        .add(
                                LineComponent.builder()
                                        .left(
                                                travelMethod
                                                        .getDisplayName()
                                        )
                                        .build()
                        );
            }
        }

        return super.render(graphics);
    }

    // =========================================================
    // CHECK PLAYER LOCATION
    // =========================================================

    private boolean isPlayerAtCurrentLocation(
            TreeRunStep currentStep
    )
    {
        if (client.getLocalPlayer() == null)
        {
            return false;
        }

        if (currentStep.getPatches().isEmpty())
        {
            return false;
        }

        WorldPoint playerLocation =
                client.getLocalPlayer()
                        .getWorldLocation();

        TreePatch currentPatch =
                currentStep.getPatches().get(0);

        TreePatchGroup currentGroup =
                currentPatch.getPatchGroup();

        /*
         * Check every patch belonging to this
         * destination group.
         *
         * For Farming Guild, for example, this
         * checks the Tree, Fruit, Celastrus and
         * Redwood areas.
         */
        for (TreePatch patch :
                TreePatch.values())
        {
            if (patch.getPatchGroup()
                    != currentGroup)
            {
                continue;
            }

            WorldArea area =
                    treePatchLocator
                            .getPatchArea(
                                    patch
                            );

            if (area == null)
            {
                continue;
            }

            if (area.getPlane()
                    != playerLocation.getPlane())
            {
                continue;
            }

            /*
             * Directly inside the patch area.
             */
            if (area.contains(
                    playerLocation
            ))
            {
                return true;
            }

            /*
             * Or close enough that we are clearly
             * already at this destination.
             */
            if (area.distanceTo(
                    playerLocation
            ) <= LOCATION_DISTANCE)
            {
                return true;
            }
        }

        return false;
    }

// =========================================================
// DISPLAY NAME
// =========================================================

    private String getStepDisplayName(
            TreeRunStep step
    )
    {
        if (step.getPatches().isEmpty())
        {
            return step.getDisplayName()
                    + " Patch";
        }

        TreePatch patch =
                step.getPatches().get(0);

        if (hasMultipleSelectedPatchesAtLocation(
                patch.getPatchGroup()
        ))
        {
            return patch.getDisplayName()
                    + " Patch ["
                    + getTreeTypeAbbreviation(
                    patch.getTreeType()
            )
                    + "]";
        }

        return patch.getDisplayName()
                + " Patch";
    }
    private boolean hasMultipleSelectedPatchesAtLocation(
            TreePatchGroup group
    )
    {
        int count = 0;

        for (TreeRunStep step :
                treeRunManager.getRoute())
        {
            if (step.getPatches().isEmpty())
            {
                continue;
            }

            TreePatch patch =
                    step.getPatches().get(0);

            if (patch.getPatchGroup() == group)
            {
                count++;

                if (count > 1)
                {
                    return true;
                }
            }
        }

        return false;
    }
    private String getTreeTypeAbbreviation(
            TreeType treeType
    )
    {
        switch (treeType)
        {
            case NORMAL:
                return "T";

            case FRUIT:
                return "FT";

            case HARDWOOD:
                return "HW";

            case CALQUAT:
                return "CALQ";

            case CRYSTAL:
                return "CT";

            case CELASTRUS:
                return "CE";

            case REDWOOD:
                return "RW";

            default:
                return treeType.getDisplayName();
        }
    }
    // =========================================================
    // PREVIOUS STEP LOCATION
    // =========================================================

    private boolean isSameLocationAsPreviousStep()
    {
        int currentIndex =
                treeRunManager.getCurrentIndex();

        if (currentIndex <= 0)
        {
            return false;
        }

        List<TreeRunStep> route =
                treeRunManager.getRoute();

        if (currentIndex >= route.size())
        {
            return false;
        }

        TreeRunStep current =
                route.get(currentIndex);

        TreeRunStep previous =
                route.get(
                        currentIndex - 1
                );

        if (current.getPatches().isEmpty()
                || previous.getPatches().isEmpty())
        {
            return false;
        }

        TreePatch currentPatch =
                current.getPatches().get(0);

        TreePatch previousPatch =
                previous.getPatches().get(0);

        return currentPatch.getPatchGroup()
                == previousPatch.getPatchGroup();
    }

    // =========================================================
    // DYNAMIC TRAVEL METHOD
    // =========================================================

    private TravelMethod getCurrentTravelMethod(
            TreeRunStep currentStep
    )
    {
        if (currentStep.getPatches().isEmpty())
        {
            return null;
        }

        TreePatch patch =
                currentStep.getPatches().get(0);

        return travelAvailabilityManager
                .getClosestAvailable(
                        patch
                );
    }
}