package mixin_labs.java.bot;

import mixin.java.sdk.MixinUtil;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;

public class Config {
  // 修改为你自己的 APP_ID
  public static final String CLIENT_ID = "a1ce2967--417d-bf12-c86571e4eefa";
  // 修改为在 developers.mixin.one/dashboard 上获取到的 SECRET
  public static final String CLIENT_SECRET = "xxxxxxxd24eeec1c4ebb6c634fd25a7b9057ee6d5939cca9b6b9fc15f4d1f";
  // 修改为在 developers.mixin.one/dashboard 上获取到的 PIN
  public static final String PIN = "33233";
  // 修改为在 developers.mixin.one/dashboard 上获取到的 SESSION_ID
  public static final String SESSION_ID = "23sdfsdf-48ff-4df2-898d-e9b318afae35";
  // 修改为在 developers.mixin.one/dashboard 上获取到的 PIN TOKEN
  public static final String PIN_TOKEN = "weqreereer/qP7LOcpfviqSfWfABdIKyZGLnWXFMrVCHpChIkBRGRAcsUguni0OoNsShddPVL3qoD5fxbF5dRUiRv14urH1Pmdl6zIZdCH159QMr5wLmmSHSGu2AihNkUHUo3bAJsrvOW0nke5y6R5YE/pNNfo=";

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

  public static final byte[] PAY_KEY = MixinUtil.decrypt(RSA_PRIVATE_KEY, PIN_TOKEN, SESSION_ID);
}
