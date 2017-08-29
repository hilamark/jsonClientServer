package hila;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class Utils {

	public static String computeCheckSum(String data){
		String res = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(data.getBytes("UTF-8"));
			res = DatatypeConverter.printHexBinary(digest);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
