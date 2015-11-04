package com.massisframework.massis.app;


import com.eteks.sweethome3d.plugin.Plugin;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.massisframework.massis.sim.AbstractSimulation;
import com.massisframework.massis.sim.RecordedSimulation;
import com.massisframework.massis.sim.Simulation;
import com.massisframework.massis.sim.SimulationWithUI;
import com.massisframework.sweethome3d.additionaldata.AdditionalDataReader;
import com.massisframework.sweethome3d.additionaldata.AdditionalDataWriter;
import com.massisframework.sweethome3d.additionaldata.SweetHome3DAdditionalDataApplication;
import com.massisframework.sweethome3d.metadata.HomeMetadataLoader;
import com.massisframework.sweethome3d.plugins.BuildingMetadataPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sim.display.Console;
import sim.display.GUIState;
import sim.engine.SimState;

public class MASSISLauncher {

    private static final String launch_opt = "run-as";
    private static final String SIMULATOR = "SIMULATOR";
    private static final String EDITOR = "EDITOR";
    private static final String BUILDING_PATH = "BUILDING_PATH";
    private static final String building_path = "building-path";
    private static final String RESOURCE_FOLDER = "RESOURCE_FOLDER";
    private static final String resource_folder = "resource-folder";
    private static final String simulation_mode = "simulation-mode";
    private static final String SIMULATION = "SIMULATION";
    private static final String PLAYBACK = "PLAYBACK";
    private static final String save_location = "save-simulation-to";
    private static final String SIMULATION_RESULTS_FILE = "SIMULATION_RESULTS_FILE";
    private static final String GUI = "GUI";
    private static final String CONSOLE = "CONSOLE";
    private static final String display = "display";
    private static final String run_for = "run-for";
    private static final String STEPS = "STEPS";

    @SuppressWarnings("static-access")
    public static void main(String[] args)
    {
      
       

        CommandLineParser parser = new PosixParser();
        // create the Options
        Options options = new Options();

        options.addOption(new Option("help", "print this message"));

        options.addOption(OptionBuilder
                .withLongOpt(launch_opt)
                .withDescription(
                "Launchs the environment editor (" + EDITOR
                + "), or MASSIS Simulator (" + SIMULATOR + ")")
                .hasArg().withArgName(SIMULATOR + "|" + EDITOR).create());

        // ================================================================================
        // Simulation
        // A building is needed.
        options.addOption(OptionBuilder
                .withLongOpt(building_path)
                .withDescription(
                "With " + launch_opt + "=" + SIMULATOR
                + ". Path of the building file").hasArg()
                .withArgName(BUILDING_PATH).create());
        // Resource folder where to look the stuff
        options.addOption(OptionBuilder
                .withLongOpt(resource_folder)
                .withDescription(
                "With "
                + launch_opt
                + "="
                + SIMULATOR
                + ". Resources folder. The resources used/created during the simulation should be here.")
                .hasArg().withArgName(RESOURCE_FOLDER).create());
        options.addOption(OptionBuilder
                .withLongOpt(simulation_mode)
                .withDescription(
                "With " + launch_opt + "=" + SIMULATOR
                + ". Simulation mode.").hasArg()
                .withArgName(SIMULATION + "|" + PLAYBACK).create());
        options.addOption(OptionBuilder
                .withLongOpt(display)
                .withDescription(
                "With " + launch_opt + "=" + SIMULATOR
                + ". Display mode.").hasArg()
                .withArgName(CONSOLE + "|" + GUI).create());
        // Simulate
        options.addOption(OptionBuilder
                .withLongOpt(save_location)
                .withDescription(
                "With "
                + simulation_mode
                + "="
                + SIMULATION
                + ". Saves the simulation into the specified file.")
                .hasArg().withArgName(SIMULATION_RESULTS_FILE).create());


        options.addOption(OptionBuilder
                .withLongOpt(run_for)
                .withDescription(
                "With " + display + "=" + CONSOLE + ". Runs for "
                + STEPS + " steps.").hasArg()
                .withArgName(STEPS).create());
        // ================================================================================
        HelpFormatter formatter = new HelpFormatter();
        final String helpHeader = "java -jar MASSIS.jar";
        try
        {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help"))
            {

                formatter.printHelp(helpHeader, options);
                return;
            }
            // Mode
            if (!line.hasOption(launch_opt))
            {
                System.err.println(launch_opt + " is mandatory.");
                formatter.printHelp(helpHeader, options);
                return;

            }
            // By launch option
            String launch_optionValue = line.getOptionValue(launch_opt);
            if (EDITOR.equals(launch_optionValue))
            {
                launchEditor();
                return;
            } else
            {
                if (SIMULATOR.equals(launch_optionValue))
                {
                    if (!line.hasOption(building_path))
                    {
                        System.err.println(building_path + " is mandatory.");
                        formatter.printHelp(helpHeader, options);
                        return;
                    }
                    String buildingFilePath = line.getOptionValue(building_path);

                    if (!line.hasOption(resource_folder))
                    {
                        System.err.println(resource_folder + " is mandatory.");
                        formatter.printHelp(helpHeader, options);
                        return;
                    }
                    String resourceFolderPath = line
                            .getOptionValue(resource_folder);
                    if (!line.hasOption(simulation_mode))
                    {
                        System.err.println(simulation_mode + " is mandatory.");
                        formatter.printHelp(helpHeader, options);
                        return;
                    }
                    String simulationMode = line.getOptionValue(simulation_mode);
                    int runFor = 0;
                    if (!line.hasOption(display)
                            || (!line.getOptionValue(display).equals(GUI) && !line
                            .getOptionValue(display).equals(CONSOLE)))
                    {
                        System.err.println(display + " is mandatory.");
                        formatter.printHelp(helpHeader, options);
                        return;

                    }

                    String displayMode = line.getOptionValue(display);

                    if (displayMode.equals(CONSOLE))
                    {
                        if (!line.hasOption(run_for)
                                || !isNumeric(line.getOptionValue(run_for)))
                        {
                            System.err.println(run_for + " is mandatory.");
                            formatter.printHelp(helpHeader, options);
                            return;

                        } else
                        {
                            runFor = Integer.parseInt(line.getOptionValue(
                                    run_for));
                        }
                    }
                    if (simulationMode.equals(SIMULATION))
                    {

                        String saveLocation = null;
                        if (line.hasOption(save_location))
                        {
                            saveLocation = line.getOptionValue(save_location);

                        }

                        simulate(buildingFilePath, resourceFolderPath,
                                saveLocation, displayMode.equals(GUI), false,
                                runFor);
                    } else
                    {
                        if (simulationMode.equals(PLAYBACK))
                        {
                            String saveLocation = null;
                            if (line.hasOption(save_location))
                            {
                                saveLocation = line.getOptionValue(save_location);

                            }
                            if (!line.hasOption(display))
                            {
                                System.err.println(display + " is mandatory.");
                                formatter.printHelp(helpHeader, options);
                                return;

                            }

                            simulate(buildingFilePath, resourceFolderPath,
                                    saveLocation, displayMode.equals(GUI), true,
                                    runFor);
                        } else
                        {
                            System.err.println("Invalid " + simulation_mode
                                    + " value. (" + simulationMode + ")");
                            formatter.printHelp(helpHeader, options);
                            return;
                        }
                    }
                } else
                {
                    System.err.println("Invalid " + launch_opt + " value.");
                    formatter.printHelp(helpHeader, options);
                    return;
                }
            }

        } catch (ParseException e)
        {
            System.err.println("Error when parsing cmd args. ");
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static boolean isNumeric(String optionValue)
    {
        try
        {
            Integer.parseInt(optionValue);
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }

    private static void simulate(final String buildingFilePath,
            final String resourceFolderPath, String logFileLocation,
            boolean gui, boolean playback, int runFor) throws IOException
    {

        if (gui)
        {
            final LauncherProgressMonitor progressMonitor = new LauncherProgressMonitor();

            SimState simState = null;
            if (playback)
            {
                simState = new RecordedSimulation(System.currentTimeMillis(),
                        buildingFilePath, resourceFolderPath, progressMonitor,
                        logFileLocation);
            } else
            {
                simState = new Simulation(System.currentTimeMillis(),
                        buildingFilePath, resourceFolderPath,
                        logFileLocation, progressMonitor);

            }
            GUIState vid = new SimulationWithUI(simState);

            Console c = new Console(vid);

            c.setIncrementSeedOnStop(false);
            //
            c.pressPlay();
            c.pressPause();
            c.setVisible(true);

        } else
        {
            if (playback)
            {
                AbstractSimulation
                        .runSimulation(RecordedSimulation.class,
                        new String[]
                {
                    "-building", buildingFilePath,
                    "-resources", resourceFolderPath,
                    "-logfile", logFileLocation, "-for",
                    String.valueOf(runFor)
                });
            } else
            {
                if (logFileLocation == null)
                {
                    AbstractSimulation.runSimulation(Simulation.class,
                            new String[]
                    {
                        "-building", buildingFilePath,
                        "-resources", resourceFolderPath,
                        "-for", String.valueOf(runFor)
                    });
                } else
                {
                    AbstractSimulation.runSimulation(
                            Simulation.class,
                            new String[]
                    {
                        "-building", buildingFilePath,
                        "-resources", resourceFolderPath,
                        "-logfile", logFileLocation, "-for",
                        String.valueOf(runFor)
                    });
                }
            }
        }

    }

    private static void launchEditor()
    {
        System.setProperty("j3d.implicitAntialiasing", "true");
        System.setProperty("j3d.optimizeForSpace", "false");
        System.setProperty("sun.java2d.opengl", "false");
        HomeMetadataLoader metadataLoader = new HomeMetadataLoader();
        List<? extends AdditionalDataWriter> writers = Arrays.asList(
                metadataLoader);
        List<? extends AdditionalDataReader> loaders = Arrays.asList(
                metadataLoader);

        List<Class<? extends Plugin>> plugins = new ArrayList<>();
        plugins.add(BuildingMetadataPlugin.class);
        //SweetHome3D.main(new String[] {});
//        PluginLoader.runSweetHome3DWithPlugins(
//                BuildingLoaderPlugin.class,
//                DesignerToolsPlugin.class,
//                NameGenerationPlugin.class,
//                MetadataPlugin.class);
        SweetHome3DAdditionalDataApplication.run(
                new String[]
        {
        },
                loaders,
                writers,
                plugins);
    }
}
