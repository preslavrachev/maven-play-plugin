Simplest project (pom.xml file) should look like:

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <parent>
        <groupId>com.google.code.maven-play-plugin</groupId>
        <artifactId>play-app-default-parent</artifactId>
        <version>1.0.0-beta7</version>
    </parent>

    <modelVersion>4.0.0</modelVersion>
    <groupId>test</groupId>
    <artifactId>test</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>play</packaging>

    <name>Play! Framework Maven Test Project</name>

    <build>
        <plugins>
            <plugin>
                <groupId>com.google.code.maven-play-plugin</groupId>
                <artifactId>play-maven-plugin</artifactId>
                <extensions>true</extensions>
            </plugin>
        </plugins>
    </build>
</project>
```

For Play! 1.3.0 version use different parent:

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <parent>
        <groupId>com.google.code.maven-play-plugin</groupId>
        <artifactId>play13-app-default-parent</artifactId>
        <version>1.0.0-beta8-SNAPSHOT</version>
    </parent>

    <modelVersion>4.0.0</modelVersion>
    <groupId>test</groupId>
    <artifactId>test</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>play</packaging>

    <name>Play! Framework 1.3.x Maven Test Project</name>

    <build>
        <plugins>
            <plugin>
                <groupId>com.google.code.maven-play-plugin</groupId>
                <artifactId>play-maven-plugin</artifactId>
                <extensions>true</extensions>
            </plugin>
        </plugins>
    </build>
</project>
```

If you use Play! modules not deployed to Maven cetral repository, add old repository definition:

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <parent>
        <groupId>com.google.code.maven-play-plugin</groupId>
        <artifactId>play-app-default-parent</artifactId>
        <version>1.0.0-beta7</version>
    </parent>

    <modelVersion>4.0.0</modelVersion>
    <groupId>test</groupId>
    <artifactId>test</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>play</packaging>

    <name>Play! Framework Maven Test Project</name>

    <repositories>
        <repository>
            <id>com.google.code.maven-play-plugin</id>
            <name>Maven Play Plugin - releases</name>
            <url>http://maven-play-plugin.googlecode.com/svn/mavenrepo/releases</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>

    <build>
        <plugins>
            <plugin>
                <groupId>com.google.code.maven-play-plugin</groupId>
                <artifactId>play-maven-plugin</artifactId>
                <extensions>true</extensions>
            </plugin>
        </plugins>
    </build>
</project>
```

For more sample projects go to [test projects](http://maven-play-plugin.googlecode.com/svn/trunk/test-projects/).