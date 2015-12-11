+++
date = "2015-11-05T13:19:52+01:00"
draft = false
title = "Creating a MASSIS Application"
categories = ["tutorials"]
+++
This tutorial covers the basic topics about creating a simple agent behavior for MASSIS. It is assumed that the sections about
[downloading]({{< relref "page/getting-started.md" >}}) , and
[environment design]({{< relref "post/tutorials/environment-design.md" >}}) sections have been already been read.

Although an experienced programmer can do all of this steps without any kind of IDE, for illustrating purposes, the [Eclipse platform](http://www.eclipse.org/downloads/) will be used here. The recommended version is the _Eclipse IDE for Java Developers_.

![](http://i.imgur.com/6574ISB.png)

## Creating a maven project based on a massis archetype

- Right click on the projects sidebar, and select "New Project".

![](http://i.imgur.com/jHjluiC.png)

- In the project type window, select new Maven Project and press _Next_.

![](http://i.imgur.com/nekxikS.png)

- Ensure that the checkbox _Skip Archetype Selection_ is **unchecked**.

![](http://i.imgur.com/uNrk3nQ.png)

- Now, we need to add the massis archetype catalog. This can be done through _Configure... -> Add Remote Catalog_, and selecting the massis archetype catalog,which is located at http://mvn.massisframework.com/nexus/content/groups/public/archetype-catalog.xml

![](http://i.imgur.com/NwtY4Wk.png)


![](http://i.imgur.com/0KDUOcB.png)


![](http://i.imgur.com/hRWww0Z.png)

- Remember to check the option _Include snapshot archetypes_

TODO






