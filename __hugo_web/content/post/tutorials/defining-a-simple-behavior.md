+++
date = "2015-11-06T13:19:52+01:00"
draft = false
title = "Creating a MASSIS Application. Part 3: Defining a simple behavior"
categories = ["tutorials"]
+++

This is the continuation of the [Second tutorial]({{< relref "post/tutorials/customizing-the-archetype.md" >}}).
In the previous tutorial, we learnt how to change properties of the elements of the environment and launching the simulation. In this tutorial, we will learn the basics of the simulation gui, and a simple example about how to manage the behavior of the agent in the environment.

# The simulation GUI.
In the previous tutorial, we executed `SimulationWithUILauncher`, and the following window appeared.Let's explain briefly the contents of this window.

- About : Shows some a welcome screen, and copyright imformation. The three buttons on the bottom-left side control the most basic functionalities of the simulator:
     - Advance one step
     - Run continuously
     - Stop
{{< fig "http://i.imgur.com/kPN5EI7.png" >}}

- Console : Controls simulation parameters.
     - Delay (Sec/Step) : Delay between steps in the simulation
     - Steps per Step-button : Number of steps to be executed every time the play-pause button is pressed.
{{< fig "http://i.imgur.com/SI7dSrf.png" >}}

- Displays: This tab offers different ways for visualizing the state of the simulation. They can be shown or hidden anytime, with the simulation running also.  {{< fig "http://i.imgur.com/PptfTUD.png" >}}  There are three different types of visualization:
	- Building map : Shows the 2D layer- based map. This is the most powerful, flexible and customizable visualization tool of MASSIS. This type of display is deeply explained in  {{< tooltipurl url="#" linktext="this tutorial" tooltiptext="Not written yet :(" >}}. {{< fig "http://i.imgur.com/IcRLXtI.png" >}}
	- Building 2D :  Shows SweetHome3D default 2D viewer. It is included because maybe it is useful in the future. Although it is not recommended: Is not optimized for the simulation, and has thread-safety issues.{{< fig "http://i.imgur.com/2tnx3mF.png" >}}
	- Building 3D: Shows the 3D viewer. The camera can be controlled with the mouse or keyboard
	- Keyboard:
		- `W` moves the camera backwards
		- `S` moves the camera forward.
		- `Caps lock` (&#8682;) makes the movement of the camera faster.
	- Mouse:
		- The mouse wheel moves the camera backwards/forward
		- The mouse movement changes the view direction

# First Run

Ok, so let's press the _Play_ (![](http://i.imgur.com/D6C9dta.png?1)) button. What happens?

1. The _step counter_ increases its value very fast
2. The output of the console is always the same:

        Hey! I am an agent!
        Hey! I am an agent!
        Hey! I am an agent!
        Hey! I am an agent!
        Hey! I am an agent!
        Hey! I am an agent!
        Hey! I am an agent!
        Hey! I am an agent!
        Hey! I am an agent!
        Hey! I am an agent!
        Hey! I am an agent!
        ... etc
3. Nothing moves.

Why? because the behavior of the agent is exactly that. If we take a look into `MyHelloHighLevelController.java`, the `step()` method looks like this:




