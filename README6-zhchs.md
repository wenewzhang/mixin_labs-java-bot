# 在OceanOne上挂单买卖任意ERC20 token
![cover](https://github.com/wenewzhang/mixin_labs-java-bot/raw/master/bitcoin_wallet-java/mixin-bitcoin-java.jpg)

在[上一课](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README5.md)中，我们介绍了如何在OceanOne交易比特币。OceanOne支持交易任何Mixin Network上的token，包括所有的ERC20和EOS token，不需要任何手续和费用，直接挂单即可。下面介绍如何将将一个ERC20 token挂上OceanOne交易！

此处我们用一个叫做Benz的[ERC20 token](https://etherscan.io/token/0xc409b5696c5f9612e194a582e14c8cd41ecdbc67)为例。这个token已经被充值进Mixin Network，你可以在[区块链浏览器](https://mixin.one/snapshots/2b9c216c-ef60-398d-a42a-eba1b298581d )看到这个token在Mixin Network内部的总数和交易
### 预备知识:
先将Ben币存入你的钱包，然后使用**getAssets** API读取它的UUID.

### 取得该币的UUID
调用 **getAssets** API 会返回json数据, 如:

- **asset_id** 币的UUID.
- **public_key** 该币的当前钱包的地址.
- **symbol**  币的名称. 如: Benz.

```java
if ( input.equals("aw") ) {
  MixinAPI mixinApiUser = generateAPI_FromCSV();
  JsonArray assets = mixinApiUser.getAssets();
  System.out.println("------------------------All Assets Information---------------------------");
  System.out.println(assets);
  assets.forEach((element) ->  {
     JsonObject jsonObj = element.getAsJsonObject();
     System.out.println(jsonObj.get("asset_id").getAsString() + " " +
                        jsonObj.get("symbol").getAsString() + " " +
                        jsonObj.get("public_key").getAsString() + " " +
                        jsonObj.get("balance").getAsString() );
  });
  System.out.println("-----------------------------------------------------------------------");
}
```
调用 **getAssets** API的完整输出如下:
```bash
Make your choose(eg: q for Exit!): aw
aw
------------------------All Assets Information---------------------------
[{"type":"asset","asset_id":"2b9c216c-ef60-398d-a42a-eba1b298581d","chain_id":"43d61dcd-e413-450d-80b8-101d5e903357","symbol":"Benz","name":"Benz coin","icon_url":"https://images.mixin.one/yH_I5b0GiV2zDmvrXRyr3bK5xusjfy5q7FX3lw3mM2Ryx4Dfuj6Xcw8SHNRnDKm7ZVE3_LvpKlLdcLrlFQUBhds=s128",
"balance":"1000","public_key":"0x62F20013472a72b8Fe3f9a99f36e5802E6e93c15","account_name":"",
"account_tag":"","price_btc":"0","price_usd":"0","change_btc":"0","change_usd":"0",
"asset_key":"0xc409b5696c5f9612e194a582e14c8cd41ecdbc67","confirmations":100,
"capitalization":0},{"type":"asset","asset_id":"6cfe566e-4aad-470b-8c9a-2fd35b49c68d",
"chain_id":"6cfe566e-4aad-470b-8c9a-2fd35b49c68d","symbol":"EOS","name":"EOS",
"icon_url":"https://images.mixin.one/a5dtG-IAg2IO0Zm4HxqJoQjfz-5nf1HWZ0teCyOnReMd3pmB8oEdSAXWvFHt2AJkJj5YgfyceTACjGmXnI-VyRo=s128","balance":"0","public_key":"","account_name":"eoswithmixin","account_tag":"889ed66a1059bc3dab60e2ee44d0f993",
"price_btc":"0.0008953","price_usd":"4.73591781","change_btc":"-0.004161988128557199","change_usd":"0.00012182287348895748",
"asset_key":"eosio.token:EOS","confirmations":64,"capitalization":0},
{"type":"asset","asset_id":"965e5c6e-434c-3fa9-b780-c50f43cd955c","chain_id":"43d61dcd-e413-450d-80b8-101d5e903357","symbol":"CNB","name":"Chui Niu Bi",
"icon_url":"https://images.mixin.one/0sQY63dDMkWTURkJVjowWY6Le4ICjAFuu3ANVyZA4uI3UdkbuOT5fjJUT82ArNYmZvVcxDXyNjxoOv0TAYbQTNKS=s128",
"balance":"0.99984","public_key":"0x62F20013472a72b8Fe3f9a99f36e5802E6e93c15","account_name":"",
"account_tag":"","price_btc":"0","price_usd":"0","change_btc":"0","change_usd":"0",
"asset_key":"0xec2a0550a2e4da2a027b3fc06f70ba15a94a6dac","confirmations":100,
"capitalization":0},{"type":"asset","asset_id":"c6d0c728-2624-429b-8e0d-d9d19b6592fa",
"chain_id":"c6d0c728-2624-429b-8e0d-d9d19b6592fa","symbol":"BTC","name":"Bitcoin",
"icon_url":"https://images.mixin.one/HvYGJsV5TGeZ-X9Ek3FEQohQZ3fE9LBEBGcOcn4c4BNHovP4fW4YB97Dg5LcXoQ1hUjMEgjbl1DPlKg1TW7kK6XP=s128","balance":"0",
"public_key":"15RNBb5GzoXWRoEMT5MJL3pqzSjdPoXf6J","account_name":"","account_tag":"",
"price_btc":"1","price_usd":"5289.7409549","change_btc":"0","change_usd":"0.004160319338628907",
"asset_key":"c6d0c728-2624-429b-8e0d-d9d19b6592fa","confirmations":6,"capitalization":0}]
2b9c216c-ef60-398d-a42a-eba1b298581d Benz 0x62F20013472a72b8Fe3f9a99f36e5802E6e93c15 1000
6cfe566e-4aad-470b-8c9a-2fd35b49c68d EOS  0
965e5c6e-434c-3fa9-b780-c50f43cd955c CNB 0x62F20013472a72b8Fe3f9a99f36e5802E6e93c15 0.99984
c6d0c728-2624-429b-8e0d-d9d19b6592fa BTC 15RNBb5GzoXWRoEMT5MJL3pqzSjdPoXf6J 0
```
### 限价挂单
- **挂限价买单**  低于或者等于市场价的单.
- **挂限价卖单**  高于或者是等于市场价的单.

OceanOne支持三种基类价格: USDT, XIN, BTC, 即: Benz/USDT, Benz/XIN, Benz/BTC, 这儿示范Benz/USDT.

### 限价挂卖单.
新币挂单后,需要等一分钟左右，等OceanOne来初始化新币的相关数据.

```java
if ( subinput.equals("x2") ) {
  MakeTheSellOrder(ERC20_BENZ,USDT_ASSET_ID);
}
public static void MakeTheSellOrder(String AssetID, String BaseAssetID) {
  System.out.print(String.format("Please input the %s price of %s: ",AssetID,BaseAssetID));
  String pinput = System.console().readLine();
  System.out.println(pinput);

  System.out.print(String.format("Please input the %s amount: ",AssetID));
  String aminput = System.console().readLine();
  System.out.println(aminput);
  float amountf = Float.valueOf(aminput.trim()).floatValue();

  String OrderMemo = GenerateOrderMemo("A",BaseAssetID,pinput);
  MixinAPI mixinApiUser = generateAPI_FromCSV();
  // UUID usdtUUID         =  UUID.fromString(USDT_ASSET_ID);
  // String memoTarget     = encodeUUID(usdtUUID);
  System.out.println("------------------Ocean.one-EXCHANGE----------------------------");
  System.out.println(OrderMemo);
  JsonObject asset = mixinApiUser.getAsset(AssetID);
  System.out.println(asset);
  System.out.println(asset.get("balance").getAsFloat());
  if ( asset.get("balance").getAsFloat()  > 0 && asset.get("balance").getAsFloat() >= amountf ) {
      JsonObject transInfo = mixinApiUser.transfer(AssetID, OCEANONE_BOT,
                                                   aminput,
                                                   OrderMemo);
      System.out.println(String.format("--------------%s Transfer To EXCHANGE Information---------",AssetID));
      System.out.println(transInfo);
      System.out.println(String.format("---Order is %s: ------",transInfo.get("trace_id").getAsString()));
   } else System.out.println(String.format("----------------Not enough %s--------------------------",AssetID));
}
```

### 限价挂买单.
新币挂单后,需要等一分钟左右，等OceanOne来初始化新币的相关数据.

```java
if ( subinput.equals("x1") ) {
  MakeTheBuyOrder(ERC20_BENZ,USDT_ASSET_ID);
}
public static void MakeTheBuyOrder(String AssetID, String BaseAssetID) {
  System.out.print(String.format("Please input the %s price of %s: ",AssetID,BaseAssetID));
  String pinput = System.console().readLine();
  System.out.println(pinput);

  System.out.print(String.format("Please input the %s amount: ",BaseAssetID));
  String aminput = System.console().readLine();
  System.out.println(aminput);
  float amountf = Float.valueOf(aminput.trim()).floatValue();

  String OrderMemo = GenerateOrderMemo("B",AssetID,pinput);
  MixinAPI mixinApiUser = generateAPI_FromCSV();
  // UUID usdtUUID         =  UUID.fromString(USDT_ASSET_ID);
  // String memoTarget     = encodeUUID(usdtUUID);
  System.out.println("------------------Ocean.one--EXCHANGE----------------------------");
  System.out.println(OrderMemo);
  JsonObject asset = mixinApiUser.getAsset(BaseAssetID);
  System.out.println(asset);
  System.out.println(asset.get("balance").getAsFloat());
  if ( asset.get("balance").getAsFloat()  > 0 && asset.get("balance").getAsFloat() >= amountf ) {
      JsonObject transInfo = mixinApiUser.transfer(BaseAssetID, OCEANONE_BOT,
                                                   aminput,
                                                   OrderMemo);
       System.out.println(String.format("--------------%s Transfer To EXCHANGE Information---------",BaseAssetID));
       System.out.println(transInfo);
       System.out.println(String.format("---Order is %s: ------",transInfo.get("trace_id").getAsString()));
    } else System.out.println(String.format("----------------Not enough %s--------------------------",BaseAssetID));
}
```
### 读取币的价格列表
读取币的价格列表，来确认挂单是否成功!

```java
if ( subinput.equals("x") ) {
  FetchOceanMarketInfos(ERC20_BENZ,XIN_ASSET_ID);
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
### ERC20相关的操作指令

Commands list of this source code:

- trb:Transfer ERC20 from Bot to Wallet
- trm:Transfer ERC20 from Wallet to Master
- o: Ocean.One Exchange

Make your choose(eg: q for Exit!):
- x:  Orders-Book of ERC20/USDT
- x1: Buy ERC20 pay USDT
- x2: Sell ERC20 get USDT
- c: Cancel the order
- q: Exit

[完整的代码](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/bitcoin_wallet-java/src/main/java/bitcoin_wallet/java/App.java)
