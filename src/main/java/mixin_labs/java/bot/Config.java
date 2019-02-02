package mixin_labs.java.bot;

import mixin.java.sdk.MixinUtil;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import mixin.java.sdk.PrivateKeyReader;
public class Config {
  // 修改为你自己的 APP_ID
  // public static final String CLIENT_ID = "21042518-85c7-4903-bb19-f311813d1f51";
  // public static final String CLIENT_SECRET = "9873769d7b4198da2ee397af3ecaa87a5054a03d0114cedf28797567defa6fd8";
  // public static final String PIN = "303289";
  // public static final String SESSION_ID = "6ca194a4-727f-4e5f-a348-3c62987536ba";
  // public static final String PIN_TOKEN = "";
  public static final String CLIENT_ID     = "a1ce2967-a534-417d-bf12-c86571e4eefa";
  public static final String CLIENT_SECRET = "c6b14c6bbb20a43c603c468e225e6e4c666c940792cde43e41b34c3f1dd45713";
  public static final String PIN           = "586071";
  public static final String SESSION_ID    = "1f1c44a3-d4d2-4dd2-bdb6-8eda67694b91";
  public static final String PIN_TOKEN     = "gjJJngHmWgIfH3S2mgH4bAsoPeoXV6hI1KoTZW9AvFUK1R8e28X1zVRCcrOMVeXkvBKQeEMgRdX1kRgH3ksITTBm2mgK5eUnfBHUuRC85oKoQGB9e2Bp4O4ZKGg/6bqLeD66pnBPcO2s7VtgLSAK0tHa2jMzmGlWuxsO6Wo5JHE=";

  private static RSAPrivateKey loadPrivateKey() {
    try {

      // Path path = Paths.get(Config.class.getClassLoader()
      // .getResource("rsa_private_key.txt").toURI());
      // System.out.println(path);
      // PrivateKey privateKey = PrivateKeyReader.fromPem(path, null);
      // // return null;
      // return (RSAPrivateKey) privateKey;
      // assertEquals("PKCS#8", privateKey.getFormat());
      PrivateKey key =
        new PrivateKeyReader(Config.class.getClassLoader().getResourceAsStream("rsa_private_key.txt"))
          .getPrivateKey();
      System.out.println(key);
      return (RSAPrivateKey) key;
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  public static final RSAPrivateKey RSA_PRIVATE_KEY = loadPrivateKey();

  // public static final byte[] PAY_KEY = Base64.getDecoder().decode("GlJxnvlfhz7nxIk1eNkEdngf+jDW8XGHxJiaQTuD9v8=");
  public static final byte[] PAY_KEY = MixinUtil.decrypt(RSA_PRIVATE_KEY, PIN_TOKEN, SESSION_ID);
}
