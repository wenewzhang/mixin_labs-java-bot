# 通过 Java 买卖Bitcoin
![cover](https://github.com/wenewzhang/mixin_labs-java-bot/raw/master/bitcoin_wallet-java/mixin-bitcoin-java.jpg)

## 方案一: 通过ExinCore API进行币币交易
[Exincore](https://github.com/exinone/exincore) 提供了基于Mixin Network的币币交易API.

你可以支付USDT给ExinCore, ExinCore会以最低的价格，最优惠的交易费将你购买的比特币转给你, 每一币交易都是匿名的，并且可以在区块链上进行验证，交易的细节只有你与ExinCore知道！

ExinCore 也不知道你是谁，它只知道你的UUID.

### 预备知识:
你先需要创建一个机器人, 方法在 [教程一](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README-zhchs.md).

#### 安装依赖包
正如教程一里我们介绍过的， 我们需要依赖 [**mixin-java-sdk**](https://github.com/wenewzhang/mixin-java-sdk/releases), 你应该先安装过它了， 这儿我们再安装其它的软件包.
下载地址: [mvnrepository](https://mvnrepository.com/)
build.gradle.kts的dependencies区块，加入如下依赖的包：
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

#### 查询ExinCore市场的价格信息
如何来查询ExinCore市场的价格信息呢？你要先了解你交易的基础币是什么，如果你想买比特币，卖出USDT,那么基础货币就是USDT;如果你想买USDT,卖出比特币，那么基础货币就是比特币.

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

#### 交易前，创建一个Memo!
在第二章里,[基于Mixin Network的 Java 比特币开发教程: 机器人接受比特币并立即退还用户](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README2-zhchs.md), 我们学习过退还用户比特币，在这里，我们除了给ExinCore支付币外，还要告诉他我们想购买的币是什么，即将想购买的币存到memo里。
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

#### 币币交易的完整流程
转币给ExinCore时，将memo写入你希望购买的币，否则，ExinCore会直接退币给你！
如果你想卖出比特币买入USDT,调用方式如下：

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

如果你想卖出USDT买入比特币,调用方式如下：

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

交易完成后，Exincore会将你需要的币转到你的帐上，同样，会在memo里，记录成交价格，交易费用等信息！你只需要按下面的方式解开即可！
- **getSnapshots** 读取钱包的交易记录。
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

一次成功的交易如下：
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

#### 读取币的余额
通过读取币的余额，来确认交易情况！
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
- q: Exit
Make your choose(eg: q for Exit!):

[完整代码](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/bitcoin_wallet-java/src/main/java/bitcoin_wallet/java/App.java)

## Solution Two: List your order on Ocean.One exchange
