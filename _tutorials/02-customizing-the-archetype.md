---
title : "Creating a MASSIS Application. Customizing the archetype"
---

This is the continuation of the [First tutorial](/tutorials/01-sample-massis-application), which explains how to create a MASSIS project based on an archetype.

## Remembering things: What is in the project?

- `EnvironmentEditor.java` : Contains the necessary code for launching the environment editor.
- `SimulationWithUILauncher.java` : This class is responsible for launching the simulator
- `MyHelloHighLevelController` : A behavior definion sample.


## Configuring the environment.

MASSIS simulator uses sweethome3d as environment definition. The behavior of the elements should be specified in the building file, and implemented as a class.

![](http://i.imgur.com/z3aw4bl.png)

### Opening the editor and loading a default building

Running the `EnvironmentEditor` class (Run As...-> Java Application) should open SweetHome3D's editor with some of the MASSIS' plugins.

![](http://i.imgur.com/pW8lgjG.png)

```
3D [dev] 1.6.0-scijava-1-pre11-daily-experimental daily

loading class com.massisframework.sweethome3d.plugins.BuildingMetadataPlugin
loading class com.massisframework.testdata.TestDataPlugin
```

This is the main window of the SweetHome3D editor. During the tutorials, some of the most important features of MASSIS and Sweethome3D editor will be explained. For loading a sample building, click on _Help -> Load Sample Home..._.

![](http://i.imgur.com/l9umHvv.png)

In the popup, select "basichouse.sh3d".

![](http://i.imgur.com/7AnSp7i.png)

A new home will be opened. This basic home contains a basic agent, four walls and a room.

![](http://i.imgur.com/mqORvHr.png)

### Referencing the behavior of the agent.

Every element in the SweetHome3D editor (with massis plugins) has metadata attached.
For modifing the metadata of an element, the element **should be selected first** and clicking on _Tools -> Add Metadata_.

![](http://i.imgur.com/mQZIAAp.png)

Let's see what kind of metadata attached has the agent in the room.

![](http://i.imgur.com/UF4NV2m.png)

- `IS_OBSTACLE` : Tells if this element is an obstacle or not. **This topic will be covered later**.
- `ID` : The unique id of the element in the building. **It is not recomended to change the value**
- `IS_DYNAMIC` : If the element is going to move during the simulation. Acts as a hint for the pathfinder and the visualization toolkit. **This topic will be covered later**.
- `CLASSNAME` : The reference to the class implementing the agent behavior. By default, its value is `com.massisframework.testdata.ai.HelloHighLevelController`. We want to change this to our custom implementation. So, the value, in this case should be
```
tutorialfollower.myfirstmassisproject.MyHelloHighLevelController
```

### Saving the sample home in another location.

Maybe you have noticed that the name of the home is kind of strange. Something like _MASSIS_278234761.sh3d_. This name is like this because the location of this home is **temporary**. In order to preserve the changes, must be saved in a **non temporary** location.

Let's save the home in the project root folder for making easier the access from the simulator, in this tutorial.
Cliking _Save As..._ -> Tutorial1.sh3d on the project's root folder.

![](http://i.imgur.com/775jxVI.png)

>Note: remember to refresh the eclipse project, in order to see if the file it is actually there.

## Running the simulation

Once saved, let's run our first simulation. First, we need to change the path of the building file.
In `SimulationWithUILauncher`, change

```
buildingFilePath = SampleHomesLoader.loadHomeTempFile("basichouse").getAbsolutePath();
```

with

```
buildingFilePath = "Tutorial1.sh3d";
```

And remove the try-catch, it is not necessary anymore.

It should appear the MASSIS default gui,

![](http://i.imgur.com/kPN5EI7.png)

and some console logs

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

What's next? [Creating a MASSIS Application. Part 3: Defining a simple behavior](/tutorials/03-defining-a-simple-behavior)
