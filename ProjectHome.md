This is [Maven](http://maven.apache.org) plugin for [Play! Framework](http://www.playframework.org).

It supports Play! **1.x** versions only.

Play! **2.x** plugin is available here http://code.google.com/p/play2-maven-plugin

Many thanks to <a href='http://www.ej-technologies.com/products/jprofiler/overview.html'><img src='http://maven-play-plugin.googlecode.com/svn/wiki/images/jprofiler.png' /></a>

**Project documentation**

Release [notes](ReleaseNotes.md)

List of [mavenized Play! modules](MavenizedModules.md)

Plugin [usage](Usage.md)

Maven generated [plugin's site](https://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-beta7/index.html). Most interesting is [plugin mojos (tasks) description page](https://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-beta7/play-maven-plugin/plugin-info.html).

Many working example projects:

- [stable (1.0.0-beta7) version](http://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-beta7/).

- [under development (1.0.0-beta8-SNAPSHOT) version](http://maven-play-plugin.googlecode.com/svn/trunk/test-projects/).

**News**

<font color='red'><strong>2015.05.13 - Play! Framework versions: 1.3.1, 1.2.7.2, 1.2.6.1, 1.2.5.5, 1.2.5.3, 1.2.5.2 and 1.2.5.1 mavenized</strong></font>

<font color='red'><strong>2015.01.27 - Play! Framework 1.3.0 mavenized</strong></font> - read [Play! 1.2.x to 1.3.x migration guide](Play12to13Migration.md) and see how [test projects were updated](https://code.google.com/p/maven-play-plugin/source/detail?r=11861).

2014.06.29 - Play! Framework 1.3.0-RC1 mavenized.

<font color='red'><strong>2014.02.14 - 1.0.0-beta7 version released</strong></font>

2014.01.02 - Play! [1.2.8-SNAPSHOT](https://oss.sonatype.org/content/repositories/snapshots/com/google/code/maven-play-plugin/org/playframework/play/1.2.8-SNAPSHOT/) new snapshot deployed (based on [Github repo 1.2.x branch current state](https://github.com/playframework/play1/tree/f0661361347d61e1a07f9461434b3dac810a0d2b))

2014.01.02 - Play! [1.3.0-SNAPSHOT](https://oss.sonatype.org/content/repositories/snapshots/com/google/code/maven-play-plugin/org/playframework/play/1.3.0-SNAPSHOT/) new snapshot deployed (based on [Github repo 1.3.x branch current state](https://github.com/playframework/play1/tree/74fe7582bab56c6013ae97fa1d1329c2b01a840f))

2012.08.30 - Play! Framework 1.2.7 mavenized.

When using [Play! application default parent](http://repo2.maven.org/maven2/com/google/code/maven-play-plugin/play-app-default-parent/1.0.0-beta6/play-app-default-parent-1.0.0-beta6.pom), just add:

```
<properties>
  <play.version>1.2.7</play.version>
</properties>
```

to your project's pom.xml file. This version will be default in future Play! application default parent versions.

2013.08.26 - Play! [1.2.7-SNAPSHOT](https://oss.sonatype.org/content/repositories/snapshots/com/google/code/maven-play-plugin/org/playframework/play/1.2.7-SNAPSHOT/) new snapshot deployed (based on [Github repo 1.2.x branch current state](https://github.com/playframework/play1/tree/68aa95e96bed5bb519b595133dfc1474af04a21d)),

2013.08.26 - Play! [1.3.0-SNAPSHOT](https://oss.sonatype.org/content/repositories/snapshots/com/google/code/maven-play-plugin/org/playframework/play/1.3.0-SNAPSHOT/) new snapshot deployed (based on [Github repo 1.3.x branch current state](https://github.com/playframework/play1/tree/7d9e2dc6abebb8af026c72241bcc4f6ddd60408f))

2013.08.20 - Play! 1.2.7-SNAPSHOT deployed (based on [Github repo 1.2.x branch current state](https://github.com/playframework/play1/tree/9b5db0efe362596c5dccd553df25fe86e244840d)),

2013.08.20 - Play! 1.3.0-SNAPSHOT new snapshot deployed (based on [Github repo 1.3.x branch current state](https://github.com/playframework/play1/tree/ce69eaa143044f4fb4e17f1e28bbb1f71627f7e9))

2012.08.11 - Play! Framework 1.2.6 mavenized.

When using [Play! application default parent](http://repo2.maven.org/maven2/com/google/code/maven-play-plugin/play-app-default-parent/1.0.0-beta6/play-app-default-parent-1.0.0-beta6.pom), just add:

```
<properties>
  <play.version>1.2.6</play.version>
</properties>
```

to your project's pom.xml file. This version will be default in future Play! application default parent versions.

When releasing 1.2.6 version, Sonatype Nexus automatically removed 1.2.6-SNAPSHOT and 1.3.0-SNAPSHOT versions from snapshot repository. New version of 1.3.0-SNAPSHOT will be deployed soon. 1.2.6-SNAPSHOT will not be redeployed.

2013.07.19 - Play! 1.2.6-SNAPSHOT new snapshot deployed (based on [Github repo 1.2.x branch current state](https://github.com/playframework/play1/tree/2b2bdeebbbeba52a8e23950a0adb712444aa6a8d)),

2013.07.19 - Play! 1.3.0-SNAPSHOT new snapshot deployed (based on [Github repo 1.3.x branch current state](https://github.com/playframework/play1/tree/0ef76646b31982f1bce3a5e0206723ed99cddc8f))

<font color='red'><strong>2013.07.11 - 1.0.0-beta6 version released</strong></font>

<font color='red'>WARNING:</font> Latest Selenium version (2.33) used by default for Selenium tests in "play-app-default-parent" is not compatible with Firefox version 22. More info [here](http://code.google.com/p/selenium/issues/detail?id=5554) and [here](https://groups.google.com/forum/#!topic/selenium-users/IDYUvv3hKJI). Use FF v.21 or older or other browser.

2013.06.11 - Play! 1.2.6-SNAPSHOT new snapshot deployed (based on [Github repo 1.2.x branch current state](https://github.com/playframework/play/tree/01024939eaabfb95ca8e5e0c63272911d324fe89)),

2013.06.11 - Play! 1.3.0-SNAPSHOT new snapshot deployed (based on [Github repo 1.3.x branch current state](https://github.com/playframework/play/tree/0b9d70a7b1b556a9899fc35eee92dbc6a91632d9))

2013.03.27 - 1.0.0-beta5 version released

2013.02.22 - Play! 1.2.6-SNAPSHOT and 1.3.0-SNAPSHOT versions deployed to [Sonatype OSS snapshots repository](https://oss.sonatype.org/content/repositories/snapshots)

1.2.6-SNAPHOT built from Play!'s [1.2.x](https://github.com/playframework/play/tree/1.2.x) Github repository branch,

1.3.0-SNAPHOT built from Play!'s [1.3.x](https://github.com/playframework/play/tree/1.3.x) Github repository branch,

When using [default parent](http://repo2.maven.org/maven2/com/google/code/maven-play-plugin/play-app-default-parent/1.0.0-beta4/play-app-default-parent-1.0.0-beta4.pom), just add:

```
<properties>
  <play.version>1.2.6-SNAPSHOT</play.version>
</properties>
```

or

```
<properties>
  <play.version>1.3.0-SNAPSHOT</play.version>
</properties>
```

to your project's pom.xml file.

If not, make sure you have:

```
<repositories>
  <repository>
    <id>sonatype-nexus-snapshots</id>
    <name>Sonatype Nexus Snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

defined in your project's pom.xml or any of it's parents.


2013.01.21 - 1.0.0-beta4 version released

2013.01.10 - ... - Play! external modules deployed to Maven central repository - work in progress

2013.01.09 - 2013.01.18 - Play! Framework (all versions) and Play! embedded modules deployed to Maven central repository

2012.07.05 - 1.0.0-beta3 version released

2012.07.04 - Play! Framework 1.2.5 mavenized

If you use "play-app-default-parent" 1.0.0-beta3, 1.2.5 is default Play! Framework version now.

If you use older version of "play-app-default-parent" or do not use it, add "play.version" property in your application's "pom.xml" file:

```
<properties>
  <play.version>1.2.5</play.version>
</properties>
```

2012.06.26 - 1.0.0-beta2 version released

2012.04.02 - 1.0.0-beta1 version released

2012.03.21 - 1.0.0-alpha7 version released

2012.01.23 - 1.0.0-alpha6 version released

2011.12.06 - 1.0.0-alpha5 version released

2011.10.31 - 1.0.0-alpha4 version released

2011.10.19 - 1.0.0-alpha3 version released