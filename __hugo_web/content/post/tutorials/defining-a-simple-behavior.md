+++
date = "2015-11-06T13:19:52+01:00"
draft = false
title = "Creating a MASSIS Application. Part 3: Defining a simple behavior"
categories = ["tutorials"]
+++

This is the continuation of the [Second tutorial]({{< relref "post/tutorials/customizing-the-archetype.md" >}}).

## Simulation GUI explained.

When launching `SimulationWithUILauncher`, appears a gui with the following tabs:

#### About

Shows the MASSIS' logo and copyright information

![](http://i.imgur.com/kPN5EI7.png)



#### Console

Controls simulation parameters.

- Delay (Sec/Step) : Delay between steps in the simulation
- Steps per Step-button : Number of steps to be executed every time the play-pause button is pressed.

![](http://i.imgur.com/SI7dSrf.png)

#### Displays

![](http://i.imgur.com/PptfTUD.png)



- Building map : Shows the 2D layer- based map.

![](http://i.imgur.com/2Ba54YT.png)

>TODO: explain layers.

![](http://i.imgur.com/IcRLXtI.png)

- Building 2D

Shows SweetHome3D default 2D viewer (not optimized,not recommended, may raise errors)

![](http://i.imgur.com/2tnx3mF.png)

- Building 3D

Shows the 3D viewer.

![](http://i.imgur.com/YZGH1rM.png)
