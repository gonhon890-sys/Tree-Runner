package com.treerunner;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TreeRunnerPanel extends PluginPanel
{
    private static final Color ORANGE =
            new Color(255, 152, 31);

    private final TreeRunManager treeRunManager;
    private final TravelAvailabilityManager travelAvailabilityManager;
    private final PatchTracker patchTracker;
    private final ConfigManager configManager;

    private final Map<TreePatch, JCheckBox> patchCheckBoxes =
            new EnumMap<>(TreePatch.class);

    private final Map<TreePatch, JLabel> patchStatusLabels =
            new EnumMap<>(TreePatch.class);
    private final Map<TreeSpecies, ImageIcon> treeIconCache =
            new EnumMap<>(TreeSpecies.class);
    private JPanel patchTimesPanel;
    private JPanel currentRoutePanel;

    private final Timer patchTimesRefreshTimer;

    @Inject
    public TreeRunnerPanel(
            TreeRunManager treeRunManager,
            TravelAvailabilityManager travelAvailabilityManager,
            PatchTracker patchTracker,
            ConfigManager configManager
    )
    {
        this.treeRunManager = treeRunManager;
        this.travelAvailabilityManager =
                travelAvailabilityManager;
        this.patchTracker = patchTracker;
        this.configManager = configManager;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(null);

        // =========================
        // CUSTOM TABS
        // =========================

        CardLayout cardLayout =
                new CardLayout();

        JPanel tabContent =
                new JPanel(cardLayout);

        tabContent.setBackground(
                ColorScheme.DARK_GRAY_COLOR
        );

        tabContent.setBorder(null);

        JPanel runSetupPanel =
                createRunSetupPanel();

        patchTimesPanel =
                createPatchTimesPanel();

        tabContent.add(
                runSetupPanel,
                "RUN_SETUP"
        );

        tabContent.add(
                patchTimesPanel,
                "PATCH_TIMES"
        );

        JPanel tabBar =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                0,
                                0
                        )
                );

        tabBar.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        tabBar.setBorder(null);

        JButton runSetupTab =
                createTabButton(
                        "Run Setup",
                        true
                );

        JButton patchTimesTab =
                createTabButton(
                        "Patch Times",
                        false
                );

        runSetupTab.addActionListener(e ->
        {
            cardLayout.show(
                    tabContent,
                    "RUN_SETUP"
            );

            setSelectedTab(
                    runSetupTab,
                    patchTimesTab
            );
        });

        patchTimesTab.addActionListener(e ->
        {
            cardLayout.show(
                    tabContent,
                    "PATCH_TIMES"
            );

            setSelectedTab(
                    patchTimesTab,
                    runSetupTab
            );
        });

        tabBar.add(runSetupTab);
        tabBar.add(patchTimesTab);

        JPanel mainContainer =
                new JPanel(
                        new BorderLayout()
                );

        mainContainer.setBackground(
                ColorScheme.DARK_GRAY_COLOR
        );

        mainContainer.setBorder(null);

        mainContainer.add(
                tabBar,
                BorderLayout.NORTH
        );

        mainContainer.add(
                tabContent,
                BorderLayout.CENTER
        );

        add(
                mainContainer,
                BorderLayout.CENTER
        );

        // Restore checked route patches.
        loadSelectedPatches();

        // Build route automatically.
        saveRoute();

        // Style RuneLite outer scrollbar.
        styleOuterPanel();

        // Refresh timers every 60 seconds.
        patchTimesRefreshTimer =
                new Timer(
                        60000,
                        e -> refreshPatchTimes()
                );

        patchTimesRefreshTimer.start();
    }

    // =========================================================
    // RUN SETUP TAB
    // =========================================================

    private JPanel createRunSetupPanel()
    {
        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(
                new BoxLayout(
                        mainPanel,
                        BoxLayout.Y_AXIS
                )
        );

        mainPanel.setBackground(
                ColorScheme.DARK_GRAY_COLOR
        );

        mainPanel.setBorder(
                new EmptyBorder(
                        6,
                        6,
                        6,
                        6
                )
        );

        JLabel subtitle =
                new JLabel(
                        "Select patches for your tree run"
                );

        subtitle.setForeground(
                ColorScheme.LIGHT_GRAY_COLOR
        );

        subtitle.setFont(
                FontManager.getRunescapeSmallFont()
        );

        subtitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        mainPanel.add(subtitle);

        mainPanel.add(
                Box.createVerticalStrut(8)
        );

        // CURRENT ROUTE - ADD ONCE
        currentRoutePanel =
                createCurrentRoutePanel();

        currentRoutePanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        mainPanel.add(
                currentRoutePanel
        );

        mainPanel.add(
                Box.createVerticalStrut(8)
        );

        // TREE SECTIONS
        for (TreeType treeType :
                TreeType.values())
        {
            JPanel section =
                    createCollapsibleSection(
                            treeType
                    );

            section.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            mainPanel.add(section);

            mainPanel.add(
                    Box.createVerticalStrut(4)
            );
        }

        return mainPanel;
    }

    // =========================================================
    // PATCH TIMES TAB
    // =========================================================
    private JPanel createCurrentRoutePanel()
    {
        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        panel.setBorder(
                new EmptyBorder(
                        6,
                        7,
                        6,
                        7
                )
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                )
        );

        // =========================
        // TITLE
        // =========================

        JLabel heading =
                new JLabel(
                        "CURRENT ROUTE"
                );

        heading.setForeground(
                new Color(
                        255,
                        152,
                        31
                )
        );

        heading.setFont(
                FontManager.getRunescapeBoldFont()
        );

        heading.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(heading);

        panel.add(
                Box.createVerticalStrut(4)
        );

        List<TreeRunStep> route =
                treeRunManager.getRoute();

        if (route.isEmpty())
        {
            JLabel emptyLabel =
                    new JLabel(
                            "No patches selected"
                    );

            emptyLabel.setForeground(
                    ColorScheme.LIGHT_GRAY_COLOR
            );

            emptyLabel.setFont(
                    FontManager.getRunescapeSmallFont()
            );

            emptyLabel.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            panel.add(emptyLabel);

            return panel;
        }

        // =========================
        // ROUTE STEPS
        // =========================

        int currentIndex =
                treeRunManager.getCurrentIndex();

        boolean runActive =
                treeRunManager.isRunActive();

        for (int i = 0;
             i < route.size();
             i++)
        {
            TreeRunStep step =
                    route.get(i);

            String prefix;

            if (runActive && i < currentIndex)
            {
                prefix = "✓ ";
            }
            else if (runActive && i == currentIndex)
            {
                prefix = "→ ";
            }
            else
            {
                prefix = "  ";
            }

            JLabel stepLabel =
                    new JLabel(
                            prefix
                                    + getRouteStepDisplayName(
                                    step
                            )
                    );

            if (runActive && i == currentIndex)
            {
                stepLabel.setForeground(
                        new Color(
                                255,
                                152,
                                31
                        )
                );
            }
            else if (runActive && i < currentIndex)
            {
                stepLabel.setForeground(
                        ColorScheme.LIGHT_GRAY_COLOR
                );
            }
            else
            {
                stepLabel.setForeground(
                        Color.WHITE
                );
            }

            stepLabel.setFont(
                    FontManager.getRunescapeSmallFont()
            );

            stepLabel.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            panel.add(stepLabel);
        }

        // =========================
        // SAPLING REQUIREMENTS
        // =========================

        panel.add(
                Box.createVerticalStrut(6)
        );

        JLabel requirementsHeading =
                new JLabel(
                        "SAPLINGS REQUIRED"
                );

        requirementsHeading.setForeground(
                new Color(
                        255,
                        152,
                        31
                )
        );

        requirementsHeading.setFont(
                FontManager.getRunescapeBoldFont()
        );

        requirementsHeading.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(requirementsHeading);

        panel.add(
                Box.createVerticalStrut(2)
        );

        Map<TreeType, Integer> counts =
                getSaplingRequirements();

        JLabel mainTrees =
                new JLabel(
                        "T: "
                                + counts.getOrDefault(
                                TreeType.NORMAL,
                                0
                        )
                                + "    FT: "
                                + counts.getOrDefault(
                                TreeType.FRUIT,
                                0
                        )
                                + "    HW: "
                                + counts.getOrDefault(
                                TreeType.HARDWOOD,
                                0
                        )
                );

        mainTrees.setForeground(
                Color.WHITE
        );

        mainTrees.setFont(
                FontManager.getRunescapeSmallFont()
        );

        mainTrees.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(mainTrees);

        JLabel specialTrees =
                new JLabel(
                        "CALQ: "
                                + counts.getOrDefault(
                                TreeType.CALQUAT,
                                0
                        )
                                + "    CT: "
                                + counts.getOrDefault(
                                TreeType.CRYSTAL,
                                0
                        )
                                + "    CE: "
                                + counts.getOrDefault(
                                TreeType.CELASTRUS,
                                0
                        )
                                + "    RW: "
                                + counts.getOrDefault(
                                TreeType.REDWOOD,
                                0
                        )
                );

        specialTrees.setForeground(
                Color.WHITE
        );

        specialTrees.setFont(
                FontManager.getRunescapeSmallFont()
        );

        specialTrees.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(specialTrees);

        return panel;
    }

    private String getRouteStepDisplayName(
            TreeRunStep step
    )
    {
        if (step.getPatches().isEmpty())
        {
            return step.getDisplayName();
        }

        TreePatch patch =
                step.getPatches().get(0);

        return step.getDisplayName()
                + " ["
                + getTreeTypeAbbreviation(
                patch.getTreeType()
        )
                + "]";
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

    private Map<TreeType, Integer> getSaplingRequirements()
    {
        Map<TreeType, Integer> counts =
                new EnumMap<>(
                        TreeType.class
                );

        for (TreeRunStep step :
                treeRunManager.getRoute())
        {
            /*
             * Count physical patches rather than route
             * steps.
             *
             * This means Fossil Island correctly counts
             * as 3 hardwood saplings.
             */
            for (TreePatch patch :
                    step.getPatches())
            {
                TreeType type =
                        patch.getTreeType();

                counts.put(
                        type,
                        counts.getOrDefault(
                                type,
                                0
                        ) + 1
                );
            }
        }

        return counts;
    }
    private JPanel createPatchTimesPanel()
    {
        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBackground(
                ColorScheme.DARK_GRAY_COLOR
        );

        panel.setBorder(
                new EmptyBorder(
                        6,
                        6,
                        6,
                        6
                )
        );

        JLabel subtitle =
                new JLabel(
                        "Tracked tree patches"
                );

        subtitle.setForeground(
                ColorScheme.LIGHT_GRAY_COLOR
        );

        subtitle.setFont(
                FontManager.getRunescapeSmallFont()
        );

        subtitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(subtitle);

        panel.add(
                Box.createVerticalStrut(8)
        );

        if (patchTracker
                .getAllTrackedTrees()
                .isEmpty())
        {
            panel.add(
                    createEmptyStatusRow(
                            "No tracked trees yet"
                    )
            );

            return panel;
        }

        for (TrackedTree tree :
                patchTracker.getAllTrackedTrees())
        {
            panel.add(
                    createTrackedTreeRow(tree)
            );

            panel.add(
                    Box.createVerticalStrut(2)
            );
        }

        return panel;
    }

    // =========================================================
    // TREE TYPE SECTIONS
    // =========================================================

    private JPanel createCollapsibleSection(
            TreeType treeType
    )
    {
        JPanel sectionPanel =
                new JPanel();

        sectionPanel.setLayout(
                new BoxLayout(
                        sectionPanel,
                        BoxLayout.Y_AXIS
                )
        );

        sectionPanel.setBackground(
                ColorScheme.DARK_GRAY_COLOR
        );

        JButton headerButton =
                new JButton(
                        treeType.getDisplayName()
                                + "    ▼"
                );

        headerButton.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        headerButton.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        28
                )
        );

        headerButton.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        headerButton.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        headerButton.setForeground(
                ORANGE
        );

        headerButton.setFont(
                FontManager.getRunescapeBoldFont()
        );

        headerButton.setFocusPainted(false);
        headerButton.setBorderPainted(false);

        headerButton.setBorder(
                BorderFactory.createEmptyBorder(
                        4,
                        7,
                        4,
                        7
                )
        );

        headerButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        JPanel contentPanel =
                new JPanel();

        contentPanel.setLayout(
                new BoxLayout(
                        contentPanel,
                        BoxLayout.Y_AXIS
                )
        );

        contentPanel.setBackground(
                ColorScheme.DARK_GRAY_COLOR
        );

        contentPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        if (treeType == TreeType.HARDWOOD)
        {
            addHardwoodPatches(
                    contentPanel
            );
        }
        else
        {
            for (TreePatch patch :
                    TreePatch.values())
            {
                if (patch.getTreeType()
                        == treeType)
                {
                    contentPanel.add(
                            createPatchRow(
                                    patch
                            )
                    );
                }
            }
        }

        headerButton.addActionListener(e ->
        {
            boolean isOpen =
                    contentPanel.isVisible();

            contentPanel.setVisible(
                    !isOpen
            );

            headerButton.setText(
                    treeType.getDisplayName()
                            + (isOpen
                            ? "    ▶"
                            : "    ▼")
            );

            revalidate();
            repaint();
        });

        sectionPanel.add(headerButton);
        sectionPanel.add(contentPanel);

        return sectionPanel;
    }

    // =========================================================
    // NORMAL PATCH ROW
    // =========================================================

    private JPanel createPatchRow(
            TreePatch patch
    )
    {
        JPanel row =
                new JPanel(
                        new BorderLayout()
                );

        row.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        row.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                ColorScheme.DARK_GRAY_COLOR
                        ),
                        new EmptyBorder(
                                5,
                                7,
                                5,
                                5
                        )
                )
        );

        row.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        45
                )
        );

        JPanel textPanel =
                new JPanel();

        textPanel.setLayout(
                new BoxLayout(
                        textPanel,
                        BoxLayout.Y_AXIS
                )
        );

        textPanel.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        JLabel patchName =
                new JLabel(
                        patch.getDisplayName()
                );

        patchName.setForeground(
                Color.WHITE
        );

        patchName.setFont(
                FontManager.getRunescapeSmallFont()
        );

        JLabel status =
                new JLabel(
                        "Not selected"
                );

        status.setForeground(
                ColorScheme.LIGHT_GRAY_COLOR
        );

        status.setFont(
                FontManager.getRunescapeSmallFont()
        );

        textPanel.add(patchName);
        textPanel.add(status);

        JCheckBox checkBox =
                createStyledCheckBox("");

        patchCheckBoxes.put(
                patch,
                checkBox
        );

        patchStatusLabels.put(
                patch,
                status
        );

        checkBox.addActionListener(e ->
        {
            if (checkBox.isSelected())
            {
                status.setText(
                        "Selected"
                );
            }
            else
            {
                status.setText(
                        "Not selected"
                );
            }

            saveRoute();
        });

        row.add(
                textPanel,
                BorderLayout.CENTER
        );

        row.add(
                checkBox,
                BorderLayout.EAST
        );

        return row;
    }

    // =========================================================
    // HARDWOOD / FOSSIL ISLAND
    // =========================================================

    private void addHardwoodPatches(
            JPanel contentPanel
    )
    {
        contentPanel.add(
                createFossilIslandRow()
        );

        for (TreePatch patch :
                TreePatch.values())
        {
            if (
                    patch.getTreeType()
                            == TreeType.HARDWOOD
                            && patch
                            != TreePatch.FOSSIL_ISLAND_HARDWOOD_1
                            && patch
                            != TreePatch.FOSSIL_ISLAND_HARDWOOD_2
                            && patch
                            != TreePatch.FOSSIL_ISLAND_HARDWOOD_3
            )
            {
                contentPanel.add(
                        createPatchRow(
                                patch
                        )
                );
            }
        }
    }

    private JPanel createFossilIslandRow()
    {
        JPanel row =
                new JPanel(
                        new BorderLayout()
                );

        row.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        row.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                ColorScheme.DARK_GRAY_COLOR
                        ),
                        new EmptyBorder(
                                5,
                                7,
                                5,
                                5
                        )
                )
        );

        row.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        45
                )
        );

        JPanel textPanel =
                new JPanel();

        textPanel.setLayout(
                new BoxLayout(
                        textPanel,
                        BoxLayout.Y_AXIS
                )
        );

        textPanel.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        JLabel patchName =
                new JLabel(
                        "Fossil Island"
                );

        patchName.setForeground(
                Color.WHITE
        );

        patchName.setFont(
                FontManager.getRunescapeSmallFont()
        );

        patchName.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel status =
                new JLabel(
                        "Not selected"
                );

        status.setForeground(
                ColorScheme.LIGHT_GRAY_COLOR
        );

        status.setFont(
                FontManager.getRunescapeSmallFont()
        );

        status.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        textPanel.add(patchName);
        textPanel.add(status);

        JCheckBox checkBox =
                createStyledCheckBox("");

        patchCheckBoxes.put(
                TreePatch.FOSSIL_ISLAND_HARDWOOD_1,
                checkBox
        );

        patchCheckBoxes.put(
                TreePatch.FOSSIL_ISLAND_HARDWOOD_2,
                checkBox
        );

        patchCheckBoxes.put(
                TreePatch.FOSSIL_ISLAND_HARDWOOD_3,
                checkBox
        );

        checkBox.addActionListener(e ->
        {
            if (checkBox.isSelected())
            {
                status.setText(
                        "Selected"
                );
            }
            else
            {
                status.setText(
                        "Not selected"
                );
            }

            saveRoute();
        });

        row.add(
                textPanel,
                BorderLayout.CENTER
        );

        row.add(
                checkBox,
                BorderLayout.EAST
        );

        return row;
    }

    // =========================================================
    // CUSTOM ORANGE CHECKBOX
    // =========================================================

    private JCheckBox createStyledCheckBox(
            String text
    )
    {
        JCheckBox checkBox =
                new JCheckBox(text);

        checkBox.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        checkBox.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        checkBox.setForeground(
                Color.WHITE
        );

        checkBox.setFocusPainted(false);
        checkBox.setOpaque(true);

        checkBox.setIcon(
                new TreeRunnerCheckBoxIcon(
                        false
                )
        );

        checkBox.setSelectedIcon(
                new TreeRunnerCheckBoxIcon(
                        true
                )
        );

        checkBox.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        28
                )
        );

        return checkBox;
    }

    // =========================================================
    // PATCH TIME ROWS
    // =========================================================

    private JPanel createTrackedTreeRow(
            TrackedTree tree
    )
    {
        JPanel row =
                new JPanel(
                        new BorderLayout(
                                8,
                                0
                        )
                );

        row.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        row.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                2,
                                0,
                                ColorScheme.DARK_GRAY_COLOR
                        ),
                        new EmptyBorder(
                                6,
                                7,
                                6,
                                7
                        )
                )
        );

        row.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        48
                )
        );

        // =========================
        // TREE ICON
        // =========================

        ImageIcon treeIcon =
                getTreeIcon(
                        tree.getSpecies()
                );

        if (treeIcon != null)
        {
            JLabel iconLabel =
                    new JLabel(treeIcon);

            iconLabel.setPreferredSize(
                    new Dimension(
                            24,
                            24
                    )
            );

            iconLabel.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            iconLabel.setVerticalAlignment(
                    SwingConstants.CENTER
            );

            row.add(
                    iconLabel,
                    BorderLayout.WEST
            );
        }

        // =========================
        // PATCH + TIMER
        // =========================

        JPanel textPanel =
                new JPanel();

        textPanel.setLayout(
                new BoxLayout(
                        textPanel,
                        BoxLayout.Y_AXIS
                )
        );

        textPanel.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        JLabel patchName =
                new JLabel(
                        tree.getPatch()
                                .getDisplayName()
                                + " - "
                                + tree.getSpecies()
                                .getDisplayName()
                );

        patchName.setForeground(
                Color.WHITE
        );

        patchName.setFont(
                FontManager.getRunescapeSmallFont()
        );

        patchName.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel status =
                new JLabel(
                        getTrackedTreeStatus(
                                tree
                        )
                );

        if (tree.isExpectedReady())
        {
            status.setForeground(
                    new Color(
                            0,
                            200,
                            0
                    )
            );
        }
        else
        {
            status.setForeground(
                    ColorScheme.LIGHT_GRAY_COLOR
            );
        }

        status.setFont(
                FontManager.getRunescapeSmallFont()
        );

        status.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        textPanel.add(
                Box.createVerticalGlue()
        );

        textPanel.add(patchName);
        textPanel.add(status);

        textPanel.add(
                Box.createVerticalGlue()
        );

        row.add(
                textPanel,
                BorderLayout.CENTER
        );

        return row;
    }
    private ImageIcon getTreeIcon(
            TreeSpecies species
    )
    {
        if (species == null)
        {
            return null;
        }

        // Use cached icon if already loaded
        ImageIcon cachedIcon =
                treeIconCache.get(species);

        if (cachedIcon != null)
        {
            return cachedIcon;
        }

        try
        {
            String resourcePath =
                    species.getIconResourcePath();

            BufferedImage image =
                    ImageUtil.loadImageResource(
                            TreeRunnerPlugin.class,
                            resourcePath
                    );

            if (image == null)
            {
                System.out.println(
                        "Tree Runner could not find icon: "
                                + resourcePath
                );

                return null;
            }

            Image scaledImage =
                    image.getScaledInstance(
                            24,
                            24,
                            Image.SCALE_SMOOTH
                    );

            ImageIcon icon =
                    new ImageIcon(
                            scaledImage
                    );

            treeIconCache.put(
                    species,
                    icon
            );

            return icon;
        }
        catch (Exception e)
        {
            System.out.println(
                    "Tree Runner failed to load icon for: "
                            + species.getDisplayName()
            );

            return null;
        }
    }
    private JPanel createEmptyStatusRow(
            String text
    )
    {
        JPanel row =
                new JPanel(
                        new BorderLayout()
                );

        row.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        row.setBorder(
                new EmptyBorder(
                        7,
                        7,
                        7,
                        7
                )
        );

        JLabel label =
                new JLabel(text);

        label.setForeground(
                ColorScheme.LIGHT_GRAY_COLOR
        );

        label.setFont(
                FontManager.getRunescapeSmallFont()
        );

        row.add(
                label,
                BorderLayout.CENTER
        );

        return row;
    }

    private String getTrackedTreeStatus(
            TrackedTree tree
    )
    {
        if (tree.isExpectedReady())
        {
            return "Ready";
        }

        java.time.Duration remaining =
                java.time.Duration.between(
                        java.time.Instant.now(),
                        tree.getExpectedReadyAt()
                );

        long hours =
                remaining.toHours();

        long minutes =
                remaining
                        .minusHours(hours)
                        .toMinutes();

        return hours
                + "h "
                + minutes
                + "m remaining";
    }

    // =========================================================
    // ROUTE SAVING + LOCATION GROUPING
    // =========================================================

    private void saveRoute()
    {
        List<TreeRunStep> selectedSteps =
                new ArrayList<>();

        List<TreePatch> selectedPatches =
                new ArrayList<>();

        JCheckBox fossilIslandCheckBox =
                patchCheckBoxes.get(
                        TreePatch.FOSSIL_ISLAND_HARDWOOD_1
                );

        for (TreePatch patch :
                TreePatch.values())
        {
            if (
                    patch
                            == TreePatch.FOSSIL_ISLAND_HARDWOOD_1
                            || patch
                            == TreePatch.FOSSIL_ISLAND_HARDWOOD_2
                            || patch
                            == TreePatch.FOSSIL_ISLAND_HARDWOOD_3
            )
            {
                continue;
            }

            JCheckBox checkBox =
                    patchCheckBoxes.get(
                            patch
                    );

            if (
                    checkBox != null
                            && checkBox.isSelected()
            )
            {
                selectedPatches.add(
                        patch
                );
            }
        }

        // =========================================
        // GROUP PATCHES BY PHYSICAL DESTINATION
        // =========================================

        List<TreePatchGroup> groupOrder =
                new ArrayList<>();

        for (TreePatch patch :
                selectedPatches)
        {
            TreePatchGroup group =
                    patch.getPatchGroup();

            if (!groupOrder.contains(group))
            {
                groupOrder.add(group);
            }
        }

        for (TreePatchGroup group :
                groupOrder)
        {
            for (TreePatch patch :
                    selectedPatches)
            {
                if (patch.getPatchGroup()
                        != group)
                {
                    continue;
                }

                selectedSteps.add(
                        new TreeRunStep(
                                patch.getDisplayName(),
                                patch.getTreeType(),
                                List.of(patch),
                                getDefaultTravelMethod(
                                        patch
                                )
                        )
                );
            }

            /*
             * Put Fossil Island in its correct
             * destination-group position if selected.
             */
            if (
                    group
                            == TreePatchGroup.FOSSIL_ISLAND
                            && fossilIslandCheckBox != null
                            && fossilIslandCheckBox.isSelected()
            )
            {
                addFossilIslandStep(
                        selectedSteps
                );
            }
        }

        /*
         * Fossil Island is not part of selectedPatches
         * because its three physical patches are grouped.
         *
         * If selected, and its group was never encountered
         * above, add it here.
         */
        if (
                fossilIslandCheckBox != null
                        && fossilIslandCheckBox.isSelected()
        )
        {
            boolean alreadyAdded = false;

            for (TreeRunStep step :
                    selectedSteps)
            {
                if (step.getPatches().contains(
                        TreePatch.FOSSIL_ISLAND_HARDWOOD_1
                ))
                {
                    alreadyAdded = true;
                    break;
                }
            }

            if (!alreadyAdded)
            {
                addFossilIslandStep(
                        selectedSteps
                );
            }
        }

        treeRunManager.setRoute(
                selectedSteps
        );

        saveSelectedPatches();

        refreshCurrentRoute();
    }
    public void refreshCurrentRoute()
    {
        if (currentRoutePanel == null)
        {
            return;
        }

        Container parent =
                currentRoutePanel.getParent();

        if (parent == null)
        {
            return;
        }

        int index =
                -1;

        Component[] components =
                parent.getComponents();

        for (int i = 0;
             i < components.length;
             i++)
        {
            if (components[i]
                    == currentRoutePanel)
            {
                index = i;
                break;
            }
        }

        if (index == -1)
        {
            return;
        }

        JPanel newRoutePanel =
                createCurrentRoutePanel();

        newRoutePanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        parent.remove(
                currentRoutePanel
        );

        currentRoutePanel =
                newRoutePanel;

        parent.add(
                currentRoutePanel,
                index
        );

        parent.revalidate();
        parent.repaint();
    }

    private void addFossilIslandStep(
            List<TreeRunStep> selectedSteps
    )
    {
        selectedSteps.add(
                new TreeRunStep(
                        "Fossil Island",
                        TreeType.HARDWOOD,
                        List.of(
                                TreePatch.FOSSIL_ISLAND_HARDWOOD_1,
                                TreePatch.FOSSIL_ISLAND_HARDWOOD_2,
                                TreePatch.FOSSIL_ISLAND_HARDWOOD_3
                        ),
                        getDefaultTravelMethod(
                                TreePatch.FOSSIL_ISLAND_HARDWOOD_1
                        )
                )
        );
    }

    // =========================================================
    // SAVED CHECKBOXES
    // =========================================================

    private void saveSelectedPatches()
    {
        StringBuilder selected =
                new StringBuilder();

        for (TreePatch patch :
                TreePatch.values())
        {
            JCheckBox checkBox =
                    patchCheckBoxes.get(
                            patch
                    );

            if (
                    checkBox != null
                            && checkBox.isSelected()
            )
            {
                if (selected.length() > 0)
                {
                    selected.append(",");
                }

                selected.append(
                        patch.name()
                );
            }
        }

        configManager.setConfiguration(
                "treerunner",
                "savedRoutePatches",
                selected.toString()
        );
    }

    private void loadSelectedPatches()
    {
        String saved =
                configManager.getConfiguration(
                        "treerunner",
                        "savedRoutePatches"
                );

        if (
                saved == null
                        || saved.isEmpty()
        )
        {
            return;
        }

        String[] patchNames =
                saved.split(",");

        for (String patchName :
                patchNames)
        {
            try
            {
                TreePatch patch =
                        TreePatch.valueOf(
                                patchName
                        );

                JCheckBox checkBox =
                        patchCheckBoxes.get(
                                patch
                        );

                if (checkBox != null)
                {
                    checkBox.setSelected(
                            true
                    );

                    JLabel status =
                            patchStatusLabels.get(
                                    patch
                            );

                    if (status != null)
                    {
                        status.setText(
                                "Selected"
                        );
                    }
                }
            }
            catch (
                    IllegalArgumentException ignored
            )
            {
                // Ignore obsolete saved patch names.
            }
        }
    }

// =========================================================
// PATCH TIMES REFRESH
// =========================================================

    public void refreshPatchTimes()
    {
        SwingUtilities.invokeLater(() ->
        {
            if (patchTimesPanel == null)
            {
                return;
            }

            /*
             * Refresh the contents of the existing
             * Patch Times card instead of replacing
             * the card itself.
             *
             * This prevents CardLayout from switching
             * back to Run Setup during a timer refresh.
             */
            JPanel updatedPanel =
                    createPatchTimesPanel();

            patchTimesPanel.removeAll();

            Component[] updatedComponents =
                    updatedPanel.getComponents();

            for (Component component :
                    updatedComponents)
            {
                patchTimesPanel.add(
                        component
                );
            }

            patchTimesPanel.revalidate();
            patchTimesPanel.repaint();
        });
    }

    public void stopRefreshTimer()
    {
        patchTimesRefreshTimer.stop();
    }

    // =========================================================
    // TRAVEL
    // =========================================================

    private TravelMethod getDefaultTravelMethod(
            TreePatch patch
    )
    {
        return travelAvailabilityManager
                .getClosestAvailable(
                        patch
                );
    }

    // =========================================================
    // CUSTOM TAB BUTTONS
    // =========================================================

    private JButton createTabButton(
            String text,
            boolean selected
    )
    {
        JButton button =
                new JButton(text);

        button.setFont(
                FontManager.getRunescapeSmallFont()
        );

        button.setForeground(
                Color.WHITE
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        button.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        button.setPreferredSize(
                new Dimension(
                        0,
                        28
                )
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        if (selected)
        {
            button.setBackground(
                    ColorScheme.DARKER_GRAY_COLOR
            );

            button.setBorder(
                    BorderFactory.createMatteBorder(
                            0,
                            0,
                            2,
                            0,
                            ORANGE
                    )
            );
        }
        else
        {
            button.setBackground(
                    ColorScheme.DARK_GRAY_COLOR
            );

            button.setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            0,
                            2,
                            0
                    )
            );
        }

        return button;
    }

    private void setSelectedTab(
            JButton selected,
            JButton other
    )
    {
        selected.setBackground(
                ColorScheme.DARKER_GRAY_COLOR
        );

        selected.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        2,
                        0,
                        ORANGE
                )
        );

        other.setBackground(
                ColorScheme.DARK_GRAY_COLOR
        );

        other.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        2,
                        0
                )
        );
    }

    // =========================================================
    // OUTER PANEL / SCROLLBAR
    // =========================================================

    private void styleOuterPanel()
    {
        SwingUtilities.invokeLater(() ->
        {
            Container component =
                    this;

            while (component != null)
            {
                if (component
                        instanceof JScrollPane)
                {
                    JScrollPane scrollPane =
                            (JScrollPane) component;

                    scrollPane.setBorder(
                            BorderFactory
                                    .createLineBorder(
                                            Color.BLACK,
                                            1
                                    )
                    );

                    scrollPane
                            .getViewport()
                            .setBorder(null);

                    scrollPane
                            .getViewport()
                            .setBackground(
                                    ColorScheme.DARK_GRAY_COLOR
                            );

                    JScrollBar vertical =
                            scrollPane
                                    .getVerticalScrollBar();

                    styleScrollBar(
                            vertical
                    );

                    vertical.revalidate();
                    vertical.repaint();

                    scrollPane.revalidate();
                    scrollPane.repaint();

                    return;
                }

                component =
                        component.getParent();
            }
        });
    }

    private void styleScrollBar(
            JScrollBar scrollBar
    )
    {
        final Color track =
                ColorScheme.DARKER_GRAY_COLOR;

        scrollBar.setPreferredSize(
                new Dimension(
                        7,
                        0
                )
        );

        scrollBar.setMinimumSize(
                new Dimension(
                        7,
                        0
                )
        );

        scrollBar.setUnitIncrement(16);
        scrollBar.setOpaque(true);
        scrollBar.setBackground(track);
        scrollBar.setBorder(null);

        scrollBar.setUI(
                new BasicScrollBarUI()
                {
                    @Override
                    protected void configureScrollBarColors()
                    {
                        thumbColor = ORANGE;
                        thumbHighlightColor = ORANGE;
                        thumbLightShadowColor = ORANGE;
                        thumbDarkShadowColor = ORANGE;

                        trackColor = track;
                        trackHighlightColor = track;
                    }

                    @Override
                    protected JButton createDecreaseButton(
                            int orientation
                    )
                    {
                        return createZeroButton();
                    }

                    @Override
                    protected JButton createIncreaseButton(
                            int orientation
                    )
                    {
                        return createZeroButton();
                    }

                    private JButton createZeroButton()
                    {
                        JButton button =
                                new JButton();

                        Dimension zero =
                                new Dimension(
                                        0,
                                        0
                                );

                        button.setPreferredSize(
                                zero
                        );

                        button.setMinimumSize(
                                zero
                        );

                        button.setMaximumSize(
                                zero
                        );

                        return button;
                    }
                }
        );
    }

    // =========================================================
    // ORANGE CHECKBOX ICON
    // =========================================================

    private static class TreeRunnerCheckBoxIcon
            implements Icon
    {
        private static final int SIZE =
                14;

        private final boolean selected;

        private TreeRunnerCheckBoxIcon(
                boolean selected
        )
        {
            this.selected = selected;
        }

        @Override
        public void paintIcon(
                Component component,
                Graphics graphics,
                int x,
                int y
        )
        {
            Graphics2D g2 =
                    (Graphics2D)
                            graphics.create();

            if (selected)
            {
                g2.setColor(
                        ORANGE
                );

                g2.fillRect(
                        x,
                        y,
                        SIZE,
                        SIZE
                );

                g2.setColor(
                        ColorScheme
                                .DARKER_GRAY_COLOR
                );

                g2.setStroke(
                        new BasicStroke(2f)
                );

                g2.drawLine(
                        x + 3,
                        y + 7,
                        x + 6,
                        y + 10
                );

                g2.drawLine(
                        x + 6,
                        y + 10,
                        x + 11,
                        y + 4
                );
            }
            else
            {
                g2.setColor(
                        ColorScheme
                                .DARKER_GRAY_COLOR
                );

                g2.fillRect(
                        x,
                        y,
                        SIZE,
                        SIZE
                );

                g2.setColor(
                        ColorScheme
                                .LIGHT_GRAY_COLOR
                );

                g2.drawRect(
                        x,
                        y,
                        SIZE - 1,
                        SIZE - 1
                );
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth()
        {
            return SIZE;
        }

        @Override
        public int getIconHeight()
        {
            return SIZE;
        }
    }
}