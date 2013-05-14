/*
 * Aquesta classe s'encarrega de gestionar les peticions de login i 
 * creació de comtpes d'usuair de Alkiria
 */
package com.whatsgroup.alkiria.loginserver;

import com.whatsgroup.alkiria.db.DataBase;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe principal del servidor de login
 */
public class AlkiriaLoginServer {
    //Constants
    public static final String STOP = "#quitServer";
    public static final int PORT = 35421;
    
    //Pool de conexions establertes
    private ArrayList<ConnectionService> connections = new ArrayList<ConnectionService>();
    //Marca de final del servei
    private boolean end=false;

    /**
     * Mètode principal
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AlkiriaLoginServer server = new AlkiriaLoginServer();
        server.listen();
    }
    
    /*
     * Mètode que s'encarrega de mantenir el socket escoltant a petitions de login
     */
    public void listen(){
        ServerSocket socolServidor = null;
        try {
            socolServidor = new ServerSocket(PORT);
            System.out.println("AlkiriaLoginServer Inicialized...");
            //Mentre no parem anem escoltant
            while(!end){
                //Establim la conexió i la guardem al pool de conexions
                Socket clientSocket = socolServidor.accept();
                generateClientConnection(clientSocket);
            }
        } catch (IOException ex) {
            Logger.getLogger(AlkiriaLoginServer.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            //Tanquem totes les conexions obertes
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
    
    /*
     * Mètode per tancar el servidor i les conexions esablertes
     */
    public void close(){
        this.end=true;
    }
    
    /*
     * Mètode que retorna totes les conexions establertes
     */
    protected ArrayList<ConnectionService> getConnections(){
        return connections;
    }
    
    /*
     * Mètode que genera una conexió en un fil separat i la manté oberta en
     * el pool de conexions
     */
    private void generateClientConnection(Socket clientToConnect){
        synchronized(getConnections()){
            System.out.println("Client connected...");
            ConnectionService servei = new ConnectionService(this, clientToConnect,new DataBase());
            getConnections().add(servei);
            (new Thread(servei)).start();
        }
    }
}
