In [the previous chapter](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README.md), we created our first app, when user sends "Hello,world!", the bot reply the same message.

# Receive and send Bitcoin
This chapter will show you that your bot can receive coin from user and then pay it back to the user immediately.
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
Execute **gradle run ** in the project directory.
```bash
response:Response{protocol=http/1.1, code=101, message=Switching Protocols, url=https://blaze.mixin.one/}
[onMessage !!!]
json: {"id":"712fbde1-1b72-4e8f-b731-b7dc6f689c3e","action":"LIST_PENDING_MESSAGES"}
LIST_PENDING_MESSAGES
<=========----> 75% EXECUTING [13s]
> :run
```
Developer can send Bitcoin to their bots in message panel. The bot receive the Bitcoin and then send back immediately.
![transfer and tokens](https://github.com/wenewzhang/mixin_network-nodejs-bot2/blob/master/transfer-any-tokens.jpg)

User can pay 0.001 Bitcoin to bot by click the button and the 0.001 Bitcoin will be refunded in 1 seconds,In fact, user can pay any coins either.
![pay-link](https://github.com/wenewzhang/mixin_network-nodejs-bot2/blob/master/Pay_and_refund_quickly.jpg)

## Source code explanation
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
When bot send Bitcoin to user successfully, the jsObj.get("amount") is negative.
When user send Bitcoin to bot, the jsObj.get("amount") is positive.
The last, call MixinBot.transfer to refund the coins back to user.