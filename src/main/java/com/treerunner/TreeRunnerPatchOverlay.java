package com.treerunner;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

public class TreeRunnerPatchOverlay extends Overlay
{
    private static final Stroke OUTLINE_STROKE =
            new BasicStroke(2);

    private final Client client;
    private final TreeRunManager treeRunManager;
    private final TreePatchLocator treePatchLocator;
    private final TreeRunnerConfig config;

    @Inject
    public TreeRunnerPatchOverlay(
            TreeRunnerPlugin plugin,
            Client client,
            TreeRunManager treeRunManager,
            TreePatchLocator treePatchLocator,
            TreeRunnerConfig config
    )
    {
        super(plugin);

        this.client = client;
        this.treeRunManager = treeRunManager;
        this.treePatchLocator = treePatchLocator;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.highlightCurrentPatch())
        {
            return null;
        }

        TreeRunStep currentStep =
                treeRunManager.getCurrentStep();

        if (currentStep == null)
        {
            return null;
        }

        for (TreePatch patch :
                currentStep.getPatches())
        {
            WorldArea area =
                    treePatchLocator.getPatchArea(
                            patch
                    );

            if (area != null)
            {
                renderPatchObject(
                        graphics,
                        area
                );
            }
        }

        return null;
    }

    private void renderPatchObject(
            Graphics2D graphics,
            WorldArea patchArea
    )
    {
        if (patchArea.getPlane()
                != client.getPlane())
        {
            return;
        }

        Scene scene =
                client.getScene();

        if (scene == null)
        {
            return;
        }

        Tile[][][] tiles =
                scene.getTiles();

        int plane =
                client.getPlane();

        if (plane < 0
                || plane >= tiles.length)
        {
            return;
        }

        Stroke oldStroke =
                graphics.getStroke();

        graphics.setStroke(
                OUTLINE_STROKE
        );

        for (int sceneX = 0;
             sceneX < tiles[plane].length;
             sceneX++)
        {
            for (int sceneY = 0;
                 sceneY < tiles[plane][sceneX].length;
                 sceneY++)
            {
                Tile tile =
                        tiles[plane][sceneX][sceneY];

                if (tile == null)
                {
                    continue;
                }

                WorldPoint tileLocation =
                        tile.getWorldLocation();

                if (tileLocation == null
                        || !patchArea.contains(
                        tileLocation
                ))
                {
                    continue;
                }

                GameObject[] gameObjects =
                        tile.getGameObjects();

                if (gameObjects == null)
                {
                    continue;
                }

                for (GameObject gameObject :
                        gameObjects)
                {
                    if (gameObject == null)
                    {
                        continue;
                    }

                    /*
                     * Large game objects can appear on
                     * multiple tiles.
                     *
                     * Only render the object from its
                     * minimum scene location so it isn't
                     * highlighted multiple times.
                     */
                    if (!gameObject
                            .getSceneMinLocation()
                            .equals(
                                    tile.getSceneLocation()
                            ))
                    {
                        continue;
                    }

                    WorldPoint objectLocation =
                            gameObject
                                    .getWorldLocation();

                    if (objectLocation == null
                            || !patchArea.contains(
                            objectLocation
                    ))
                    {
                        continue;
                    }

                    Shape hull =
                            gameObject
                                    .getConvexHull();

                    if (hull == null)
                    {
                        continue;
                    }

                    /*
                     * Get the colour selected in
                     * Tree Runner settings.
                     */
                    Color outlineColor =
                            config.highlightColor();

                    /*
                     * Use the same colour with transparency
                     * for the inside of the patch.
                     */
                    Color fillColor =
                            new Color(
                                    outlineColor.getRed(),
                                    outlineColor.getGreen(),
                                    outlineColor.getBlue(),
                                    50
                            );

                    /*
                     * Fill the patch object.
                     */
                    graphics.setColor(
                            fillColor
                    );

                    graphics.fill(hull);

                    /*
                     * Draw the solid outline.
                     */
                    graphics.setColor(
                            outlineColor
                    );

                    graphics.draw(hull);
                }
            }
        }

        graphics.setStroke(
                oldStroke
        );
    }
}