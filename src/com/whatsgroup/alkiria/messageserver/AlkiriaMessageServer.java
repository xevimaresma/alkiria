/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.messageserver;

import com.whatsgroup.alkiria.entities.Encryption;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

/**
 *
 * @author PC
 */
public class AlkiriaMessageServer {
     /**
     * @param args the command line arguments
     */
    private int port=9876;
           
    public static void main(String[] args) throws Exception {
        System.out.println("Hello world!");
        Encryption encripta=new Encryption();
        encripta.setClau("prova");
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[196];
        byte[] sendData = new byte[1024];
        String capitalizedSentence;
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);            
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            byte[] dadesRebudes=receivePacket.getData();
            String prova=new String(dadesRebudes);
            ByteBuffer buffer = ByteBuffer.wrap(dadesRebudes);
            int tipus = buffer.getInt();
            String resposta="KO";
            if (tipus==2) {                            
                byte[] arrtoken = new byte[64];
                buffer.get(arrtoken);
                byte[] arrdesti = new byte[64];
                buffer.get(arrdesti);
                byte[] arrmsg = new byte[64];
                buffer.get(arrmsg);
                prova=new String(arrtoken);
                System.out.println("Token: " + prova);
                prova=new String(arrdesti);
                System.out.println("Destí: " + prova);
                prova=new String(arrmsg);
                System.out.println("Missatge: " + prova);
                encripta.decrypt(arrmsg);            
                System.out.println("Missatge desencriptat: " + encripta.getMsgDesencriptat());                
                resposta="OK";
            } 
            byte[] valors = new byte[68];
            ByteBuffer bufferSend = ByteBuffer.wrap(valors);
            bufferSend.putInt(2);        
            bufferSend.put(resposta.getBytes());
                        
            DatagramPacket sendPacket = new DatagramPacket(bufferSend.array(), bufferSend.array().length, IPAddress, port);
            serverSocket.send(sendPacket);

        }
    }
    
     public int getPort() {
        return this.port;
    }
}
