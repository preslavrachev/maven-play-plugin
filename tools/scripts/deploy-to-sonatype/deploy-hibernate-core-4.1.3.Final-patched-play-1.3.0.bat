call ..\set-play-home-1.3.0-RC1.bat

set GROUP_ID_PREFIX=com.google.code.maven-play-plugin.
set GROUP_ID=%GROUP_ID_PREFIX%org.hibernate
set ARTIFACT_ID=hibernate-core
set VERSION=4.1.3.Final-patched-play-1.3.0

set REPO_ID=sonatype-nexus-staging
set REPO_URL=https://oss.sonatype.org/service/local/staging/deploy/maven2

set SRC_DIR=../sources/hibernate-core/4.1.3.Final/patched-play-1.3.0/hibernate-core

call mvn gpg:sign-and-deploy-file -Durl=%REPO_URL% -DrepositoryId=%REPO_ID% -DpomFile=../poms/play/%ARTIFACT_ID%-%VERSION%.pom -Dfile=%PLAY_HOME%/framework/lib/hibernate-core-4.1.3.Final.jar -Dclassifiers=sources,javadoc -Dtypes=jar,jar -Dfiles=%SRC_DIR%/target/libs/%ARTIFACT_ID%-4.1.3.Final-sources.jar,%SRC_DIR%/target/libs/%ARTIFACT_ID%-4.1.3.Final-javadoc.jar
