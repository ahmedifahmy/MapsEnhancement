package com.test.flatmarkers;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.text.BasicTextEncryptor;

public class UrlSign {
    private static BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
    private static final String ENC_PRIVATE_KEY = "jkO2yt1NQYEVkGbW2nZUxMOVF4VM7K8GRjTjSH81LjQtOGWzUXCrKg==";
    private static final String ENC_CLIENT_ID = "qII3eN/PZSRacUzPekc/XYF9+KKLqKEG";
    private static String PRIVATE_KEY;
    private static String CLIENT_ID;
    private byte[] key;

    static {
        textEncryptor.setPassword("");
        PRIVATE_KEY = textEncryptor.decrypt("jkO2yt1NQYEVkGbW2nZUxMOVF4VM7K8GRjTjSH81LjQtOGWzUXCrKg==");
        CLIENT_ID = textEncryptor.decrypt("qII3eN/PZSRacUzPekc/XYF9+KKLqKEG");
    }

    public static String signUrl(String urlString) throws IOException, InvalidKeyException, NoSuchAlgorithmException, URISyntaxException {
        urlString = urlString + CLIENT_ID;
        URL url = new URL(urlString);
        UrlSign signer = new UrlSign(PRIVATE_KEY);
        String request = signer.signRequest(url.getPath(), url.getQuery());
        return url.getProtocol() + "://" + url.getHost() + request;
    }

    public UrlSign(String keyString) throws IOException {
        keyString = keyString.replace('-', '+');
        keyString = keyString.replace('_', '/');
        System.out.println("Key: " + keyString);
        this.key = Base64.decodeBase64(keyString.getBytes());
    }

    public String signRequest(String path, String query) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, URISyntaxException {
        String resource = path + '?' + query;
        SecretKeySpec sha1Key = new SecretKeySpec(this.key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(sha1Key);
        byte[] sigBytes = mac.doFinal(resource.getBytes());
        String signature = new String(Base64.encodeBase64(sigBytes));
        signature = signature.replace('+', '-');
        signature = signature.replace('/', '_');
        return resource + "&signature=" + signature;
    }
}
