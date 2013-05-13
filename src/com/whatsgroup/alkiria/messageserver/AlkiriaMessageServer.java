/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.messageserver;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.whatsgroup.alkiria.db.DataBase;
import com.whatsgroup.alkiria.entities.*;
import com.whatsgroup.alkiria.messages.MsgSender;
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
           
    // Engeguem el servidor.
    public static void main(String[] args) throws Exception {        
        // Instanciem un encriptador, sockets i iterem en un bucle infinit escoltant
        Encryption encripta=new Encryption();        
        System.out.println("UDP Server initialized");
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[196];
        byte[] sendData = new byte[1024];
        String capitalizedSentence;
        while(true) {
            // Si rebem un paquet, n'extraiem el TIPUS, que arriba en un INT al principi, en bytes.
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);            
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            byte[] dadesRebudes=receivePacket.getData();            
            ByteBuffer buffer = ByteBuffer.wrap(dadesRebudes);
            int tipus = buffer.getInt();
            String resposta="KO";
            // Si tipus=2, 
            if (tipus==2) {                            
                // Rutina d'enviament de missatge (remitent-servidor)
                byte[] arrtoken = new byte[64];
                buffer.get(arrtoken);
                byte[] arrdesti = new byte[64];
                buffer.get(arrdesti);
                byte[] arrmsg = new byte[64];
                buffer.get(arrmsg);                
                String token=new String(arrtoken).trim(); 
                // Passem les dades
                encripta.setClau(token);
                //System.out.println("Token: " + token);
                String desti=new String(arrdesti);
                //System.out.println("Destí: " + desti);
                String missatgeS=new String(arrmsg);
                //System.out.println("Missatge: " + missatgeS);
                DataBase db=new DataBase(); 
                User user = new User();
                user.setMail(token);                
                BasicDBObject resultat;
                // Cerquem l'usuari remitent
                try {
                    resultat = (BasicDBObject)db.findById(user,token.trim());                                                
                } catch (Exception e) {
                    resultat=null;
                }
                if(resultat==null){
                    //System.out.println("Error Login");
                    // Si no existeix ho mostrem
                    resposta="Error, el remitent no existeix";
                    byte[] valors = new byte[68];
                    ByteBuffer bufferSend = ByteBuffer.wrap(valors);
                    bufferSend.putInt(-1);        
                    bufferSend.put(resposta.getBytes());
                    DatagramPacket sendPacket = new DatagramPacket(bufferSend.array(), bufferSend.array().length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }else{
                    // Si existeix desem el missatge a BD després de trobar el token del remitent
                    User usuariRemitent=new User();
                    usuariRemitent.loadFromDBObject(resultat);
                    String remitent=usuariRemitent.getMail();                    
                    //System.out.println("Remitent: "+usuariRemitent+" ("+usuariRemitent.getToken()+" - "+remitent+")");                    
                    encripta.decrypt(arrmsg);            
                    //System.out.println("Missatge desencriptat ("+token+"): " + encripta.getMsgDesencriptat());                
                    resposta="OK";

                    // Guardem el missatge                               
                    Message missatge=new Message();
                    missatge.setRemitent(remitent.trim());
                    missatge.setDestinatari(desti.trim());
                    missatge.setMissatge(encripta.getMsgDesencriptat().trim());
                    missatge.setHoraEnviament((int)System.currentTimeMillis());
                    missatge.setHoraLliurament(0);
                    db.save(missatge);     

                    // I enviem un OK al client.
                    byte[] valors = new byte[68];
                    ByteBuffer bufferSend = ByteBuffer.wrap(valors);
                    bufferSend.putInt(2);        
                    bufferSend.put(resposta.getBytes());
                    DatagramPacket sendPacket = new DatagramPacket(bufferSend.array(), bufferSend.array().length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }
                
            } else if (tipus==3) {
                // Rutina de lliurament de missatge (servidor - destinatari)
                byte[] arrtoken = new byte[64];
                buffer.get(arrtoken);
                //System.out.println(arrtoken);
                String token=new String(arrtoken);
                //System.out.println(token);
                DataBase db=new DataBase(); 
                User user = new User();
                token=token.trim();
                encripta.setClau(token);
                user.setToken(token);
                // Busquem l'usuari destinatari segons el token
                BasicDBObject resultat;
                try {
                    resultat = (BasicDBObject)db.findById(user,token.trim());                                                
                } catch (Exception e) {
                    resultat=null;
                } 
                User usuariCerca=new User();
                if(resultat==null){
                    //System.out.println("Error Login");
                    // Preparar missatge d'error
                    resposta="Error, l'usuari no existeix";
                    byte[] valors = new byte[68];
                    ByteBuffer bufferSend = ByteBuffer.wrap(valors);
                    bufferSend.putInt(-1);        
                    bufferSend.put(resposta.getBytes());
                    DatagramPacket sendPacket = new DatagramPacket(bufferSend.array(), bufferSend.array().length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }else{
                    usuariCerca.loadFromDBObject(resultat);                    
                    String cercaMail=usuariCerca.getMail(); 
                    Message msgsCerca=new Message();
                    msgsCerca.setDestinatari(cercaMail);
                    Message msgTrobat=new Message();
                    // I si el trobem, iterarem a través dels missatges coincidents (els que tenen el mateix destinatari)
                    DBCursor msgTrobats = (DBCursor)db.findMessages(cercaMail);                                                            
                    if(!msgTrobats.hasNext()) {
                        // Si no hi ha missatge ho diem
                        resposta="No hi ha missatges per a "+cercaMail;
                        byte[] valors = new byte[68];
                        ByteBuffer bufferSend = ByteBuffer.wrap(valors);
                        bufferSend.putInt(-1);        
                        bufferSend.put(resposta.getBytes());
                        DatagramPacket sendPacket = new DatagramPacket(bufferSend.array(), bufferSend.array().length, IPAddress, port);
                        serverSocket.send(sendPacket); 
                    } else {      
                        int i=0;
                        // Si n'hi ha, iterem i els mostrem.
                        while ( msgTrobats.hasNext() ) {                                                          
                            i++;
                            BasicDBObject msgTemp=(BasicDBObject)msgTrobats.next();                               
                            resultat = (BasicDBObject)db.findById(msgTrobat,msgTemp.getString("_id"));
                            msgTrobat.loadFromDBObject(resultat); 
                            User usuariRemiteAra=new User();
                            user.setMail(msgTrobat.getRemitent());
                            BasicDBObject resultatRem = (BasicDBObject)db.find(usuariRemiteAra);
                            User usuariRemiteAra2=new User();
                            usuariRemiteAra2.loadFromDBObject(resultatRem);
                            String encriptaAra=usuariRemiteAra2.getToken();                            
                            
                            //System.out.println("A punt d'enviar "+msgTrobat.toString());
                            MsgSender enviament = new MsgSender(msgTrobat.getMissatge(),encriptaAra);
                            byte[] enviamentMsg=enviament.enviaMsg(token,msgTrobat.getDestinatari(),msgTrobat.getRemitent(),3);
                            try {                                                              
                                DatagramPacket sendPacket = new DatagramPacket(enviamentMsg, enviamentMsg.length, IPAddress, port);
                                serverSocket.send(sendPacket);                                                                                                 
                            } catch (Exception e) {
                                e.printStackTrace();                                
                            }                                                        
                        }                        
                    }
                    
                }
            }

           tipus=0; 
        }
    }
    
     public int getPort() {
        return this.port;
    }
     
    
}
