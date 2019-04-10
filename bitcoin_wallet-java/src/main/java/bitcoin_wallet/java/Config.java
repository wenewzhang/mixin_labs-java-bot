package bitcoin_wallet.java;

import mixin.java.sdk.MixinUtil;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import mixin.java.sdk.PrivateKeyReader;
public class Config {

  public static final String CLIENT_ID     = "a1ce2967-a534-417d-bf12-c86571e4eefa";
  public static final String CLIENT_SECRET = "788f2dac7db35d423ec08eb11dac87e1f242add1db090a2cbf04762b502f1cdf";
  public static final String PIN           = "163085";
  public static final String SESSION_ID    = "d8c2f4e4-b045-4557-ad9a-9176649d145a";
  public static final String PIN_TOKEN     = "exBo8Y5Kfeu2TAQ8PoxbOgLTkUtI8W0Cl+t13gtILU39zxyJQxrD10Z/6ZIqv3EuyfTeoY0a0Xs41OvCeXjKf8wEK5zGbLZEkMdMgekO6zNCam9i03PJAI6LnArAf4xAnAQ8jeo+6gZyMzdy6pHq+/fUfbMdpxRlml8zfBBkKg0=";

  private static RSAPrivateKey loadPrivateKey() {
    try {

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
  public static final byte[] PAY_KEY = MixinUtil.decrypt(RSA_PRIVATE_KEY, PIN_TOKEN, SESSION_ID);
}
