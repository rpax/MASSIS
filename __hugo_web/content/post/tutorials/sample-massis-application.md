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

![](http://i.imgur.com/3lvAJlo.png)

- Select the artifact id, the package and the version. In this tutorial, it will be.
- In this example, it will be:
 - GroupId : `com.myawesomesimulator`
 - artifactId : `myawesomesimulator`
 - version : `0.0.1-SNAPSHOT`
 - package : `com.myawesomesimulator`

![](http://i.imgur.com/IrXGbgv.png)

Maven will start downloading the basic dependencies for this project. It should take a few minutes, depending on your internet connection. Take a coffee break.

Once the process is finished, the project should look something like this:

![](http://i.imgur.com/95aEmU2.png)

Now we have our own simulator!. Lets explain what's in it.

- `pom.xml`. Contains the information about this project, and its dependencies. The `repositories` tag contains where are the massis dependencies stored, and the `dependencies` tag, the dependencies of our project. If you want to know more about maven dependencies, please take a look at the [Introduction to the Dependency Mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.myawesomesimulator</groupId>
	<artifactId>myawesomesimulator</artifactId>
	<version>0.0.1-SNAPSHOT</version>
    <repositories>
        <repository>
            <id>nexus-massisframework</id>
            <name>nexus-massisframework repository</name>
            <url>http://mvn.massisframework.com/nexus/content/groups/public</url>
        </repository>
    </repositories>

	<dependencies>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>3.8.1</version>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>com.massisframework.massis</groupId>
			<artifactId>massis-core</artifactId>
			<version>1.0-SNAPSHOT</version>
		</dependency>
	</dependencies>
	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<maven.compiler.source>1.7</maven.compiler.source>
		<maven.compiler.target>1.7</maven.compiler.target>
	</properties>
	<build>
		<resources>
			<resource>
				<directory>src/main/java</directory>
				<excludes>
					<exclude>**/*.java</exclude>
				</excludes>
			</resource>
			<resource>
				<directory>src/main/resources</directory>
			</resource>
		</resources>
	</build>
</project>

```

- `HelloHighLevelController.java` : Represents a simple AI that prints "Hello!" at every simulation step

```
public class HelloHighLevelController extends HighLevelController {

	private static final long serialVersionUID = 1L;
	
	public HelloHighLevelController
    (LowLevelAgent agent, Map<String, String> metadata, String resourcesFolder) {
		super(agent, metadata, resourcesFolder);
		this.agent.setHighLevelData(this);
	}

	@Override
	public void stop() {
		/*
		 * Clean resources, threads...etc
		 */
	}

	@Override
	public void step() {
		System.out.println("Hello!");
	}
}
```

- `EnvironmentEditor.java`. A simple launcher for the sweethome3D editor, with the default [dynamic plugins](#TODO).

```
public class EnvironmentEditor {

	public static void main(String[] args) {
		HomeMetadataLoader metadataLoader = new HomeMetadataLoader();
		List<? extends AdditionalDataWriter> writers = Arrays.asList(metadataLoader);
		List<? extends AdditionalDataReader> loaders = Arrays.asList(metadataLoader);

		List<Class<? extends Plugin>> plugins = new ArrayList<>();
		plugins.add(BuildingMetadataPlugin.class);
		SweetHome3DAdditionalDataApplication.run(new String[] {}, loaders, writers, plugins);
	}

}
```

- `SimulationWithUILauncher.java` : An example of a simulation launcher with different types of 2D layers.

```
public class SimulationWithUILauncher {

/**
 * @param args the building file path
 */
public static void main(String[] args) {

    final String buildingFilePath = args[0];
    /*
     * Not needed, really. We are not going to load any kind of resources
     * during the simulation.
     */
    final String resourceFolderPath = "";

    Simulation simState = new Simulation(System.currentTimeMillis(), buildingFilePath, resourceFolderPath, null);
    /**
     * Basic Layers. Can be added more, or removed.
     */
    @SuppressWarnings("unchecked")
    DrawableLayer<DrawableFloor>[] floorMapLayers=
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
                new QTLayer(false)
            };

    GUIState vid = new SimulationWithUI(simState,floorMapLayers);

    Console c = new Console(vid);

    c.setIncrementSeedOnStop(false);
    //
    c.pressPlay();
    c.pressPause();
    c.setVisible(true);

}

}

```

And that's all!. A complete example of a massis application can be found in the tutorial _[Operation: Rescue the robots]({{< relref "post/tutorials/operation-rescue-the-robots.md" >}})_






