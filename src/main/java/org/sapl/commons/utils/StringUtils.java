package org.sapl.commons.utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.UUID;

public class StringUtils {


    public static String getPossibleMsisdn(String source) {
        return getMsisdn(source, true);
    }

    public static String getValidMsisdn(String source) {
        return getMsisdn(source, false);
    }

    private static String getMsisdn(String source, boolean possible) {
        source = removeNonDigits(source);
        if (source == null || source.length() == 0) return null;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse("+" + source, "");

            if (possible && phoneUtil.isPossibleNumber(number)) return source;

            return phoneUtil.isValidNumber(number) ? source : null;

        } catch (Exception e) {
            //
        }
        return null;
    }

    /**
     * faster then regexp
     */
    public static String removeNonDigits(String text, char... accepted) {
        if (text == null) return null;
        int length = text.length();
        StringBuffer buffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if (Character.isDigit(ch)) {
                buffer.append(ch);
            }
            if (accepted != null && accepted.length > 0) {
                for (char a : accepted) {
                    if (a == ch) buffer.append(ch);
                }
            }
        }
        return buffer.toString();
    }


    public static String generateHash() {
        return sh1(UUID.randomUUID().toString());
    }

    public static String sh1(String src) {
        if (src == null) {
            return null;
        } else {
            try {
                MessageDigest nsae = MessageDigest.getInstance("SHA1");
                nsae.update(src.getBytes());
                byte[] output = nsae.digest();
                return bytesToHex(output);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public static String bytesToHex(byte[] b) {
        char[] hexDigit = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        StringBuffer buf = new StringBuffer();

        for (int j = 0; j < b.length; ++j) {
            buf.append(hexDigit[b[j] >> 4 & 15]);
            buf.append(hexDigit[b[j] & 15]);
        }

        return buf.toString();
    }

    public static String encryptDES(String value, String secretKey) throws Exception {
        byte[] utf8 = value.getBytes("UTF8");
        Cipher ecipher = Cipher.getInstance("DES");
        ecipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey.getBytes(), "DES"));
        byte[] enc = ecipher.doFinal(utf8);
        return new sun.misc.BASE64Encoder().encode(enc);
    }

    public static String decryptDES(String value, String secretKey) throws Exception {
        Cipher dcipher = Cipher.getInstance("DES");
        dcipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey.getBytes(), "DES"));
        byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(value);
        byte[] utf8 = dcipher.doFinal(dec);
        return new String(utf8, "UTF8");
    }


}
