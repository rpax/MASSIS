---
title : "Perception"
---

This tutorial shows how agents can query some of the properties of the environment, and illustrates it by implementing agents that play the [Tag game][tag_game].

## How agents can perceive the environment

The `LowLevelAgent` component of each agent can obtain information about its environment, such as the room where it is, its current location, and which other agents are around.

For instance, the method [`getAgentsInRange()`](https://github.com/rpax/MASSIS/blob/master/massisframework/massis/massis-core/src/main/java/com/massisframework/massis/model/agents/LowLevelAgent.java#L52) returns which LowLevelAgents are within a range of the agent that calls the method.

To practice with this, add the following method to the `MyHelloHighLevelController` class, which will print information on the agents that are in a radius of the current agent:

```java
void printAgentsIDsInRange(double range) {
    StringBuilder sb = new StringBuilder();

    for (LowLevelAgent otherAgent : this.agent.getAgentsInRange(range)) {
	final int otherId = otherAgent.getID();
	final Location agentLoc = this.agent.getLocation();
	final Location otherLoc = otherAgent.getLocation();
	final double distance = agentLoc.distance2D(otherLoc);

	sb.append("\tAgent #").append(otherId).append(". distance: ")
			.append(distance).append("\n");
    }
    if (sb.length() > 0) {
	System.out.println("Agent #" + this.agent.getID()+
		" has in the range of "+range+" cm:");
	System.out.println(sb.toString());
    }
}
```

and invoke it at the beginning of the `step()` method

```java
@Override
public void step() {

	printAgentsIDsInRange(200);

	if (this.currentTarget == null) {
		/* 1 */
     //etc
```

This should print in the console output something like this:

```
Agent #34 has in the range of 200.0 cm:
	Agent #38. distance: 192.92385854227328
	Agent #37. distance: 89.10633064780974
	Agent #36. distance: 72.07709132770508

Agent #37 has in the range of 200.0 cm:
	Agent #34. distance: 101.36873154443963
	Agent #36. distance: 77.3262787388507

Agent #36 has in the range of 200.0 cm:
	Agent #34. distance: 92.07489260134396
	Agent #37. distance: 72.37192136615275

Agent #33 has in the range of 200.0 cm:
	Agent #35. distance: 85.91914541801796
...
```

So, agent's can move, and see each other. Let's make them to play a game.

# The Game

## Game Rules

[Tag Game rules (from Wikipedia)][tag_game]:

>A group of players (two or more) decide who is going to be "it", often using a counting-out game such as eeny, meeny, miny, moe.
>
>The player selected to be "it" then chases the others, attempting to get close enough to "tag" one of them (touching them with a hand) while the others try to escape.
>
>A tag makes the tagged player "it" - in some variations[...]

## Implementation

The behavior of the agents in this case can be modelled with the flowchart below:

![Flowchart of the behaviour of an agent](http://i.imgur.com/UNrajuM.png)

### Conditions
The conditions of the flowchart can be implemented as follows:

- Checking whether the agent is tagged. This can be done in two ways:

  1. Adding a new property to `MyHelloHighLevelController`.

	```java
private boolean tagged;
public boolean isTagged() {
		return tagged;
}
public void setTagged(boolean tagged) {
		this.tagged = tagged;
}
	```

  2. By defining a  property "TAGGED" for the LowLevelAgent, which can be read with `getProperty()` and modified with `setProperty()`. These methods provide a simple way for storing information in the low-level agents. However, it becomes quickly unmaintanable. The way for doing this should be the following:

	```java
  public boolean isTagged() {
     	return "true".equals(this.agent.getProperty("TAGGED"));
  }
  public void setTagged(boolean tagged) {
    	this.agent.setProperty("TAGGED", String.valueOf(tagged));
  }
	```

  The initial value of `"TAGGED"` should be introduced first as metadata for all the agents in the environment.
        ![Adding TAGGED attribute in the agent metadata](http://i.imgur.com/ITGP0JP.gif)
  Then, in the constructor of the HightLevelController, the metadata from the environment has to be recovered to set the corresponding agent attributes:

```java
public MyHelloHighLevelController(LowLevelAgent agent,
   	Map<String, String> metadata, String resourcesFolder) {

	super(agent, metadata, resourcesFolder);

	this.agent.setHighLevelData(this);

	String taggedStr = metadata.get("TAGGED");

	if (taggedStr == null || !"true".equals(metadata.get("TAGGED"))) {
		this.setTagged(false);
	} else {
		this.setTagged(true);
       }
}
```

-  Looking for near tagged and untagged agents

```java
private MyHelloHighLevelController getNearestAgent
		(double range, boolean tagStatus) {

	/* Set a high limit */
	double minDist = Float.MAX_VALUE;

	/* Location of this agent */
	final Location agentLoc = this.agent.getLocation();

	/* Nearest agent found */
	MyHelloHighLevelController nearest = null;
	for (LowLevelAgent otherAgent : this.agent.getAgentsInRange(range)) {
		/*
		* Retrieve the high-level data of the other agent. It should be of
		* the type of agent playing this game, MyHelloHighLevelController.
		*/
	   final Object highLevelData = otherAgent.getHighLevelData();
	   if (highLevelData instanceof MyHelloHighLevelController) {
			 MyHelloHighLevelController otherCtrl =
				(MyHelloHighLevelController) highLevelData;
		/*
		* Check the search condition
		*/
		if (otherCtrl.isTagged() == tagStatus) {
			final Location otherLoc = otherAgent.getLocation();
			final double distance = agentLoc.distance2D(otherLoc);
			/*
			* Store if nearest.
			*/
			if (distance < minDist) {
				nearest = otherCtrl;
				minDist=distance;
			}
		}
	    }
	}
	return nearest;
}
```

- Check for detecting if the distance to the tagged agent is less than the maximum allowed: (_sees tagged agent_, in the flowchart)

```java
private boolean seesTaggedAgent() {
	// true, because is tagged
	return getNearestAgent(search_range, true) != null;
}
```

- Check for the distance being less than 0.5 m (For the sake of completeness):

```java
private boolean isDistanceLessThan50cm(LowLevelAgent a1,
					LowLevelAgent a2) {
	return a1.getLocation().distance2D(a2.getLocation())<50;
}
```

### Coding the flow

We have written the conditional checks of the flowchart. Let's put the pieces together.
The `START` section of the flowchart corresponds to:

```java
@Override
public void step() {
	if (this.isTagged()) {
		runAsTagged();
	} else {
		runAsNotTagged();
	}
}
```

Now, going to the _not tagged_ branch,

![Not tagged bracnch diagram](http://i.imgur.com/nidqPY6.png)

```java
    private void runAsNotTagged() {
        // sees tagged agent?
        if (seesTaggedAgent()) {
            /*
             * Retrieve the polygon associated with the current room
             */
            KPolygon polygon = this.agent.getRoom().getPolygon();
            /*
             * If the agent's target is in the same room of the agent (and in
             * the same room as the "tagged" one), select a new target in a
             * different room.
             */
            while (this.randomTarget == null
                    || polygon.contains(this.randomTarget.getXY())) {
                this.randomTarget = this.agent.getRandomRoom().getRandomLoc();
            }
        }
        this.moveRandomly();
    }
```

The new part here is the `getPolygon()` method. In MASSIS, every simulation object has a polygonal representation. Here, we take advantage of that possibility to detect if a point is contained in a the room's shape.

For the  _tagged_ branch,

![tagged branch diagram](http://i.imgur.com/AyOQSGJ.png)

This is a little bit more complex than the other branch.

- First, we need to check if there is any _not tagged_ agent in range. Remember that the `false` parameter was because we want to obtain the agents that are not tagged.

		MyHelloHighLevelController nearest = getNearestAgent(search_range, false);

- After that, if there is any agent in range (that is, `nearest!=null`), we check the distance to it
	- If the distance < 0.5 meters (50 cm),
        - The agent _tags_ it
        - The agent _untags_ itself

                final Location nearestLoc = nearest.agent.getLocation();
                // Yes, and is the closest one.
                // if distance < 0.5 m, (50 cm), tag it
                final double distance = agentLoc.distance2D(nearestLoc);
                if (distance < tag_max_distance) {
                    // tag it
                    nearest.setTagged(true);
                    // un-tag itself
                    this.setTagged(false);
                    // end
                }

	- If not, the tagged agent just chase its nearest target.

            this.agent.approachTo(nearestLoc, new ApproachCallback() {

                @Override
                public void onTargetReached(LowLevelAgent agent) {
                    // Nothing this time. We are handling the logic
                    // elsewhere.
                }

                @Override
                public void onSucess(LowLevelAgent agent) {}

                @Override
                public void onPathFinderError(
                        PathFinderErrorReason reason) {
                    // Error!
                    Logger.getLogger(
                            MyHelloHighLevelController.class.getName())
                            .log(Level.SEVERE,
                                    "Error when approaching to {0} Reason: {1}",
                                    new Object[] { nearestLoc, reason });
                }
            });
- If there wasn't any agent in range, just move randomly

		this.moveRandomly();

Putting all together,

    private void runAsTagged() {
        final Location agentLoc = this.agent.getLocation();
        // Sees un-tagged agent?
        MyHelloHighLevelController nearest = getNearestAgent(search_range,
                false);
        if (nearest != null) {
            final Location nearestLoc = nearest.agent.getLocation();
            // Yes, and is the closest one.
            // if distance < 0.5 m, (50 cm), tag it
            final double distance = agentLoc.distance2D(nearestLoc);
            if (distance < tag_max_distance) {
                // tag it
                nearest.setTagged(true);
                // un-tag itself
                this.setTagged(false);
                // end
            } else {
                // chase him
                this.agent.approachTo(nearestLoc, new ApproachCallback() {

                    @Override
                    public void onTargetReached(LowLevelAgent agent) {
                        // Nothing this time. We are handling the logic
                        // elsewhere.
                    }

                    @Override
                    public void onSucess(LowLevelAgent agent) {
                    }

                    @Override
                    public void onPathFinderError(
                            PathFinderErrorReason reason) {
                        // Error!
                        Logger.getLogger(
                                MyHelloHighLevelController.class.getName())
                                .log(Level.SEVERE,
                                        "Error when approaching to {0} Reason: {1}",
                                        new Object[] { nearestLoc, reason });
                    }
                });
            }
        } else {
            // no target found:
            this.moveRandomly();
        }

    }

The `moveRandomly()` method is an adaptation of the previous code that made the agent move to random targets.

    private Location randomTarget = null;

    private void moveRandomly() {
        if (this.randomTarget == null) {
            Location randomLocation = this.agent.getRandomRoom().getRandomLoc();
            this.randomTarget = randomLocation;
        }
        this.agent.approachTo(this.randomTarget, new ApproachCallback() {

            @Override
            public void onTargetReached(LowLevelAgent agent) {
                randomTarget = null;
            }

            @Override
            public void onSucess(LowLevelAgent agent) {
            }

            @Override
            public void onPathFinderError(PathFinderErrorReason reason) {
                // Error!
                Logger.getLogger(MyHelloHighLevelController.class.getName())
                        .log(Level.SEVERE,
                                "Error when finding path Reason: {0}", reason);
            }
        });
    }

If everything went ok, the result should be something like this:

![](http://i.imgur.com/Jva4HPG.gif)

>Note: The code for the complete behavior is [in this gist](https://gist.github.com/rpax/b457fa14d2a9d14779ab)

# What to do next?

When running the game, one question may arrive: **_Who is the tagged agent_ ? Because all the agents seem identical!**.

The [Next Tutorial][tutorial6] solves that problem, explaining how to develop visualization utilities.



[tutorial4]: {{ site.baseurl }}/tutorials/04-bigger-environment-and-multiple-agents
[tutorial6]: {{ site.baseurl }}/tutorials/06-extending-the-gui
[tobedone]:  {{ site.baseurl }}/tobedone


[tag_game]: https://en.wikipedia.org/wiki/Tag_(game)
