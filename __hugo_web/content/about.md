 	+++
date = "2015-11-01T13:23:29+01:00"
draft = false
title = "What is MASSIS?"

+++

MASSIS is a simulation framework for scenarios in indoor environments, allowing to design spaces, and specifying the behavior of the elements and people in them. These behaviors specifications may vary substantially: From a simple presence detector to human behavior. It is capable of supporting thousands of agents, each one with an specific behavior. The behavior specification is done outside the simulation platform, and multiple behavior models can be integrated.

![MASSISFrameworkDiagram](http://i.imgur.com/1AnvPlq.png)

The simulation progress can be visualized in 3D, from different perspectives, or in 2D. The 2D visualization library is based on layers (the elements of each layer are drawn on top of the previous layer), making easier the development of a new type of visualization for specific purposes. Also, it allows to save the simulation changes, recording each agent state in every step of the simulation. These changes are saved in an open and independent format (JSON), allowing the analysis of the results from any other platform and language.

![3DLayers](http://i.imgur.com/PuXmC7F.png) ![2DLayers](http://i.imgur.com/Uw6JFi3.png)

Both MASSIS and its components are open source, allowing the extension of its functionality by third parties.