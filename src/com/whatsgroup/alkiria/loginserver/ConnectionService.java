/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.loginserver;

import com.mongodb.BasicDBObject;
import com.whatsgroup.alkiria.db.DataBase;
import com.whatsgroup.alkiria.entities.Encryption;
import com.whatsgroup.alkiria.entities.User;
import com.whatsgroup.alkiria.messages.MsgUser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.types.ObjectId;

/**
 *
 * @author XeviPortatil
 */
public class ConnectionService implements Runnable{
    Socket socket;
    DataInputStream in;
    BufferedWriter out;
    AlkiriaLoginServer server;
    DataBase db;
    
    public ConnectionService(AlkiriaLoginServer server, Socket socket, DataBase db){
        this.server = server;
        this.socket = socket;
        this.db = db;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            listen();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionService.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void listen() throws IOException{
        int len = in.readInt();
        System.out.println("Len: " + len);
        byte[] data = new byte[len];
        in.readFully(data);
        Encryption encrypt = new Encryption();
        encrypt.setClau(Encryption.CLAU);
        try {
            data = encrypt.decryptBytes(data);
        } catch (Exception ex) {
            Logger.getLogger(MsgUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Len data: " + data.length);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        System.out.println("Len Buffer: " + buffer.capacity());
        int tipus = buffer.getInt();
        if(tipus == 1){
            //Crear usuari
            //System.out.println(buffer.asCharBuffer());
            byte[] arrlogin = new byte[64];
            buffer.get(arrlogin);
            System.out.println("Len Buffer2: " + buffer.capacity());
            byte[] arrpass = new byte[64];
            buffer.get(arrpass);
            String login = new String(arrlogin,"UTF-8").trim();
            String pass = new String(arrpass,"UTF-8").trim();
            //Comprobem si l'usuari ja existeix
            User user = new User();
            user.setMail(login);
            BasicDBObject resultat = (BasicDBObject)db.find(user);
            User resp = new User();
            if(resultat==null){
                //Creem l'usuari a la BD
                resp.setMail(login);
                resp.setPass(pass);
                db.save(resp);
                resp.loadFromDBObject((BasicDBObject)db.find(resp));
                System.out.println(user);
                System.out.println("Usuari Creat OK");
            }else{
                resp.loadFromDBObject((BasicDBObject)db.find(user));
                System.out.println(resp);
                System.out.println("Usuari Existent");
            }

            //Gestionar respostes
            System.out.println(resp.getToken());
            out.write(resp.getToken());
            out.flush();
            //User user = new User(new String(arrlogin,"UTF-8"), null, null, null);
            //db.save(user);
        }else if(tipus == 2){
            //Crear usuari
            //System.out.println(buffer.asCharBuffer());
            byte[] arrlogin = new byte[64];
            buffer.get(arrlogin);
            System.out.println("Len Buffer2: " + buffer.capacity());
            byte[] arrpass = new byte[64];
            buffer.get(arrpass);
            String login = new String(arrlogin,"UTF-8").trim();
            String pass = new String(arrpass,"UTF-8").trim();
            //Comprobem si l'usuari ja existeix
            User user = new User();
            user.setMail(login);
            user.setPass(pass);
            BasicDBObject resultat = (BasicDBObject)db.find(user);
            User resp = new User();
            if(resultat==null){
                System.out.println("Error Login");
                out.write("LOGIN ERROR");
                out.flush();
            }else{
                resp.loadFromDBObject((BasicDBObject)db.find(user));
                System.out.println(resp);
                System.out.println("Usuari Existent");
                System.out.println(resp.getToken());
                out.write(resp.getToken());
                out.flush();
            }
            //User user = new User(new String(arrlogin,"UTF-8"), null, null, null);
            //db.save(user);
            
        }
        
    }
    
    private void broadcast(String missatge) throws IOException{
        int i=0;
        synchronized(server.getConnections()){
            while(i<server.getConnections().size()){
                if(server.getConnections().get(i).isClosed()){
                    server.getConnections().remove(i);
                }else{
                    server.getConnections().get(i).send(missatge);
                    i++;
                }
            }
        }
    }
    
    private void send(String missatge) throws IOException{
        out.write(missatge);
        out.newLine();
        out.flush();
    }
    
    public boolean isClosed(){
        return socket.isClosed();
    }
    
    public void close(){
        try {
            socket.shutdownInput();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
