---
title : "Customizing the archetype"
---

This is the continuation of the [First tutorial][tutorial1], which explains how to create a MASSIS project based on an archetype.

## Remembering things: What is in the project?

After creation of the project with the MASSIS archetype, there are three Java classes, in package `tutorialfollower.myfirstmassisproject`:

- `EnvironmentEditor.java`: It contains the necessary code for launching the environment editor.
- `SimulationWithUILauncher.java`: This class is responsible for launching the simulator.
- `MyHelloHighLevelController`: An example of a very simple behavior for an agent.


## Configuring the environment.

MASSIS simulator uses the [Sweet Home 3D tool][sweethome3d] for the design of the environment. This tool has been extended with plugins, which allow the definition of links from elements in the environment to classes that define their behaviour. This information will be used by the simulation engine.

![The simulation, the environment and the behaviors of elements](http://i.imgur.com/z3aw4bl.png)

### Opening the editor and loading a default building

The Sweet Home 3D editor, with MASSIS plugins, is executed by running the `EnvironmentEditor` class. Select this class and try _Run As...-> Java Application_. The editor will appear:

![The Sweet Home 3D editor](http://i.imgur.com/pW8lgjG.png)

```
3D [dev] 1.6.0-scijava-1-pre11-daily-experimental daily

loading class com.massisframework.sweethome3d.plugins.BuildingMetadataPlugin
loading class com.massisframework.testdata.TestDataPlugin
```

This is the main window of the Sweet Home 3D editor. During the tutorials, some of the most important features of MASSIS and Sweet Home 3D editor will be explained. 

To start with a sample building, click on _Help -> Load Sample Home..._.

![Selecting Help -> Load Sample Home....](http://i.imgur.com/l9umHvv.png)

In the popup, select `basichouse.sh3d`.

![Load sample home window](http://i.imgur.com/7AnSp7i.png)

This opens a very simple one-room house, with four walls and one agent inside:

![The selected house in the editor](http://i.imgur.com/mqORvHr.png)

### Referencing the behavior of the agent.

Every element in the Sweet Home 3D editor (with Massis plugins) has some  metadata associated. One important data identifies the code where the behavior of the element is defined. In this tutorial we are going to associate a Java class that implements the behaviour of the agent.

In order to edit and modify the metadata of an element, the element **should be selected first**, then select the menu item _Tools -> Add Metadata_.

![Selection of the agent and the menu item Add Metadata in the Tools menu](http://i.imgur.com/mQZIAAp.png)

A popup window shows the metadata of the agent in the room:

![Window showing metadata associated to the agent](http://i.imgur.com/UF4NV2m.png)

- `IS_OBSTACLE` : It indicates whether the element is an obstacle or not. _This topic will be covered later_.
- `ID` : A unique identifier for the element in the building. **It is highly recommended to leave this value unchanged. Do not modify it.**
- `IS_DYNAMIC` : It indicates whether the element is going to move during the simulation. This is useful to the pathfinder algorithm of the simulator and the visualization toolkit. _This topic will be covered later_.
- `CLASSNAME` : The reference to the class implementing the agent behavior. By default, its value is `com.massisframework.testdata.ai.HelloHighLevelController`. 

In this tutorial this value is changed to a custom implementation, which was provided by the archetype. This is the class `MyHelloHighLevelController`, which has been mentioned before. Therefore, modify this value to:
```
tutorialfollower.myfirstmassisproject.MyHelloHighLevelController
```

### Saving the sample home in another location.

This house is in a  **temporary** location, therefore it has to be saved in a place where it can be easily found by the simulation engine. In this case it will be saved in the project root folder.

This is done by selecting the  _Save As..._ option in the _File_ menu. In the Save popup window, navigate to the project folder (normally, `myfirstmassisproject`, in the workspace directory) and save the file as `Tutorial1.sh3d`.

![Save the house in the project directory](http://i.imgur.com/775jxVI.png)

>Note: Remember to refresh the eclipse project, in order to check that the file has been actually saved there.

If everything is right, exit the editor with the _Exit_ option in the _File_ menu.

## Edit the simulation launcher class

The `SimulationWithUILauncher` class has the code to launch the simulation. It has to make reference to the house that has been created, which is the environment for the simulation. The elements that have a particular behaviour, such as the agent, will be managed by the simulator by using the metadata that was edited before, which can be found in the file describing the house.

In this case, edit the `SimulationWithUILauncher` class in order to change the sentence:

```
buildingFilePath = SampleHomesLoader.loadHomeTempFile("basichouse").getAbsolutePath();
```

by the following code, which indicates the path to the file where the house has been saved (in this case in the same directory of the project, so only the name of the file is needed):

```
buildingFilePath = "Tutorial1.sh3d";
```

Also remove the try-catch block, which is not longer necessary. 

Save the file.

## Running the simulation

The simulation is started by selecting the `SimulationWithUILauncher` class and then _Run As...-> Java Application_.

This launches the MASSIS default GUI (a console for controlling the simualation):

![](http://i.imgur.com/kPN5EI7.png)

and some console logs:

```
current-max-id: 17
Creating level NONAME
======================================================
Creating floor from level [NONAME]
Initializing simulation objects..
# of SimulationObjects: 1
# of rooms: 1
======================================================
Before/After Reduction : 4/2,[22=>16] took 6 ms

```

By clicking on the play button (the first on the bottom left of the window), the simulation goes step by step. The agent in this case is very simple and only prints a message, which should be seen in the console:


```
Hey! I am an agent!

```

The simulation can be finalized by selecting the _Quit_ option in the _File_ menu of the simulation window.

# What to do next?

The next part of the tutorial, [Creating a MASSIS Application. Part 3: Defining a simple behavior][tutorial3], will show how to modify the behaviour of the agent. 


[tutorial1]: {{ site.baseurl }}/tutorials/01-sample-massis-application.html
[tutorial3]: {{ site.baseurl }}/tutorials/03-defining-a-simple-behavior.html
[sweethome3d]: http://www.sweethome3d.com/
