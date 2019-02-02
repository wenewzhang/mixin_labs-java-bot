/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package mixin_labs.java.bot;
import mixin.java.sdk.MixinBot;
import mixin.java.sdk.MixinUtil;
import mixin.java.sdk.MIXIN_Category;
import mixin.java.sdk.MIXIN_Action;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
// import java.util.Base64;
import org.apache.commons.codec.binary.Base64;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class App {

    public static void main(String[] args) {
        MixinBot.connectToRemoteMixin(new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
          System.out.println("[onOpen !!!]");
          System.out.println("request header:" + response.request().headers());
          System.out.println("response header:" + response.headers());
          System.out.println("response:" + response);

          // 请求获取所有 pending 的消息
          MixinBot.sendListPendingMessages(webSocket);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
          System.out.println("[onMessage !!!]");
          System.out.println("text: " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
          try {
            System.out.println("[onMessage !!!]");
            String msgIn = MixinUtil.bytesToJsonStr(bytes);
            System.out.println("json: " + msgIn);
            JsonObject obj = new JsonParser().parse(msgIn).getAsJsonObject();
            MIXIN_Action action = MIXIN_Action.parseFrom(obj);
            System.out.println(action);
            MIXIN_Category category = MIXIN_Category.parseFrom(obj);
            // System.out.println(category);
            if (action == MIXIN_Action.CREATE_MESSAGE && obj.get("data") != null &&
                category != null ) {
              String userId;
              String messageId = obj.get("data").getAsJsonObject().get("message_id").getAsString();
              MixinBot.sendMessageAck(webSocket, messageId);
              switch (category) {
                case PLAIN_TEXT:
                    String conversationId =
                      obj.get("data").getAsJsonObject().get("conversation_id").getAsString();
                    userId =
                      obj.get("data").getAsJsonObject().get("user_id").getAsString();
                    byte[] msgData = Base64.decodeBase64(obj.get("data").getAsJsonObject().get("data").getAsString());
                    MixinBot.sendText(webSocket,conversationId,userId,new String(msgData,"UTF-8"));
                    break;
                case SYSTEM_ACCOUNT_SNAPSHOT:
                    userId =
                      obj.get("data").getAsJsonObject().get("user_id").getAsString();
                    byte[] JsData = Base64.decodeBase64(obj.get("data").getAsJsonObject().get("data").getAsString());
                    String JsStr = new String(JsData);
                    System.out.println("SYSTEM_ACCOUNT_SNAPSHOT json: " + JsStr);
                    JsonObject jsObj = new JsonParser().parse(JsStr).getAsJsonObject();
                    System.out.println(jsObj.get("amount").getAsFloat());
                    System.out.println(jsObj.get("asset_id").getAsString());
                    // JsonObject JsObj = new JsonParser().parse(JsStr).getAsJsonObject();
                    // System.out.println(JsObj.get("asset_id").getAsString());
                    // System.out.println(JsObj.get("amount").getAsString());
                    if (jsObj.get("amount").getAsFloat() > 0) {
                      String aesKey = new String (Base64.encodeBase64(Config.PAY_KEY));
                      System.out.println(aesKey);
                      System.out.println(Config.PAY_KEY.length);
                      String encryptPin = MixinUtil.encryptPayKey(Config.PIN,Config.PAY_KEY);
                      MixinBot.transfer(
                          jsObj.get("asset_id").getAsString(),
                          jsObj.get("opponent_id").getAsString(),
                          jsObj.get("amount").getAsFloat(),
                          encryptPin,
                          Config.RSA_PRIVATE_KEY,
                          Config.CLIENT_ID,
                          Config.SESSION_ID
                      );
                    }
                    break;
                default:
                    System.out.println("Category: " + category);
              }
              // switch (category) {
              //   case PLAIN_TEXT:
              //   case PLAIN_STICKER:
              //   case PLAIN_IMAGE:
              //   case PLAIN_CONTACT:
              //     // 确认收到的消息，使得对方界面的消息状态，由单钩变双钩
              //     String messageId =
              //       obj.get("data").getAsJsonObject().get("message_id").getAsString();
              //     MixinBot.sendMessageAck(webSocket, messageId);
              //
              //     // 回复给对方消息
              //     String conversationId =
              //       obj.get("data").getAsJsonObject().get("conversation_id").getAsString();
              //     String userId =
              //       obj.get("data").getAsJsonObject().get("user_id").getAsString();
              //     System.out.println("conversationId = " + conversationId);
              //     System.out.println("userId = " + userId);
              //
              //     MixinBot.sendText(
              //       webSocket,
              //       conversationId,
              //       userId,
              //       "很高兴见到你！");
              //
              //     MixinBot.sendSticker(
              //       webSocket,
              //       conversationId,
              //       userId,
              //       "eyJhbGJ1bV9pZCI6IjM2ZTk5NzdjLTJiYWItNDNjYS1hMmI2LTdlMDFmNWViNjhkZSIsIm5hbWUiOiJpbGx1c2lvbiJ9");
              //
              //     MixinBot.sendContact(
              //       webSocket,
              //       conversationId,
              //       userId,
              //       "0c21b607-5e5b-461b-963f-95708346c21d");
              //
              //     // MixinBot.transferTo(
              //     //   "965e5c6e-434c-3fa9-b780-c50f43cd955c", // CNB
              //     //   userId,
              //     //   1.0
              //     // );
              //
              //     break;
              //   default:
              //     System.out.println("Category: " + category);
              // }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
          System.out.println("[onClosing !!!]");
          System.out.println("code: " + code);
          System.out.println("reason: " + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
          System.out.println("[onClosed !!!]");
          System.out.println("code: " + code);
          System.out.println("reason: " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
          System.out.println("[onFailure !!!]");
          System.out.println("throwable: " + t);
          System.out.println("response: " + response);
        }
      }, Config.RSA_PRIVATE_KEY, Config.CLIENT_ID, Config.SESSION_ID);
    }
}
