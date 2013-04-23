/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.client;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import com.whatsgroup.alkiria.loginserver.AlkiriaLoginServer;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author XeviPortatil
 */
public class AlkiriaClient {    
    //Constants
    public static final String STOP = "#quitServer";
    public static final int PORT = 35421;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    private Socket client = null;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AlkiriaClient programa = new AlkiriaClient();
        programa.inicia();
    }
    
    public void inicia(){
        try {
            Socket client = new Socket("localhost",PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            //Escribim text
            out.write("Hola que ase!");
            out.newLine();
            out.flush();
            String inputLine;
            while((inputLine=in.readLine())!=null){
                System.out.println(inputLine);
            }
            out.write(STOP);
            out.newLine();
            out.flush();
        } catch (UnknownHostException ex) {
            Logger.getLogger(AlkiriaClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AlkiriaClient.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                in.close();
                out.close();
                client.close();
            } catch (IOException ex) {
                Logger.getLogger(AlkiriaClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
}
