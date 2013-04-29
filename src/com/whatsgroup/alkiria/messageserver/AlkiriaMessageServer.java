/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.messageserver;

import com.whatsgroup.alkiria.entities.Encryption;
import java.io.*;
import java.net.*;

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
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);            
            byte[] dadesRebudes=receivePacket.getData();
            String prova=new String(dadesRebudes);
            System.out.println("He Rebut: " + prova);
            encripta.decrypt(dadesRebudes);            
            System.out.println("He Rebut 2: " + encripta.getMsgDesencriptat());
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String capitalizedSentence = encripta.getMsgDesencriptat().toUpperCase();
            sendData = capitalizedSentence.getBytes();
            DatagramPacket sendPacket =
            new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
        }
    }
    
     public int getPort() {
        return this.port;
    }
}
