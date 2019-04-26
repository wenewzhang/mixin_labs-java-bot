# How to trade bitcoin through Java
![cover](https://github.com/wenewzhang/mixin_labs-java-bot/raw/master/bitcoin_wallet-java/mixin-bitcoin-java.jpg)

## Solution Two: List your order on Ocean.One exchange
[Ocean.one](https://github.com/mixinNetwork/ocean.one) is a decentralized exchange built on Mixin Network, it's almost the first time that a decentralized exchange gain the same user experience as a centralized one.

You can list any asset on OceanOne. Pay the asset you want to sell to OceanOne account, write your request in payment memo, OceanOne will list your order to market. It send asset to your wallet after your order is matched. 

* No sign up required
* No deposit required
* No listing process.

### Pre-request:
You should  have created a bot based on Mixin Network. Create one by reading [Java Bitcoin tutorial](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README.md).

#### Install required packages
[Chapter 4](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README4.md), assume it has installed before.

#### Deposit USDT or Bitcoin into your Mixin Network account and read balance
The Ocean.one can match any order. Here we exchange between USDT and Bitcoin, Check the wallet's balance & address before you make order.

- Check the address & balance, find it's Bitcoin wallet address.
- Deposit Bitcoin to this Bitcoin wallet address.
- Check Bitcoin balance after 100 minutes later.

**Omni USDT address is same as Bitcoin address**

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

#### Read orders book from Ocean.one
How to check the coin's price? You need understand what is the base coin. If you want buy Bitcoin and sell USDT, the USDT is the base coin. If you want buy USDT and sell Bitcoin, the Bitcoin is the base coin.


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

#### Create a memo to prepare order
The chapter two: [Echo Bitcoin](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README2.md) introduce transfer coins. But you need to let Ocean.one know which coin you want to buy.
- **Side** "B" or "A", "B" for buy, "A" for Sell.
- **AssetUUID** Assets UUID
- **Price** If Side is "B", Price is AssetUUID; if Side is "A", Price is the asset which transfer to Ocean.one.

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

#### Pay BTC to OceanOne with generated memo
Transfer Bitcoin(BTC_ASSET_ID) to Ocean.one(OCEANONE_BOT), put you target asset uuid(USDT) in the memo.
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
If you want sell USDT buy BTC, call it like below:
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
A success order output like below:
```bash
------------------Ocean.one--EXCHANGE----------------------------
hKFToUKhQcQQyUrIj0ZxOXa2CgkGTxgR6KFQojExoVShTA==
{"type":"asset","asset_id":"815b0b1a-2764-3736-8faa-42d694fa620a","chain_id":"c6d0c728-2624-429b-8e0d-d9d19b6592fa","symbol":"USDT","name":"Tether USD","icon_url":"https://images.mixin.one/ndNBEpObYs7450U08oAOMnSEPzN66SL8Mh-f2pPWBDeWaKbXTPUIdrZph7yj8Z93Rl8uZ16m7Qjz-E-9JFKSsJ-F=s128","balance":"2","public_key":"15RNBb5GzoXWRoEMT5MJL3pqzSjdPoXf6J","account_name":"","account_tag":"","price_btc":"0.00018747","price_usd":"0.98731224","change_btc":"0.021985715750932774","change_usd":"-0.01843154053058167","asset_key":"815b0b1a-2764-3736-8faa-42d694fa620a","confirmations":6,"capitalization":0}
2.0
--------------815b0b1a-2764-3736-8faa-42d694fa620a Transfer To EXCHANGE Information---------
{"type":"transfer","snapshot_id":"f526fd18-c3d2-4a6d-a0f0-3a720e93c48e","opponent_id":"aaff5bef-42fb-4c9f-90e0-29f69176b7d4","asset_id":"815b0b1a-2764-3736-8faa-42d694fa620a","amount":"-2","trace_id":"6cf37ca6-4efa-4e78-bea1-53c5c5d54d2b","memo":"hKFToUKhQcQQyUrIj0ZxOXa2CgkGTxgR6KFQojExoVShTA==","created_at":"2019-04-26T01:56:00.803572612Z","counter_user_id":"0b4f49dc-8fb4-4539-9a89-fb3afc613747"}
---Order is 6cf37ca6-4efa-4e78-bea1-53c5c5d54d2b: ------
```
## Cancel the Order
Ocean.one take the trace_id as the order id, for example, **6cf37ca6-4efa-4e78-bea1-53c5c5d54d2b** is a order id,
We can use it to cancel the order!
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
#### Read Bitcoin balance
Check the wallet's balance.
```java
MixinAPI mixinApiUser = generateAPI_FromCSV();
JsonObject asset = mixinApiUser.getAsset(BTC_ASSET_ID);
```

## Source code usage
Build it and then run it.
- **gradle build**  build project.
- **java -cp** run it.
```bash
java -cp .:build/libs/bitcoin_wallet-java.jar:libs/* bitcoin_wallet.java.App
```

Caution：DO NOT use **gradle run** to run it, because **System.console().readLine()** doesn't supported by Gradle, use **java -cp** instead.

Commands list of this source code:

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
[Full source code](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/bitcoin_wallet-java/src/main/java/bitcoin_wallet/java/App.java)
