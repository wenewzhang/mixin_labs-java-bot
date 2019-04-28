# Java Bitcoin tutorial based on Mixin Network
![cover](https://github.com/wenewzhang/mixin_labs-java-bot/raw/master/bitcoin_wallet-java/mixin-bitcoin-java.jpg)

A Mixin messenger bot will be created in this tutorial. The bot is powered by Java and echo message and Bitcoin from user.

Full Mixin network resource [index](https://github.com/awesome-mixin-network/index_of_Mixin_Network_resource)

## What you will learn from this tutorial
1. [How to create bot in Mixin messenger and reply message to user](https://github.com/wenewzhang/mixin_labs-java-bot) | [Chinese](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README-zhchs.md)
2. [How to receive Bitcoin and send Bitcoin in Mixin Messenger](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README2.md) | [Chinese](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README2-zhchs.md)
3. [How to create a Bitcoin wallet based on Mixin Network API](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README3.md) | [Chinese](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README3-zhchs.md)
4. [How to trade bitcoin through Java: Pay to ExinCore API](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README4.md) | [Chinese](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README4-zhchs.md)
5. [How to trade bitcoin through Java: List your order on Ocean.One](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README5.md) | [Chinese](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README5-zhchs.md)
6. [How to trade ERC-20 compliant coins on OceanOne through Java](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README5.md)

## Install java on your OS
On macOS, download java jdk from [here](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html),double click jdk-11.0.2_osx-x64_bin.dmg, then click on JDK 11.0.2.pkg in popup window, follow the instruction to install java, java could be installed in /Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home/bin/ directory, add this path to environment variable $PATH
```bash
echo 'export PATH=/Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home/bin/:$PATH' >> ~/.bash_profile
source ~/.bash_profile
```
Run command **java --version** to check the installation
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
On Ubuntu 16.04, the openjdk java has been installed. run command  **java --version** to check installation
```bash
root@ubuntu:~# java --version
openjdk 10.0.2 2018-07-17
OpenJDK Runtime Environment (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4)
OpenJDK 64-Bit Server VM (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4, mixed mode)
```
## Install Gradle
This tutorial use Gradle to build whole project. You can download the latest gradle [here](https://gradle.org/install/#manually)

macOS
```bash
brew update
brew install gradle
```
Ubuntu: The gradle is too old, we need to update it.
```bash
cd ~/Downloads
wget https://services.gradle.org/distributions/gradle-5.1.1-bin.zip
unzip gradle-5.1.1-bin.zip
```
After unzip the gradle-5.1.1-bin.zip, Add the path to $PATH environment variable
```bash
echo 'export PATH=/root/gradle-5.1.1/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```
Run **gradle -v** to check gradle installation
```bash
root@ubuntu:~# gradle -v
------------------------------------------------------------
Gradle 5.1.1
------------------------------------------------------------
...
```
### Create your first app in Mixin Network developer dashboard
You need to create an app in dashboard. This [tutorial](https://mixin-network.gitbook.io/mixin-network/mixin-messenger-app/create-bot-account) can help you.

### Generate parameter of your app in dashboard
After app is created in dashboard, you need to [generate parameter](https://mixin-network.gitbook.io/mixin-network/mixin-messenger-app/create-bot-account#generate-secure-parameter-for-your-app)
and write down required content, these content will be written into config.java file.


## Hello, world in java
Go to your workspace, create the project mixin_labs-java-bot directory by running **gradle init**.
```bash
gradle init --dsl kotlin --type java-application --test-framework junit --project-name mixin_labs-java-bot
```

Goto src/main/java/mixin_labs/java/bot, create a file called Config.java. Copy the following content into it.
> Config.java
```java
package mixin_labs.java.bot;
import mixin.java.sdk.MixinUtil;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import mixin.java.sdk.PrivateKeyReader;
public class Config {

public static final String CLIENT_ID     = "b1ce2967-a534-417d-bf12-c86571e4eefa";
public static final String CLIENT_SECRET = "e6b14c6bbb20a43c603c468e225e6e4c666c940792cde43e41b34c3f1dd45713";
public static final String PIN           = "536071";
public static final String SESSION_ID    = "2f1c44a3-d4d2-4dd2-bdb6-8eda67694b91";
public static final String PIN_TOKEN     = "ajJJngHmWgIfH3S2mgH4bAsoPeoXV6hI1KoTZW9AvFUK1R8e28X1zVRCcrOMVeXkvBKQeEMgRdX1kRgH3ksITTBm2mgK5eUnfBHUuRC85oKoQGB9e2Bp4O4ZKGg/6bqLeD66pnBPcO2s7VtgLSAK0tHa2jMzmGlWuxsO6Wo5JHE=";

  private static RSAPrivateKey loadPrivateKey() {
    try {

      PrivateKey key =
        new PrivateKeyReader(Config.class.getClassLoader().getResourceAsStream("rsa_private_key.txt"))
          .getPrivateKey();
      System.out.println(key);
      return (RSAPrivateKey) key;
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  public static final RSAPrivateKey RSA_PRIVATE_KEY = loadPrivateKey();
  public static final byte[] PAY_KEY = MixinUtil.decrypt(RSA_PRIVATE_KEY, PIN_TOKEN, SESSION_ID);
}

```
Replace the value with content generated in dashboard.
> App.java
```java
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package mixin_labs.java.bot;
import mixin.java.sdk.MixinBot;
import mixin.java.sdk.MixinUtil;
import mixin.java.sdk.MIXIN_Category;
import mixin.java.sdk.MIXIN_Action;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
// import java.util.Base64;
import org.apache.commons.codec.binary.Base64;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class App {

    public static void main(String[] args) {
        MixinBot.connectToRemoteMixin(new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
          System.out.println("[onOpen !!!]");
          System.out.println("request header:" + response.request().headers());
          System.out.println("response header:" + response.headers());
          System.out.println("response:" + response);

          // Request unread messages
          MixinBot.sendListPendingMessages(webSocket);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
          System.out.println("[onMessage !!!]");
          System.out.println("text: " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
          try {
            System.out.println("[onMessage !!!]");
            String msgIn = MixinUtil.bytesToJsonStr(bytes);
            System.out.println("json: " + msgIn);
            JsonObject obj = new JsonParser().parse(msgIn).getAsJsonObject();
            MIXIN_Action action = MIXIN_Action.parseFrom(obj);
            System.out.println(action);
            MIXIN_Category category = MIXIN_Category.parseFrom(obj);
            System.out.println(category);
            if (action == MIXIN_Action.CREATE_MESSAGE && obj.get("data") != null &&
                category != null ) {
              String userId;
              String messageId = obj.get("data").getAsJsonObject().get("message_id").getAsString();
              MixinBot.sendMessageAck(webSocket, messageId);
              switch (category) {
                case PLAIN_TEXT:
                    String conversationId =
                      obj.get("data").getAsJsonObject().get("conversation_id").getAsString();
                    userId =
                      obj.get("data").getAsJsonObject().get("user_id").getAsString();
                    byte[] msgData = Base64.decodeBase64(obj.get("data").getAsJsonObject().get("data").getAsString());
                    MixinBot.sendText(webSocket,conversationId,userId,new String(msgData,"UTF-8"));
                    break;
                default:
                    System.out.println("Category: " + category);
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
          System.out.println("[onClosing !!!]");
          System.out.println("code: " + code);
          System.out.println("reason: " + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
          System.out.println("[onClosed !!!]");
          System.out.println("code: " + code);
          System.out.println("reason: " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
          System.out.println("[onFailure !!!]");
          System.out.println("throwable: " + t);
          System.out.println("response: " + response);
        }
      }, Config.RSA_PRIVATE_KEY, Config.CLIENT_ID, Config.SESSION_ID);
    }
}

```
Goto src/main/resources, create a file: rsa_private_key.txt, fill the private key content which you have already generated in dashboard.
> rsa_private_key.txt
```java
-----BEGIN RSA PRIVATE KEY-----
...
-----END RSA PRIVATE KEY-----
```

Go to the project directory, download the mixin-java-sdk from github,

```bash
mkdir libs
cd libs
wget https://github.com/wenewzhang/mixin-java-sdk/releases/download/v5.0/mixin-java-sdk.jar
```
Add dependencies package info into build.gradle.kts
```kotlin
dependencies {
    // This dependency is found on compile classpath of this component and consumers.
    implementation("com.google.guava:guava:26.0-jre")
    // dependent on mixin-java-sdk, copy it to libs directory
    compile(files("libs/mixin-java-sdk.jar"))
    implementation("commons-codec:commons-codec:1.11")
    implementation("com.auth0:java-jwt:3.5.0")
    implementation("com.squareup.okio:okio:2.2.1")
    implementation("com.squareup.okhttp3:okhttp:3.12.1")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.20")
    // Use JUnit test framework
    testImplementation("junit:junit:4.12")
}
```
Goto the directory src/test/java/mixin_labs/java/bot, comment the test code,
> AppTest.java
```java
        // assertNotNull("app should have a greeting", classUnderTest.getGreeting());
```
The last step, go back in mixin_labs-java-bot directory, build it and run,
```bash
gradle build
gradle run
```
Console will output:

```bash
response:Response{protocol=http/1.1, code=101, message=Switching Protocols, url=https://blaze.mixin.one/}
[onMessage !!!]
json: {"id":"4ee01b68-817e-4f29-bcb4-b40f7c163f61","action":"LIST_PENDING_MESSAGES"}
LIST_PENDING_MESSAGES
```
Add the bot(for example, this bot id is 7000101639) as your friend in [Mixin Messenger](https://mixin.one/messenger) and send your messages.
![mixin_messenger](https://github.com/wenewzhang/mixin_labs-php-bot/blob/master/helloworld.jpeg)

## Source code summary
#### Create websocket and connect to Mixin Messenger Server
```java
MixinBot.connectToRemoteMixin(new WebSocketListener() {
@Override
public void onOpen(WebSocket webSocket, Response response) {
  MixinBot.sendListPendingMessages(webSocket);
}
```
Send message "LISTPENDINGMESSAGES" to Mixin Messenger server and server will send unread messages to your bot.

#### Receive message callback
```java
        public void onMessage(WebSocket webSocket, ByteString bytes) {
          try {
            System.out.println("[onMessage !!!]");
            String msgIn = MixinUtil.bytesToJsonStr(bytes);

```
onMessage func will be called when server push message to bot

#### Send message response
```java
String messageId = obj.get("data").getAsJsonObject().get("message_id").getAsString();
MixinBot.sendMessageAck(webSocket, messageId);
```

Send a READ operation message to the server let it knows this message has been read. The bot will receive the duplicated message when the bot connected to server again if bot don't send response.

#### Echo chat contant
```java
              switch (category) {
                case PLAIN_TEXT:
                    String conversationId =
                      obj.get("data").getAsJsonObject().get("conversation_id").getAsString();
                    userId =
                      obj.get("data").getAsJsonObject().get("user_id").getAsString();
                    byte[] msgData = Base64.decodeBase64(obj.get("data").getAsJsonObject().get("data").getAsString());
                    MixinBot.sendText(webSocket,conversationId,userId,new String(msgData,"UTF-8"));
```

Not only texts, images and other type message will be pushed to your bot. You can find more [details](https://developers.mixin.one/api/beta-mixin-message/websocket-messages/) about Messenger message.
### End
Now your bot worked. You can hack it.

Full code is [here](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/src/main/java/mixin_labs/java/bot/App.java)
