package cn.nukkit.utils;

import cn.nukkit.entity.Entity;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Binary {

    //Triad: {0x00,0x00,0x01}<=>1
    public static int readTriad(byte[] bytes) {
        return bytes[0] << 16 | bytes[1] << 8 | bytes[2];
    }

    public static byte[] writeTriad(int value) {
        return new byte[]{
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)};
    }

    //LTriad: {0x01,0x00,0x00}<=>1
    public static int readLTriad(byte[] bytes) {
        return bytes[0] | bytes[1] << 8 | bytes[2] << 16;
    }

    public static byte[] writeLTriad(int value) {
        return new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF)};
    }

    public static byte[] writeMetadata(Map<Integer, Object[]> data) {
        byte[] m = new byte[0];
        for (Map.Entry<Integer, Object[]> entry : data.entrySet()) {
            int bottom = entry.getKey();
            Object[] d = entry.getValue();
            appendBytes(m, new byte[]{(byte) ((((int) d[0] << 5) | (bottom & 0x1F)) & 0xff)});
            switch ((int) d[0]) {
                case Entity.DATA_TYPE_BYTE:
                    appendBytes(m, new byte[]{(byte) d[1]});
                    break;
                case Entity.DATA_TYPE_SHORT:
                    appendBytes(m, writeLShort((short) d[1]));
                    break;
                case Entity.DATA_TYPE_INT:
                    appendBytes(m, writeLInt((int) d[1]));
                    break;
                case Entity.DATA_TYPE_FLOAT:
                    appendBytes(m, writeLFloat((float) d[1]));
                    break;
                case Entity.DATA_TYPE_STRING:
                    String s = (String) d[1];
                    appendBytes(m, writeLShort((short) (s.getBytes(StandardCharsets.UTF_8).length)), s.getBytes(StandardCharsets.UTF_8));
                    break;
                case Entity.DATA_TYPE_SLOT:
                    Object[] o = (Object[]) d[1];
                    appendBytes(m,
                            writeLShort((short) o[0]),
                            new byte[]{(byte) o[1]},
                            writeLShort((short) o[2])
                    );
                    break;
                case Entity.DATA_TYPE_POS:
                    o = (Object[]) d[1];
                    appendBytes(m,
                            writeLInt((int) o[0]),
                            writeLInt((int) o[1]),
                            writeLInt((int) o[2])
                    );
                    break;
                case Entity.DATA_TYPE_LONG:
                    appendBytes(m, writeLLong((long) d[1]));
                    break;
            }
        }

        appendBytes(m, new byte[]{0x7f});
        return m;
    }

    public Map<Integer, Object[]> readMetadata(byte[] payload) {
        int offset = 0;
        Map<Integer, Object[]> m = new HashMap<>();
        byte b = payload[offset];
        ++offset;
        while (b != 0x7f && offset < payload.length) {
            int bottom = b & 0x1f;
            int type = b >> 5;
            Object r = null;
            Object[] rr = null;
            switch (type) {
                case Entity.DATA_TYPE_BYTE:
                    r = payload[offset];
                    ++offset;
                    break;
                case Entity.DATA_TYPE_SHORT:
                    r = readLShort(subBytes(payload, offset, 2));
                    offset += 2;
                    break;
                case Entity.DATA_TYPE_INT:
                    r = readLInt(subBytes(payload, offset, 4));
                    offset += 4;
                    break;
                case Entity.DATA_TYPE_FLOAT:
                    r = readLFloat(subBytes(payload, offset, 4));
                    offset += 4;
                    break;
                case Entity.DATA_TYPE_STRING:
                    int len = readLShort(subBytes(payload, offset, 2));
                    offset += 2;
                    r = subBytes(payload, offset, len);
                    offset += len;
                    break;
                case Entity.DATA_TYPE_SLOT:
                    rr = new Object[3];
                    rr[0] = readLShort(subBytes(payload, offset, 2));
                    offset += 2;
                    rr[1] = payload[offset];
                    ++offset;
                    rr[2] = readLShort(subBytes(payload, offset, 2));
                    offset += 2;
                    break;
                case Entity.DATA_TYPE_POS:
                    rr = new Object[3];
                    for (int i = 0; i < 3; ++i) {
                        rr[i] = readLInt(subBytes(payload, offset, 4));
                        offset += 4;
                    }
                    break;
                case Entity.DATA_TYPE_LONG:
                    r = readLLong(subBytes(payload, offset, 4));
                    offset += 8;
                    break;
                default:
                    return new HashMap<>();
            }

            if (r != null) {
                m.put(bottom, new Object[]{type, r});
            } else if (rr != null) {
                m.put(bottom, new Object[]{type, rr});
            }
            b = payload[offset];
            ++offset;
        }

        return m;
    }

    public static boolean readBool(byte b) {
        return b == 0;
    }

    public static byte writeBool(boolean b) {
        return (byte) (b ? 0x01 : 0x00);
    }

    public static int readByte(byte b) {
        return readByte(b, true);
    }

    public static int readByte(byte b, boolean signed) {
        return signed ? b : b & 0xFF;
    }

    public static byte writeByte(byte b) {
        return b;
    }

    public static short readShort(byte[] bytes) {
        return (short) (((bytes[0] << 8) & 0x0000ff00) | (bytes[1] & 0x000000ff));
    }

    public static short readSignedShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }

    public static byte[] writeShort(short s) {
        return ByteBuffer.allocate(2).putShort(s).array();
    }

    public static byte[] writeUnsignedShort(int s) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put((byte) ((s >> 8) & 0xff));
        bb.put((byte) (s & 0xff));
        return bb.array();
    }

    public static int readLShort(byte[] bytes) {
        return readShort(reserveBytes(bytes));
    }

    public static short readSignedLShort(byte[] bytes) {
        return readSignedShort(reserveBytes(bytes));
    }

    public static byte[] writeLShort(short s) {
        return reserveBytes(writeShort(s));
    }

    public static byte[] writeUnsignedLShort(int s) {
        return reserveBytes(writeUnsignedShort(s));
    }

    public static int readInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static byte[] writeInt(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    public static int readLInt(byte[] bytes) {
        return readInt(reserveBytes(bytes));
    }

    public static byte[] writeLInt(int i) {
        return reserveBytes(writeInt(i));
    }

    public static float readFloat(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public static byte[] writeFloat(float f) {
        return ByteBuffer.allocate(4).putFloat(f).array();
    }

    public static float readLFloat(byte[] bytes) {
        return readFloat(reserveBytes(bytes));
    }

    public static byte[] writeLFloat(float f) {
        return reserveBytes(writeFloat(f));
    }

    public static double readDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static byte[] writeDouble(double d) {
        return ByteBuffer.allocate(8).putDouble(d).array();
    }

    public static double readLDouble(byte[] bytes) {
        return readDouble(reserveBytes(bytes));
    }

    public static byte[] writeLDouble(double d) {
        return reserveBytes(writeDouble(d));
    }

    public static long readLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static byte[] writeLong(long l) {
        return ByteBuffer.allocate(8).putLong(l).array();
    }

    public static long readLLong(byte[] bytes) {
        return readLong(reserveBytes(bytes));
    }

    public static byte[] writeLLong(long l) {
        return reserveBytes(writeLong(l));
    }

    public static byte[] reserveBytes(byte[] bytes) {
        byte[] newBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            newBytes[bytes.length - 1 - i] = bytes[i];
        }
        return newBytes;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            //stringBuilder.append(hv).append(" ");
        }
        return stringBuilder.toString().toUpperCase();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        String str = "0123456789ABCDEF";
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (((byte) str.indexOf(hexChars[pos]) << 4) | ((byte) str.indexOf(hexChars[pos + 1])));
        }
        return d;
    }

    public static byte[] subBytes(byte[] bytes, int start, int length) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.position(start);
        byte[] bytes2 = new byte[length];
        bb.get(bytes2);
        return bytes2;
    }

    public static byte[] subBytes(byte[] bytes, int start) {
        return subBytes(bytes, start, bytes.length - start);
    }

    public static byte[][] splitBytes(byte[] bytes, int chunkSize) {
        byte[][] splits = new byte[1024][chunkSize];
        int chunks = 0;
        for (int i = 0; i < bytes.length; i += chunkSize) {
            if ((bytes.length - i) > chunkSize) {
                splits[chunks] = Arrays.copyOfRange(bytes, i, i + chunkSize);
            } else {
                splits[chunks] = Arrays.copyOfRange(bytes, i, bytes.length);
            }
            chunks++;
        }

        splits = Arrays.copyOf(splits, chunks);

        return splits;
    }

    public static byte[] appendBytes(byte byte1, byte[]... bytes2) {
        int length = 1;
        for (byte[] bytes : bytes2) {
            length += bytes.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(byte1);
        for (byte[] bytes : bytes2) {
            buffer.put(bytes);
        }
        return buffer.array();
    }

    public static byte[] appendBytes(byte[] bytes1, byte[]... bytes2) {
        int length = bytes1.length;
        for (byte[] bytes : bytes2) {
            length += bytes.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(bytes1);
        for (byte[] bytes : bytes2) {
            buffer.put(bytes);
        }
        return buffer.array();
    }


}
