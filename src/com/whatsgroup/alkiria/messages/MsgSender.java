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
    // Definim variables per tota la classe, amb les constants de tipus d'enviament i el port
    public static final int TIPUS_ENVIA_MSG = 2;
    public static final int TIPUS_DEMANA_MSG = 3;
    public static final int TIPUS_LLIURA_MSG = 4;
    private String missatge;   
    private String clauEncriptacio;    
    private String mailDesti;
    
    private int port=9876;
    
    // Constructor només amb missatge
    public MsgSender(String missatgeRep) {
        this.missatge=missatgeRep;
        this.arreglaCadena();
    }
    
    // Constructor amb missatge i clau d'encriptació
    public MsgSender(String missatgeRep, String clauEncripta) {
        this.missatge=missatgeRep;
        this.clauEncriptacio=clauEncripta;
        this.arreglaCadena();
    }
    
    // Setter de Missatge
    public void setMissatge(String missatgeRep) {
        this.missatge=missatgeRep;
        this.arreglaCadena();
    }
    
    // Getter de missatge
    public String getMissatge() {
        this.arreglaCadena();
        return this.missatge;
    }
    
    // Setter de clau
    public void setClau(String clauEncripta) {
        this.clauEncriptacio=clauEncripta;
    }
    
    // Mètode per preparar l'enviament - si no té paràmetres, en posem de prova.
    // Un cop provat, podrem eliminar aquesta definició
    public byte[] enviaMsg() throws Exception {                
        String token="515dd85856861ee247ccf15a";
        String destinatari="prova";
        String remitent="xevimaresma@gmail.com";
        int tipusMissatge=TIPUS_ENVIA_MSG;
        return enviaMsg(token,destinatari,remitent,0,0,tipusMissatge);
    }
    
    // Mètode per preparar enviament de missatge
    // Rep token, destianari, remitent i tipus de missatge i retorna un byte
    public byte[] enviaMsg(String token, String destinatari, String remitent,int horaLliurament,int horaEnviament, int tipusMissatge) throws Exception {                        
        byte[] sendData = new byte[64];                                  
        Encryption encripta=new Encryption();        
        encripta.setClau(token);
        encripta.encrypt(this.missatge);
        this.arreglaCadena();                    
        sendData=encripta.getMsgEncriptat();     
        ByteBuffer buffer;
        // Si el tipus és 3 (sol·licitud de missatges rebuts)         
        if (tipusMissatge==3) {
            // Envioem el missatge afegint el remitent abans del missatge
            byte[] valors = new byte[268];        
            buffer = ByteBuffer.wrap(valors);
            buffer.putInt(tipusMissatge);        
            buffer.put(token.trim().getBytes());
            buffer.position(68);
            buffer.put(destinatari.trim().getBytes());
            buffer.position(132);
            buffer.put(remitent.trim().getBytes());
            buffer.position(196);                        
            buffer.put(sendData);
            buffer.position(260);
            buffer.putInt(horaLliurament);
            buffer.position(264);
            buffer.putInt(horaEnviament);
        } else {
            // Si no, serà el tipus 2, que desarà el missatge sencer, pensat pel client
            byte[] valors = new byte[200];        
            buffer = ByteBuffer.wrap(valors);
            buffer.putInt(tipusMissatge);        
            buffer.put(token.getBytes());
            buffer.position(68);
            buffer.put(destinatari.getBytes());
            buffer.position(132);
            buffer.put(sendData);
            buffer.position(196);
            buffer.putInt((int)System.currentTimeMillis());
        }
        
        return buffer.array();
    }
    
    // Mètode per fer la tramesa via UDP
    public void enviamentUDP(byte[] msg){
        try {            
            //System.out.println(new String(msg));
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
    
    // Mètode per afegir ||END|| al final del text per poder eliminar els espais blancs al receptor.
    public void arreglaCadena() {
        String[] partsCadena;
        partsCadena=this.missatge.split("\\|\\|END\\|\\|");
        this.missatge=partsCadena[0];               
    }
    
    
}
