---
title : "Extending the GUI"
---

In the [preceding part of the tutorial][tutorial5] several agents are playing the Tag game but it is not possible to see which one is tagged.
This implies the need to visualize the _state_ of the agents (in this case, whether they are tagged or not).

A [previous tutorial][tutorial3] already introduced  different types of visualization that MASSIS offers. Now we will go deeper into this topic, and  will learn how to extend the default MASSIS layer system, which is the basis for the 2D visualization system.

# MASSIS visualization displays revisited

All the changes made in the environment during the simulation are reflected in real time by 3D  and 2D displays.
Although a 3D display is more realistic (interesting for demos), the 2D view is more useful for analysis and debugging. Also, the 2D layered visualization system allows the creation of user-defined layers.

## 3D display

This type of visualization is an adaptation of the 3D view of SweetHome3D. Although it lacks of flexibility, it gives a better view of what is happening during the simulation.

![](http://i.imgur.com/e0P2XJ5.gif)

##   2D display from Sweet Home 3D

Although this display is shown as an alternative (it appears in the _Displays_ tab as _Building 2D_), it is not maintained by MASSIS. It has some thread safety problems  and does not always work properly. For this reason its usage it is not recommended. It has been left just because it was used in the first versions of MASSIS.

## MASSIS 2D display

This is the most powerful visualization utility of MASSIS, with  a great degree of flexibility and extensibility. MASSIS 2D displays are based on layers, which can be drawn one on top of each other, so multiple things can be viewed at the same time. Each layer can  be enabled and disabled at any time during the simulation.
Therefore, this type of display is the recommended visualization tool for developers and people interested in analizing concrete issues.

![](http://i.imgur.com/Q6mbTo8.gif)

### Tabs & Levels

Each floor of the building (level) in the simulation environment has its own tab, which makes the task of switching between floors faster.
This type of display allows zooming and panning, and each tab maintains its zoom state, so switching between interesting points (such as bottlenecks in different floors), is relatively easy.

![](http://i.imgur.com/DTBu6RA.gif)

### Default Layers

MASSIS 2D display comes with several general-purpose layers, which are useful most of the times, and  can also serve as an example for creating new ones. They are the following:

- Rooms: Draws the rooms areas.
- Rooms Labels: Draws the name of the rooms.
- Vision Radio : Draws the vision radio of the agents.
- Crowd density: Draws the crowd density represented as a heatmap.
- Walls : Draws the walls of the building.
- Connections : Draws the pathfinder visibility graph connections.
- Doors : Draws the doors of the floor.
- Paths : Draws the paths that the agents are following.
- PathFinder : Draws the obstacles recognized by the pathfinder.
- People : Draws the dynamic obstacles, as arrows.
- Body radios : Draws the proximity radio of the dynamic elements of the environment.
- Visible agent lines: Shows  which nodes are the agents and the edges are the visible connection between them.
- QuadTree : Shows the bottom-up propagation quadtree representation.


## Creating a custom layer

### How layers are structured
This section explains how to implement a custom layer.

The layer system in MASSIS is based on the [MASSIS GUI utility package][massisgui].

The building visualization system is composed by a set of layers of `DrawableFloor`s.
For instance, one of the most basic layers is the **Room** layer. It only fills the polygons representing the rooms:

```java
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
```

Every time a repaint is needed, the visualization system  calls to `draw(DrawableFloor dfloor, Graphics2D g)`. That is the place where the drawing should occur. The scaling, offseting and panning issues are already resolved,
so in this method the only things that should be done are the calls to the `Graphics2D` methods.

>Tip: It is very convenient to make use of the utility class [`FloorMapLayersUtils`][floormaplayerutils], which  contains several methods that make less tedious the drawing common tasks (e.g., casting to int, drawing points, lines, polygons, etc.)


Another interesting layer is the **People** layer. This layer shows the triangles representing the dynamic elements of the simulation.

```java
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
```

Note that the idea of this "layered" way for drawing is modularity. Each layer is conceived for a very specific task, and should be small. If it  becomes big, think about splitting it into two.

### The game layer

Coming back to the Tag game, the issue now is to create a layer that shows the difference between the agents status. For example, the tagged agent should be red. This can be easily done by creating a new layer to draw colors for the player agents, depending on whether they are tagged or not. This requires to 

1. Create a new class `TagGameLayer`:

	```java
	import java.awt.Color;
	import java.awt.Graphics2D;
	import com.massisframework.gui.DrawableLayer;
	import com.massisframework.massis.displays.floormap.layers.DrawableFloor;
	import com.massisframework.massis.model.agents.DefaultAgent;
	import com.massisframework.massis.model.building.Floor;
	import straightedge.geom.KPolygon;

	public class TagGameLayer extends DrawableLayer<DrawableFloor> {
	    private static Color STATIC_AGENT_COLOR = new Color(165,42,42);
	    private static Color DEFAULT_PERSON_FILL_COLOR = Color.WHITE;
	    private static Color DEFAULT_PERSON_DRAW_COLOR = Color.BLUE;
	    private static Color TAGGED_PERSON_DRAW_COLOR = Color.RED;
	    private static Color UNTAGGED_PERSON_DRAW_COLOR = Color.GREEN;

	    public TagGameLayer(boolean enabled) {
			super(enabled);
	    }

	    @Override
	    public void draw(DrawableFloor dfloor, Graphics2D g)
	    {
	        final Floor f = dfloor.getFloor();
        	for ( DefaultAgent p : f.getAgents() ) {
        		if ( p.isDynamic() )
        		{
        			KPolygon poly = KPolygon.
				   createRegularPolygon(3, p.getPolygon().getRadius());
	        		poly.scale(1, 0.6);	
	        		poly.rotate(p.getAngle());
	        		poly.translateTo(p.getX(), p.getY());
	    			g.setColor(DEFAULT_PERSON_DRAW_COLOR);
	    			g.draw(poly);
    				if (p.hasProperty("TAGGED")) {
    					if ("true".equals(p.getProperty("TAGGED"))) {
    						g.setColor(TAGGED_PERSON_DRAW_COLOR);
    					} else { // untagged agents
    						g.setColor(UNTAGGED_PERSON_DRAW_COLOR);
    					}
    				} else {
    					g.setColor(DEFAULT_PERSON_FILL_COLOR);
    				}
    				g.fill(poly);
    				
        		}
        		else
        		{
        			g.setColor(STATIC_AGENT_COLOR);
        			g.fill(p.getPolygon());
        		}
        	}
	    }

	    @Override
	    public String getName() {
		return "TAGGED AGENTS";
	    }
	}
	```

2. Add this layer when declaring the layers in the `SimulationWithUILauncher` class:

```java
	@SuppressWarnings("unchecked")
	final DrawableLayer<DrawableFloor>[] floorMapLayers = 
		new DrawableLayer[] {
			new RoomsLayer(true),
			new RoomsLabelLayer(false),
			new VisionRadioLayer(false),
			new CrowdDensityLayer(false),
			new WallLayer(true),
			new DoorLayer(true),
			new ConnectionsLayer(false),
			new PathLayer(false),
			new PeopleLayer(true),
			new RadioLayer(true),
			new PathFinderLayer(false),
			new PeopleIDLayer(false),
			new VisibleAgentsLines(false),
			new QTLayer(false),
			new TagGameLayer(true) // Add the new layer for the game
	};
```

Run the simulation to see how the tagged agent (in red) follows other agents until it tags one of them, and the colors change.

# What to do next?
With all these elements it is possible to create new environments with different types of agents and more complex behaviours. 

An more complex problem is proposed on a [mission to rescue robots in a remote satellite][exercise].

[tutorial3]: {{ site.baseurl }}/tutorials/03-defining-a-simple-behavior
[tutorial5]: {{ site.baseurl }}/tutorials/05-perception
[exercise]: {{ site.baseurl }}/tutorials/operation-rescue-the-robots
[massisgui]: https://github.com/rpax/MASSIS/tree/master/massisframework-utils/massis-gui
[floormaplayerutils]: https://github.com/rpax/MASSIS/blob/master/massisframework/massis/massis-core/src/main/java/com/massisframework/massis/displays/floormap/layers/FloorMapLayersUtils.java
[tobedone]:  {{ site.baseurl }}/tobedone



