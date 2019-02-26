In [the previous chapter](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README.md), your first bot just worked. The bot echo message from user.

# Receive and send Bitcoin in Mixin Messenger
Add new "Case-condition" in switch block of the App.java
> App.java
```java
switch (category) {
...
case SYSTEM_ACCOUNT_SNAPSHOT:
    userId =
      obj.get("data").getAsJsonObject().get("user_id").getAsString();
    byte[] JsData = Base64.decodeBase64(obj.get("data").getAsJsonObject().get("data").getAsString());
    String JsStr = new String(JsData);
    System.out.println("SYSTEM_ACCOUNT_SNAPSHOT json: " + JsStr);
    JsonObject jsObj = new JsonParser().parse(JsStr).getAsJsonObject();
    System.out.println(jsObj.get("amount").getAsString());
    System.out.println(jsObj.get("asset_id").getAsString());
    if (jsObj.get("amount").getAsFloat() > 0) {
      String aesKey = new String (Base64.encodeBase64(Config.PAY_KEY));
      System.out.println(aesKey);
      System.out.println(Config.PAY_KEY.length);
      String encryptPin = MixinUtil.encryptPayKey(Config.PIN,Config.PAY_KEY);
      MixinBot.transfer(
          jsObj.get("asset_id").getAsString(),
          jsObj.get("opponent_id").getAsString(),
          jsObj.get("amount").getAsString(),
          encryptPin,
          Config.RSA_PRIVATE_KEY,
          Config.CLIENT_ID,
          Config.SESSION_ID
      );
    }
    break;
...
```
### Hello Bitcoin!
Execute **gradle run** in the project directory.
```bash
response:Response{protocol=http/1.1, code=101, message=Switching Protocols, url=https://blaze.mixin.one/}
[onMessage !!!]
json: {"id":"712fbde1-1b72-4e8f-b731-b7dc6f689c3e","action":"LIST_PENDING_MESSAGES"}
LIST_PENDING_MESSAGES
<=========----> 75% EXECUTING [13s]
> :run
```
Developer can send Bitcoin to their bots in chatting page. The bot will send Bitcoin back immediately after receive it.
![transfer and tokens](https://github.com/wenewzhang/mixin_network-nodejs-bot2/blob/master/transfer-any-tokens.jpg)

User can pay 0.001 Bitcoin to bot by click the button and the 0.001 Bitcoin will be refunded in 1 second. In fact, user can pay any coin.
![pay-link](https://github.com/wenewzhang/mixin_network-nodejs-bot2/blob/master/Pay_and_refund_quickly.jpg)

## Source code summary
```java
if (jsObj.get("amount").getAsFloat() > 0) {
  String encryptPin = MixinUtil.encryptPayKey(Config.PIN,Config.PAY_KEY);
  MixinBot.transfer(
      jsObj.get("asset_id").getAsString(),
      jsObj.get("opponent_id").getAsString(),
      jsObj.get("amount").getAsString(),
      encryptPin,
      Config.RSA_PRIVATE_KEY,
      Config.CLIENT_ID,
      Config.SESSION_ID
  );
}
```
* jsObj.get("amount") is negative if bot sends Bitcoin to user successfully.
* jsObj.get("amount") is positive if bot receives Bitcoin from user.
Call MixinBot.transfer to refund the coins back to user.

## Advanced usage
Mixin Messenger provides many APIs so you can enable user to pay coins to bot. 

### Send AppCard
Send a link which contains icon, title and description, users click the icon to pay.
```java
if (msgP.toLowerCase().equals("pay")) {
  MixinBot.sendAppCard(webSocket,
                      Config.CLIENT_ID,
                      "6cfe566e-4aad-470b-8c9a-2fd35b49c68d",
                      "0.0001",
                      conversationId);
}
```
### Send Button Group
Create a group of buttons for user, users click one button to pay.
```java
else if (msgP.toLowerCase().equals("appsgroup")) {
  String payLinkEOS = "https://mixin.one/pay?recipient=" +
                 Config.CLIENT_ID  + "&asset=" +
                 "6cfe566e-4aad-470b-8c9a-2fd35b49c68d"   +
                 "&amount=" + "0.1" +
                 "&trace="  + UUID.randomUUID().toString() +
                 "&memo=";
  JsonObject msgJsEOS = new JsonObject();
  msgJsEOS.addProperty("label", "Pay 0.1 EOS");
  msgJsEOS.addProperty("color", "#0080FF");
  msgJsEOS.addProperty("action",payLinkEOS);


  String payLinkBTC = "https://mixin.one/pay?recipient=" +
                 Config.CLIENT_ID  + "&asset=" +
                 "c6d0c728-2624-429b-8e0d-d9d19b6592fa"   +
                 "&amount=" + "0.001" +
                 "&trace="  + UUID.randomUUID().toString() +
                 "&memo=";
  JsonObject msgJsBTC = new JsonObject();
  msgJsBTC.addProperty("label", "Pay 0.001 BTC");
  msgJsBTC.addProperty("color", "#FF8000");
  msgJsBTC.addProperty("action",payLinkBTC);

  JsonArray msgArr = new JsonArray();
  msgArr.add(msgJsEOS);//button for EOS
  msgArr.add(msgJsBTC);//button for Bitcoin

  JsonObject msgParams = new JsonObject();
  msgParams.addProperty("conversation_id",conversationId);
  msgParams.addProperty("category","APP_BUTTON_GROUP");
  msgParams.addProperty("status","SENT");
  msgParams.addProperty("message_id",UUID.randomUUID().toString());
  msgParams.addProperty("data",new String(Base64.encodeBase64(msgArr.toString().getBytes())));

  MixinBot.send(webSocket, MIXIN_Action.CREATE_MESSAGE, msgParams.toString());
}
```
![pay-link](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/appcard.jpeg)
