Release notes:

**1.0.0-beta7 (2014.02.14)**

1. Enhancements

- <font color='red'>multi-module build support</font> - this is most important feature in this release (see [Issue 137](http://code.google.com/p/maven-play-plugin/issues/detail?id=137)). There are [Sample projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/multimodule) showing different multi-module sample configurations. More documentation will be added soon.

- <font color='red'>Eclipse integration</font> - second most important featurev(see [Issue 74](http://code.google.com/p/maven-play-plugin/issues/detail?id=74)). From this version Play! projects can be imported into and developed in Eclipse with M2Eclipse plugin installed. This is easy, just import as "Existing Maven project". More documentation will be added soon.

- "application.conf" inclusions - (see [Issue 136](http://code.google.com/p/maven-play-plugin/issues/detail?id=136))

2. Change in default behaviour

- selenium-maven-plugin's "browserSessionReuse" configuration parameter's default value changed from "true" to "false" - (see [Issue 139](http://code.google.com/p/maven-play-plugin/issues/detail?id=139))

**1.0.0-beta6 (2013.07.11)**

1. Enhancements

- war generation - default value of "warConfResourcesIncludes" property changed from "application.conf,messages,messages.`*`,routes" to "" (see [Issue 127](http://code.google.com/p/maven-play-plugin/issues/detail?id=127)) <font color='red'>Check if your projects' configurations relied on previous default value and need changes now</font>

- war generation - custom "web.xml" files are filtered just like default ones are (see [Issue 123](http://code.google.com/p/maven-play-plugin/issues/detail?id=123))

- Maven Surefire Plugin version used upgraded from 2.14 to 2.15, <font color='red'>don't forget to change in your projects if not using play-app-default-parent, because Surefire provider API changed</font> (see [Issue 125](http://code.google.com/p/maven-play-plugin/issues/detail?id=125))

- "startTimeout" and "stopTimeout" parameters added to Surefire provider for better control of Play! start and stop before and after unit tests (see [Issue 129](http://code.google.com/p/maven-play-plugin/issues/detail?id=129))

- additional improvements in Surefire provider (see [Issue 126](http://code.google.com/p/maven-play-plugin/issues/detail?id=126) and [Issue 128](http://code.google.com/p/maven-play-plugin/issues/detail?id=128))

- "org.sonatype.oss:oss-parent" set as parent-pom of "play-app-default-parent" and "play-module-default-parent" to allow Maven central deployment for all applications and modules using these default parents (see [Issue 130](http://code.google.com/p/maven-play-plugin/issues/detail?id=130))

2. Mavenized modules:

a) new modules:

- Hazelcast: 0.6 - [test project](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/hazelcast/)

<font color='blue'><a href='http://code.google.com/p/maven-play-plugin/issues/list?can=1&q=Milestone%3D1.0.0-beta6'>All issues for 1.0.0-beta6</a>.</font>


**1.0.0-beta5 (2013.03.27)**

1. Enhancements

- Java 1.5 compatibility (see [Issue 113](http://code.google.com/p/maven-play-plugin/issues/detail?id=113))

- "compileApp" and "compileTest" plugin configuration parameters removed, source code is always compiled (see [Issue 118](http://code.google.com/p/maven-play-plugin/issues/detail?id=118))

- Surefire version used upgraded from 2.11 to 2.14, <font color='red'>don't forget to change in your projects if not using play-app-default-parent, because Surefire provider API changed</font> (see [Issue 119](http://code.google.com/p/maven-play-plugin/issues/detail?id=119))

[See all issues related to this version](http://code.google.com/p/maven-play-plugin/issues/list?can=1&q=1.0.0-beta5)

2. Mavenized modules:

a) new versions of previously mavenized modules:

- Cobertura: 2.4-jdk15 - Java 1.5 compatible version of Cobertura 2.4 module

<font color='blue'><a href='http://code.google.com/p/maven-play-plugin/issues/list?can=1&q=Milestone%3D1.0.0-beta5'>All issues for 1.0.0-beta5</a>.</font>


**1.0.0-beta4 (2013.01.21)**

1. Enhancements:

- deployed to [Maven central repository](http://repo2.maven.org/maven2/com/google/code/maven-play-plugin), no other repository needed

2. Mavenized modules:

a) new modules:

- Redis: 0.3 - [test projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/redis/)

b) new versions of previously mavenized modules:

- Japid: 0.9.4.2, 0.9.4.3, 0.9.5 - [test projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/japid/)

- Morphia: 1.2.12 - [test projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/morphia/)

- SecureSocial: 0.2.5, 0.2.6 - [test projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/securesocial/)

<font color='blue'><a href='http://code.google.com/p/maven-play-plugin/issues/list?can=1&q=Milestone%3D1.0.0-beta4'>All issues for 1.0.0-beta4</a>.</font>


**1.0.0-beta3 (2012.07.05)**

1. Enhancements:

a) plugin

- [#104](http://code.google.com/p/maven-play-plugin/issues/detail?id=104) - Configure inclusion/exclusion filters for "conf" directory resources being copied to "WEB-INF/classes" directory in war mojos ("log4j.properties" for example)

2. Mavenized modules:

a) Play! Framework 1.2.5

b) new versions of previously mavenized modules:

- Japid: 0.9.4.1 - [test projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/japid/)

- Rythm: 1.0.0-RC8 - [test projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/rythm/)

<font color='blue'><a href='http://code.google.com/p/maven-play-plugin/issues/list?can=1&q=Milestone%3D1.0.0-beta3'>All issues for 1.0.0-beta3</a>.</font>


**1.0.0-beta2 (2012.06.26)**

Here is [full list of changes](http://code.google.com/p/maven-play-plugin/issues/list?can=1&q=Milestone=1.0.0-beta2). The most important ones:

1. Bugs fixed:

- [#64](http://code.google.com/p/maven-play-plugin/issues/detail?id=64) - "conf" directory not in classpath

- [#68](http://code.google.com/p/maven-play-plugin/issues/detail?id=68) - use playTestId when reading configuration in "precompile" mojo with "precompileTests=true"

- [#84](http://code.google.com/p/maven-play-plugin/issues/detail?id=84) - "Duplicate file WEB-INF\web.xml" exception in "play:war" and "play-war-exploded" mojos with custom "war/WEB-INF/web.xml" file in the project

- [#93](http://code.google.com/p/maven-play-plugin/issues/detail?id=93) - sometimes module's dependencies (jar libraries) copied to application's "lib" directory instead of module's "lib" directory

2. Enhancements:

a) plugin:

- [#65](http://code.google.com/p/maven-play-plugin/issues/detail?id=65) - create dist-exploded mojo

- [#77](http://code.google.com/p/maven-play-plugin/issues/detail?id=77) - add "skip" configuration parameters for "run", "test", "start" and "stop" mojos

- [#78](http://code.google.com/p/maven-play-plugin/issues/detail?id=78) - add "runWithTests" configuration parameter to "run" mojo (similar to "startWithTests" in "start" mojo)

- [#81](http://code.google.com/p/maven-play-plugin/issues/detail?id=81) - allow play testing to use environment variables when server is started up e.g. -DrunEnv=local

b) Selenium tests support:

- [#69](http://code.google.com/p/maven-play-plugin/issues/detail?id=69) - upgrade Selenium plugin version from 2.2 to 2.3

- [#99](http://code.google.com/p/maven-play-plugin/issues/detail?id=99) - parametrize Selenium server host name and port number for integration testing

- [#86](http://code.google.com/p/maven-play-plugin/issues/detail?id=86) - implement [asser|verify]Selected Selenium command

- [#101](http://code.google.com/p/maven-play-plugin/issues/detail?id=101) - implement Play! Selenium user extension commands

c) other:

- [#79](http://code.google.com/p/maven-play-plugin/issues/detail?id=79) - don't use "expanded" snapshot dependency versions

- [#91](http://code.google.com/p/maven-play-plugin/issues/detail?id=91) - add module version to directory name when extracting provided-scoped modules

3. Mavenized modules:

a) new modules - templating engines:

- Japid: 0.9.3.4, 0.9.3.7 (additionally created play-japid-maven-plugin, see [test projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/japid/) for usage)

- Faster Groovy Templates 1.7 - [test project](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/fastergt/)

- Rythm: 1.0.0-RC5, 1.0.0-RC6, 1.0.0-RC7 - [test projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/rythm/)

b) other new modules:

- AccessLog 1.2

- BetterLogs 1.0

- PDF 0.9

c) new versions of previously mavenized modules:

- Deadbolt: 1.5.3, 1.5.4 - [test projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/deadbolt/)

- Less 0.9.1 - [test projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/less/)

- Morphia 1.2.6a - [test projects](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/morphia/)

- Spring 1.0.3 - [test project](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/spring/)

BACKWARD INCOMPATIBLE CHANGES:

- [#91](http://code.google.com/p/maven-play-plugin/issues/detail?id=91) - add module version to directory name when extracting provided-scoped modules

<font color='blue'><a href='http://code.google.com/p/maven-play-plugin/issues/list?can=1&q=Milestone%3D1.0.0-beta2'>All issues for 1.0.0-beta2</a>.</font>


**1.0.0-beta1 (2012.04.02)**

WAR support extended (see new mojos section below)

GAE and Siena support added:

- new [war-exploded](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-beta1/play-maven-plugin/war-exploded-mojo.html) and [war-inplace](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-beta1/play-maven-plugin/war-inplace-mojo.html) mojos (described below)

- GAE 1.6.0 and Siena 2.0.7 modules mavenized, sample projects added (check [Mavenized Modules](http://code.google.com/p/maven-play-plugin/wiki/MavenizedModules) page)

new mojos:

- [clean](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-beta1/play-maven-plugin/clean-mojo.html) (bound to clean cycle)

- [war-exploded](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-beta1/play-maven-plugin/war-exploded-mojo.html) (invoke mvn play:war-exploded, sample usage in [GAE test project](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/gae))

- [war-inplace](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-beta1/play-maven-plugin/war-inplace-mojo.html) (invoke mvn play:war-inplace, sample usage in [Siena test project](https://maven-play-plugin.googlecode.com/svn/trunk/test-projects/external-modules/siena))

improved mojos:

- [war](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-beta1/play-maven-plugin/war-mojo.html) - parametrization of the webapp folder location ("war" by default), webapp directory content inclusion/exclusion filters ("warWebappIncludes" and "warWebappExcludes" parameters), dependency inclusion/exclusion filters ("warDependencyIncludes" and "warDependencyExcludes" parameters), possibility to attach the resulting war file to the project ("warAttach" parameter)

- [dist](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-beta1/play-maven-plugin/dist-mojo.html) - possibility to attach the resulting war file to the project ("distAttach" parameter)

BACKWARD INCOMPATIBLE CHANGES:

- "uberzip" mojo renamed to "[dist](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-beta1/play-maven-plugin/dist-mojo.html)" because it has similar functionality to Play! 2.0 "dist" command

- [war](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-beta1/play-maven-plugin/war-mojo.html) mojo's "warIncludes" and "warExcludes" configuration parameters renamed to "warApplicationIncludes" and "warApplicationExcludes"

<font color='blue'><a href='http://code.google.com/p/maven-play-plugin/issues/list?can=1&q=Milestone%3D1.0.0-beta1'>All issues for 1.0.0-beta1</a>.</font>


**1.0.0-alpha7 (2012.03.21)**

Maven3 + Surefire problems fixed

Play! server starting mojos ("run", "test", "start") improved (details here: http://code.google.com/p/maven-play-plugin/issues/detail?id=24#c2)

"precompileTests" parameter for "precompile" mojo added

Many Play! modules mavenized (complete list: http://code.google.com/p/maven-play-plugin/wiki/MavenizedModules), Scala among them (requires some code hacks implemented in plugin's 1.0.0-alpha7 version)

<font color='blue'><a href='http://code.google.com/p/maven-play-plugin/issues/list?can=1&q=Milestone%3D1.0.0-alpha7'>All issues for 1.0.0-alpha7</a>.</font>


**1.0.0-alpha6 (2012.01.23)**

new mojos:

- [run](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha6/play-maven-plugin/run-mojo.html) (invoke mvn play:run)

- [test](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha6/play-maven-plugin/test-mojo.html) (invoke mvn play:test)

- [start](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha6/play-maven-plugin/start-mojo.html) (invoke mvn play:start)

- [stop](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha6/play-maven-plugin/stop-mojo.html) (invoke mvn play:stop)

[start-server](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha6/play-maven-plugin/start-server-mojo.html), [stop-server](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha6/play-maven-plugin/stop-server-mojo.html) and [precompile](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha6/play-maven-plugin/precompile-mojo.html) mojos used for Selenium tests rewritten from Groovy to plain Java (for better performance, debugging, and maintainability)

common Play! dependencies added to [default Play! module parent pom](https://maven-play-plugin.googlecode.com/svn/tags/play-default-parents-1.0.0-alpha6/play-module-default-parent/), they can be removed from every Play! application pom.xml file (see in [test projects](http://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha6/))

"play.version" property set to "1.2.4 in [default Play! module parent pom](https://maven-play-plugin.googlecode.com/svn/tags/play-default-parents-1.0.0-alpha6/play-module-default-parent/), overwrite it to change inherited dependencies versions

better dependency handling in packaging mojos (zip, war, uberzip) for example packaging only active modules from "application.conf" file, and excluding "play-selenium-junit4" test dependency and its transitives

upgraded Play! Surefire provider's compatible Surefire version from 2.10 to 2.11 (Surefire provider API is not stable, so every upgrade requires changes in provider code)

upgraded Selenium Maven Plugin version in [default Play! module parent pom](https://maven-play-plugin.googlecode.com/svn/tags/play-default-parents-1.0.0-alpha6/play-module-default-parent/) from 2.0 to 2.1

changed "user-extensions.js" file location for Selenium Maven Plugin from "${project.build.directory}/selenium/user-extensions.js" to "${project.build.directory}/play/home/modules/testrunner/public/test-runner/selenium/scripts/user-extensions.js"; this is important only for projects not using "play-app-default-parent" as their parent artifact

<font color='blue'><a href='http://code.google.com/p/maven-play-plugin/issues/list?can=1&q=Milestone%3D1.0.0-alpha6'>All issues for 1.0.0-alpha6</a>.</font>


**1.0.0-alpha5 (2011.12.06)**

improved working with Play! Framework and modules loaded from dependencies,

removed the possibility to work with preinstalled Play! Framework (not coming from dependency) - ${play.home} variable is useless now,

changed wrong assumption that play-testrunner jar dependency is required only for Selenium tests; executing unit and functional tests without it leads to strange classloader problems, so it's needed for these tests too; warning in Play! Surefire provider when test class'es classloader is different from Play! Framework's one.

upgraded Play! Surefire provider's compatible Surefire version from 2.9 to 2.10 (Surefire provider API is not stable, so every upgrade requires changes in provider code)

"dependenciesOverride" configuration parameter name changed to [dependenciesOverwrite](https://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha5/play-maven-plugin/dependencies-mojo.html)

added [dependenciesSkipJars](https://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha5/play-maven-plugin/dependencies-mojo.html) configuration parameter to ignore jar dependencies when executing "play:dependencies"; they have to be copied to "lib" directory for Play! Framework to work with the project, but are not required if only Maven is used (Maven uses it's dependency mechanism exclusively for jar dependencies)

added [dependenciesSkip](https://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha5/play-maven-plugin/dependencies-mojo.html), [uberzipSkip](https://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha5/play-maven-plugin/uberzip-mojo.html) and [warSkip](https://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha5/play-maven-plugin/war-mojo.html) configuration properties to skip corresponding mojo executions

application's "conf" directory added to project resources (Java classpath)

multiple Selenium tests in one test file supported,

improved [default Play! application parent pom](https://maven-play-plugin.googlecode.com/svn/tags/play-default-parents-1.0.0-alpha5/play-app-default-parent/)

improved [default Play! module parent pom](https://maven-play-plugin.googlecode.com/svn/tags/play-default-parents-1.0.0-alpha5/play-module-default-parent/)

polished [test projects](https://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha5/)

mavenized modules (list of [all mavenized Play! modules](MavenizedModules.md)):
[Ebean 1.0.5](https://maven-play-plugin.googlecode.com/svn/mavenrepo/releases/com/google/code/maven-play-plugin/org/playframework/modules/ebean/play-ebean/1.0.5/),
[Excel 1.2.3](https://maven-play-plugin.googlecode.com/svn/mavenrepo/releases/com/google/code/maven-play-plugin/org/playframework/modules/excel/play-excel/1.2.3/),
[FBGraph 0.3](https://maven-play-plugin.googlecode.com/svn/mavenrepo/releases/com/google/code/maven-play-plugin/org/playframework/modules/fbgraph/play-fbgraph/0.3/),
[Morphia 1.2.4b](https://maven-play-plugin.googlecode.com/svn/mavenrepo/releases/com/google/code/maven-play-plugin/org/playframework/modules/morphia/play-morphia/1.2.4b/),
[Spring 1.0.2](https://maven-play-plugin.googlecode.com/svn/mavenrepo/releases/com/google/code/maven-play-plugin/org/playframework/modules/spring/play-spring/1.0.2/)

added test projects for modules:
[Ebean](https://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha5/external-modules/ebean/)
[Excel](https://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha5/external-modules/excel/)
[Morphia](https://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha5/external-modules/morphia/)
[Sass](https://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha5/external-modules/sass/)
[Spring](https://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha5/external-modules/spring/)

**1.0.0-aplha4 (2011.10.31)**

["war"](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha4/play-maven-plugin/war-mojo.html) and ["uberzip"](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha4/play-maven-plugin/uberzip-mojo.html) mojos implemented

Additional parametrization for ["dependencies"](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha4/play-maven-plugin/dependencies-mojo.html) mojo

Maven generated plugin page is [here](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha4/index.html). Most interesting is plugin's goals [page](http://maven-play-plugin.googlecode.com/svn/mavensite/1.0.0-alpha4/play-maven-plugin/plugin-info.html).

**1.0.0-alpha3 (2011.10.19)**

This is not a Play! module, this is a Maven plugin. It defines new
"play" Maven lifecycle.
For those understanding Maven internals here is this [lifecycle](http://maven-play-plugin.googlecode.com/svn/tags/plugin-1.0.0-alpha3/play-maven-plugin/src/main/resources/META-INF/plexus/components.xml) definition.

Plugin's home page is [here](http://code.google.com/p/maven-play-plugin/).

Plugin sources are [here](http://maven-play-plugin.googlecode.com/svn/tags/plugin-1.0.0-alpha3/).

There is no documentation yet, but there are many working example
[projects](http://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha3/).

The main features of "play" Maven lifecycle are:

> compiling application and test classes using standard Maven [Compiler](http://maven.apache.org/plugins/maven-compiler-plugin/) Plugin, so Maven reporting plugins using class files ([FindBugs](http://mojo.codehaus.org/findbugs-maven-plugin/), http://maven.apache.org/plugins/maven-pmd-plugin/ PMD], etc.) work as expected

> automatically executing unit and functional tests using standard [Maven Surefire Plugin](http://maven.apache.org/plugins/maven-surefire-plugin/)

> automatically executing Selenium tests using Selenium client-server architecture with [Selenium Maven Plugin](http://mojo.codehaus.org/selenium-maven-plugin/)

> packaging application as a zip file ready for deployment

> working with Play! Framework versions from 1.1 to 1.2.3

> working with Play! external modules needs their mavenization, I began this work with Play! LESS Module, and you can see [test project](http://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha3/external-modules/less/0.3/less-sample/)

> working with Play! Framework distribution on your disk, or without it (all needed Play! resources coming from Maven dependencies)


There are three additional mojos (Maven plugin goals) not connected to
"play" lifecycle:

> "dependencies" analogous to "play dependencies" command,  but resolving application's
> jar and module dependencies from Maven dependencies instead of "dependencies.yml" file

> "precompile" - simple wrapper to "play precompile" command

> "war-support" - for preparation tasks before creating "war" file

"precompile" and "war-support" mojos are used in [test projects](http://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha3/packagings/war/using-assembly/)

In the near future I play to implement "war" and "uberzip" (Play!
Framework with application
and all required modules in one archive ready for deployment) packagings.
While this is a work in progress, I prepared workarounds using [Maven Assembly Plugin](http://maven.apache.org/plugins/maven-assembly-plugin/).

Test projects using assemblies for "war" and "uberzip" packagings are [here](http://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha3/packagings/war/using-assembly/) and [here](http://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha3/packagings/uberzip/using-assembly/).

Maven repository containing: mavenized Play! framework and embedded modules (crud, docviewer, grizzly, secure and testrunner), framework's dependencies not available in
other public repositories and this plugin's artifacts is available [here](http://maven-play-plugin.googlecode.com/svn/mavenrepo/releases/).

Look at this plugin, Play! with it and if you will like it, I will be happy to extend and improve it. All contributors are welcomed. All constructive critics are welcomed too. Start with [test projects](http://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha3/), it's the best way to understand this plugin.

The best way to start mavenizing your own application is to copy "pom.xml" file from one of [test projects](http://maven-play-plugin.googlecode.com/svn/tags/test-projects-1.0.0-alpha3/) and customize it.