package xxtt.scan.lib;

public class Tools {
    // byte 转十六进制
    public static String Bytes2HexString(byte[] b, int size) {
	String ret = "";
	for (int i = 0; i < size; i++) {
	    String hex = Integer.toHexString(b[i] & 0xFF);
	    if (hex.length() == 1) {
		hex = "0" + hex;
	    }
	    ret += hex.toUpperCase();
	}
	return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
	byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
		.byteValue();
	_b0 = (byte) (_b0 << 4);
	byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
		.byteValue();
	byte ret = (byte) (_b0 ^ _b1);
	return ret;
    }

    // 十六进制转byte
    public static byte[] HexString2Bytes(String src) {
	int len = src.length() / 2;
	byte[] ret = new byte[len];
	byte[] tmp = src.getBytes();

	for (int i = 0; i < len; i++) {
	    ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
	}
	return ret;
    }
}
