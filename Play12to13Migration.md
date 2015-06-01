# Play! 1.2.x to 1.3.x migration

# Introduction #

This topis describes migration of existing project created for Play! Framework 1.2.x to Play! 1.3.x.

Instructions how to set up new project using Play! Framework 1.2.x are in [Usage](Usage.md) section.

From the Maven point of view the main differences between Play1 1.2.x and 1.3.x are:
  * **play-docviewer** module contains a jar file,
  * module dependencies defined in **conf/application.conf** file are not supported anymore.

# Details #

## 1. 'pom.xml' file changes ##

Required migration steps are slightly different for projects using **play-app-default-parent** and not using it.

### Projects not using **play-app-default-parent** ###

  * If you define Play! version as a property, change it's value to 1.3.0:
```
    <properties>
        <play.version>1.3.0</play.version>
    </properties>
```

  * If you have **play-docviewer** module-type dependency:
```
        <dependency>
            <groupId>com.google.code.maven-play-plugin.org.playframework.modules.docviewer</groupId>
            <artifactId>play-docviewer</artifactId>
            <version>${play.version}</version>
            <classifier>module-min</classifier> <!-- or <classifier>module</classifier> -->
            <type>zip</type>
            <scope>provided</scope>
        </dependency>
```
add jar-type one (**play-docviewer-${version}.jar** is new in Play! 1.3.x):
```
        <dependency>
            <groupId>com.google.code.maven-play-plugin.org.playframework.modules.docviewer</groupId>
            <artifactId>play-docviewer</artifactId>
            <version>${play.version}</version>
            <scope>provided</scope>
        </dependency>
```

### Projects using **play-app-default-parent** ###

  * if you use and want to stay with latest release version change **play.version** property value:
```
    <properties>
        <play.version>1.3.0</play.version>
    </properties>
```
and add **play-docviewer** jar-type dependency (**play-docviewer-${version}.jar** is new in Play! 1.3.x):
```
        <dependency>
            <groupId>com.google.code.maven-play-plugin.org.playframework.modules.docviewer</groupId>
            <artifactId>play-docviewer</artifactId>
            <version>${play.version}</version>
            <scope>provided</scope>
        </dependency>
```

  * alternatively (if snapshot is allowed) you can change the parent to **play13-app-default-parent**:
```
    <parent>
        <groupId>com.google.code.maven-play-plugin</groupId>
        <artifactId>play13-app-default-parent</artifactId>
        <version>1.0.0-beta8-SNAPSHOT</version>
    </parent>
```
This parent is new, starting from **1.0.0-beta8** version of the plugin.

## 2. module dependencies defined in 'conf/application.conf' file ##

In Play! 1.1.x modules had to be installed inside **PLAY\_HOME/modules** directory (**play install** command) and included in **conf/application.conf** file for example:
```
# Additional modules
# ~~~~~
# A module is another play! application. Add a line for each module you want
# to add to your application. Modules path are either absolutes or relative to
# the application root.
# They get loaded from top to bottom; Syntax: module.{name}={path}
#
# Keep the next line as is to help the play script to manage modules.
# ---- MODULES ----
module.crud=${play.path}/modules/crud
```

In **pom.xml** file such dependency has to be defined with **provided** scope and is extracted to **target/play/modules/${modulename}** directory.

This method was deprecated (but still working) in Play! 1.2.x.
In Play! 1.3.x it was removed.

All dependent modules have to be added to **conf/dependencies.yml** file and declared with default scope in **pom.xml** file (remove `<scope>provided</scope>`).

There is additional automatic, transparent to the users, dependency mechanism in Play! **docviewer** and **testrunner** modules are added to every project in runtime without the need to declare them in **conf/application.conf** or **conf/dependencies.yml** file.
They are declared in **pom.xml** file (in **play-app-default-parent** if you are using it) with **provided** scope. This scope should not be changed.

## 3. 'conf/dependencies.yml' file content ##

**conf/dependencies.yml** configuration file was introduced in Play! 1.2.x and was used exclusively by **play dependencies** (or **deps**) command. It is ignored in Maven environment.
In Play! 1.3.x things changed. Every Play! **module** dependency must be included in it. This configuration is processed in Play! initialization (application start) phase.
Jar-type dependencies on the other hand, are used only by **play dependencies** command, and are not required when working in Maven environment. They can be added to **conf/dependencies.yml** configuration file, but will be ignored by Maven plugin.

# Limitations and problems found #

## 1. **cobertura** module ##

**Cobertura** module is not compatible with Play! 1.3.x because of **ASM** dependency versions conflict (3.0 vs. 5.0.3).

## 2. **siena** module ##

I think I found a bug in Play! 1.3.0 when working with **siena** module, will investigate.
By default Siena disables Play! JPA plugin. This is not implemented properly and application fails to start.
The workaround is to leave it enabled (I don't know of any consequences of this change) by adding:
```
siena.jpa.disable=false
```
to **conf/application.conf** configuration file.

## 3. **scala** module ##

**Scala** module does not work, but who uses Play! 1.x with Scala these days?

**NOTE**:
I'm not native English speaker and I'm aware that this page is written in my 'dialect' of English. If you find something not clear or just not written properly, let me know, I will fix it.

Additionally, I don't know, how many users of this plugin are still actively developing their systems. If you read this, you probably still use Play! 1.x with Maven. Leave short comment/note below.

There is a chance, that nobody needs this plugin anymore. If there will be no feedback, I will know that it's development should be suspended.