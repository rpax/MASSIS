+++
date = "2015-11-05T13:19:52+01:00"
draft = false
title = "Creating a MASSIS Application. Part 1: Downloading a basic archetype"
categories = ["tutorials"]
+++
This tutorial covers the basic topics about creating a simple agent behavior for MASSIS. It is assumed that the sections about
[downloading]({{< relref "page/getting-started.md" >}}) , and
[environment design]({{< relref "post/tutorials/environment-design.md" >}}) sections have been already been read.

Although an experienced programmer can do all of this steps without any kind of IDE, for illustrating purposes, the [Eclipse platform](http://www.eclipse.org/downloads/) will be used here. The recommended version is the _Eclipse IDE for Java Developers_.

{{< fig "http://i.imgur.com/6574ISB.png" >}}

## Creating a maven project based on a massis archetype

- Right click on the projects sidebar, and select "New Project".

{{< fig "http://i.imgur.com/jHjluiC.png" >}}

- In the project type window, select new Maven Project and press _Next_.

{{< fig "http://i.imgur.com/nekxikS.png" >}}

- Ensure that the checkbox _Skip Archetype Selection_ is **unchecked**.

{{< fig "http://i.imgur.com/uNrk3nQ.png" >}}

- Now, we need to add the massis archetype catalog. This can be done through _Configure... -> Add Remote Catalog_, and selecting the massis archetype catalog,which is located at http://mvn.massisframework.com/nexus/content/groups/public/archetype-catalog.xml

{{< fig "http://i.imgur.com/NwtY4Wk.png" >}}


{{< fig "http://i.imgur.com/0KDUOcB.png" >}}


{{< fig "http://i.imgur.com/hRWww0Z.png" >}}

- Remember to check the option _Include snapshot archetypes_

{{< fig "http://i.imgur.com/h4hT8Jr.png" >}}

- The next window asks for the information about our groupId and artifactId. In this example, the groupId will be `tutorialfollower` and the artifactId `myfirstmassisproject`.

{{< fig "http://i.imgur.com/KbjsvNR.png" >}}

After pressing _Finish_, Eclipse (maven) will start downloading the required dependencies for this project. Depending on your internet connection, this may take a while. The progress can be seen on the _Progress_ tab. (Or clicking in the bottom right corner of the window).

{{< fig "http://i.imgur.com/YjNf5ai.png" >}}

If you want to see the output of the maven console, this can be done in the _Console tab -> Maven Console_ 

{{< fig "http://i.imgur.com/Bojyyev.png" >}}

The output should be something like this:

```
12/11/15, 6:11:42 PM GMT+1: [INFO] Downloading https://repo1.maven.org/maven2/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-macosx-universal.jar
12/11/15, 6:11:43 PM GMT+1: [INFO] Downloaded https://repo1.maven.org/maven2/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-macosx-universal.jar
12/11/15, 6:11:43 PM GMT+1: [INFO] Downloading https://repo1.maven.org/maven2/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-solaris-amd64.jar
12/11/15, 6:11:44 PM GMT+1: [INFO] Downloaded https://repo1.maven.org/maven2/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-solaris-amd64.jar
12/11/15, 6:11:44 PM GMT+1: [INFO] Downloading https://repo1.maven.org/maven2/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-solaris-i586.jar
12/11/15, 6:11:44 PM GMT+1: [INFO] Downloaded https://repo1.maven.org/maven2/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-solaris-i586.jar
12/11/15, 6:11:44 PM GMT+1: [INFO] Downloading https://repo1.maven.org/maven2/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-windows-amd64.jar
12/11/15, 6:11:45 PM GMT+1: [INFO] Downloaded https://repo1.maven.org/maven2/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-windows-amd64.jar
12/11/15, 6:11:45 PM GMT+1: [INFO] Downloading https://repo1.maven.org/maven2/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-windows-i586.jar
12/11/15, 6:11:45 PM GMT+1: [INFO] Downloaded https://repo1.maven.org/maven2/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-windows-i586.jar
12/11/15, 6:09:57 PM GMT+1: [INFO] Downloaded https://repo1.maven.org/maven2/xalan/xalan/2.6.0/xalan-2.6.0.jar
```

Once all dependencies are downloaded, the project should contain three files:

- `EnvironmentEditor.java` : Contains the necessary code for launching the environment editor.
- `SimulationWithUILauncher.java` : This class is responsible for launching the simulator
- `MyHelloHighLevelController` : A behavior definion sample.


{{< fig "http://i.imgur.com/37HqUP4.png" >}}

What to do next? The tutorial continues on [Creating a MASSIS Application. Part 2: Customizing the archetype]({{< relref "post/tutorials/customizing-the-archetype.md" >}})

