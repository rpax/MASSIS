+++
date = "2015-11-06T13:19:52+01:00"
draft = false
title = "Creating a MASSIS Application. Part 4: Bigger environment and multiple agents"
categories = ["tutorials"]
+++

This is the continuation of the [Third tutorial][tutorial_3]. In this tutorial, we will improve the environment and we will add more agents to the simulation.

# Editing the environment

Let's add more elements to the environment. For that, we need to launch the editor (`EnvironmentEditor.java`).
Sweethome3D saves the recent buildings edited, so clicking on _File -> Open Recent -> Tutorial1.sh3d_ will open the our simulation environment.

{{< fig "http://i.imgur.com/5JPhrUn.gif" >}}

## Adding walls & rooms

Walls can be added clicking on _Plan -> Create Walls_, with the shortcut `Ctrl + Shift + W` or pressing the ![](http://i.imgur.com/bc5HLBQ.png) button.

{{< fig "http://i.imgur.com/42HAACQ.gif" >}}

The editor supports panning and zooming. Zooming can be done pressing `Ctrl` while moving the mouse wheel. Also, it helps with connecting one wall with another. When the mouse is near the wall corners, a bigger point is shown, in order to make easier this task.
{{<fig "http://i.imgur.com/ig7DusU.gif" >}}

Adding rooms to the scene can be accomplished clicking in _Plan-> Create Rooms_, or in the shorcut button ![](http://i.imgur.com/vOnfWjk.png). Can be added _point-per-point_, but the recommended way is making double click in an area enclosed by walls. It is **very recommended** to do it that way, because makes easier the environment recognition by the simulator.
{{<fig "http://i.imgur.com/busq3sc.gif" >}}




[tutorial_3]: {{< relref "post/tutorials/defining-a-simple-behavior.md" >}}