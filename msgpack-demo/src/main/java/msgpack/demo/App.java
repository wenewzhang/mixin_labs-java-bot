/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package msgpack.demo;
import java.util.Base64;
import java.util.UUID;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePack.PackerConfig;
import org.msgpack.core.MessagePack.UnpackerConfig;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.ExtensionValue;
import org.msgpack.value.FloatValue;
import org.msgpack.value.IntegerValue;
import org.msgpack.value.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class App {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
        UUID btcUUID = UUID.fromString("c6d0c728-2624-429b-8e0d-d9d19b6592fa");
        try {

          MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
          packer.writePayload(asBytes(btcUUID));
          packer.close();
          byte[] packedData = packer.toByteArray();
          String encodeBtcUUID = Base64.getEncoder().encodeToString(packedData);
          System.out.println(encodeBtcUUID);
          MessageUnpacker unpacker2 = MessagePack.newDefaultUnpacker(packer.toByteArray());
          ByteBuffer out = ByteBuffer.wrap(new byte[16]);
          unpacker2.readPayload(out);
          printBytes(out);
          unpacker2.close();


          String btcEncode = "gaFBxBDG0McoJiRCm44N2dGbZZL6";
          byte[] encoded = Base64.getDecoder().decode(btcEncode);
          printBytes2(encoded);
          MessageUnpacker unpacker3 = MessagePack.newDefaultUnpacker(encoded);
          ByteBuffer out2 = ByteBuffer.wrap(new byte[16]);
          System.out.println(btcEncode);
          unpacker3.readPayload(out2);
          printBytes(out2);
          unpacker3.close();
          // xtDHKCYkQpuODdnRm2WS+g==

          // MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
          // packer.packString(btcUuid);
          // packer.close();
          // byte[] packedData = packer.toByteArray();
          // String encodeBtcUUID = Base64.getEncoder().encodeToString(packedData);
          // System.out.println(encodeBtcUUID);
          //2SRjNmQwYzcyOC0yNjI0LTQyOWItOGUwZC1kOWQxOWI2NTkyZmE=
          MessageBufferPacker packer2 = MessagePack.newDefaultBufferPacker();
          packer2
                  .packInt(1)
                  .packString("leo")
                  .packArrayHeader(2)
                  .packString("xxx-xxxx")
                  .packString("yyy-yyyy");
          packer2.close(); // Never forget to close (or flush) the buffer

          // Deserialize with MessageUnpacker
          MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packer2.toByteArray());
          int id = unpacker.unpackInt();             // 1
          String name = unpacker.unpackString();     // "leo"
          int numPhones = unpacker.unpackArrayHeader();  // 2
          String[] phones = new String[numPhones];
          for (int i = 0; i < numPhones; ++i) {
              phones[i] = unpacker.unpackString();   // phones = {"xxx-xxxx", "yyy-yyyy"}
          }
          unpacker.close();

          System.out.println(String.format("id:%d, name:%s, phone:[%s]", id, name, phones));

        } catch(Exception e) { e.printStackTrace(); }
    }
    public static UUID asUuid(byte[] bytes) {
      ByteBuffer bb = ByteBuffer.wrap(bytes);
      long firstLong = bb.getLong();
      long secondLong = bb.getLong();
      return new UUID(firstLong, secondLong);
    }

    public static byte[] asBytes(UUID uuid) {
      ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
      bb.putLong(uuid.getMostSignificantBits());
      bb.putLong(uuid.getLeastSignificantBits());
      return bb.array();
    }
    public static void printBytes(ByteBuffer bb) {
      // for (int i=0, len=bytes.length; i<len; i++) {
      //    // System.out.println(bytes[i]);// = Byte.parseByte(byteValues[i].trim());
      //    if ( bytes[i] > 0 ) {
      //      System.out.println(bytes[i]);
      //    } else { System.out.println(bytes[i]+256);}
      // }
      // ByteBuffer bb = ByteBuffer.wrap(bytes);
      bb.rewind();
      System.out.println("Byte Buffer");
      while (bb.hasRemaining())
        System.out.println(bb.position() + " -> " + bb.get());
    }
    public static void printBytes2(byte[] bytes) {
      for (int i=0, len=bytes.length; i<len; i++) {
         // System.out.println(bytes[i]);// = Byte.parseByte(byteValues[i].trim());
         if ( bytes[i] > 0 ) {
           System.out.println(bytes[i]);
         } else { System.out.println(bytes[i]+256);}
      }
      // ByteBuffer bb = ByteBuffer.wrap(bytes);
      // bb.rewind();
      // System.out.println("Byte Buffer");
      // while (bb.hasRemaining())
      //   System.out.println(bb.position() + " -> " + bb.get());
    }
}
