/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.client;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import com.whatsgroup.alkiria.loginserver.AlkiriaLoginServer;
import com.whatsgroup.alkiria.messages.MsgSender;
import com.whatsgroup.alkiria.messages.MsgUserCreate;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
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
    private DataOutputStream out = null;
    private Socket client = null;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AlkiriaClient programa = new AlkiriaClient();
        programa.inicia();
    }
    
    public void inicia(){
        menu();
    }
    
    public void menu(){
        System.out.println("*****************************");
        System.out.println("*** Alkiria Admin Client ****");
        System.out.println("*****************************");
        System.out.println("1. Crear Usuaris");
        System.out.println("2. Obtenir Token");
        System.out.println("3. Enviar missatge");
        System.out.println("0. Sortir");
        
        Scanner lector = new Scanner(System.in);
        boolean end = false;
        int opcio = -1;
        while(!end){
            System.out.println("Introdueix una opció: ");
            opcio = lector.nextInt();
            switch(opcio){
                case 0:
                    end = true;
                break;
                case 1:
                    menuCrearUsuari();
                break;
                case 2:
                break;
                case 3:
                    enviaMsgUDP();
                break;
                default:
                    System.out.println("Ha d'introduir una opció del menú.");
                    System.out.println("");
            }
        }
    }
    
    private void enviaMsgUDP()  {
        Scanner lector = new Scanner(System.in);
        
        System.out.println("Escriu el missatge: ");
        String msg = lector.nextLine();
        MsgSender missatge=new MsgSender(msg);
        try {
            missatge.enviaMsg();
        } catch (Exception e) {
            
        }
        
    }
    
    private void menuCrearUsuari(){
        Scanner lector = new Scanner(System.in);
        
        System.out.println("Introdeixi el Login: ");
        String login = lector.nextLine();
        System.out.println("Introdeixi el Password: ");
        String password = lector.nextLine();
        
        crearUsuari(login, password);
        //menu();
    }
    
    private void crearUsuari(String login, String pass){
/*
 * 

Client-Servidor
4 Bytes [INT - 1 = Alta]
64 Bytes [STRING - md5(password)]
64 Bytes [STRING - mail] -no n'he vist de gaire més llargues-
1 Bytes [Char - | = Fi de missatge]

Servidor - Client
4 Bytes [INT - 1 = Login]
64 Bytes [STRING - token o bé un null o cadena buida si error? O fem Error: XXX on XXX sigui un codi per mostrar des de l'app? Usuari ocupat, mail ja registrat...?]
1 Bytes [Char - | = Fi de missatge]

*/        
        MsgUserCreate msguser = new MsgUserCreate();
        msguser.setLogin(login);
        msguser.setPass(pass);
        byte[] msg = msguser.getMessage();
        sendMessage(msg);
        
    }
    
    public void sendMessage(byte[] msg){
        try {
            System.out.println(msg);
            client = new Socket("localhost",PORT);
            out = new DataOutputStream(client.getOutputStream());
            //Escribim text
            out.writeInt(msg.length);
            out.write(msg);
            out.flush();
        } catch (UnknownHostException ex) {
            Logger.getLogger(AlkiriaClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AlkiriaClient.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                out.close();
                client.close();
            } catch (IOException ex) {
                Logger.getLogger(AlkiriaClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    
}
