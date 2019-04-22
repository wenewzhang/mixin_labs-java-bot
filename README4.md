# How to trade bitcoin through Java
![cover](https://github.com/wenewzhang/mixin_labs-java-bot/raw/master/bitcoin_wallet-java/mixin-bitcoin-java.jpg)

## Solution One: pay to ExinCore API
[Exincore](https://github.com/exinone/exincore) provide a commercial trading API on Mixin Network.

You pay USDT to ExinCore, ExinCore transfer Bitcoin to you on the fly with very low fee and fair price. Every transaction is anonymous to public but still can be verified on blockchain explorer. Only you and ExinCore know the details.

ExinCore don't know who you are because ExinCore only know your client's uuid.

### Pre-request:
You should  have created a bot based on Mixin Network. Create one by reading [Java Bitcoin tutorial](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README.md).

#### Install required packages
As you know, we introduce you the [**mixin-java-sdk**](https://github.com/wenewzhang/mixin-java-sdk/releases) in [chapter 1](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README.md), assume it has installed before, let's install others here.
Add dependent packages to build.gradle.kts.
```java
  implementation("com.google.guava:guava:27.0.1-jre")
  implementation("commons-codec:commons-codec:1.11")
  implementation("com.auth0:java-jwt:3.8.0")
  compile(files("libs/mixin-java-sdk.jar"))
  implementation("com.squareup.okhttp3:okhttp:3.12.1")
  implementation("com.squareup.okio:okio:2.2.2")
  implementation("com.google.code.gson:gson:2.8.5")
  implementation("org.bouncycastle:bcprov-jdk16:1.46")
  implementation("org.apache.commons:commons-csv:1.6")
  implementation("org.msgpack:msgpack-core:0.8.16")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
  implementation("com.fasterxml.jackson.core:jackson-core:2.9.8")
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.30")
```

#### Deposit USDT or Bitcoin into your Mixin Network account and read balance
The ExinCore can exchange between Bitcoin, USDT, EOS, ETH etc. Here show you how to exchange between USDT and Bitcoin,
Check the wallet's balance & address before you make order.

- Check the address & balance, remember it Bitcoin wallet address.
- Deposit Bitcoin to this Bitcoin wallet address.
- Check Bitcoin balance after 100 minutes later.

**By the way, Bitcoin & USDT 's address are the same.**

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

#### Read market price
How to check the coin's price? You need understand what is the base coin. If you want buy Bitcoin and sell USDT, the USDT is the base coin. If you want buy USDT and sell Bitcoin, the Bitcoin is the base coin.

```java
  if ( input.equals("8") ) {
   JsonArray res = FetchExinOneMarketInfos(USDT_ASSET_ID);
   System.out.println("--exchange_asset--exchange_asset_symbol/base_asset_symbol--price--minimum--maximum--exchanges--");
   // System.out.println(res);
   res.forEach((element) ->  {
      JsonObject jsonObj = element.getAsJsonObject();
      System.out.println(jsonObj.get("exchange_asset").getAsString() + " " +
                         jsonObj.get("exchange_asset_symbol").getAsString() + "/" +
                         jsonObj.get("base_asset_symbol").getAsString() + " " +
                         jsonObj.get("price").getAsString() + " " +
                         jsonObj.get("minimum_amount").getAsString() + " " +
                         jsonObj.get("maximum_amount").getAsString() + " " +
                         jsonObj.get("exchanges").getAsString() );
   });
   System.out.println("-----------------------------------------------------------------------");
  }
  private static JsonArray FetchExinOneMarketInfos(String url) {
    OkHttpClient client = new OkHttpClient();
    String baseUrl = "https://exinone.com/exincore/markets?base_asset=";
    String fullUrl = baseUrl + url;
    Request request = new Request.Builder()
                               .url(fullUrl)
                               .build();
    try {
       Response response = client.newCall(request).execute();
       if (!response.isSuccessful()) {
         throw new IOException("Unexpected code " + response);
       }
       return processJsonObjectWithDataOrError(response.body().string());
     } catch(Exception e) { e.printStackTrace(); }
     return null;
  }
  public static JsonArray processJsonObjectWithDataOrError(String res) {
   JsonParser parser = new JsonParser();
   JsonElement jsonTree = parser.parse(res);
   if ( jsonTree.isJsonObject() ) {
     if ( jsonTree.getAsJsonObject().get("data") != null ) {
        return  jsonTree.getAsJsonObject().get("data").getAsJsonArray();
     }
   }
   return null;
  }
```

#### Create a memo to prepare order
The chapter two: [Echo Bitcoin](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README2.md) introduce transfer coins. But you need to let ExinCore know which coin you want to buy. Just write your target asset into memo.
```java
public static String encodeUUID(UUID uuid) {
  try {
    byte[] byteUuid = asBytes(uuid);
    MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
    packer.writePayload(byteUuid);
    packer.close();
    byte[] packedData = packer.toByteArray();
    byte[] prex = { (byte)129, (byte)161, 65, (byte)196, 16 };
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(prex);
    output.write(packedData);
    byte[] out = output.toByteArray();
    return Base64.getEncoder().encodeToString(out);
  } catch (Exception e) { e.printStackTrace(); }
    return null;
}
```

#### Pay BTC to API gateway with generated memo
Transfer Bitcoin(BTC_ASSET_ID) to ExinCore(EXIN_BOT), put you target asset uuid in the memo, otherwise, ExinCore will refund you coin immediately!
```java
private static final String EXIN_BOT         = "61103d28-3ac2-44a2-ae34-bd956070dab1";
private static final String BTC_ASSET_ID     = "c6d0c728-2624-429b-8e0d-d9d19b6592fa";
private static final String EOS_ASSET_ID     = "6cfe566e-4aad-470b-8c9a-2fd35b49c68d";
private static final String USDT_ASSET_ID    = "815b0b1a-2764-3736-8faa-42d694fa620a";
private static final String BTC_WALLET_ADDR  = "14T129GTbXXPGXXvZzVaNLRFPeHXD1C25C";
private static final String MASTER_UUID      = "0b4f49dc-8fb4-4539-9a89-fb3afc613747";

if ( input.equals("5") ) {
 MixinAPI mixinApiUser = generateAPI_FromCSV();
 UUID usdtUUID         =  UUID.fromString(USDT_ASSET_ID);
 String memoTarget     = encodeUUID(usdtUUID);
 System.out.println("------------------------USDT-BTC-EXCHANGE----------------------------");
 System.out.println(memoTarget);
 JsonObject asset = mixinApiUser.getAsset(BTC_ASSET_ID);
 System.out.println(asset);
 System.out.println(asset.get("balance").getAsFloat());
 if ( (asset.get("balance").getAsFloat() * 10000) >= 1 ) {
     JsonObject transInfo = mixinApiUser.transfer(BTC_ASSET_ID, EXIN_BOT,
                                                "0.0001",memoTarget);
     System.out.println("------------------------BTC Transfer To EXCHANGE Information----------------------");
     System.out.println(transInfo);
     System.out.println("-----------------------------------------------------------------------");
  } else System.out.println("-----------------------------------------------------------------------");
}
```
If you want sell USDT buy BTC, call it like below:
```java
if ( input.equals("6") ) {
 MixinAPI mixinApiUser = generateAPI_FromCSV();
 UUID btcUUID          =  UUID.fromString(BTC_ASSET_ID);
 String memoTarget     = encodeUUID(btcUUID);
 System.out.println(memoTarget);
 System.out.println("-------------------------BTC-USDT-EXCHANGE----------------------------");
 JsonObject asset = mixinApiUser.getAsset(BTC_ASSET_ID);
 System.out.println(asset);
 if ( asset.get("balance").getAsFloat() >= 1 ) {
     JsonObject transInfo = mixinApiUser.transfer(USDT_ASSET_ID, EXIN_BOT,
                                                "1",memoTarget);
     System.out.println("------------------------USDT-BTC  EXCHANGE Information----------------------");
     System.out.println(transInfo);
     System.out.println("-----------------------------------------------------------------------");
  } else System.out.println("-----------------------------------------------------------------------");
}
```

The ExinCore should transfer the target coin to your bot, meanwhile, put the fee, order id, price etc. information in the memo, unpack the data like below.
- **getSnapshots** Read snapshots of the user.
```java
if ( input.equals("7") ) {
  MixinAPI mixinApiUser = generateAPI_FromCSV();
  String transDatetime  = "";
  String assetUUID      = "";
  if ( mixinApiUser.getClientID().equals("091651f2-19c3-34f0-b45e-724ff203d921") ) {
    transDatetime = "2019-04-19T06:53:22.593529Z";
    assetUUID     = USDT_ASSET_ID;
  } else {
    System.out.print("Input the transaction Date time (eg:2019-04-19T06:53:22.593529Z):");
    transDatetime = System.console().readLine();
  }
  JsonArray snapshots = mixinApiUser.getSnapshots(assetUUID,3,transDatetime,"ASC");
  // System.out.println(snapshots);
  snapshots.forEach((element) ->  {
     JsonObject jsonObj = element.getAsJsonObject();
     if ( jsonObj.get("amount").getAsFloat() > 0 && jsonObj.get("data") != null ) {
       System.out.println(jsonObj.get("data").getAsString() );
       try {
       byte[] encoded = Base64.getDecoder().decode(jsonObj.get("data").getAsString());
       MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(encoded);
       Value memoVal = unpacker.unpackValue();
       if ( memoVal.isMapValue()) {
         Map<Value, Value> map = memoVal.asMapValue().map();
         System.out.println(map.size());
         if ( map.get(ValueFactory.newString("C")).asIntegerValue().asInt() == 1000 ) {
           System.out.println("Exchange successful" + " Code: " +
                              map.get(ValueFactory.newString("C")).asIntegerValue());
           System.out.println("Price is " + map.get(ValueFactory.newString("P")).asStringValue());
           System.out.println("Fee is " + map.get(ValueFactory.newString("F")).asStringValue());
           System.out.println("Type is " + map.get(ValueFactory.newString("T")).asStringValue());
           ByteBuffer AssetBinValue = map.get(ValueFactory.newString("FA")).asRawValue().asByteBuffer();
           System.out.println("Fee is asset UUID is  " + ByteBufferAsUuid(AssetBinValue));
           ByteBuffer TraceBinValue = map.get(ValueFactory.newString("O")).asRawValue().asByteBuffer();
           System.out.println("The trace id is " + ByteBufferAsUuid(TraceBinValue));
        }
       }
      } catch(Exception e) { e.printStackTrace(); }
     }
  });
}
```

If you coin exchange successful, console output like below:
```bash
Make your choose(eg: q for Exit!): 7
7
-------------------------get-Snapshots---------------------------------------------
hqFDzQPooVCnNTIyNi4wM6FGqTAuMDAxMDQ1MqJGQcQQgVsLGidkNzaPqkLWlPpiCqFUoVKhT8QQPAsBVqKtQ5uS67M/ZTNleg==
Exchange successful Code: 1000
Price is 5226.03
Fee is 0.0010452
Type is R
Fee is asset UUID is  815b0b1a-2764-3736-8faa-42d694fa620a
The trace id is 3c0b0156-a2ad-439b-92eb-b33f6533657a
---------------------end-of-get-Snapshots---------------------------------------------
```

#### Read Bitcoin balance
Check the wallet's balance.
```java
MixinAPI mixinApiUser = generateAPI_FromCSV();
JsonObject asset = mixinApiUser.getAsset(BTC_ASSET_ID);
```

## Source code usage
Build it and then run it.
- **Gradle build**  build project.
- **java -cp .:build/libs/bitcoin_wallet-java.jar:libs/* bitcoin_wallet.java.App** run it.

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
- q: Exit
Make your choose(eg: q for Exit!):

[Full source code](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/bitcoin_wallet-java/src/main/java/bitcoin_wallet/java/App.java)

## Solution Two: List your order on Ocean.One exchange
