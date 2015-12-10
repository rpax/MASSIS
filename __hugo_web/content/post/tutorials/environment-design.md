+++
date = "2056-11-05T10:21:39+01:00"
draft = false
title = "Designing the simulation environment"
categories = ["tutorials"]
+++

## Environment editor

The environment editor of MASSIS is  [SweetHome3D](http://www.sweethome3d.com), which has been extended with plugins, for connecting the editor with the simulation.
More information about extending SweetHome3D via plugins can be found in the [Plugins' developer guide](http://www.sweethome3d.com/pluginDeveloperGuide.jsp)

>**More information:**
The code for SweetHome3D is also [available at github](https://github.com/rpax/sweethome3d), and configured as maven dependency.

>**More information:**
 Note that the editor launched *is not exactly* the SweetHome3D editor. The main class of the editor extends `SweetHome3D` class, with support for loading plugins at runtime. This can be used for other applications. The source code can be found at
>
>https://github.com/rpax/massis-sh3d-plugins/tree/master/sh3d-additionaldata-app
>
>More information about how to load custom plugins dynamically can be found
>[here](/post/loading-custom-plugins-dynamically).

## Designing the environment
### Creating & Editing walls
The creation of walls can be done in two ways:
* Selecting Plan → Create walls

  ![](http://i.imgur.com/dkkDSaR.png)

* Pressing the ![](http://i.imgur.com/i4qzaDX.png) icon.

After that, every left click in the map will create a wall. For finishing the wall creation mode, just click the arrow icon or press the Esc key.

![](http://i.imgur.com/DAiFfI5.png)

### Adding doors, windows & furniture
The furniture, doors and windows can be selected from the left pane, and can be placed into the building by dragging and dropping them. **Note**: it is important to place the doors and windows in the walls. Otherwise, the simulation may not work correctly.

![](http://i.imgur.com/bdr0Vvp.png)

### Importing 3D objects
Extra 3D objects can be imported, and configured as window/door/furniture, from Furniture → Import Furniture.
### Drawing rooms
Rooms can be added the same way as walls, using the  button or selecting the option in Plan → Create Rooms.
_**Very important note**_: This way is highly discouraged for creating environments for MASSIS. As MASSIS uses the rooms in its preprocessing algorithms, a bad placement of them can make the simulation behave incorrectly, or even failing. The preferred way is double clicking a closed area, surrounded by walls. In this way, the new room will occupy the entire space.

![](http://i.imgur.com/1SjFlDv.png)

Levels can be added by clicking the + icon in the Levels tab

![](http://i.imgur.com/csxX8hB.png)

## Metadata Editor

MASSIS needs extra, user-provided information about the elements of the building. This information is stored as metadata inside each element of the building. Every element in the building has metadata, can be viewed and edited directly from Tools → Add Metadata. This popups a simple modal dialog, with input fields in the form of  key – value pairs. These values are read and processed later by MASSIS.

![](http://i.imgur.com/sq3FNZE.png)



## Teleport Linking
In a building of multiple floors, the agents must be capable of moving through the different floors of the building. This is done via _Teleports_ : Special elements in the building that, as the name suggests, they teleport the agent from one location to another, and they are unidirectional.

A correctly configured teleport consists on two elements, representing the origin area and the destination area. Any furniture object can be configured as teleport. However, MASSIS release comes with a group of 3D objects designed with this purpose, in order to make easier the design and recognition of them.

![](http://i.imgur.com/79suUPY.png)


A Teleport object must have the following parameters,
`type` : `START` or `END`
`teleport`: the teleport name
So, in the building, there would be two elements with the same teleport attribute, one with `START` as value of type, and the other one with `END` as value. The meradata of an element conforming the Teleport `SB1_B_LEFT` would be like this:

![](http://i.imgur.com/jwtsqoT.png)

## Other MASSIS' Design Utilities

>**Important Note**: Due to some important changes in the MASSIS' framework during November 2015, this feature has been temporarily disabled. Although The code can be found in the [main MASSIS repository](https://github.com/rpax/MASSIS/tree/master/massis-sh3d-plugins), it is not integrated in the application. These plugins will be moved to the [plugins repository](https://github.com/rpax/massis-sh3d-plugins/) as soon as possible. The information shown below it is not longer valid.

MASSIS comes with some other design utilities, that can be found at Tools → Designer tools or Name Generation.
### Designer tools
This plugin can make the external walls invisible(not every wall, so the interior of the building can be viewed from the outside, without making all the walls invisible).
One of the most boring things when designing a building is doing repetitive tasks one by one. For example, the color of the floor. This must be done clicking and modifying each room, in the SweetHome3D's way. With the Rooms Color plugin, this task can be done automatically.

![](http://i.imgur.com/gDWh2vBm.png)

### Name Generation

This plugin gives a name with the specified prefix to every room, or door in the building.

![](http://i.imgur.com/rKIMX5W.png)

## Final notes
This section is not intended to replace SweetHome3D's manual, only as a getting started guide. For more information, refer to the [SweetHome3D manual](http://www.sweethome3d.com/userGuide.jsp)


