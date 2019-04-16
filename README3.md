# Java Bitcoin tutorial based on Mixin Network III: Create Bitcoin wallet, read balance and send Bitcoin
![cover](https://github.com/wenewzhang/mixin_labs-java-bot/raw/master/bitcoin_wallet-java/mixin-bitcoin-java.jpg)

We have created a bot to [echo message](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README.md) and [echo Bitcoin](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README2.md).

### What you will learn from this chapter
1. How to create Bitcoin wallet
2. How to read Bitcoin balance
3. How to send Bitcoin with zero transaction fee and confirmed in 1 second
4. How to send Bitcoin to other wallet


Pre-request: You should have a Mixin Network app account. Create an account:

```js
KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
kpg.initialize(1024);
KeyPair kp = kpg.genKeyPair();

RSAPrivateKey priv = (RSAPrivateKey) kp.getPrivate();
RSAPublicKey pub = (RSAPublicKey) kp.getPublic();

String SessionSecret = Base64.getEncoder().encodeToString(pub.getEncoded());
JsonObject walletInfo = mixinApi.createUser("java wallet",SessionSecret);
System.out.println(walletInfo.get("session_id").getAsString());

BufferedWriter writer = Files.newBufferedWriter(Paths.get(WALLET_FILANAME),
                                                StandardOpenOption.CREATE,
                                                StandardOpenOption.APPEND);
CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(','));
csvPrinter.printRecord(Arrays.asList(Base64.getEncoder().encodeToString(priv.getEncoded()),
                      walletInfo.get("pin_token").getAsString(),
                      walletInfo.get("session_id").getAsString(),
                      walletInfo.get("user_id").getAsString()));
csvPrinter.flush();
MixinAPI mixinApiUser = generateAPI_FromCSV();
JsonObject asset = mixinApiUser.updatePin("","123456");
System.out.println(asset);

```
The function create a RSA key pair automatically, then call Mixin Network to create an account and return all account information, save it to csv file.


Now you need to carefully keep the account information. These information are required to read asset balance and other content of account.
### Create Bitcoin wallet for the Mixin Network account
The Bitcoin  wallet is not generated automatically at same time when we create Mixin Network account. Read Bitcoin asset once to generate a Bitcoin wallet.
```java
  MixinAPI mixinApiUser = generateAPI_FromCSV();
  JsonObject asset = mixinApiUser.getAsset(BTC_ASSET_ID);
  System.out.println(asset);
  System.out.println("------------------------BTC------Information---------------------------");
  System.out.println("The BTC wallet address is " + asset.get("public_key").getAsString());
  System.out.println("The BTC wallet balance is " + asset.get("balance").getAsString());
  System.out.println("-----------------------------------------------------------------------");
```
You can found information about Bitcoin asset in the account. Public key is the Bitcoin deposit address. Full response of read  Bitcoin asset is
```bash
Make your choose:2
2
{"type":"asset","asset_id":"c6d0c728-2624-429b-8e0d-d9d19b6592fa","chain_id":
"c6d0c728-2624-429b-8e0d-d9d19b6592fa","symbol":"BTC","name":"Bitcoin",
"icon_url":"https://images.mixin.one/HvYGJsV5TGeZ-X9Ek3FEQohQZ3fE9LBEBGcOcn4c4BNHovP4fW4YB97Dg5LcXoQ1hUjMEgjbl1DPlKg1TW7kK6XP=s128",
"balance":"0","public_key":"1KVcMVbSNdubRPKwGcaf9isuMXWFKCCkAH","account_name":"",
"account_tag":"","price_btc":"1","price_usd":"5081.36352765","change_btc":"0",
"change_usd":"-0.019033421740167297","asset_key":
"c6d0c728-2624-429b-8e0d-d9d19b6592fa","confirmations":6,"capitalization":0}
------------------------BTC------Information---------------------------
The BTC wallet address is 1KVcMVbSNdubRPKwGcaf9isuMXWFKCCkAH
The BTC wallet balance is 0
-----------------------------------------------------------------------
```


The API provide many information about Bitcoin asset.
* Deposit address:[public_key]
* Logo: [icon_url]
* Asset name:[name]
* Asset uuid in Mixin network: [asset_key]
* Price in USD from Coinmarketcap.com: [price_usd]
* Least confirmed blocks before deposit is accepted by Mixin network:[confirmations]


### Private key?
Where is Bitcoin private key? The private key is protected by multi signature inside Mixin Network so it is invisible for user. Bitcoin asset can only be withdraw to other address when user provide correct RSA private key signature, PIN code and Session key.

### Not only Bitcoin, but also Ethereum, EOS
The account not only contain a Bitcoin wallet, but also contains wallet for Ethereum, EOS, etc. Full blockchain support [list](https://mixin.one/network/chains). All ERC20 Token and EOS token are supported by the account.

Create other asset wallet is same as create Bitcoin wallet, just read the asset.
#### Mixin Network support cryptocurrencies (2019-02-19)

|crypto |uuid in Mixin Network
|---|---
|EOS|6cfe566e-4aad-470b-8c9a-2fd35b49c68d
|CNB|965e5c6e-434c-3fa9-b780-c50f43cd955c
|BTC|c6d0c728-2624-429b-8e0d-d9d19b6592fa
|ETC|2204c1ee-0ea2-4add-bb9a-b3719cfff93a
|XRP|23dfb5a5-5d7b-48b6-905f-3970e3176e27
|XEM|27921032-f73e-434e-955f-43d55672ee31
|ETH|43d61dcd-e413-450d-80b8-101d5e903357
|DASH|6472e7e3-75fd-48b6-b1dc-28d294ee1476
|DOGE|6770a1e5-6086-44d5-b60f-545f9d9e8ffd
|LTC|76c802a2-7c88-447f-a93e-c29c9e5dd9c8
|SC|990c4c29-57e9-48f6-9819-7d986ea44985
|ZEN|a2c5d22b-62a2-4c13-b3f0-013290dbac60
|ZEC|c996abc9-d94e-4494-b1cf-2a3fd3ac5714
|BCH|fd11b6e3-0b87-41f1-a41f-f0e9b49e5bf0

If you read EOS deposit address, the deposit address is composed of two parts: account_name and account tag. When you transfer EOS token to your account in Mixin network, you should fill both account name and memo. The memo content is value of 'account_tag'.
Result of read EOS asset is:
```bash
{"type":"asset","asset_id":"6cfe566e-4aad-470b-8c9a-2fd35b49c68d",
"chain_id":"6cfe566e-4aad-470b-8c9a-2fd35b49c68d",
"symbol":"EOS","name":"EOS",
"icon_url":"https://images.mixin.one/a5dtG-IAg2IO0Zm4HxqJoQjfz-5nf1HWZ0teCyOnReMd3pmB8oEdSAXWvFHt2AJkJj5YgfyceTACjGmXnI-VyRo=s128",
"balance":"0","public_key":"",
"account_name":"eoswithmixin","account_tag":"10ad0870e9595bdedb4334360a8d4165",
"price_btc":"0.00105635","price_usd":"5.38549443","change_btc":"-0.014835576787888972",
"change_usd":"-0.031082698851444927","asset_key":"eosio.token:EOS","confirmations":64,"capitalization":0}
------------------------EOS------Information---------------------------
The EOS wallet Name is eoswithmixin Tag is 10ad0870e9595bdedb4334360a8d4165
The EOS wallet balance is 0
-----------------------------------------------------------------------
```

### Deposit Bitcoin and read balance
Now you can deposit Bitcoin into the deposit address.

This is maybe too expensive for this tutorial. There is a free and lightening fast solution to deposit Bitcoin: add the address in your Mixin messenger account withdrawal address and withdraw small amount Bitcoin from your account to the address. It is free and confirmed instantly because they are both on Mixin Network.

Now you can read Bitcoin balance of the account.
```java
  JsonObject asset = mixinApiUser.getAsset(BTC_ASSET_ID);
```
### Send Bitcoin inside Mixin Network to enjoy instant confirmation and ZERO transaction fee
Any transaction happen between Mixin network account is free and is confirmed in 1 second.

Pre-request: A PIN has been created for account

A PIN is required to send any asset in Mixin Network. Let's create PIN for the account if it is missing.
```java
  MixinAPI mixinApiUser = generateAPI_FromCSV();
  JsonObject asset = mixinApiUser.updatePin("","123456");
  System.out.println(asset);
```
#### Send Bitcoin to another Mixin Network account
We can send Bitcoin to our bot through Mixin Messenger, and then transfer Bitcoin from bot to new user.

```java
  if ( input.equals("tbb") ) {
   MixinAPI mixinApiUser = generateAPI_FromCSV();
   JsonObject asset = mixinApi.getAsset(BTC_ASSET_ID);
   System.out.println(asset);
   if ( asset.get("balance").getAsFloat() > 0 ) {
       JsonObject transInfo = mixinApi.transfer(BTC_ASSET_ID,mixinApiUser.getClientID(),
                                                asset.get("balance").getAsString(),"hi");
       System.out.println("------------------------BTC Transfer from Bot Information---------------------------");
       System.out.println(transInfo);
       System.out.println("-----------------------------------------------------------------------");
    }
  }
```

Read bot's Bitcoin balance to confirm the transaction.
Caution: **mixinApiUser** is for the New User!
```java
  JsonObject asset = mixinApiUser.getAsset(BTC_ASSET_ID);
```
### Send Bitcoin to another Bitcoin exchange or wallet
If you want to send Bitcoin to another exchange or wallet, you need to know the destination deposit address, then add the address in withdraw address list of the Mixin network account.

Pre-request: Withdrawal address is added and know the Bitcoin withdrawal fee

#### Add destination address to withdrawal address list
Call createAddress, the ID of address will be returned in result of API and is required soon.
```java
  private static final String BTC_WALLET_ADDR  = "14T129GTbXXPGXXvZzVaNLRFPeHXD1C25C";
  private static final String BTC_ASSET_ID     = "c6d0c728-2624-429b-8e0d-d9d19b6592fa";

  MixinAPI mixinApiUser = generateAPI_FromCSV();
  JsonObject addrInfo = mixinApiUser.createWithdrawAddress(BTC_ASSET_ID,
                                                         BTC_WALLET_ADDR,"","","123456","hi");
  System.out.println(addrInfo);
  System.out.println("------------------------BTC---Withdrawal---Information---------------------------");
  System.out.println("The BTC Witchdrawal address is " + addrInfo.get("public_key").getAsString());
  System.out.println("The BTC withdraw fee  is " + addrInfo.get("fee").getAsString());
  System.out.print("Input the BTC withdraw amount:");
  String eosAmount = System.console().readLine();
  JsonObject withdrawInfo = mixinApiUser.withdrawals(addrInfo.get("address_id").getAsString(),
                                                 eosAmount,"","123456","memo");
  System.out.println(withdrawInfo);
```

The **14T129GTbXXPGXXvZzVaNLRFPeHXD1C25C** is a Bitcoin wallet address, Output like below, fee is 0.0025738 BTC, The API result contains the withdrawal address ID.                                                   
```bash
Make your choose:wb
wb
{"type":"address","address_id":"58860d12-cbdc-40ae-8a4a-b0fc72f418b3",
"asset_id":"c6d0c728-2624-429b-8e0d-d9d19b6592fa","public_key":"14T129GTbXXPGXXvZzVaNLRFPeHXD1C25C",
"label":"hi","account_name":"","account_tag":"","fee":"0.00118846","reserve":"0","dust":"0.0001",
"updated_at":"2019-04-16T06:12:15.686517454Z"}
------------------------BTC---Withdrawal---Information---------------------------
The BTC Witchdrawal address is 14T129GTbXXPGXXvZzVaNLRFPeHXD1C25C
The BTC withdraw fee  is 0.00118846
```


#### Read withdraw fee anytime
```java
 JsonObject addrInfo3 = mixinApiUser.getAddress(addrInfo.get("address_id").getAsString());
 System.out.println(addrInfo3);
```

#### Send Bitcoin to destination address
Submit the withdrawal request to Mixin Network, the withdrawAddress.address_id is the address id return by createAddress
```java
  MixinAPI mixinApiUser = generateAPI_FromCSV();
  JsonObject addrInfo = mixinApiUser.createWithdrawAddress(BTC_ASSET_ID,
                                                         BTC_WALLET_ADDR,"","","123456","hi");
  System.out.println(addrInfo);
  System.out.println("------------------------BTC---Withdrawal---Information---------------------------");
  System.out.println("The BTC Witchdrawal address is " + addrInfo.get("public_key").getAsString());
  System.out.println("The BTC withdraw fee  is " + addrInfo.get("fee").getAsString());
  System.out.print("Input the BTC withdraw amount:");
  String eosAmount = System.console().readLine();
  JsonObject withdrawInfo = mixinApiUser.withdrawals(addrInfo.get("address_id").getAsString(),
                                                 eosAmount,"","123456","memo");
  System.out.println(withdrawInfo);
  System.out.println("-----------------------------------------------------------------------");
```
#### Confirm the transaction in blockchain explore

[Full source code](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/bitcoin_wallet-java/src/main/java/bitcoin_wallet/java/App.java)
