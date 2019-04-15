package bitcoin_wallet.java;

import mixin.java.sdk.MixinUtil;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import mixin.java.sdk.PrivateKeyReader;
public class Config {

  public static final String CLIENT_ID     = "a1ce2967-a534-417d-bf12-c86571e4eefa";
  public static final String CLIENT_SECRET = "788f2dac7db35d423ec08eb11dac87e1f242add1db090a2cbf04762b502f1cdf";
  public static final String PIN           = "682444";
  public static final String SESSION_ID    = "bebe1185-d7b1-4135-b6f5-58a7090a6414";
  public static final String PIN_TOKEN     = "CUPiFUMlaU3KoC4F+CZIKxuiSY5rSJGmIhMnFWJIhWx2+Mk8m0v8UVXvouY18ryGhGYfaMw0vre2LFwuCKAE9mJ2yGH3F1Bf1wMMVTqTeKxgN+vBqUdoQfqKEeCzslwS7cK6ZybdHj48anmF/hffE/MxlkfFX4I4vN/cH+bctNI=";

  private static RSAPrivateKey loadPrivateKey() {
    try {

      PrivateKey key =
        new PrivateKeyReader(Config.class.getClassLoader().getResourceAsStream("rsa_private_key.txt"))
          .getPrivateKey();
      // System.out.println(key);
      return (RSAPrivateKey) key;
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  public static final RSAPrivateKey RSA_PRIVATE_KEY = loadPrivateKey();
  public static final byte[] PAY_KEY = MixinUtil.decrypt(RSA_PRIVATE_KEY, PIN_TOKEN, SESSION_ID);
}
