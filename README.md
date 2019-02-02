# Mixin Messenger application development tutorial in java
## Install java on your OS
on macOS
Download java jdk from [here](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html),double click jdk-11.0.2_osx-x64_bin.dmg, and then in the pop window click JDK 11.0.2.pkg, follow the prompt finish the installation, the java could be installed in /Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home/bin/ directory, add this path to environment variable $PATH,
```bash
echo 'export PATH=/Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home/bin/:$PATH' >> ~/.bash_profile
source ~/.bash_profile
```
if installed successfully, execute **java --version** will get message like below.
```bash
wenewzha:mixin_labs-java-bot wenewzhang$ java --version
java 11.0.2 2019-01-15 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.2+9-LTS)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.2+9-LTS, mixed mode)
```
on Ubuntu
```bash
apt update
apt upgrade
apt install unzip
java --version
```
On ubuntu 16.04, the openjdk java has been installed default came with OS, execute **java --version** will get message like below.
```bash
root@ubuntu:~# java --version
openjdk 10.0.2 2018-07-17
OpenJDK Runtime Environment (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4)
OpenJDK 64-Bit Server VM (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4, mixed mode)
```
## Install Gradle on your OS
This tutorial use Gradle to build, you can download the latest gradle [here](https://gradle.org/install/#manually)
on macOS
```bash
brew update
brew install gradle
```
on Ubuntu
The gradle is too old for ubuntu, so we need download it ourself.
```bash
cd ~/Downloads
wget https://services.gradle.org/distributions/gradle-5.1.1-bin.zip
unzip gradle-5.1.1-bin.zip
```
After unzip the gradle-5.1.1-bin.zip, Let's add the path to $PATH environment variable
```bash
echo 'export PATH=/root/gradle-5.1.1/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```
When gradle installed, execute **gradle -v** could output message like below:
```bash
root@ubuntu:~# gradle -v
------------------------------------------------------------
Gradle 5.1.1
------------------------------------------------------------
...
```
## Create the project mixin_labs-java-bot
Go to your workspace, execute **gradle init** to generate the project base information.
```bash
gradle init --dsl kotlin --type java-application --test-framework junit --project-name mixin_labs-java-bot
```
Download the mixin-java-jdk from github,
```bash
wget https://github.com/wenewzhang/mixin-java-sdk/releases/download/v2/mixin-java-sdk.jar
```

steps of gradle helloworld  
```bash
brew install
gradle wrapper
./gradlew build
./gradlew run
```

gradle sockets settings:
```bash
echo 'org.gradle.jvmargs=-DsocksProxyHost=127.0.0.1 -DsocksProxyPort=10060' >> ~/.gradle/gradle.properties
```
```bash
mkdir libs
//cp mixin-java-sdk-unspecified.jar to libs
vi build.gradle.kts
//add line in dependencies block:
compile(files("libs/mixin-java-sdk-unspecified.jar"))
```
