# 通过 Java 买卖Bitcoin
![cover](https://github.com/wenewzhang/mixin_labs-java-bot/raw/master/bitcoin_wallet-java/mixin-bitcoin-java.jpg)

## 方案二: 挂单Ocean.One交易所
[Ocean.one](https://github.com/mixinNetwork/ocean.one)是基于Mixin Network的去中心化交易所，它中心化交易所一样友好。
你可以在OceanOne上交易任何资产，只需要将你的币转给OceanOne, 将交易信息写在交易的memo里，OceanOne会在市场里列出你的交易需求，
交易成功后，会将目标币转入到你的MixinNetwork帐上，它有三大特点与优势：
- 不需要在OceanOne注册
- 不需要存币到交易所
- 

### 预备知识:
你先需要创建一个机器人, 方法在 [教程一](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README-zhchs.md).

#### 安装依赖包
正如教程一里我们介绍过的， 我们需要依赖 [**mixin-java-sdk**](https://github.com/wenewzhang/mixin-java-sdk/releases), 你应该先安装过它了，zhang/mixin_labs-java-bot/blob/master/README.md).

#### Install required packages
[第四课](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README4-zhchs.md), 在上一课中已经安装好了.

#### 充币到 Mixin Network, 并读出它的余额.
通过ExinCore API, 可以进行BTC, USDT, EOS, ETH 等等交易， 此处演示用 USDT购买BTC 或者 用BTC购买USDT。交易前，先检查一下钱包地址。
完整的步骤如下:
- 检查比特币或USDT的余额，钱包地址。并记下钱包地址。
- 从第三方交易所或者你的冷钱包中，将币充到上述钱包地址。
- 再检查一下币的余额，看到帐与否。(比特币的到帐时间是5个区块的高度，约100分钟)。

比特币与USDT的充值地址是一样的。

```java
  private static final String BTC_ASSET_ID     = "c6d0c728-2624-429b-8e0d-d9d19b6592fa";
  private static final String EOS_ASSET_ID     = "6cfe566e-4aad-470b-8c9a-2fd35b49c68d";
  private static final String USDT_ASSET_ID    = "815b0b1a-2764-3736-8faa-42d694fa620a";

  MixinAPI mixinApiUser = generateAPI_FromCSV();
  JsonObject asset = mixinApiUser.getAsset(BTC_ASSET_ID);
  System.out.println(asset);
  System.out.println("------------------------BTC------Information---------------------------");
  System.out.println("The BTC wallet address is " + asset.get("public_key").getAsString());
  System.out.println("The BTC wallet balance is " + asset.get("balance").getAsString());
  System.out.println("-----------------------------------------------------------------------");
```

#### 取得Ocean.one的市场价格信息
如何来查询Ocean.one市场的价格信息呢？你要先了解你交易的基础币是什么，如果你想买比特币，卖出USDT,那么基础货币就是USDT;如果你想买USDT,卖出比特币，那么基础货币就是比特币.

```java
if ( subinput.equals("1") ) {
  FetchOceanMarketInfos(BTC_ASSET_ID,USDT_ASSET_ID);
}
private static void FetchOceanMarketInfos(String targetAssetID, String baseAssetID) {
  OkHttpClient client = new OkHttpClient();
  String baseUrl = "https://events.ocean.one/markets/%s-%s/book ";
  String fullUrl = String.format(baseUrl,targetAssetID,baseAssetID);
  // String fullUrl = baseUrl + url;
  System.out.println(fullUrl);
  Request request = new Request.Builder()
                             .url(fullUrl)
                             .build();
  try {
     Response response = client.newCall(request).execute();
     if (!response.isSuccessful()) {
       throw new IOException("Unexpected code " + response);
     }
     String res = response.body().string();
     // System.out.println(res);
     JsonParser parser = new JsonParser();
     JsonElement jsonTree = parser.parse(res);
     JsonObject orders;
     orders =  jsonTree.getAsJsonObject();
     JsonArray asksOrders = orders.get("data").getAsJsonObject().
                                   get("data").getAsJsonObject().
                                   get("asks").getAsJsonArray();
     JsonArray bidsOrders = orders.get("data").getAsJsonObject().
                                   get("data").getAsJsonObject().
                                   get("bids").getAsJsonArray();
     // System.out.println(orders.get("data").getAsJsonObject().get("data").getAsJsonObject().get("bids").getAsJsonArray());
     System.out.println("--Side--Price--Amount--Funds---");
     asksOrders.forEach((element) ->  {
        JsonObject jsonObj = element.getAsJsonObject();
        System.out.println(jsonObj.get("side").getAsString() + " " +
                           jsonObj.get("price").getAsString() + " " +
                           jsonObj.get("amount").getAsString() + " " +
                           jsonObj.get("funds").getAsString() );
     });
     bidsOrders.forEach((element) ->  {
        JsonObject jsonObj = element.getAsJsonObject();
        System.out.println(jsonObj.get("side").getAsString() + " " +
                           jsonObj.get("price").getAsString() + " " +
                           jsonObj.get("amount").getAsString() + " " +
                           jsonObj.get("funds").getAsString() );
     });
     System.out.println("----endo--of--btc/usdt----");
     return;
   } catch(Exception e) { e.printStackTrace(); }
   return;
}
```

#### 交易前，创建一个Memo!
在第二章里,[基于Mixin Network的 Java 比特币开发教程: 机器人接受比特币并立即退还用户](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README2-zhchs.md), 我们学习过转帐，这儿我们介绍如何告诉Ocean.one，我们给它转帐的目的是什么，信息全部放在memo里.
- **Side** 方向,"B" 或者 "A", "B"是购买, "A"是出售.
- **AssetUUID** 目标虚拟资产的UUID.
- **Price** 价格，如果操作方向是"B", 价格就是AssetUUID的价格; 如果操作方向是"B", 价格就是转给Ocean.one币的价格.

```java
public static String GenerateOrderMemo(String Side, String AssetUUID, String Price) {
  try {
    MessageBufferPacker m = MessagePack.newDefaultBufferPacker();
    UUID myAssetUUID  =  UUID.fromString(AssetUUID);
    Value map = ValueFactory.newMap(ValueFactory.newString("S"), ValueFactory.newString(Side),
                                    ValueFactory.newString("A"), ValueFactory.newBinary(asBytes(myAssetUUID)),
                                    ValueFactory.newString("P"), ValueFactory.newString(Price),
                                    ValueFactory.newString("T"), ValueFactory.newString("L") );
    m.packValue(map);
    return Base64.getEncoder().encodeToString(m.toByteArray());
  } catch (Exception e) { e.printStackTrace(); }
    return "";
}
```

#### 出售BTC的例子
转打算出售的BTC给Ocean.one(OCEANONE_BOT),将你打算换回来的目标虚拟资产的UUID放入memo.

```java
private static final String OCEANONE_BOT     = "aaff5bef-42fb-4c9f-90e0-29f69176b7d4";
private static final String BTC_ASSET_ID     = "c6d0c728-2624-429b-8e0d-d9d19b6592fa";
private static final String EOS_ASSET_ID     = "6cfe566e-4aad-470b-8c9a-2fd35b49c68d";
private static final String USDT_ASSET_ID    = "815b0b1a-2764-3736-8faa-42d694fa620a";
private static final String BTC_WALLET_ADDR  = "14T129GTbXXPGXXvZzVaNLRFPeHXD1C25C";
private static final String MASTER_UUID      = "0b4f49dc-8fb4-4539-9a89-fb3afc613747";

if ( subinput.equals("s1") ) {
  System.out.print("Please input the BTC price base USDT: ");
  String pinput = System.console().readLine();
  System.out.println(pinput);

  System.out.print("Please input the BTC amount: ");
  String aminput = System.console().readLine();
  System.out.println(aminput);
  float amountf = Float.valueOf(aminput.trim()).floatValue();

  String buyMemo = GenerateOrderMemo("A",USDT_ASSET_ID,pinput);
  MixinAPI mixinApiUser = generateAPI_FromCSV();
  // UUID usdtUUID         =  UUID.fromString(USDT_ASSET_ID);
  // String memoTarget     = encodeUUID(usdtUUID);
  System.out.println("------------------Ocean.one-USDT-BTC-EXCHANGE----------------------------");
  System.out.println(buyMemo);
  JsonObject asset = mixinApiUser.getAsset(BTC_ASSET_ID);
  System.out.println(asset);
  System.out.println(asset.get("balance").getAsFloat());
  if ( asset.get("balance").getAsFloat()  > 0 && asset.get("balance").getAsFloat() >= amountf ) {
      JsonObject transInfo = mixinApiUser.transfer(BTC_ASSET_ID, OCEANONE_BOT,
                                                 aminput,
                                                 buyMemo);
      System.out.println("------------------------BTC Transfer To EXCHANGE Information----------------------");
      System.out.println(transInfo);
      System.out.println("-----------------------------------------------------------------------");
   } else System.out.println("----------------Not enough BTC--------------------------------------------");
}
```
如果你是打算买BTC,操作如下:
```java
if ( subinput.equals("b1") ) {
  System.out.print("Please input the BTC price base USDT: ");
  String pinput = System.console().readLine();
  System.out.println(pinput);

  System.out.print("Please input the USDT amount: ");
  String aminput = System.console().readLine();
  System.out.println(aminput);
  float amountf = Float.valueOf(aminput.trim()).floatValue();
  String buyMemo = GenerateOrderMemo("B",BTC_ASSET_ID,pinput);
  MixinAPI mixinApiUser = generateAPI_FromCSV();
  // UUID usdtUUID         =  UUID.fromString(USDT_ASSET_ID);
  // String memoTarget     = encodeUUID(usdtUUID);
  System.out.println("------------------Ocean.one-USDT-BTC-EXCHANGE----------------------------");
  System.out.println(buyMemo);
  JsonObject asset = mixinApiUser.getAsset(USDT_ASSET_ID);
  System.out.println(asset);
  System.out.println(asset.get("balance").getAsFloat());
  if ( asset.get("balance").getAsFloat()  >= 1 && asset.get("balance").getAsFloat() >= amountf ) {
      JsonObject transInfo = mixinApiUser.transfer(USDT_ASSET_ID, OCEANONE_BOT,
                                                 aminput,
                                                 buyMemo);
      System.out.println("------------------------BTC Transfer To EXCHANGE Information----------------------");
      System.out.println(transInfo);
      System.out.println("-----------------------------------------------------------------------");
   } else System.out.println("----------------Not enough USDT--------------------------------------------");
}
```
一个成功的挂单如下：
```bash
------------------Ocean.one--EXCHANGE----------------------------
hKFToUKhQcQQyUrIj0ZxOXa2CgkGTxgR6KFQojExoVShTA==
{"type":"asset","asset_id":"815b0b1a-2764-3736-8faa-42d694fa620a","chain_id":"c6d0c728-2624-429b-8e0d-d9d19b6592fa","symbol":"USDT","name":"Tether USD","icon_url":"https://images.mixin.one/ndNBEpObYs7450U08oAOMnSEPzN66SL8Mh-f2pPWBDeWaKbXTPUIdrZph7yj8Z93Rl8uZ16m7Qjz-E-9JFKSsJ-F=s128","balance":"2","public_key":"15RNBb5GzoXWRoEMT5MJL3pqzSjdPoXf6J","account_name":"","account_tag":"","price_btc":"0.00018747","price_usd":"0.98731224","change_btc":"0.021985715750932774","change_usd":"-0.01843154053058167","asset_key":"815b0b1a-2764-3736-8faa-42d694fa620a","confirmations":6,"capitalization":0}
2.0
--------------815b0b1a-2764-3736-8faa-42d694fa620a Transfer To EXCHANGE Information---------
{"type":"transfer","snapshot_id":"f526fd18-c3d2-4a6d-a0f0-3a720e93c48e","opponent_id":"aaff5bef-42fb-4c9f-90e0-29f69176b7d4","asset_id":"815b0b1a-2764-3736-8faa-42d694fa620a","amount":"-2","trace_id":"6cf37ca6-4efa-4e78-bea1-53c5c5d54d2b","memo":"hKFToUKhQcQQyUrIj0ZxOXa2CgkGTxgR6KFQojExoVShTA==","created_at":"2019-04-26T01:56:00.803572612Z","counter_user_id":"0b4f49dc-8fb4-4539-9a89-fb3afc613747"}
---Order is 6cf37ca6-4efa-4e78-bea1-53c5c5d54d2b: ------
```
#### 取消挂单
Ocean.one将trace_id当做订单，比如上面的例子， **6cf37ca6-4efa-4e78-bea1-53c5c5d54d2b** 就是订单号，我们用他来取消订单。
```java
public static String GenerateOrderCancelMemo(String myUuid) {
  try {
    MessageBufferPacker m = MessagePack.newDefaultBufferPacker();
    UUID AssetUUID  =  UUID.fromString(myUuid);
    Value map = ValueFactory.newMap(
                                    ValueFactory.newString("O"), ValueFactory.newBinary(asBytes(AssetUUID)) );
    m.packValue(map);
    return Base64.getEncoder().encodeToString(m.toByteArray());
  } catch (Exception e) { e.printStackTrace(); }
    return "";
}
```
#### 通过读取资产余额，来确认到帐情况
Check the wallet's balance.
```java
MixinAPI mixinApiUser = generateAPI_FromCSV();
JsonObject asset = mixinApiUser.getAsset(BTC_ASSET_ID);
```

## 源代码执行
编译执行，即可开始交易了.

- **gradle build**  编译项目.
- **java -cp** 运行项目.
```bash
java -cp .:build/libs/bitcoin_wallet-java.jar:libs/* bitcoin_wallet.java.App
```

注意：不能使用gradle run来运行，因为我们使用的**System.console().readLine()**不被Gradle支持，只能使用**java -cp**
来运行！

本代码执行时的命令列表:

- 1: Create Bitcoin Wallet and update PIN
- 2: Read Bitcoin balance & address
- 3: Read USDT balance & address
- 4: Read EOS balance & address
- tbb:Transfer BTC from Bot to Wallet
- tbm:Transfer BTC from Wallet to Master
- teb:Transfer EOS from Bot to Wallet
- tem:Transfer EOS from Wallet to Master
- tub:Transfer USDT from Bot to Wallet
- tum:Transfer USDT from Wallet to Master
- 5: pay 0.0001 BTC buy USDT
- 6: pay $1 USDT buy BTC
- 7: Read Snapshots
- 8: Fetch market price(USDT)
- 9: Fetch market price(BTC)
- v: Verify Wallet Pin
- wb: Withdraw BTC
- we: WitchDraw EOS
- a: Read All Assets Infos
- o: Ocean.One Exchange
- q: Exit

Make your choose(eg: q for Exit!):

- 1:  Orders-Book of BTC/USDT
- b1: Buy BTC pay USDT
- s1: Sell BTC get USDT
- 2:  Orders-Book of EOS/USDT
- b2: Buy EOS pay USDT
- s2: Sell EOS get USDT
- 3:  Orders-Book of XIN/USDT
- b3: Buy XIN pay USDT
- s3: Sell XIN get USDT
- 4:  Orders-Book of XIN/BTC
- b4: Buy XIN pay BTC
- s4: Sell XIN get BTC
- 5:  Orders-Book of EOS/BTC
- b4: Buy EOS pay BTC
- s4: Sell EOS get BTC
- 6:  Orders-Book of SC/BTC
- b6: Buy SC pay BTC
- s6: Sell SC get BTC
- 7:  Orders-Book of EOS/XIN
- b7: Buy EOS pay XIN
- s7: Sell EOS get XIN
- 8:  Orders-Book of ETH/XIN
- b8: Buy ETH pay XIN
- s8: Sell EOS get XIN
- 9:  Orders-Book of SC/XIN
- b9: Buy SC pay XIN
- s9: Sell SC get XIN
- c: Cancel the order
- q: Exit

[完整代码](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/bitcoin_wallet-java/src/main/java/bitcoin_wallet/java/App.java)
