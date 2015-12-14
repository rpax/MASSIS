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

[tutorial_4]: {{< relref "post/tutorials/bigger-environment-and-multiple-agents.md" >}}
