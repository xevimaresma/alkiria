/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.loginserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author XeviPortatil
 */
public class ConnectionService implements Runnable{
    Socket socket;
    BufferedReader in;
    BufferedWriter out;
    AlkiriaLoginServer server;
    
    public ConnectionService(AlkiriaLoginServer server, Socket socket){
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
        String inputLine;
        while((inputLine=in.readLine())!=null){
            if(inputLine.startsWith(AlkiriaLoginServer.STOP)){
                server.close();
            }else{
                //Enviem els missatges...
                System.out.println(inputLine);
                broadcast(inputLine);
            }
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
