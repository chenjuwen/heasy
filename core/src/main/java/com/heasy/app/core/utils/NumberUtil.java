package com.heasy.app.core.utils;

/**
 * 高位在前，低位在后
 */
public class NumberUtil {
	public static byte[] long8ToByteArray(long value) {
	    byte[] arr = new byte[8];  
	    arr[0] = (byte) ((value >> 56) & 0xFF); 
	    arr[1] = (byte) ((value >> 48) & 0xFF); 
	    arr[2] = (byte) ((value >> 40) & 0xFF); 
	    arr[3] = (byte) ((value >> 32) & 0xFF); 
	    arr[4] = (byte) ((value >> 24) & 0xFF);  
	    arr[5] = (byte) ((value >> 16) & 0xFF);  
	    arr[6] = (byte) ((value >> 8) & 0xFF);    
	    arr[7] = (byte) (value & 0xFF);       
	    return arr;  
	}
	
	public static byte[] int4ToByteArray(int value) {
	    byte[] arr = new byte[4];
	    arr[0] = (byte) ((value >> 24) & 0xFF);  
	    arr[1] = (byte) ((value >> 16) & 0xFF);  
	    arr[2] = (byte) ((value >> 8) & 0xFF);    
	    arr[3] = (byte) (value & 0xFF);       
	    return arr;  
	}
	
	public static byte[] int2ToByteArray(int value){
	    byte[] arr = new byte[2];  
	    arr[0] = (byte)((value >> 8) & 0xFF);
	    arr[1] = (byte)(value & 0xFF);
	    return arr;
	}
	
	public static int byteArrayToInt8(byte[] arr) {  
	    int value;    
	    value = (int) ( 
	    		((arr[0] & 0xFF) << 56)
	    		|((arr[1] & 0xFF) << 48)
	    		|((arr[2] & 0xFF) << 40)
	    		|((arr[3] & 0xFF) << 32)
	    		|((arr[4] & 0xFF) << 24)  
	            |((arr[5] & 0xFF) << 16)  
	            |((arr[6] & 0xFF) << 8)  
	            |(arr[7] & 0xFF));  
	    return value;  
	}
	
	public static int byteArrayToInt4(byte[] arr) {  
	    int value;    
	    value = (int) ( ((arr[0] & 0xFF) << 24)  
	            |((arr[1] & 0xFF) << 16)  
	            |((arr[2] & 0xFF) << 8)  
	            |(arr[3] & 0xFF));  
	    return value;  
	}
	
	public static int byteArrayToInt2(byte[] arr) {  
	    int value;    
	    value = (int) ( ((arr[0] & 0xFF) << 8)  
	            |(arr[1] & 0xFF));  
	    return value;  
	}

	/**
	 * 十六进制异或运算
	 * @param hexValue1 十六进制值一
	 * @param hexValue2 十六进制值二
	 * @return
	 */
	public static String hexXOR(String hexValue1, String hexValue2){
		long n1 = Long.parseLong(hexValue1, 16);
		long n2 = Long.parseLong(hexValue2, 16);
		long n3 = n1 ^ n2;
		String hexResult = Long.toHexString (n3);
		return hexResult;
	}
	
}
