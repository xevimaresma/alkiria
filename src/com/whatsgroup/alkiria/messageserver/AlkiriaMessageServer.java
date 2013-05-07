/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.messageserver;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.whatsgroup.alkiria.db.DataBase;
import com.whatsgroup.alkiria.entities.*;
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
    DataBase db;
           
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
                String token=new String(arrtoken);
                System.out.println("Token: " + token);
                String desti=new String(arrdesti);
                System.out.println("Destí: " + desti);
                String missatgeS=new String(arrmsg);
                System.out.println("Missatge: " + missatgeS);
                DataBase db=new DataBase(); 
                User user = new User();
                user.setToken(token);
                BasicDBObject resultat = (BasicDBObject)db.find(user);
                User usuariRemitent=new User();
                if(resultat==null){
                    System.out.println("Error Login");
                    // Preparar missatge d'error
                    resposta="Error, el remitent no existeix";
                    byte[] valors = new byte[68];
                    ByteBuffer bufferSend = ByteBuffer.wrap(valors);
                    bufferSend.putInt(-1);        
                    bufferSend.put(resposta.getBytes());
                    DatagramPacket sendPacket = new DatagramPacket(bufferSend.array(), bufferSend.array().length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }else{
                    usuariRemitent.loadFromDBObject((BasicDBObject)db.find(user));
                    System.out.println(usuariRemitent);
                    System.out.println("Usuari Existent");
                    System.out.println(usuariRemitent.getToken());
                    
                    String remitent=usuariRemitent.getMail();
                
                    encripta.decrypt(arrmsg);            
                    System.out.println("Missatge desencriptat: " + encripta.getMsgDesencriptat());                
                    resposta="OK";

                    // Guardem el missatge                               
                    Message missatge=new Message();
                    missatge.setRemitent(remitent);
                    missatge.setDestinatari(desti);
                    missatge.setMissatge(encripta.getMsgDesencriptat());
                    missatge.setHoraEnviament((int)System.currentTimeMillis());
                    db.save(missatge);     

                    byte[] valors = new byte[68];
                    ByteBuffer bufferSend = ByteBuffer.wrap(valors);
                    bufferSend.putInt(2);        
                    bufferSend.put(resposta.getBytes());
                    DatagramPacket sendPacket = new DatagramPacket(bufferSend.array(), bufferSend.array().length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }
                
            } else if (tipus==3) {
                byte[] arrtoken = new byte[64];
                buffer.get(arrtoken);
                String token=new String(arrtoken);
                DataBase db=new DataBase(); 
                User user = new User();
                user.setToken(token);
                BasicDBObject resultat = (BasicDBObject)db.find(user);
                User usuariCerca=new User();
                if(resultat==null){
                    System.out.println("Error Login");
                    // Preparar missatge d'error
                    resposta="Error, l'usuari no existeix";
                    byte[] valors = new byte[68];
                    ByteBuffer bufferSend = ByteBuffer.wrap(valors);
                    bufferSend.putInt(-1);        
                    bufferSend.put(resposta.getBytes());
                    DatagramPacket sendPacket = new DatagramPacket(bufferSend.array(), bufferSend.array().length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }else{
                    usuariCerca.loadFromDBObject((BasicDBObject)db.find(user));                    
                    String cercaMail=usuariCerca.getMail(); 
                    Message msgsCerca=new Message();
                    msgsCerca.setDestinatari(cercaMail);
                    DBCursor msgTrobats = (DBCursor)db.findAll(msgsCerca);
                    if (msgTrobats==null) {
                        resposta="No hi ha missatges";
                        byte[] valors = new byte[68];
                        ByteBuffer bufferSend = ByteBuffer.wrap(valors);
                        bufferSend.putInt(-1);        
                        bufferSend.put(resposta.getBytes());
                        DatagramPacket sendPacket = new DatagramPacket(bufferSend.array(), bufferSend.array().length, IPAddress, port);
                        serverSocket.send(sendPacket); 
                    } else {
                        while ( msgTrobats.hasNext() ) {
                            Message msgTrobat = (Message)msgTrobats.next();                                                                                    
                            byte[] valors = new byte[196];                            
                            String destinatari="girona@gmail.com";
                            ByteBuffer bufferSend = ByteBuffer.wrap(valors);
                            bufferSend.putInt(3);        
                            bufferSend.put(token.getBytes());
                            bufferSend.position(68);
                            bufferSend.put(destinatari.getBytes());
                            bufferSend.position(132);
                            bufferSend.put(msgTrobat.getMissatge().getBytes());
                            bufferSend.put(resposta.getBytes());
                            DatagramPacket sendPacket = new DatagramPacket(bufferSend.array(), bufferSend.array().length, IPAddress, port);
                            serverSocket.send(sendPacket);
                        }
                    }
                    
                }
            }

            
        }
    }
    
     public int getPort() {
        return this.port;
    }
}
