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

{{< fig "http://i.imgur.com/ig7DusU.gif" >}}

Adding rooms to the scene can be accomplished clicking in _Plan-> Create Rooms_, or in the shorcut button
![](http://i.imgur.com/vOnfWjk.png). Can be added _point-per-point_, but the recommended way is making double click in an area enclosed by walls. It is **very recommended** to do it that way, because makes easier the environment recognition by the simulator.

{{< fig "http://i.imgur.com/busq3sch.gif" >}}

## Adding doors

Doors play an important role in the simulation. Besides allowing agents to move through them, they made a logical separation between spaces that enhance the performance of the simulation.

Doors are under the section _Doors and Windows_. It is important to snap the doors to the walls, otherwise the simulation pathfinder might fail.

{{< fig "http://i.imgur.com/dzvD847.gif" >}}

## Adding more agents

Let's are more agents to the environment. We need to **copy and paste** the original agent that we have modified in the previous tutorial. Otherwise, the metadata values will be the default ones, and the behavior class will be the default one.

{{< fig "http://i.imgur.com/vriBsoA.gif" >}}


## Running the simulation again

If we run the simulation again, the agents will start moving randomly. But, they are not using the doors!. That's because in the behavior, we have limited the movement _in the current room_, not in the whole environment.

Changing a little bit the behavior will allow agents to move through doors:

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

            /* 2 */ Location randomLocation = rndRoom.getRandomLoc();
            this.currentTarget = randomLocation;
        }
    //...etc
    }

¿What are we doing here?

- First, we get the agent's location.
- Then, we access the **Floor** of that location. (What a floor is will be explained in the next section)
- We pick a random room of the current floor.
- And finally, a random point in that room.

{{< fig "http://i.imgur.com/w5GfLJT.gif" >}}

## What is a Floor?

The building model inside the MASSIS simulator is the following:

- A building is composed by different floors, at different heights each one.
- Each floor is divided into _rooms_, which are separated by _walls_ and connected by _doors_.
- _Teleports_ can connect everything in the building. But that {{< tooltipurl url="#" linktext="is another tutorial" tooltiptext="Not written yet :(" >}}.

{{< fig "http://i.imgur.com/HRzd8cD.png" >}}

## Creating obstacles

At this moment, the environment has no obstacles. Let's add a some. In the environment editor, go to _Miscillaneous -> Box_, and drag it to the scene.

{{< fig "http://i.imgur.com/oIAeeED.gif" >}}

Save the building and run the simulation again.

{{< fig "http://i.imgur.com/SCqpt8D.gif" >}}

What happens? The elements added are recognized by the simulator as **static** obstacles by default, like walls. (Walls are _always_ an obstacle).

In this context, the elements can be playing different roles, depending on their metadata. The two tables below explain this keys more formally.

- - -

| `IS_OBSTACLE`      |                                                                 |
|--------------------|-----------------------------------------------------------------|
| **Meaning**        | Tells if the element represents an obstacle in the environment. |
| **Default**        | true                                                            |
| **Allowed values** | true,false                                                      |
| **Keys required**  | none                                                            |

- - -

| `IS_DYNAMIC`       |                                                                                                                     |
|--------------------|---------------------------------------------------------------------------------------------------------------------|
| **Meaning**        | Tells if the element has the ability to move. Depending on this value, will be treated as an static obstacle or not |
| **Default**        | false                                                                                                               |
| **Allowed values** | true,false                                                                                                          |
| **Keys required**  | `IS_OBSTACLE`                                                                                                       |


Let's add the entry `IS_OBSTACLE = false` in the boxes' metadata, and see what happens.

{{< fig "http://i.imgur.com/mvnnlmv.gif" >}}

The agents now don't try to avoid the boxes, because they are marked as _not an obstacle_.

{{< fig "http://i.imgur.com/OjLZ6QT.gif" >}}

And, what if we set a box with the entry `IS_DYNAMIC = true` ?

{{< fig "http://i.imgur.com/1nLgQ66.gif" >}}
The visualization treats the element in a different way, because it is marked as dynamic. Also, the pathfinder does not include this element in the pathfinding process, and the responsability for not _bumbing into things_ is taken by other method, with [steering behaviors](http://www.red3d.com/cwr/steer/), that are explained in  {{< tooltipurl url="#" linktext="another tutorial" tooltiptext="Not written yet :(" >}}.


# What to do next?

Our agents can move from/to different locations in the environment, but, what about their _perception_? This is explained in the [Next tutorial][tutorial_5].




[tutorial_3]: {{< relref "post/tutorials/defining-a-simple-behavior.md" >}}
[tutorial_5]: {{< relref "post/tutorials/perception.md" >}}