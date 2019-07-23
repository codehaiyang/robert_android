package cn.app.robert.utils;

/**
 * 字节工具类
 */
public class ByteUtils {

    public static String byteToStr(byte[] datas){
        StringBuffer sb = new StringBuffer();
        for(byte i : datas){
            sb.append("|");
            String str = String.format("%02X",i&0xFF);
            sb.append(str);
        }
        return sb.toString();
    };

    public static byte[] convertToBytes(int data, int length) {
        byte[] reslut = new byte[length];
        for(int i=0;i<length;i++) {
            reslut[i] = (byte)((data >> (8 * i))&  0xFF);
        }
        return reslut;

    }
}
