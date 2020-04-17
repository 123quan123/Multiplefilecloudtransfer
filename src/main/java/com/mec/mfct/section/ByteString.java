package com.mec.mfct.section;

/**
 * 
 * <ol>
 * 功能：工具类，用来字节与字符串即数字的转换
 * </ol>
 * @author Quan
 * @date 2020/03/06
 * @version 0.0.1
 */
public class ByteString {
    public static final String HEX_STR = "0123456789ABCDEF";

    public ByteString() {
    }
    
    public static String byteToString(byte value) {
        return "" + HEX_STR.charAt((value >> 4) & 0x0F)
            + HEX_STR.charAt(value & 0x0F);
    }
    
    public static String bytesToString(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        
        for (int i = 0; i < bytes.length; i++) {
            buffer.append(byteToString(bytes[i]));
        }
        
        return buffer.toString();
    }
    
    public static String bytesToString(byte[] bytes, int maxColumn) {
        StringBuffer buffer = new StringBuffer();
        
        for (int i = 0; i < bytes.length; i++) {
            if ((i+1) % maxColumn == 0) {
                buffer.append("\n");
            }
            buffer.append(byteToString(bytes[i]));
        }
        
        return buffer.toString();
    }
    
    public static byte[] intToBytes(int value) {
        int len = 4;
        byte[] result = new byte[len];
        
        for (int i = 0; i < len; i++) {
            result[len - 1 - i] = (byte) ((value >> (i*8)) & 0xFF);
        }
        return result;
    }
    
    public static int bytesToInt(byte[] bytes) {
        int result = 0;
        
        int mod = 0xFF;
        for (int i = 0; i < 4; i++) {
            result |= (((bytes[3 - i]) << (i*8)) & (mod << (i*8)));
        }
        return result;
    }
    
    public static byte[] longToBytes(long value) {
        int len = 8;
        byte[] result = new byte[len];
        
        for (int i = 0; i < len; i++) {
            result[len - 1 - i] = (byte) ((value >> (i*8)) & 0xFF);
        }
        return result;
    }
    
    public static long bytesToLong(byte[] bytes) {
        int result = 0;
        
        long mod = 0xFF;
        for (int i = 0; i < 4; i++) {
            result |= (((bytes[7 - i]) << (i*8)) & (mod << (i*8)));
        }
        return result;
    }
    
    public static void setBytesAt(byte[] target,int offset, byte[] source) {
        for (int i = 0; i < source.length; i++) {
            target[i + offset] = source[i];
        }
    }

    public static byte[] getBytesAt(byte[] source, int begin, int len) {
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = source[i + begin];
        }
        return result;
    }
    
    @SuppressWarnings("null")
    public static byte StringToByte(String str) {
        int len = str.length();
        if (str == null || len > 2) {
            return (Byte) null;
        }
        str = (len == 1 ? ("0" + str) : str);
        
        byte result = 0;
        result += (byte)(HEX_STR.indexOf(str.substring(0, 1)) << 4);
        result += (byte)(HEX_STR.indexOf(str.substring(1, 1+1)));
        return result;
    }
    
    public static byte[] StringToBytes(String str) {
        int len = str.length();
        if (len % 2 != 0) {
            return null;
        }
        
        byte[] result = new byte[len/2];
        
        for (int i = 0; i < len/2; i += 2) {
            result[i] = StringToByte(str.substring(i, i+2));
        }
        return result;
    }
    
    public static final String calculateCapacity(long size) {
        if (size < 1024) {
            return String.valueOf((int)(size) + 100).substring(1) + "B";
        }
        if (size < (1 << 20)) {
            return String.valueOf(size >> 10) + "." 
                    + String.valueOf((int)((size & 0x03FF) / 1024.0 * 100)+ 100).substring(1) + "KB";
        }
        if (size < (1 << 30)) {
            return String.valueOf(size >> 20) + "." 
                    + String.valueOf((int)((size & 0xFFFFF) / (1024.0*1024.0)* 100) + 100).substring(1) + "MB";
        }
        if (size < (1L << 40)) {
            return String.valueOf(size >> 30) + "." 
                    + String.valueOf((int)((size & 0x3FFFFFFF) / (1024.0*1024.0*1024.0) * 100) + 100).substring(1) + "GB";
        }
        return null;
    }
}
