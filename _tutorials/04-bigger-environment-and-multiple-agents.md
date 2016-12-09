---
title : "Richer environment and multiple agents"
---

Following the  [previous part of the tutorial][tutorial3], now we will improve the environment with new elements and  will add more agents to the simulation.


# Editing the environment

Adding more elements to the environment can be done with the editor, by running the `EnvironmentEditor` class.
To continue working with the previous house, select in the  _File_ menu the option _Open Recent_ and then the file `Tutorial1.sh3d`.

![Opening the file Tutorial1.sh3d](http://i.imgur.com/5JPhrUn.gif)

## Adding walls & rooms

Walls can be added clicking on _Plan -> Create Walls_, with the shortcut `Ctrl + Shift + W` or pressing the button <span style="display: inline-block"><img src="http://i.imgur.com/bc5HLBQ.png alt="Create walls button" style="margin: 0" width="30" height="30"></span>.

![Creating a wall](http://i.imgur.com/42HAACQ.gif)

The editor supports panning and zooming. Zooming can be done by pressing `Ctrl` while moving the mouse wheel. 

The editor also helps with connecting one wall with another. When the mouse is near the wall corners, a bigger point is shown, in order to facilitate this task.

![Connecting walls](http://i.imgur.com/ig7DusU.gif)

Adding rooms to the scene can be accomplished clicking in _Plan-> Create Rooms_, or with the shorcut button 
<span style="display: inline-block"><img src="http://i.imgur.com/vOnfWjk.png alt="Create room button" style="margin: 0" width="30" height="30"></span>. 
A room can be defined by marking its four corners, but it is easier and recommended to do it by making double click in an area enclosed by walls. Indeed, it is **highly recommended** to do like that, because it will make easier the  recognition of the environment by the simulator.

![Create rooms](http://i.imgur.com/busq3sch.gif)

## Adding doors

Doors play an important role in the simulation. Besides allowing agents to move through them, they made a logical separation between spaces that enhance the performance of the simulation.

Doors are under the section _Doors and Windows_. It is important to snap the doors to the walls, otherwise the simulation pathfinder might fail. For the tutorial, start by adding some door frames as in the video:

![Adding door frames](http://i.imgur.com/dzvD847.gif)

## Adding more agents

Add more agents to the environment. The best way now to do it is by doing **copy and paste** the original agent that was modified in the previous tutorial. Otherwise, the metadata values will be the default ones, and the behavior class will be the default one.

![Copy and paste to add more agents](http://i.imgur.com/vriBsoA.gif)


## Running the simulation again

Run the simulation again. The agents will start moving randomly. But they are not using the doors! This is because in the behavior the movement is limited to _the current room_, not to the whole environment.

## Moving around rooms

Changing a little bit the behavior will allow agents to move through doors:

```java
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

//...

@Override
public void step() {

    if (this.currentTarget == null) {
    /* 1 */
        Random rnd = ThreadLocalRandom.current();
        Location agentLocation = agent.getLocation();
        Floor agentFloor = agentLocation.getFloor();
        List<SimRoom> roomsInFloor = agentFloor.getRooms();
        int numberOfRooms = roomsInFloor.size();
        int rndRoomIndex = rnd.nextInt(numberOfRooms);
        final SimRoom rndRoom = roomsInFloor.get(rndRoomIndex);

     /* 2 */ 
        Location randomLocation = rndRoom.getRandomLoc();
        this.currentTarget = randomLocation;
     }
    //...etc
}
```

¿What is being doing here?

- First, get the agent's location.
- Then,  access the **Floor** of that location. (What a floor is will be explained in the next section)
- Select a random room of the current floor.
- Finally, select a random point in that room.

![Agents wandering around rooms](http://i.imgur.com/w5GfLJT.gif)

Try to see also with the 3D display. Observe what the agents do when they cross at a door or any other point of the rooms.

## What is a Floor?

The *building model* inside the MASSIS simulator is the following:

- A building is composed of different floors, at different levels each one.
- Each floor is divided into _rooms_, which are separated by _walls_ and connected by _doors_.
- _Teleports_ can connect everything in the building. They are used to model elements such as elevators. {{< tooltipurl url="#" linktext="is another tutorial" tooltiptext="Not written yet :(" >}}

![MASSIS building model](http://i.imgur.com/HRzd8cD.png)

## Creating obstacles

There may be other elements in the environment, such as furniture or other objects. These have to be taken into account as obstacles in the way of the agents. 

Adding obstacles in the environment editor can be done by selecting _Miscellaneous -> Box_, and drag it to the scene.

![Adding boxes in the environment](http://i.imgur.com/oIAeeED.gif)

Save the building and run the simulation again.

![Simulation with obstacles](http://i.imgur.com/SCqpt8D.gif)

What happens? The elements added are recognized by the simulator as **static** obstacles by default, like walls. (Walls are _always_ an obstacle).

In this context, the elements can be playing different roles, depending on their metadata. The two tables below explain these two keys that can be edited as metadata for every element.

- - -

| `IS_OBSTACLE`      |  DESCRIPTION                                                    |
|--------------------|-----------------------------------------------------------------|
| **Meaning**        | Indicates whether the element represents an obstacle in the environment. |
| **Default**        | true                                                            |
| **Allowed values** | true,false                                                      |
| **Keys required**  | none                                                            |

- - -

| `IS_DYNAMIC`       |                                                                                                                     |
|--------------------|---------------------------------------------------------------------------------------------------------------------|
| **Meaning**        | Indicates whether the element has the ability to move. Depending on this value, will be treated as an static obstacle or not |
| **Default**        | false                                                                                                               |
| **Allowed values** | true,false                                                                                                          |
| **Keys required**  | `IS_OBSTACLE`                                                                                                       |


Add the entry `IS_OBSTACLE = false` in the boxes' metadata, and see what happens.

![Change metadata IS_OBSTACLE for boxes](http://i.imgur.com/mvnnlmv.gif)

The agents now don't try to avoid the boxes, because they are marked as _not an obstacle_.

![Simulation with boxes that are not obsacles](http://i.imgur.com/OjLZ6QT.gif)

And, what if we set a box with the entry `IS_DYNAMIC = true` ?

![](http://i.imgur.com/1nLgQ66.gif)
The visualization treats the element in a different way, because it is marked as dynamic. Also, the pathfinder does not include this element in the pathfinding process, and the responsability for not _bumbing into things_ is taken by other method, with [steering behaviors](http://www.red3d.com/cwr/steer/), that will be explained in [the future][tobedone]{{< tooltipurl url="#" linktext="another tutorial" tooltiptext="Not written yet :(" >}}.


# What to do next?

The agents now can move from and to different locations in the environment, but, what about their _perception_? This is explained in the [next tutorial][tutorial5].



[tutorial3]: {{ site.baseurl }}/tutorials/03-defining-a-simple-behavior
[tutorial5]: {{ site.baseurl }}/tutorials/05-perception
[tobedone]:  {{ site.baseurl }}/tobedone.html


