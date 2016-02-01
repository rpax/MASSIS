---
title : "Creating a MASSIS project using a Maven archetype"
---
This tutorial covers the basic topics about creating a simple simulation project in MASSIS. It is assumed that the section  [Getting Started][getting-started] has been read.

Although an experienced programmer can do all of these steps without an IDE, it is recommended to use one. 
This tutorial uses the [Eclipse platform][eclipse_page]. The recommended version is the _Eclipse IDE for Java Developers_.

![Downloading Eclipse IDE for Java Developers](http://i.imgur.com/6574ISB.png)

## Creating a maven project based on a massis archetype

Eclipse is already distributed with [Maven][maven_eclipse], which can greatly facilitate the creation of new projects based on archetypes (project templates).
The MASSIS framework provides a Maven archetype to support the creation of new projects for simulation of multiple agents in indoor scenarios.

In order to create a project using the archetype in eclipse, follow these steps:

1. Right click on the projects sidebar, and select _New -> Project..._

    ![Eclipse new project](http://i.imgur.com/jHjluiC.png)

2. In the project type window, select _Maven Project_ and press _Next>_.

    ![Creating a new project as a Maven Project](http://i.imgur.com/nekxikS.png)

    Ensure that the checkbox _Create a simple project (skip archetype selection)_ is **unchecked**:

    ![Create a simple project (skip archetype selection) checkbos is unchecked](http://i.imgur.com/uNrk3nQ.png)


3. Type _hellosimulator_ in the search box and select _hellosimulator-archetype_. Click _Next>_ .

    ![Selection of MASSIS hellosimulator-archetype](http://i.imgur.com/9yN8wIN.png)

	> **NOTE:** In case Maven Central archetype repository is not well configured, it may be needed to add Maven Central to the list of Catalogs. This can be done with the button _Configure..._ Then the button _Add Remote Catalog..._ and in the popup window indicate as Catalog File: http://repo.maven.apache.org/maven2  and as Description something like "Maven Central".

4. The next window asks for the information about the _group Id_ and _artifact Id_. In this example, the _group Id_ will be `tutorialfollower` and the _artifact Id_ will be `myfirstmassisproject`.

    ![Introduction of group and artifica ids](http://i.imgur.com/KbjsvNR.png)

    After pressing _Finish_, Eclipse (Maven) will start downloading the required dependencies for this project. Depending on your internet connection, this may take a while. The progress can be seen on the _Progress_ tab. (Or by clicking in the bottom right corner of the window).

    ![Progress bar](http://i.imgur.com/YjNf5ai.png)

It is possible to see the output of the Maven console,  in the _Console tab -> Maven Console_ 

![Maven console selection](http://i.imgur.com/Bojyyev.png)

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

- `EnvironmentEditor.java` : It contains the necessary code for launching the environment editor.
- `SimulationWithUILauncher.java` : This class is responsible for launching the simulator.
- `MyHelloHighLevelController` : An example of a very simple behavior for an agent.

![View of the project files in Eclipse](http://i.imgur.com/37HqUP4.png)

# What to do next?

The next tutorial, [Customizing the archetype][tutorial2], explains how to configure the environment and associating behaviour to one agent in the environment, so a simulation can be run.


[getting-started]: {{ site.baseurl }}/getting-started.html
[tutorial2]: {{ site.baseurl }}/tutorials/02-customizing-the-archetype.html
[eclipse_page]: http://www.eclipse.org/downloads/
[maven_eclipse]: http://www.eclipse.org/m2e/
