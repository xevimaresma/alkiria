/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.loginserver;

import com.whatsgroup.alkiria.entities.User;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.sun.media.sound.AlawCodec;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author XeviPortatil
 */
public class AlkiriaLoginServer {
    //Constants
    public static final String STOP = "#quitServer";
    public static final int PORT = 35421;
    
    private ArrayList<ConnectionService> connections = new ArrayList<ConnectionService>();
    private boolean end=false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AlkiriaLoginServer server = new AlkiriaLoginServer();
        server.listen();
    }
    
    public void listen(){
        ServerSocket socolServidor = null;
        try {
            socolServidor = new ServerSocket(PORT);
            //socolServidor.setSoTimeout(5000);
            byte[] entradaDades = new byte[1024];
            
            System.out.println("AlkiriaLoginServer Inicialized...");
            while(!end){
                Socket clientSocket = socolServidor.accept();
                generateClientConnection(clientSocket);
            }
            
            //ServerConnection server = new ServerConnection();
            //server.inicia();
        } catch (IOException ex) {
            Logger.getLogger(AlkiriaLoginServer.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            for (ConnectionService servei: getConnections()){
                if(!servei.isClosed()){
                    System.out.println("Client Closed...");
                    servei.close();
                }
            }
        }
        try {
            //Tanquem el servidor
            socolServidor.close();
            System.out.println("AlkiriaLoginServer Shutdown...");
        } catch (IOException ex) {
            Logger.getLogger(AlkiriaLoginServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void close(){
        this.end=true;
    }
    
    protected ArrayList<ConnectionService> getConnections(){
        return connections;
    }
    
    private void generateClientConnection(Socket clientToConnect){
        synchronized(getConnections()){
            System.out.println("Client connected...");
            ConnectionService servei = new ConnectionService(this, clientToConnect);
            getConnections().add(servei);
            (new Thread(servei)).start();
        }
    }
    
    private void testDB(){
        try {
            // TODO code application logic here
            MongoClient mc = new MongoClient("alkiria.xevimr.eu");
            DB db = mc.getDB("test");
            //boolean auth = db.authenticate("admin", "admin".toCharArray());
            
            Set<String> colls = db.getCollectionNames();
            
            for (String s : colls){
                System.out.println("Col: "+s);
            }
            
            DBCollection usertbl = db.getCollection("users");
            
            User user = new User("xevimaresma2@gmail.com","proves",null,"estat");
            usertbl.save(user);
            
            DBCursor cursor = usertbl.find();
            while(cursor.hasNext()){
                DBObject dades = cursor.next();
                System.out.println("Email: "+dades.get("mail"));
            }
            
            //System.out.println("Autenticat: "+auth);
        } catch (UnknownHostException ex) {
            Logger.getLogger(AlkiriaLoginServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
}
