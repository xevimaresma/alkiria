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
    public static final int TIPUS_DEMANA_MSG = 3;
    public static final int TIPUS_LLIURA_MSG = 4;
    private String missatge;   
    private String clauEncriptacio;    
    private String mailDesti;
    
    private int port=9876;
    
    public MsgSender(String missatgeRep) {
        this.missatge=missatgeRep;
        this.arreglaCadena();
    }
    
    public MsgSender(String missatgeRep, String clauEncripta) {
        this.missatge=missatgeRep;
        this.clauEncriptacio=clauEncripta;
        this.arreglaCadena();
    }
    
    public void setMissatge(String missatgeRep) {
        this.missatge=missatgeRep;
        this.arreglaCadena();
    }
    
    public String getMissatge() {
        this.arreglaCadena();
        return this.missatge;
    }
    
    public void setClau(String clauEncripta) {
        this.clauEncriptacio=clauEncripta;
    }
    
    public byte[] enviaMsg() throws Exception {        
        //String token="518bd9ed53f1b46ca694ddb5";
        String token="515dd85856861ee247ccf15a";
        String destinatari="prova";
        int tipusMissatge=TIPUS_ENVIA_MSG;
        return enviaMsg(token,destinatari,tipusMissatge);
    }
    
    public byte[] enviaMsg(String token, String destinatari, int tipusMissatge) throws Exception {                        
        byte[] sendData = new byte[64];          
                        
        Encryption encripta=new Encryption();
        encripta.setClau(this.clauEncriptacio);
        encripta.encrypt(this.missatge);
        this.arreglaCadena();            
        System.out.println(encripta.toString());
        sendData=encripta.getMsgEncriptat();                
        
        byte[] valors = new byte[196];        
        ByteBuffer buffer = ByteBuffer.wrap(valors);
        buffer.putInt(tipusMissatge);        
        buffer.put(token.getBytes());
        buffer.position(68);
        buffer.put(destinatari.getBytes());
        buffer.position(132);
        buffer.put(sendData);
        
        return buffer.array();
    }
    
    public void enviamentUDP(byte[] msg){
        try {            
            System.out.println(new String(msg));
            byte[] sendData = new byte[196];
            byte[] receiveData = new byte[1024]; 
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, IPAddress, port);
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void arreglaCadena() {
        String[] partsCadena;
        partsCadena=this.missatge.split("\\|\\|END\\|\\|");
        this.missatge=partsCadena[0];               
    }
    
    
}
