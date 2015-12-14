+++
date = "2015-11-06T13:19:52+01:00"
draft = false
title = "Creating a MASSIS Application. Part 5: Perception"
categories = ["tutorials"]
+++

This is the continuation of the [fourth tutorial][tutorial_4]. In this tutorial, we will learn how agents can query some of the properties of the environment, and we will make them to play the tag game.

# How agents can perceive the environment.

The `LowLevelAgent` can obtain information about its environment, such as the room where it is, its current location, and other agents near him.

Let's add a simple print method for checking this out. The method [`getAgentsInRange`](https://github.com/rpax/MASSIS/blob/master/massisframework/massis/massis-core/src/main/java/com/massisframework/massis/model/agents/LowLevelAgent.java#L52) returns the LowLevelAgents within a range of the agent on which the method was performed.

So, in `MyHelloHighLevelControllerjava` we can write the following method:

	private void printAgentsIDsInRange(double range) {
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
			System.out.println("Agent #" + this.agent.getID()+" has in the range of "+range+" cm:");
			System.out.println(sb.toString());
		}
	}

and add it at the beginning of the `step()` method

	@Override
	public void step() {

		printAgentsIDsInRange(200);
		if (this.currentTarget == null) {
			/* 1 */
    //etc

This should print in the console output something like this:

    Agent #12 has in the range of 200.0 cm:
        Agent #42. distance: 233.04966693463686

    Agent #12 has in the range of 200.0 cm:
        Agent #42. distance: 213.96123582412258

	...etc

So, agent's can move, and see each other. Let's make them to play a game.

# Game Rules

[Tag Game rules from Wikipedia][tag_game]:

>A group of players (two or more) decide who is going to be "it", often using a counting-out game such as eeny, meeny, miny, moe.
>
>The player selected to be "it" then chases the others, attempting to get close enough to "tag" one of them (touching them with a hand) while the others try to escape.
>
>A tag makes the tagged player "it" - in some variations[...]

We can model the behavior of the agents following the flowchart below:

{{< fig "http://i.imgur.com/2CENpcd.png" >}}

- Checking if the agent is tagged.

	Adding a new property to `MyHelloHighLevelController` will serve well for this purpose:

        private boolean tagged;

        public boolean isTagged() {
            return tagged;
        }

        public void setTagged(boolean tagged) {
            this.tagged = tagged;
        }
- Check for seeing a tagged / untagged agent

        private MyHelloHighLevelController getNearestAgent(double range,
                boolean tagStatus) {
            /*
             * We set a high limit
             */
            double minDist = Float.MAX_VALUE;
            /*
             * Location of this agent
             */
            final Location agentLoc = this.agent.getLocation();
            /*
             * Nearest agent found
             */
            MyHelloHighLevelController nearest = null;
            for (LowLevelAgent otherAgent : this.agent.getAgentsInRange(range)) {
                /*
                 * Retrieve the high-level data of the other agent. It should be of
                 * the type of agent playing this game, MyHelloHighLevelController.
                 */
                final Object highLevelData = otherAgent.getHighLevelData();
                if (highLevelData instanceof MyHelloHighLevelController) {
                    MyHelloHighLevelController otherCtrl = (MyHelloHighLevelController) highLevelData;
                    /*
                     * Satisfies the search condition?
                     */
                    if (otherCtrl.isTagged() == tagStatus) {
                        final Location otherLoc = otherAgent.getLocation();
                        final double distance = agentLoc.distance2D(otherLoc);
                        /*
                         * Store if nearest.
                         */
                        if (distance < minDist) {
                            nearest = otherCtrl;
                        }
                    }
                }
            }
            return nearest;
        }


With those two methods, we can design the rest of the behavior.









[tutorial_4]: {{< relref "post/tutorials/bigger-environment-and-multiple-agents.md" >}}
[tag_game]: https://en.wikipedia.org/wiki/Tag_(game)