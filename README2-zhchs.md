
在 [上一篇教程中](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README-zhchs.md), 我们创建了自动回复消息的机器人,当用户发送消息"Hello,World!"时，机器人会自动回复同一条消息!

# 第二课: 机器人接受比特币并立即退还用户
按本篇教程后学习后完成后，你的机器人将会接受用户发送过来的加密货币，然后立即转回用户。
在App.java中，找到switch条件，加入新的"CASE",内容如下：
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
在项目目录下，执行 **gradle run **
```bash
response:Response{protocol=http/1.1, code=101, message=Switching Protocols, url=https://blaze.mixin.one/}
[onMessage !!!]
json: {"id":"712fbde1-1b72-4e8f-b731-b7dc6f689c3e","action":"LIST_PENDING_MESSAGES"}
LIST_PENDING_MESSAGES
<=========----> 75% EXECUTING [13s]
> :run
```
开发者可以通过消息面板，给机器人转比特币，当机器人收到比特币后，马上返还给用户！
![transfer and tokens](https://github.com/wenewzhang/mixin_network-nodejs-bot2/blob/master/transfer-any-tokens.jpg)

事实上，用户可以发送任意的币种给机器人，它都能马上返还！
![pay-link](https://github.com/wenewzhang/mixin_network-nodejs-bot2/blob/master/Pay_and_refund_quickly.jpg)

## 源代码解释
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
如果机器人收到币，jsObj.get("amount") 大于零；如果机器人支付币给用户，接收到的消息是一样的，唯一不同的是jsObj.get("amount")是一个负数.
最后一步，调用SDK的MixinBot.transfer将币返还用户！
