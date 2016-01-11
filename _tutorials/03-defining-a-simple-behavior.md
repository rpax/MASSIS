---
title : "Defining a simple agent behavior"
---

The [previous part of the tutorial][tutorial2] has shown how to change properties of the elements of the environment and launch the simulation. This part of the tutorial explains the basics of the simulation GUI, and a simple example about how to manage the behavior of the agent in the environment.

## The simulation GUI
Launching the simulation by running the  `SimulationWithUILauncher` class makes appear the following window, which allows to monitor and control the execution of the simulation. This window consists of the following tabs:

- _About_: This tab shows a welcome screen and copyright imformation. 

    The three buttons on the bottom-left side control the most basic functionalities of the simulator:
     - Advance one step of the simulation.
     - Run continuously.
     - Stop (a simulation running).

    ![The About tab](http://i.imgur.com/kPN5EI7.png)

- _Console_: This tab shows the simulation parameters, which can be modified through the respective controls:
    - Delay (Sec/Step): Delay between two consecutive steps of the simulation (0 by default).
    - Steps per Step-button: Number of steps to be executed every time the play-pause button is pressed (1 by default).

    ![The Console tab](http://i.imgur.com/SI7dSrf.png)

- _Displays_: This tab lists the different ways for visualizing the state of the simulation. 
Each display can be shown or hidden at anytime, even while the simulation is running.  

    ![The Displays tab](http://i.imgur.com/PptfTUD.png) 

    There are three basic  types of visualization:
	- _Building map_: This type of display shows a 2D layer-based map. This is the most powerful, flexible and customizable visualization tool of MASSIS. This type of display is deeply explained in [Building map][building-map]. 
![](http://i.imgur.com/IcRLXtI.png)
	- _Building 2D_:  This is the  Sweet Home 3D default 2D viewer. It can be is useful in the future, but currently it is not recommended because it is not optimized for the simulation, and has thread-safety issues. 
![](http://i.imgur.com/2tnx3mF.png)
	- _Building 3D_: This provides a 3D view. The camera can be controlled with the mouse or the keyboard.
	    - _Keyboard_:
               - `W` moves the camera backwards
               - `S` moves the camera forward.
               - `Caps lock` (&#8682;) makes the movement of the camera faster.
	    - _Mouse_:
               - The mouse wheel moves the camera backwards/forward
               - The mouse movement changes the view direction

## Simulation Run

Pressing the _Play_ button (the second from the left <span style="display: inline-block"><img src="http://i.imgur.com/D6C9dta.png?1 alt="Play button" style="margin: 0" width="30" height="30"></span> ), what happens?

1. The _step counter_ increases its value very fast.
2. The output of the console is always the same:

    ```
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
        ...
    ```


3. Nothing moves.

Why? This is  the behavior of the agent, which is defined by a  _High Level Controller_ class.

## What is a _HighLevelController_ ?

In MASSIS, a _High Level Controller_ implements the agent's behavior. In the case of this tutorial, the `MyHelloHighLevelController` Java class contains the following code:

```java

public class MyHelloHighLevelController extends HighLevelController {

    public MyHelloHighLevelController(
            /*1*/ LowLevelAgent agent,
            /*2*/ Map<String, String> metadata,
            /*3*/ String resourcesFolder) {

            super(agent, metadata, resourcesFolder);
            this.agent.setHighLevelData(this);
    }

    @Override
    public void stop() {
            /*4*/
            /*  Clean resources, threads...etc */
    }

    @Override
    public void step() {
            /*5*/
            System.out.println("Hey! I am an agent!");
    }
}
```

This code has the following parts (which are marked in the code between `/**/`).

First,  the constructor of the class requires three parameters:

1. A _LowLevelAgent_ reference.

    An agent in MASSIS has two components: one taking care of agent decisions (the high level controller) and other that controls agent movements and perception in the environment (the low level agent). The _LowLevelAgent_  provides the _HighLevelController_ the necessary information for taking decisions. Also, the _HighLevelController_ changes the state of the _LowLevelAgent_, by making it move, changing its properties,  interacting, etc. This will be illustrated later with some examples.

    The MASSIS architecture shows the elements that constitute each of these components, and their relationships:
    ![](http://i.imgur.com/SBuHKz7.png)

2.  _Metadata_.

    The `metadata`  is a _map_ containing all the key-value entries that are introduced in the agent through the environment editor. Depending on the complexity of the behavior, the agent may need additional parameters.

3. The _resources folder_.

	The agent may need external resources (like a plan definition), or using some files or devices to perform some I/O operations. The `resourcesFolder` parameter indicates where the agent should do it.

There are two methods for controlling agent behaviour:

5. `step()` method (`/*5*/`)

    The `step()` method is where the agent's logic occurs. Every simulation step provokes a call to the agent's _step_ method. Changing the content of this method changes the behavior of the agent.

    In this example it does not work too much, it only prints a message in the console. Each time this method is called (i.e., at each simulation step) the agent will print that message.

4.  `stop()` method (`/*4*/`)

    Before the simulation finishes (or when the agent is shutdown), the `stop()` method is called. It can be seen as the last call to the `step()` method.



## Bringing the agent the ability to move (randomly)

Let's make the agent move around the room. For achieving this goal, the high level controller needs some knowledge of the environment, such as the room where the agent is. This kind of information is provided by the LowLevelAgent.  It will _command_ tasks to perform on the environment, and this is requested to the LowLevelAgent. The _LowLevelAgent_ interface provides the necessary methods for accomplishing these. This interface is defined in [LowLevelAgent][low-level-agent-interface].

Moving the agent around the room can be done by adding the following sentences to the `step()` method:

1. Obtain in which room is the agent.
	Rooms are represented by the  [`SimRoom`](https://github.com/rpax/MASSIS/blob/master/massisframework/massis/massis-core/src/main/java/com/massisframework/massis/model/building/SimRoom.java) class. Obtaining the room of the agent can be done by calling the `getRoom()` method:

	```java
    /*1*/ SimRoom currentRoom=this.agent.getRoom();
    ```
2. Select a destination where the agent will move to, in this case a random location in the current room. Locations in the environment are represented by the [`Location`](https://github.com/rpax/MASSIS/blob/master/massisframework/massis/massis-core/src/main/java/com/massisframework/massis/model/location/Location.java) class.

	```java
	/*2*/ Location randomLocation = currentRoom.getRandomLoc();
	```

3. Make the agent move to that point.

 * The agent needs a target to follow. This can be modeled as an attribute of the High-Level controller:

	```java
        /*3*/ private Location currentTarget;
	```

 * The easiest way for approaching to one location is by calling to the method `approachTo`:

 ```java
 /**
 * Try to approach to a specific {@link Location} in the building.
 * <p>This method <b>does not</b> "move" the agent to one location to
 * another instantly. Instead, the agent tries to be closer to 
 * the given location, avoiding obstacles.</p>
 * <p>
 * To move the agent to one location <i>instantly</i> use the method
 * {@link #moveTo(rpax.massis.model.location.Location)}.
 *
 * @param location the target {@link Location}
 * @return if the agent has reached the location or not
 */
 public void approachTo(Location location, ApproachCallback callback);
 ```

 This method requires an `ApproachCallback` method as parameter, which will be called when the pathfinding process has been finished in the current step. In this case:

```java
 ApproachCallback callback = new ApproachCallback() {

   @Override
   public void onTargetReached(LowLevelAgent agent) {
       // Target has been reached.
   }

    @Override
    public void onSucess(LowLevelAgent agent) {
       // Everything ok. The agent has moved a little bit.
    }

    @Override
    public void onPathFinderError(PathFinderErrorReason reason) {
        //Error!
        Logger.getLogger(MyHelloHighLevelController.class.getName())
                    .log(Level.SEVERE,
                    "Error when approaching to {0} Reason: {1}",
                    new Object[] { currentTarget, reason });
    }
 };
```

Putting all together in the `step()` method:

```java
@Override
public void step() {

    if (this.currentTarget == null) {
        /* 1 */ SimRoom currentRoom = this.agent.getRoom();
        /* 2 */ Location randomLocation = currentRoom.getRandomLoc();
                   this.currentTarget = randomLocation;
    }
    
    ApproachCallback callback = new ApproachCallback() {
        @Override
        public void onTargetReached(LowLevelAgent agent) {
            // Target has been reached.
        }

        @Override
        public void onSucess(LowLevelAgent agent) {
            // Everything ok. The agent has moved a little bit.
        }

        @Override
        public void onPathFinderError(PathFinderErrorReason reason) {
            // Error!
            Logger.getLogger(MyHelloHighLevelController.class.getName())
               .log(Level.SEVERE,
               "Error when approaching to {0} Reason: {1}",
               new Object[] { currentTarget, reason });
        }
    };

    this.agent.approachTo(this.currentTarget, callback);
}
```


Some imports are required:

```java
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.managers.movement.ApproachCallback;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult.PathFinderErrorReason;
```

Then,  launch the simulation again. Now the agent will move. In order to see this, go to the _Displays_ tab, click twice on the `Building Map`. A new window appears, and in the NONAME tab there is a 2D representation of the room and the agent (as a small arrow). Try step by step or a long run to see how the agent moves. 

![Agent moving forward and backwards](http://i.imgur.com/hHYGFYY.gif)

The direction depends on the target that was randomly selected. It is possible to stop the simulation and start it again. A new target will be selected. Observe that new displays are provided in the _Displays_ tab for this new simulation. Try also to see the `Buliding 3D` display.


# Moving randomly to different locations

In the previous code, the `onTargetReached()` method was empty. This method is called when the agent is on the target location. For creating new targets every time the agent reaches one, reset the `currentTarget` to `null` every time the agent reaches to the `currentTarget` location.

```java
ApproachCallback callback = new ApproachCallback() {

	@Override
	public void onTargetReached(LowLevelAgent agent) {
		currentTarget = null;
	}

	@Override
	public void onSucess(LowLevelAgent agent) {
		// Everything ok. The agent has moved a little bit.
	}

	@Override
	public void onPathFinderError(PathFinderErrorReason reason) {
		// Error!
		Logger.getLogger(MyHelloHighLevelController.class.getName())
			.log(Level.SEVERE,
			"Error when approaching to {0} Reason: {1}",
			new Object[] { currentTarget, reason });
	}
};
```

![Agent moving around](http://i.imgur.com/KgzcWmv.gif)


# What to do next?

The [next part of the tutorial][tutorial4], shows how to work with more agents and a richer environment with different objects.



[tutorial2]: {{ site.baseurl }}/tutorials/02-customizing-the-archetype.html
[tutorial4]: {{ site.baseurl }}/tutorials/04-bigger-environment-and-multiple-agents.html
[mason]: http://cs.gmu.edu/~eclab/projects/mason/
[building-map]: {{ site.baseurl }}/tobedone.html
[low-level-agent-interface]: https://github.com/rpax/MASSIS/blob/master/massisframework/massis/massis-core/src/main/java/com/massisframework/massis/model/agents/LowLevelAgent.java
