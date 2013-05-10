/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.entities;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 *
 * @author PC
 */

/* La idea de funcionament és la següent:

* 1. Per un costat, haurem de veure d'on treiem la clau. Podria ser el token mateix.
* 2. Llavors, s'instancia un objecte Encryption, i se li indica una clau (via constructor o setClau)
* 3. En cas de no tenir clau, hauríem de mirar si fem un throw exception o donem un missatge.
* 4. Llavors, es crida a obj.encrypt i obj.decrypt per encriptar i desencriptar.
* 4a. Aquests dos mètodes els deixo sense retornar (publiquen el resultat a un atribut) per poder retornar excepcions o missatges sense problemes de tipus (byte, etc...)
* 5. Els mètodes getMsgEncriptat i getMsgDesencriptat tenen els valors de l'encrypt i el decrypt.
* 
* Segurament necessita una mica més d'estudi, però ens pot valer per anar provant. De moment deixo el main per fer proves.
*/
public class Encryption {
    
    public static final String CLAU = "Hola";
    private String clauEncriptacio="",msgDesencriptat="";
    private byte[] msgEncriptat;
        
    public Encryption () {        
    }
    
    public Encryption(String clau) {
        this.setClau(clau);
    }

    public void setClau(String clau) {
        this.clauEncriptacio=clau;
    }
    
    public byte[] getMsgEncriptat() {
        return this.msgEncriptat;
    }
    
    public String getMsgDesencriptat() {
        return this.msgDesencriptat;
    }
    
    public void encrypt(String message) throws Exception {          
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(clauEncriptacio.getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
        for (int j = 0, k = 16; j < 8;) {
                keyBytes[k++] = keyBytes[j++];
        }
        DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
        SecretKey key = factory.generateSecret(keySpec);
        final Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        message+="||END||";
        
        final byte[] plainTextBytes = message.getBytes();
        final byte[] cipherText = cipher.doFinal(plainTextBytes);
        this.msgEncriptat=cipherText;        
    }
    
    public void encrypt(byte[] message) throws Exception {          
        final MessageDigest md = MessageDigest.getInstance("md5");
        System.out.println(clauEncriptacio);
        final byte[] digestOfPassword = md.digest(clauEncriptacio.getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
        for (int j = 0, k = 16; j < 8;) {
                keyBytes[k++] = keyBytes[j++];
        }

        DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
        SecretKey key = factory.generateSecret(keySpec);

        final Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, key);        
        final byte[] cipherText = cipher.doFinal(message);
        this.msgEncriptat=cipherText;        
    }

    public void decrypt(byte[] message) throws Exception {    	
        final MessageDigest md = MessageDigest.getInstance("md5");
        
    	final byte[] digestOfPassword = md.digest(clauEncriptacio.getBytes("UTF-8"));
    	final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
    	for (int j = 0, k = 16; j < 8;) {
    		keyBytes[k++] = keyBytes[j++];
    	}            	
        DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
        SecretKey key = factory.generateSecret(keySpec);
        final Cipher decipher = Cipher.getInstance("DESede/ECB/NoPadding");
        decipher.init(Cipher.DECRYPT_MODE, key);    	
    	final byte[] plainText = decipher.doFinal(message);        
        this.msgDesencriptat=new String(plainText, "UTF-8");    	
        System.out.println(this.clauEncriptacio+" i "+this.msgDesencriptat);
    }
    
    public byte[] decryptBytes(byte[] message) throws Exception {    	
        final MessageDigest md = MessageDigest.getInstance("md5");        
    	final byte[] digestOfPassword = md.digest(clauEncriptacio.getBytes("utf-8"));
    	final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
    	for (int j = 0, k = 16; j < 8;) {
    		keyBytes[k++] = keyBytes[j++];
    	}       
        DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
        SecretKey key = factory.generateSecret(keySpec);
        final Cipher decipher = Cipher.getInstance("DESede/ECB/NoPadding");
        decipher.init(Cipher.DECRYPT_MODE, key);
    	return decipher.doFinal(message);
    }
    public String toString(){
        return "Clau: " + this.clauEncriptacio + " Missatge: " + this.msgDesencriptat ;
    }    
    
}
