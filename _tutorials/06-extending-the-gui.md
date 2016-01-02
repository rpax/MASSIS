---
title : "Extending the GUI"
---

This is the continuation of the [Tutorial 5](/tutorials/05-perception).
The problem we faced in the previous tutorial was that we couldn't visualize the _state_ of the agents (if they were tagged or not).

In a [previous tutorial](/tutorials/defining-a-simple-behavior) was introduced the different types of visualization that MASSIS has. In this tutorial we will go deeper into this topic, and we will learn how to extend the default MASSIS layer system.

# MASSIS visualization displays revisited

All the changes made in the environment during the simulation are reflected in real time by 3D  and 2D displays.
Although 3D display is more realistic, the 2D view is useful for analysis and debugging. Also, the 2D layered visualization system allows the creation of user-defined layers.

# 3D display

This type of visualization is an adaptation of the 3D view of SweetHome3D. Although it lacks of flexibility, gives a better view of what's happening in the simulation.

![](http://i.imgur.com/e0P2XJ5.gif)

#  SweetHome3D 2D display

During the first development iteration, this type of display was the one that was used, but it is not maintained anymore. It had thread safety problems, and does not always work. Its usage it is discoraged. It has been left because...why not?. Maybe it is useful in the future.

# MASSIS 2D display

This is the most powerful visualization utility of MASSIS. It is not intended for realism, (it is plain 2D), but it provides a great flexibility, and extensibility. It is based on layers, which can be drawn one on top of each other, and multiple things can be viewed at the same time. Also, they can be enabled and disabled.
It is the recommended visualization for developers and people interested in analizing concrete issues.

![](http://i.imgur.com/Q6mbTo8.gif)

## Tabs & Levels

Each floor (level) of the simulation environment has its own tab, that makes the task of switching between floors faster.
This type of display allows zooming and panning, and each tab maintains its zoom state, so switching between interesting points (such as bottlenecks in different floors), does not imply a headache.

![](http://i.imgur.com/DTBu6RA.gif)

## Default Layers

MASSIS 2D display comes with several general-purpose layers, that might be useful most of the times, and also can serve as an example for creating new ones. They are as follows:

- Rooms: Draws the rooms areas
- Rooms Labels: Draws the name of the rooms
- Vision Radio : Draws the vision radio of the agents
- Crowd density: Draws the crowd density represented as a heatmap
- Walls : Draws the walls of the building
- Connections : Draws the pathfinder visibility graph connections
- Doors : Draws the doors of the floor
- Paths : Draws the paths that the agents are following
- PathFinder : Draws the obstacles recognized by the pathfinder
- People : Draws the dynamic obstacles, as arrows
- Body radios : Draws the proximity radio of the dynamic elements of the environment
- Visible agent lines: Shows a which nodes are the agents and the edges are the visible connection between them
- QuadTree : Shows the bottom-up propagation quadtree representation


# Creating a custom layer

## How layers are structured
After this introduction, the obvious question is: _How do I implement my own custom layer?_.

The layer system in massis is based on the [massis gui utility package](https://github.com/rpax/MASSIS/tree/master/massisframework/massisframework-utils/massis-gui).
The building visualization system is composed by a set of layers of `DrawableFloor`s.

One of the most basic layer is the Room layer. It only fills the polygons representing the rooms:

    /**
     * Draws each room of the floor
     *
     * @author rpax
     *
     */
    public class RoomsLayer extends DrawableLayer<DrawableFloor> {

        public RoomsLayer(boolean enabled)
        {
            super(enabled);

        }

        @Override
        public void draw(DrawableFloor dfloor, Graphics2D g)
        {
            final Floor f = dfloor.getFloor();
            for (SimRoom r : f.getRooms())
            {
                g.setColor(Color.gray);
                g.fill(r.getPolygon());
            }

        }

        @Override
        public String getName()
        {
            return "Rooms";
        }
    }

Every time a repaint is needed, the visualization systems makes a call to `draw(DrawableFloor dfloor, Graphics2D g)`. That is the place where the drawing should occur. The scaling, offseting and panning issues are already resolved,
so in this method the only things that should be done are the calls to the `Graphics2D` methods.

>Tip: you can also take advantage of the utility class [`FloorMapLayersUtils`](https://github.com/rpax/MASSIS/blob/master/massisframework/massis/massis-core/src/main/java/com/massisframework/massis/displays/floormap/layers/FloorMapLayersUtils.java), which contains several methods that make less tedious the drawing common tasks. (Casting to ints, drawing points, lines, polygons, etc.)


Another interesting layer is the "People" layer. This layer shows the triangles representing the dynamic elements of the simulation.

    private static Color DEFAULT_PERSON_FILL_COLOR = Color.WHITE;
    private static Color DEFAULT_PERSON_DRAW_COLOR = Color.BLUE;

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
        final Floor f = dfloor.getFloor();
        g.setColor(Color.red);

        for (DefaultAgent p : f.getAgents())
        {
            if (!p.isDynamic())
            {
                g.setColor(new Color(165, 42, 42));
                g.fill(p.getPolygon());
            }
        }
        for (DefaultAgent p : f.getAgents())
        {
            if (p.isDynamic())
            {
                KPolygon poly = KPolygon.createRegularPolygon(3, p.getPolygon()
                        .getRadius());
                poly.scale(1, 0.6);

                poly.rotate(p.getAngle());
                poly.translateTo(p.getX(), p.getY());

                g.setColor(DEFAULT_PERSON_FILL_COLOR);

                g.fill(poly);
                g.setColor(DEFAULT_PERSON_DRAW_COLOR);
                g.draw(poly);
            }
        }

    }

As yu can observe, the idea of this "layered" way for drawing is modularity. A layer is conceived for a very specific task, and should be small. If it starts to become bigger, think about splitting it into two.

## The game layer

We want to create a layer that shows the difference between the agents status. For example, the tagged agent should be red. Taking `PeopleLayer` as an example,


    private static Color DEFAULT_PERSON_FILL_COLOR = Color.WHITE;
    private static Color DEFAULT_PERSON_DRAW_COLOR = Color.BLUE;

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
        final Floor f = dfloor.getFloor();
        g.setColor(Color.red);

        for (DefaultAgent p : f.getAgents())
        {
            if (!p.isDynamic())
            {
                g.setColor(new Color(165, 42, 42));
                g.fill(p.getPolygon());
            }
        }
        for (DefaultAgent p : f.getAgents())
        {
            if (p.isDynamic())
            {
                KPolygon poly = KPolygon.createRegularPolygon(3, p.getPolygon()
                        .getRadius());
                poly.scale(1, 0.6);

                poly.rotate(p.getAngle());
                poly.translateTo(p.getX(), p.getY());

                g.setColor(DEFAULT_PERSON_FILL_COLOR);

                g.fill(poly);
                g.setColor(DEFAULT_PERSON_DRAW_COLOR);
                g.draw(poly);
            }
        }

    }





