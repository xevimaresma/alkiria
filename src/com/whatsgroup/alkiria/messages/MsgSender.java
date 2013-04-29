/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.messages;

import com.whatsgroup.alkiria.entities.Encryption;
import java.io.*;
import java.net.*;

/**
 *
 * @author PC
 */
public class MsgSender {
    private String missatge;    
    private String clauEncriptacio;
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
    
    public void enviaMsg() throws Exception {        
        Encryption encripta=new Encryption();
        encripta.setClau(this.clauEncriptacio);
        encripta.encrypt(this.missatge);        
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];   
        sendData=encripta.getMsgEncriptat();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + modifiedSentence);
        clientSocket.close();
    }
}
