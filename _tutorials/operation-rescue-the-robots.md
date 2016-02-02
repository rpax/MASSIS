---
title : "Operation: Rescue the robots. Part 1"
---

It is assumed in this tutorial that the reader has already gone through the basic tutorials, such as [the Getting started][getting-started] and [Creating a MASSIS Project][tutorial1] sections have been already been read.

For designing the scenario, it is also recommended to read the [environment design][tutorial4] section.

## Scenario description

The impact of a meteorite has caused irreparable damage to an extraterrestrial station. Luckily, there were no humans on the station, only robots.

But these robots were controlled remotely, and the meteorite impact has broken one antenna. In order to solve this issue, a new kind of robots are sent to this station. Their mission will consist on rescuing the robots that are in the incommunicated station, by finding  and taking them to the landing zone of the station.

The file with the description of this extraterrestrial station for this tutorial can be downloaded [from here][escenario]. When opening this should look something like this figure:

![Scenario](http://i.imgur.com/tRexsTw.png)


There are two kinds of robots, those that  were in the station (workers), and those that have arrived to rescue them (rescuers). Their behaviour, in this case of incommunication of the station, is the following:

- **Worker** robot behavior
 1. Wait  until someone tells it what target should be following.
- **Rescuer** robot behavior
 1. Try to visit all the rooms in the building. Whenever this robot sees a worker robot in the room, requests that worker robot to follow it.
 2. When two (or more) _rescuer_ robots are in the same room, they share information about the rooms visited.
 3. When all the rooms have been explored, the robot goes back to the _landing zone_

## Implementation - From the idea to the code

This project will be based on the  [basic MASSIS archetype explained previously][tutorial1].

### Two behaviors imply two classes.

Each behavior should be modeled as a Java class. In this scenario, we have two different behaviors, the _Worker_ behavior and the _Rescuer_ behavior.

The _Worker_ behavior is fairly simple. It is waiting and when it has a target to follow, continues to do this forever. A Rescuer agent will indicate that target when it finds it by using the method `setFollowTarget()`.

```java
package com.myawesomesimulator.ai;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.managers.movement.ApproachCallback;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult.PathFinderErrorReason;

public class WorkerController extends HighLevelController {

  private static final long serialVersionUID = 1L;

  private LowLevelAgent followTarget;

  public WorkerController(LowLevelAgent agent, Map<String, String> metadata,
      String resourcesFolder) {
    super(agent, metadata, resourcesFolder);
    this.agent.setHighLevelData(this);
  }

  @Override
  public void stop() {

  }

  @Override
  public void step() {
    if (this.followTarget != null) {
      this.agent.approachTo(this.followTarget.getLocation(),
        new ApproachCallback() {

          @Override
          public void onTargetReached(LowLevelAgent agent) {
            // Nothing. We are going to follow the target forever.

          }

          @Override
          public void onSucess(LowLevelAgent agent) {
            // Continue following the target.
          }

          @Override
          public void onPathFinderError(PathFinderErrorReason reason) {
            Logger.getLogger(WorkerController.class.getName()).log(
                Level.SEVERE,
                "An error occurred when approaching to {0}. Reason: {1}",
                new Object[] { followTarget.getLocation(), reason });

          }
        });
    }
  }

  public void setFollowTarget(LowLevelAgent agent) {
    this.followTarget = agent;
  }

  public boolean isFollowingSomeone() {
    return this.followTarget != null;
  }
}

```
That is! Simple, huh?

The _Rescuer_ behavior is a little more tricky. Let's split it into parts:

- _Try to explore all the rooms in the building._
 - This implies that the robot should track the rooms that it has visited.
- _Whenever the Rescuer robot finds a worker robot in the room, it makes that worker robot to follow it._
 - Gathering information about the current room.
- _When two (or more) Rescuer robots are in the same room, they share information about the rooms visited._
 - This can be accomplished by interchanging the visited rooms of each robot.
- _When all the rooms have been explored, the robot goes back to the landing zone_
 - This is just a basic operation.

With this facts identified, the only thing left is to code them in a `HighLevelController`.
As we have created the project using the archetype, designing the behavior becomes a _fill in the gaps_ exercise.

Several  attributes are needed to account for the unexplored rooms, the next room to visit and (optional, but useful in this case) if the work has finished or not.

```java
private Collection<SimRoom> roomsUnexplored;
private SimRoom assignedRoom;
private boolean workFinished;
```

These attributes are initialized in the constructor, to indicate  the collection of unexplored rooms, and setting the `workFinished` attribute to false.

```java
public RescuerController(LowLevelAgent agent, Map<String, String> metadata,
        String resourcesFolder) {
    super(agent, metadata, resourcesFolder);
    this.agent.setHighLevelData(this);
    this.workFinished = false;
    this.roomsUnexplored = new HashSet<>();
    for (SimRoom r : this.agent.getLocation().getFloor().getRooms()) {
        this.roomsUnexplored.add(r);
    }
    this.logger = Logger.getLogger("Rescuer_" + agent.getID());
    this.assignNewRoomToExplore();
}
```

This robot should be capable of auto-assigning rooms to explore:

```java
private void assignNewRoomToExplore() {
    if (!this.roomsUnexplored.isEmpty()) {
        SimRoom[] rem = this.roomsUnexplored.toArray(new SimRoom[] {});
        this.assignedRoom = rem[ThreadLocalRandom.current()
                .nextInt(rem.length)];
        this.roomsUnexplored.remove(this.assignedRoom);
    }
}
```

This robot should also exchange the information about the rooms with other robots:

```java
private void shareRoomsWith(RescuerController rescuer) {
    // remove every item that is not present in the other map
    final boolean changed = this.roomsUnexplored
            .retainAll(rescuer.roomsUnexplored);
    if (changed) {
        logger.log(Level.INFO,
                "Updated information about rooms to explore: {0}",
                this.roomsUnexplored);
    }
}
```


Also, for approaching to its current target, this method can be written for clarity:

```java
private void approachToAssignedRoom() {
  this.agent.approachTo(this.assignedRoom.getLocation(),
    new ApproachCallback() {

        @Override
        public void onTargetReached(LowLevelAgent agent) {
            assignNewRoomToExplore();
        }

        @Override
        public void onSucess(LowLevelAgent agent) {
        }

        @Override
        public void onPathFinderError(
                PathFinderErrorReason reason) {
            logger.log(Level.SEVERE,
                    "An error occurred when approaching to room {0} Reason: {1}",
                    new Object[] { reason, assignedRoom.getID() });

        }
    });
}
```

Finally, for approaching to the _landing zone_,

```java
private void approachToLandingZone() {
    if (!this.workFinished) {
        this.agent.approachToNamedLocation("LANDING_ZONE",
            new ApproachCallback() {
                @Override
                public void onTargetReached(LowLevelAgent agent) {
                    // Work finished.
                    RescuerController.this.workFinished = true;
                }
                @Override
                public void onSucess(LowLevelAgent agent) {
                }
                @Override
                public void onPathFinderError(
                        PathFinderErrorReason reason) {
                    logger.log(Level.SEVERE,
                            "An error occurred when approaching "
                                    + "to landing zone. Reason: {0}",
                            reason);

                }
            });
    }
}
```

These utility methods are intended for their use in the `step()` method:

```java
@Override
public void step() {
    /*
     * Is anybody in the room?
     */
    if (!this.roomsUnexplored.isEmpty()) {
        /*
         * For each agent found in the room, do the corresponding task
         */
        for (LowLevelAgent otherAgent : this.agent.getAgentsInRoom()) {
            // avoid self
            if (this.agent == otherAgent)
                continue;
            /*
             * Is a rescuer?
             */
            if (otherAgent
                    .getHighLevelData() instanceof RescuerController) {
                /*
                 * Affirmative. Share room information
                 */
                shareRoomsWith(
                        (RescuerController) otherAgent.getHighLevelData());
                /*
                 * Is a worker? follow me.
                 */
            } else if (otherAgent
                    .getHighLevelData() instanceof WorkerController) {

                final WorkerController follower = (WorkerController) otherAgent
                        .getHighLevelData();
                /*
                 * Check if the robot is already following someone
                 */
                if (!follower.isFollowingSomeone()) {
                    follower.setFollowTarget(this.agent);
                }
            }

        }
        this.approachToAssignedRoom();

    } else {
        // Go to landing zone.
        this.approachToLandingZone();
    }
}
```

# Conclusion
That's everything! If you prefer to download the entire project, with the building included, [you can do it from here](https://drive.google.com/open?id=0B3-oRpDTDan3Nl9pdUdDRUY0cDQ)

[getting-started]: {{ site.baseurl }}/getting-started.html
[tutorial1]: {{ site.baseurl }}/tutorials/01-sample-massis-application.html
[tutorial2]: {{ site.baseurl }}/tutorials/02-customizing-the-archetype.html
[tutorial3]: {{ site.baseurl }}/tutorials/03-defining-a-simple-behavior.html
[tutorial4]: {{ site.baseurl }}/tutorials/04-bigger-environment-and-multiple-agents.html
[tutorial5]: {{ site.baseurl }}/tutorials/05-perception.html
[tutorial6]: {{ site.baseurl }}/tutorials/06-extending-the-gui.html
[exercise]: {{ site.baseurl }}/tutorials/operation-rescue-the-robots.html

[escenario]: https://drive.google.com/open?id=0B3-oRpDTDan3Z3dRQmVXalBYYkU
[tobedone]:  {{ site.baseurl }}/tobedone.html
