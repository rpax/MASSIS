---
layout: page
title: What is MASSIS?
---

MASSIS is a software framework that facilitates the simulation of scenarios with multiple agents (representing people, robots, sensors, etc.) in indoor environments (i.e., inside a building). MASSIS provides support for designing the environment and specifying the behavior of the elements and agents. It allows the simulation of a great diversity of behaviours, from a simple sensor to the decisions of a person. MASSIS has been designed to keep this flexibility withough hindering performance. The framework is capable of supporting thousands of agents, each one with a specific behavior. 

The next figure shows the main components of the framework. First, MASSIS relies on Sweet Home 3D for the edition of the environment. This has been enhanced with some plugins that allow to include meta-information that is used by the simulator in order to link the elements in the environment with their behaviour. The simulator relies on Mason agent-based simulation tool, which is enriched with a set of components that take care of performance in common operations, support for different visualizations, and tools for exporting simulation data in different formats for further processing.

![](http://i.imgur.com/1AnvPlq.png)


The simulation progress can be visualized from different perspectives in 3D or in 2D. The 2D visualization library is based on layers (the elements of each layer are drawn on top of the previous layer). This facilitates the development of a new type of visualization for specific purposes. Also, it is possible to save the simulation changes, recording each agent state in every step of the simulation. These changes are saved in an open and independent format, such as JSON, which allows further analysis of the results with other tools.

![](http://i.imgur.com/PuXmC7F.png)
![](http://i.imgur.com/Uw6JFi3.png)

The  video below shows MASSIS in action with a simulation of the building of our faculty of Computer Science. At the beginning it shows how the building is designed with Sweet Home 3D. This is followed by the execution of a simulation and finally the different views that can be obtained, to focus on different aspects.

<iframe width="560" height="315" src="https://www.youtube.com/embed/CpAeQ13Go9A" frameborder="0" allowfullscreen></iframe>

Both MASSIS and its components are open source, allowing the extension of its functionality by third parties.

To start using MASSIS, go the [Getting Started section]({{ site.baseurl }}/getting-started.html), and then follow the [Tutorials]({{ site.baseurl }}/tutorials/).
