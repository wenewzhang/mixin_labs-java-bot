/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package mixin_labs.java.bot;
import mixin.java.sdk.MixinBot;
import mixin.java.sdk.MixinUtil;
import mixin.java.sdk.Category;
import mixin.java.sdk.Action;
import mixin.java.sdk.Library;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class App {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
        Library libMixin = new Library();
        if (libMixin.someLibraryMethod())  System.out.println("true"); else System.out.println("false");
        System.out.println(Config.RSA_PRIVATE_KEY);
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
            Action action = Action.parseFrom(obj);
            Category category = Category.parseFrom(obj);
            if (action == Action.CREATE_MESSAGE && category != null) {
              switch (category) {
                case PLAIN_TEXT:
                case PLAIN_STICKER:
                case PLAIN_IMAGE:
                case PLAIN_CONTACT:
                  // 确认收到的消息，使得对方界面的消息状态，由单钩变双钩
                  String messageId =
                    obj.get("data").getAsJsonObject().get("message_id").getAsString();
                  MixinBot.sendMessageAck(webSocket, messageId);

                  // 回复给对方消息
                  String conversationId =
                    obj.get("data").getAsJsonObject().get("conversation_id").getAsString();
                  String userId =
                    obj.get("data").getAsJsonObject().get("user_id").getAsString();
                  System.out.println("conversationId = " + conversationId);
                  System.out.println("userId = " + userId);

                  MixinBot.sendText(
                    webSocket,
                    conversationId,
                    userId,
                    "很高兴见到你！");

                  MixinBot.sendSticker(
                    webSocket,
                    conversationId,
                    userId,
                    "eyJhbGJ1bV9pZCI6IjM2ZTk5NzdjLTJiYWItNDNjYS1hMmI2LTdlMDFmNWViNjhkZSIsIm5hbWUiOiJpbGx1c2lvbiJ9");

                  MixinBot.sendContact(
                    webSocket,
                    conversationId,
                    userId,
                    "0c21b607-5e5b-461b-963f-95708346c21d");

                  // MixinBot.transferTo(
                  //   "965e5c6e-434c-3fa9-b780-c50f43cd955c", // CNB
                  //   userId,
                  //   1.0
                  // );

                  break;
                default:
                  System.out.println("Category: " + category);
              }
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
      }, Config.RSA_PRIVATE_KEY, Config.APP_ID, Config.SESSION_ID);
    }
}
