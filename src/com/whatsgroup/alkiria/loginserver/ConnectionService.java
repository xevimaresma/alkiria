/*
 * Classe que representa una conexió cleint 
*/
package com.whatsgroup.alkiria.loginserver;

import com.mongodb.BasicDBObject;
import com.whatsgroup.alkiria.db.DataBase;
import com.whatsgroup.alkiria.entities.Encryption;
import com.whatsgroup.alkiria.entities.User;
import com.whatsgroup.alkiria.messages.MsgUser;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe que representa una conexió client
 * 
 */
public class ConnectionService implements Runnable{
    //Definició de variables
    Socket socket;
    DataInputStream in;
    BufferedWriter out;
    AlkiriaLoginServer server;
    DataBase db;
    
    /*
     * Constructor per defecte on se li passa el server, el propi socket i la 
     * instància de base de dades
     */
    public ConnectionService(AlkiriaLoginServer server, Socket socket, DataBase db){
        this.server = server;
        this.socket = socket;
        this.db = db;
    }

    @Override
    public void run() {
        //Obtenim els Streams per llegir i escriure
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
    
    /*
     * Mètode que escolta conexions del client
     */
    public void listen() throws IOException{
        //Llegim la longitud del paquet
        int len = in.readInt();
        System.out.println("Len: " + len);
        //Creem un byte d'arrays amb la longitud
        byte[] data = new byte[len];
        //Omplim l'array amb el contingut del socket
        in.readFully(data);
        //Desencriptem les dades amb la clau secreta compartida
        Encryption encrypt = new Encryption();
        encrypt.setClau(Encryption.CLAU);
        try {
            data = encrypt.decryptBytes(data);
        } catch (Exception ex) {
            Logger.getLogger(MsgUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Len data: " + data.length);
        //Creem un bytebuffer per poder separar les dades correctament
        ByteBuffer buffer = ByteBuffer.wrap(data);
        System.out.println("Len Buffer: " + buffer.capacity());
        //Llegim el tipus de missatge
        int tipus = buffer.getInt();
        //Crear usuari
        if(tipus == 1){
            //Llegim el login
            byte[] arrlogin = new byte[64];
            buffer.get(arrlogin);
            System.out.println("Len Buffer2: " + buffer.capacity());
            //Llegim el password
            byte[] arrpass = new byte[64];
            buffer.get(arrpass);
            String login = new String(arrlogin,"UTF-8").trim();
            String pass = new String(arrpass,"UTF-8").trim();
            //Comprobem si l'usuari ja existeix
            User user = new User();
            user.setMail(login);
            //Busquem l'usuari a la base de dades
            BasicDBObject resultat = (BasicDBObject)db.find(user);
            User resp = new User();
            //Si la resposta és null, creem l'usuari
            if(resultat==null){
                //Creem l'usuari a la BD
                resp.setMail(login);
                resp.setPass(pass);
                db.save(resp);
                //Carreguem les dades al objecte
                resp.loadFromDBObject((BasicDBObject)db.find(resp));
                System.out.println(user);
                System.out.println("Usuari Creat OK");
            }else{
                //Usuari ja existeix //TODO: Ara mateix estem enviant el token que hem trobat
                // aquí hauriem de tornar un error que l'usuari ja existeix i no el token.
                // Ho hem fet així per poder fer proves.
                resp.loadFromDBObject((BasicDBObject)db.find(user));
                System.out.println(resp);
                System.out.println("Usuari Existent");
            }

            //Gestionar respostes
            System.out.println(resp.getToken());
            out.write(resp.getToken());
            out.flush();
         //Login usuari
        }else if(tipus == 2){
            //Llegim el login
            byte[] arrlogin = new byte[64];
            buffer.get(arrlogin);
            System.out.println("Len Buffer2: " + buffer.capacity());
            //Llegim el password
            byte[] arrpass = new byte[64];
            buffer.get(arrpass);
            String login = new String(arrlogin,"UTF-8").trim();
            String pass = new String(arrpass,"UTF-8").trim();
            //Comprobem si l'usuari ja existeix
            User user = new User();
            user.setMail(login);
            user.setPass(pass);
            //Busquem l'usuari a la base de dades
            BasicDBObject resultat = (BasicDBObject)db.find(user);
            User resp = new User();
            //Si l'uauri no existeix retornem error
            if(resultat==null){
                System.out.println("Error Login");
                out.write("LOGIN ERROR");
                out.flush();
            }else{
                //
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
