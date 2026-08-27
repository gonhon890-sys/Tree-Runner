package com.treerunner;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Singleton
public class TreeRunManager
{
    private final List<TreeRunStep> route =
            new ArrayList<>();

    private final Set<TreePatch> completedPatches =
            EnumSet.noneOf(TreePatch.class);

    private final PatchTracker patchTracker;
    private final TreeRunnerConfig config;

    private int currentIndex = 0;
    private boolean runActive = false;

    @Inject
    public TreeRunManager(
            PatchTracker patchTracker,
            TreeRunnerConfig config
    )
    {
        this.patchTracker = patchTracker;
        this.config = config;
    }

    public void setRoute(
            List<TreeRunStep> steps
    )
    {
        route.clear();
        route.addAll(steps);

        completedPatches.clear();

        if (route.isEmpty())
        {
            currentIndex = 0;
            runActive = false;
            return;
        }

        restoreProgressFromPatchTimers();
    }

    private void restoreProgressFromPatchTimers()
    {
        int latestStepIndex = -1;
        Instant latestPlantingTime = null;

        for (int i = 0; i < route.size(); i++)
        {
            TreeRunStep step = route.get(i);

            for (TreePatch patch : step.getPatches())
            {
                TrackedTree trackedTree =
                        patchTracker.getTrackedTree(
                                patch
                        );

                if (trackedTree == null)
                {
                    continue;
                }

                Instant plantedAt =
                        trackedTree.getPlantedAt();

                if (latestPlantingTime == null
                        || plantedAt.isAfter(
                        latestPlantingTime
                ))
                {
                    latestPlantingTime =
                            plantedAt;

                    latestStepIndex = i;
                }
            }
        }

        if (latestStepIndex == -1)
        {
            currentIndex = 0;
            runActive = true;
            return;
        }

        int nextIndex =
                latestStepIndex + 1;

        if (nextIndex >= route.size())
        {
            currentIndex = 0;
            runActive = false;
            return;
        }

        currentIndex = nextIndex;
        runActive = true;
    }

    public void checkPrimeRun()
    {
        if (runActive)
        {
            return;
        }

        if (route.isEmpty())
        {
            return;
        }

        TreeType triggerType =
                config.runTriggerType();

        boolean triggerTypeInRoute = false;

        for (TreeRunStep step : route)
        {
            for (TreePatch patch : step.getPatches())
            {
                if (patch.getTreeType() == triggerType)
                {
                    triggerTypeInRoute = true;
                    break;
                }
            }

            if (triggerTypeInRoute)
            {
                break;
            }
        }

        if (!triggerTypeInRoute)
        {
            return;
        }

        if (patchTracker.isTreeTypeReady(triggerType))
        {
            startRun();
        }
    }

    public List<TreeRunStep> getRoute()
    {
        return Collections.unmodifiableList(
                route
        );
    }

    public TreeRunStep getCurrentStep()
    {
        if (!runActive || route.isEmpty())
        {
            return null;
        }

        if (currentIndex < 0
                || currentIndex >= route.size())
        {
            return null;
        }

        return route.get(currentIndex);
    }

    public TreeRunStep getNextStep()
    {
        if (!runActive)
        {
            return null;
        }

        int nextIndex =
                currentIndex + 1;

        if (nextIndex >= route.size())
        {
            return null;
        }

        return route.get(nextIndex);
    }

    public void markPatchCompleted(
            TreePatch patch
    )
    {
        TreeRunStep currentStep =
                getCurrentStep();

        if (currentStep == null)
        {
            return;
        }

        if (!currentStep
                .getPatches()
                .contains(patch))
        {
            return;
        }

        completedPatches.add(
                patch
        );

        if (completedPatches.containsAll(
                currentStep.getPatches()
        ))
        {
            advanceToNextStep();
        }
    }

    public boolean isPatchCompleted(
            TreePatch patch
    )
    {
        return completedPatches.contains(
                patch
        );
    }

    public void advanceToNextStep()
    {
        if (!runActive)
        {
            return;
        }

        currentIndex++;

        completedPatches.clear();

        if (currentIndex >= route.size())
        {
            finishRun();
        }
    }

    public void goToPreviousStep()
    {
        if (!runActive)
        {
            return;
        }

        if (currentIndex > 0)
        {
            currentIndex--;
            completedPatches.clear();
        }
    }

    public void startRun()
    {
        if (!route.isEmpty())
        {
            currentIndex = 0;
            completedPatches.clear();
            runActive = true;
        }
    }

    public void finishRun()
    {
        runActive = false;
        currentIndex = 0;
        completedPatches.clear();
    }

    public boolean isRunActive()
    {
        return runActive;
    }

    public int getCurrentIndex()
    {
        return currentIndex;
    }

    public int getRouteSize()
    {
        return route.size();
    }

    public void clearRoute()
    {
        route.clear();
        currentIndex = 0;
        completedPatches.clear();
        runActive = false;
    }
}