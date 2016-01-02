---
title : "Defining a simple behavior"
---

This is the continuation of the [Second tutorial](/tutorials/customizing-the-archetype).

In the previous tutorial, we learnt how to change properties of the elements of the environment and launching the simulation. In this tutorial, we will learn the basics of the simulation gui, and a simple example about how to manage the behavior of the agent in the environment.

# The simulation GUI.
In the previous tutorial, we executed `SimulationWithUILauncher`, and the following window appeared.Let's explain briefly the contents of this window.

- About : Shows some a welcome screen, and copyright imformation. The three buttons on the bottom-left side control the most basic functionalities of the simulator:
     - Advance one step
     - Run continuously
     - Stop
![](http://i.imgur.com/kPN5EI7.png)

- Console : Controls simulation parameters.
     - Delay (Sec/Step) : Delay between steps in the simulation
     - Steps per Step-button : Number of steps to be executed every time the play-pause button is pressed.
![](http://i.imgur.com/SI7dSrf.png)

- Displays: This tab offers different ways for visualizing the state of the simulation. They can be shown or hidden anytime, with the simulation running also.  ![](http://i.imgur.com/PptfTUD.png)  There are three different types of visualization:
	- Building map : Shows the 2D layer- based map. This is the most powerful, flexible and customizable visualization tool of MASSIS. This type of display is deeply explained in  {{< tooltipurl url="#" linktext="this tutorial" tooltiptext="Not written yet :(" >}}. ![](http://i.imgur.com/IcRLXtI.png)
	- Building 2D :  Shows SweetHome3D default 2D viewer. It is included because maybe it is useful in the future. Although it is not recommended: Is not optimized for the simulation, and has thread-safety issues.![](http://i.imgur.com/2tnx3mF.png)
	- Building 3D: Shows the 3D viewer. The camera can be controlled with the mouse or keyboard
	- Keyboard:
		- `W` moves the camera backwards
		- `S` moves the camera forward.
		- `Caps lock` (&#8682;) makes the movement of the camera faster.
	- Mouse:
		- The mouse wheel moves the camera backwards/forward
		- The mouse movement changes the view direction

# First Run

Ok, so let's press the _Play_ ( <img src="http://i.imgur.com/D6C9dta.png?1"> ) button. What happens?

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

Why? because the behavior of the agent, defined by its _High Level Controller_ is exactly that.

# What is a _HighLevelController_ ?

In MASSIS, the _High Level Controllers_ manage the agent's behavior. In our case, the `MyHelloHighLevelController.java` file contains the following code.

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

Let's explain it by parts (marked in the code between `/**/`)

1. What is a _LowLevelAgent_ ?

    A _LowLevelAgent_ is the element that provides the _HighLevelController_ the necessary information for taking decisions. Also, the HighLevelController changes the state of the LowLevelAgent. (Making it move, changing its properties, other agent's properties, interaction, etc.).

    The following diagram may help to understand how MASSIS is structured:
    ![](http://i.imgur.com/SBuHKz7.png)

2. What is the _metadata_ parameter?

    The `metadata` map is the map containing all the key-value entries that are introduced in the agent through the environment editor. Depending on the complexity of the behavior, the agent may need additional parameters.

3. ...And the resources folder?

	The agent may need external resources (like a plan definition), or maybe the function of that agent is to write something. The `resourcesfolder` parameter is the folder in where the I/O of the agent should be done.

4. What is the `stop()` method for?

	Before the simulation finishes (or the agent is shut down), the `stop()` method is called. Can be seen as the last call to the `step()` method.

5. And the `step()` method?

	The `step()` method is where the agent's logic occurs. Every simulation step provokes a call to the agent's step method. Changing the content of this method changes the behavior of the agent.

# Bringing the agent the ability to move (randomly)

Let's make our agent move. For accomplishing that, the highlevel controller needs the knowledge of the environment that is provided by the LowLevelAgent (such as the room where the agent is), and needs to _command_ things to it's low-level representation. The _LowLevelAgent_ interface provides the necessary methods for accomplishing that. Remember that the code is [hosted on github](https://github.com/rpax/MASSIS), so you can check what are [the methods offered by a LowLevelAgent](https://github.com/rpax/MASSIS/blob/master/massisframework/massis/massis-core/src/main/java/com/massisframework/massis/model/agents/LowLevelAgent.java).

What we are going to do is the following:

1. Obtain in which room is the agent.
	Rooms are represented by the  [`SimRoom`](https://github.com/rpax/MASSIS/blob/master/massisframework/massis/massis-core/src/main/java/com/massisframework/massis/model/building/SimRoom.java) class. Obtaining the room of the agent can be done by calling the `getRoom()` method:

	```
    /*1*/ SimRoom currentRoom=this.agent.getRoom();
    ```
2. Select a random location in that room. Locations in the environment are represented by the [`Location`](https://github.com/rpax/MASSIS/blob/master/massisframework/massis/massis-core/src/main/java/com/massisframework/massis/model/location/Location.java) class.

	```
	/*2*/ Location randomLocation = currentRoom.getRandomLoc();
	```

3. Make the agent move to that point.

	1. The agent needs a target to follow. We will model that as an attribute of the High-Level controller.

        ```
        /*3*/ private Location currentTarget;
        ```
    2. The easiest way for approaching to one location is calling to the method `approachTo`.


            /**
            * Tries to approach to an specific {@link Location} in the building.
            * <p>This method <b>does not</b> "Move" the agent to one location to
            * another instantly. Instead, the agent tries to be closer to the location
            * given, avoiding obstacles.</p>
            * <p>
            * For moving the agent to one location <i>instantly</i> the method
            * {@link #moveTo(rpax.massis.model.location.Location)} should be used
            * instead
            *
            * @param location the target {@link Location}
            * @return if the agent has reached the location or not
            */
            public void approachTo(Location location,ApproachCallback callback);

		This method requires an `ApproachCallback`. The `ÀpproachCallback` methods will be called when the pathfinding process has been finished in the current step. In our case,

            ApproachCallback callback = new ApproachCallback() {

                @Override
                public void onTargetReached(LowLevelAgent agent) {
                    // Target has been reached.
                }

                @Override
                public void onSucess(LowLevelAgent agent) {
                    // Ok, there was no problem. The agent has moved a little bit.
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

		Putting all together in the `step()` method,

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
                    	// Ok, there was no problem.
                        //The agent has moved a little bit.
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

You can try launching the simulation again. The agent is moving! But it only has a target, and when that target is reached, it starts going forward and backwards, trying again and again to reach the target. We can do a little better.

![](http://i.imgur.com/hHYGFYY.gif)


# Moving randomly to different locations

In the previous code, the `onTargetReached()` method was empty. This method is called when the agent is on the target location. For creating new targets every time the agent reaches one, we can do reset `currentTarget` to `null` every time the agent reaches the `currentTarget` location.

		ApproachCallback callback = new ApproachCallback() {

			@Override
			public void onTargetReached(LowLevelAgent agent) {
				currentTarget = null;
			}

			@Override
			public void onSucess(LowLevelAgent agent) {
				// Ok, there was no problem. The agent has moved a little bit.
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

![](http://i.imgur.com/KgzcWmv.gif)

There's a lot of things to improve! Take a look at the [Next tutorial](/tutorials/04-bigger-environment-and-multiple-agents)

