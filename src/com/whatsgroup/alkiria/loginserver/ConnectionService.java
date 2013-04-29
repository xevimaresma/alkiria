/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.loginserver;

import com.whatsgroup.alkiria.db.DataBase;
import com.whatsgroup.alkiria.entities.User;
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

/**
 *
 * @author XeviPortatil
 */
public class ConnectionService implements Runnable{
    Socket socket;
    DataInputStream in;
    BufferedWriter out;
    AlkiriaLoginServer server;
    
    public ConnectionService(AlkiriaLoginServer server, Socket socket){
        this.server = server;
        this.socket = socket;
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
        byte[] data = new byte[len];
        in.readFully(data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int tipus = buffer.getInt();
        if(tipus == 1){
            //Crear usuari
            //System.out.println(buffer.asCharBuffer());
            byte[] arrlogin = new byte[64];
            buffer.get(arrlogin);
            
            System.out.println(new String(arrlogin,"UTF-8"));
            //Creem l'usuari a la BD
            DataBase db = new DataBase();
            User user = new User(new String(arrlogin,"UTF-8"), null, null, null);
            db.save(user);
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
