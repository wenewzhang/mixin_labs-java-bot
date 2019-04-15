/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package bitcoin_wallet.java;

import mixin.java.sdk.MixinHttpUtil;
import mixin.java.sdk.MixinAPI;
import java.security.PrivateKey;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import mixin.java.sdk.PrivateKeyReader;

import java.util.Base64;
import java.security.Key;
import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
import java.io.IOException;
// import java.security.NoSuchAlgorithmException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.io.Console;
import java.nio.file.StandardOpenOption;

public class App {

    public static final String EXIN_BOT         = "61103d28-3ac2-44a2-ae34-bd956070dab1";
    public static final String BTC_ASSET_ID     = "c6d0c728-2624-429b-8e0d-d9d19b6592fa";
    public static final String EOS_ASSET_ID     = "6cfe566e-4aad-470b-8c9a-2fd35b49c68d";
    public static final String USDT_ASSET_ID    = "815b0b1a-2764-3736-8faa-42d694fa620a";
    public static final String BTC_WALLET_ADDR  = "14T129GTbXXPGXXvZzVaNLRFPeHXD1C25C";
    public static final String MASTER_UUID      = "0b4f49dc-8fb4-4539-9a89-fb3afc613747";
    private static final String WALLET_FILANAME = "./mybitcoin_wallet.csv";

    public static void main(String[] args) {
        MixinAPI mixinApi = new MixinAPI(Config.CLIENT_ID, Config.CLIENT_SECRET,
                                         Config.PIN, Config.SESSION_ID, Config.PIN_TOKEN,
                                         Config.RSA_PRIVATE_KEY);

        do {
          String PromptMsg;
          PromptMsg  = "1: Create Bitcoin Wallet and update PIN\n2: Read Bitcoin balance & address \n3: Read USDT balance & address\n4: Read EOS balance & address\n";
          PromptMsg += "5: pay 0.0001 BTC buy USDT\n6: pay $1 USDT buy BTC\n7: Read Snapshots\n8: Fetch market price(USDT)\n9: Fetch market price(BTC)\n";
          PromptMsg += "v: Verify Wallet Pin\n";
          PromptMsg += "q: Exit \nMake your choose:";
          System.out.print(PromptMsg);
          String input = System.console().readLine();
          System.out.println(input);
          if ( input.equals("q") ) { System.exit(0); }
          if ( input.equals("1") ) {

          // JsonArray assets = mixinApi.getAssets();
          // assets.forEach((element) ->  {
          //    JsonObject jsonObj = element.getAsJsonObject();
          //    System.out.println(jsonObj.get("asset_id").getAsString() + " " +
          //                       jsonObj.get("symbol").getAsString() + " " +
          //                       jsonObj.get("balance").getAsString() );
          // });
          // JsonObject asset = mixinApi.getAsset(BTC_ASSET_ID);
          // System.out.println(asset);

          // JsonObject transInfo = mixinApi.transfer("965e5c6e-434c-3fa9-b780-c50f43cd955c",MASTER_UUID,"0.1","hi");
          // System.out.println(transInfo);

          // JsonObject vInfo = mixinApi.verifyPin(Config.PIN);
          // System.out.println(vInfo);

          try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();

            RSAPrivateKey priv = (RSAPrivateKey) kp.getPrivate();
            RSAPublicKey pub = (RSAPublicKey) kp.getPublic();

            String SessionSecret = Base64.getEncoder().encodeToString(pub.getEncoded());
            JsonObject walletInfo = mixinApi.createUser("java wallet",SessionSecret);
            System.out.println(walletInfo.get("session_id").getAsString());

            BufferedWriter writer = Files.newBufferedWriter(Paths.get(WALLET_FILANAME),
            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(','));
            csvPrinter.printRecord(Arrays.asList(Base64.getEncoder().encodeToString(priv.getEncoded()),
                                  walletInfo.get("pin_token").getAsString(),
                                  walletInfo.get("session_id").getAsString(),
                                  walletInfo.get("user_id").getAsString()));
            csvPrinter.flush();
            MixinAPI mixinApiUser = generateAPI_FromCSV();
            JsonObject asset = mixinApiUser.updatePin("","123456");
            System.out.println(asset);
          } catch(Exception e) { e.printStackTrace(); }
        }
        if ( input.equals("2") ) {
         MixinAPI mixinApiUser = generateAPI_FromCSV();
         JsonObject asset = mixinApiUser.getAsset(BTC_ASSET_ID);
         System.out.println(asset);
         System.out.println("------------------------BTC------Information---------------------------");
         System.out.println("The BTC wallet address is " + asset.get("public_key").getAsString());
         System.out.println("The BTC wallet balance is " + asset.get("balance").getAsString());
         System.out.println("-----------------------------------------------------------------------");
        }
        if ( input.equals("3") ) {
         MixinAPI mixinApiUser = generateAPI_FromCSV();
         JsonObject asset = mixinApiUser.getAsset(USDT_ASSET_ID);
         System.out.println(asset);
         System.out.println("------------------------USDT------Information---------------------------");
         System.out.println("The USDT wallet address is " + asset.get("public_key").getAsString());
         System.out.println("The USDT wallet balance is " + asset.get("balance").getAsString());
         System.out.println("-----------------------------------------------------------------------");
        }
        if ( input.equals("4") ) {
         MixinAPI mixinApiUser = generateAPI_FromCSV();
         JsonObject asset = mixinApiUser.getAsset(EOS_ASSET_ID);
         System.out.println(asset);
         System.out.println("------------------------EOS------Information---------------------------");
         System.out.println("The EOS wallet Name is " + asset.get("account_name").getAsString() +
                            " Tag is " + asset.get("account_tag").getAsString());
         System.out.println("The EOS wallet balance is " + asset.get("balance").getAsString());
         System.out.println("-----------------------------------------------------------------------");
        }
        if ( input.equals("v") ) {
         MixinAPI mixinApiUser = generateAPI_FromCSV();
         JsonObject asset = mixinApiUser.verifyPin("123456");
         System.out.println(asset);
        }
      }while ( true );
    }
    private static MixinAPI generateAPI_FromCSV() {
      try {
       Reader reader = Files.newBufferedReader(Paths.get(WALLET_FILANAME));
       CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
       PrivateKey privKey = null;
       for (CSVRecord csvRecord : csvParser) {
         // System.out.println("Name : " + csvRecord.get(0));
         byte[] encoded = Base64.getDecoder().decode(csvRecord.get(0));
         PKCS8EncodedKeySpec keySpec = null;
         keySpec = new PKCS8EncodedKeySpec(encoded);

         KeyFactory kf = KeyFactory.getInstance("RSA");
         privKey = kf.generatePrivate(keySpec);
         return new MixinAPI(csvRecord.get(3), "do not need",
                                           "123456", csvRecord.get(2), csvRecord.get(1),
                                           (RSAPrivateKey) privKey);
       }
     } catch(Exception e) { e.printStackTrace(); }
     return null;
   }
}
