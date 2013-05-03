/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.messages;

import com.whatsgroup.alkiria.entities.Encryption;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

/**
 *
 * @author PC
 */
public class MsgSender {
    public static final int TIPUS_ENVIA_MSG = 2;
    private String missatge;    
    private String clauEncriptacio;    
    private String mailDesti;
    
    private int port=9876;
    
    public MsgSender(String missatgeRep) {
        this.missatge=missatgeRep;
    }
    
    public MsgSender(String missatgeRep, String clauEncripta) {
        this.missatge=missatgeRep;
        this.clauEncriptacio=clauEncripta;
    }
    
    public void setMissatge(String missatgeRep) {
        this.missatge=missatgeRep;
    }
    
    public String getMissatge() {
        return this.missatge;
    }
    
    public void setClau(String clauEncripta) {
        this.clauEncriptacio=clauEncripta;
    }
    
    public byte[] enviaMsg() throws Exception {        
        Encryption encripta=new Encryption();
        encripta.setClau(this.clauEncriptacio);
        encripta.encrypt(this.missatge);        
        
        byte[] sendData = new byte[64];
        byte[] receiveData = new byte[1024];   
        sendData=encripta.getMsgEncriptat();
        
        byte[] valors = new byte[196];
        String token="1234";
        String destinatari="girona@gmail.com";
        ByteBuffer buffer = ByteBuffer.wrap(valors);
        buffer.putInt(TIPUS_ENVIA_MSG);        
        buffer.put(token.getBytes());
        buffer.position(68);
        buffer.put(destinatari.getBytes());
        buffer.position(132);
        buffer.put(sendData);
        
        return buffer.array();
        //System.out.println(new String(buffer));       
        
    }
}
