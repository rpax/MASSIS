 	+++
date = "2015-11-01T13:23:29+01:00"
draft = false
title = "What is MASSIS?"

+++

MASSIS is a  framework that facilitates the simulation of scenarios with multiple agents (representing people, robots, sensors, etc.) in indoor environments (i.e., in a building). MASSIS provides support for designing spaces and specifying the behavior of the elements and agents in them. It is possible to define a great diversity of behaviours, from a simple sensor to the decisions of a person. MASSIS has been designed to keep this flexibility withough hindering performance. The framework is capable of supporting thousands of agents, each one with an specific behavior. 

{{< fig "http://i.imgur.com/1AnvPlq.png" >}}

{{< youtube "CpAeQ13Go9A" >}}

The simulation progress can be visualized from different perspectives in 3D or in 2D. The 2D visualization library is based on layers (the elements of each layer are drawn on top of the previous layer). This facilitates the development of a new type of visualization for specific purposes. Also, it is possible to save the simulation changes, recording each agent state in every step of the simulation. These changes are saved in an open and independent format (JSON), which allows further analysis of the results with other tools.

{{< fig "http://i.imgur.com/PuXmC7F.png" >}}
{{< fig "http://i.imgur.com/Uw6JFi3.png" >}}

Both MASSIS and its components are open source, allowing the extension of its functionality by third parties.

