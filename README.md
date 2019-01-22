# mixin_labs-java-bot

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
