---
title : "Operation: Rescue the robots. Part 1"
---

It is assumed in this tutorial that the reader has already gone through the basic tutorials, such as [the getting started guide]({{< relref "page/getting-started.md" >}}) and [Creating a MASSIS Application]({{< relref "post/tutorials/sample-massis-application.md" >}}) sections have been already been read.

For designing the scenario, it is recommended to read the [environment design]({{< relref "post/tutorials/environment-design.md" >}}) section.

## Scenario description
![Scenario](http://i.imgur.com/tRexsTw.png)

The scenario to be simulated is the following:

he impact of a meteorite has caused irreparable damage to an extraterrestrial station. Luckily, there were no humans on the station, only robots.

But these robots were controlled remotely, and the meteorite impact has broken one antenna. In order to solve this issue, a new kind of robots are sent to this station. Their mission will consist on rescuing the robots that are in the incommunicated station.

The scenario used in this tutorial can be downloaded [from here](https://drive.google.com/open?id=0B3-oRpDTDan3Z3dRQmVXalBYYkU)


First, we will start modelling the behavior of the robot in the station (the one incommunicated).
In case of lost of communication, these robots behave as follows:

- "Worker" robot behavior
 1. Waits until someone tells him what target should be following.
- "Rescuer" robot behavior
 1. Tries to visit all the rooms in the building. Whenever this robot sees a worker robot in the room, makes that worker robot to follow him.
 2. When two (or more) _rescuer_ robots are in the same room, they share information about the rooms visited.
 3. When all the rooms have been explored, this robot goes back to the _landing zone_

## Implementation - From the idea to the code

This project will be based on the  [basic MASSIS archetype explained previously]({{< relref "post/tutorials/sample-massis-application.md" >}}).

### Two behaviors imply two classes.

Each behavior should be modeled as a Java class. In this scenario, we have two different behaviors, the _Worker_ behavior and the _Rescuer_ behavior.

The _Worker_ behavior is fairly simple: Just a translation of the behavior definition:

```
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

- _Tries to visit all the rooms in the building._
 - This imply that the robot should track the rooms that it has visited.
- _Whenever this robot sees a worker robot in the room, makes that worker robot to follow him._
 - We need some type of gathering information about the current room.
- _When two (or more) rescuer robots are in the same room, they share information about the rooms visited._
 - This can be accomplished interchanging the visited rooms of each robot.
- _When all the rooms have been explored, this robot goes back to the landing zone_
 - This is just a basic operation.

With this facts identified, the only thing left is to code them in a `HighLevelController`.
As we have created the project using the archetype, designing the behavior becomes a _fill in the gaps_ exercise.
The attributes needed should be the unexplored rooms, the next room to visit and (optional, but useful in this case) if the work has finished or not.

```
private Collection<SimRoom> roomsUnexplored;
private SimRoom assignedRoom;
private boolean workFinished;
```

In the constructor we should initialize the unexplored rooms, and setting the `workFinished` attribute to false.
```
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
This robot should interchange the information about the rooms with other robots:
```
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
This robot should be capable of auto-assigning rooms to explore:
```
private void assignNewRoomToExplore() {
    if (!this.roomsUnexplored.isEmpty()) {
        SimRoom[] rem = this.roomsUnexplored.toArray(new SimRoom[] {});
        this.assignedRoom = rem[ThreadLocalRandom.current()
                .nextInt(rem.length)];
        this.roomsUnexplored.remove(this.assignedRoom);
    }

}
```
Also, for approaching to its current target, this method can be written for clarity:
```
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
```
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

This utility methods are intended for using them in the `step()` method:

```
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
That's everything! If you prefer to download the entire project, with the building included, [you can do it from here](https://drive.google.com/open?id=0B3-oRpDTDan3Nl9pdUdDRUY0cDQ)
