---
layout: page
title : "Getting started"
---

The following requirements are needed for running MASSIS:

## Software

- **Java 1.7 or later** (Can be downloaded from the [Oracle Java downloads web site](http://www.oracle.com/technetwork/java/javase/downloads/index.html))

>_Note_: Remember to add the `JAVA_HOME` environment variable in your OS.

- **Maven 3.1.1+** (Can be downloaded from the [Maven download site](https://maven.apache.org/download.cgi))

>_Note_: Remember to add the `M2_HOME` environment variable in your OS.


- **Ant:** (Can be downloaded from the [Apache Ant download page](https://ant.apache.org/bindownload.cgi))

>_Note_: Remember to add the `ANT_HOME` environment variable in your OS.

## IDE

Although MASSIS can be used without any kind of IDE, it is recommended to do so. Two good IDE's are [Eclipse](https://eclipse.org/downloads/) and [Netbeans](https://netbeans.org/downloads/).

## Using MASSIS

There are two ways for using MASSIS. Typical users who want to create their own environment and agents can use  MASSIS maven artifacts to create their application in an easy way. For those who need to modify core features of MASSIS,  the source code is available from Github.

### Option 1: Using MASSIS' maven artifacts

This is the preferred option if the objective of your project is developing an application using MASSIS' features, without changing MASSIS' source code. MASSIS maven artifacts allow to create a prototype of a simulation application, which has to be customized by defining the particular environment and the elements (agents, actuators, sensors) in it.

The [tutorials section]({{ site.baseurl }}/tutorials/) explains how to create custom applications using the MASSIS maven archetypes, with step by step examples about how to do it. It is recommended to follow these tutorials in order to learn how to work with MASSIS.


### Option 2: Downloading the source code

This is the recommended option for changing core features of MASSIS. The necessary MASSIS' modules are hosted at Github, in the following repositories:

- [Main MASSIS Repository](https://github.com/rpax/MASSIS): Contains the basic MASSIS' features.
- [StraightEdge Repository](https://github.com/rpax/straightedge): Contains the same source code of the original. [StraightEdge's Google Code page](https://code.google.com/p/straightedge/), but it has been _mavenized_.
- [SweetHome3D repository](https://github.com/rpax/sweethome3d): Contains the source code of the original [SweetHome3D](http://www.sweethome3d.com/download.jsp) page, but its external libraries are referenced as maven dependencies.
- [SweetHome3D plugins repository](https://github.com/rpax/massis-sh3d-plugins). Contains extensions for the environment design with SweetHome3D for MASSIS.


## What to do next?

Go to the  [Tutorials section]({{ site.baseurl }}/tutorials/) to start working with MASSIS.



