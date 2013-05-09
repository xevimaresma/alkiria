/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.client;

import com.whatsgroup.alkiria.loginserver.AlkiriaLoginServer;
import com.whatsgroup.alkiria.messages.MsgSender;
import com.whatsgroup.alkiria.messages.MsgUser;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
    public String token;
    
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
        System.out.println("4. Sol·licitar missatges");
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
                    menuLoginUsuari();
                break;
                case 3:
                    enviaMsgUDP();
                break;
                case 4:
                    refrescaMsgUDP();
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
            missatge.setClau(this.token);
            byte[] msgara = missatge.enviaMsg();
            sendMessageUDP(msgara);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void refrescaMsgUDP()  {
        Scanner lector = new Scanner(System.in);
                
        MsgSender missatge=new MsgSender("");
        try {
            missatge.setClau(this.token);
            byte[] msgara = missatge.enviaMsg(this.token,"",3);
            sendMessageUDP(msgara);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void menuLoginUsuari(){
        Scanner lector = new Scanner(System.in);
        
        System.out.println("Introdeixi el Login: ");
        String login = lector.nextLine();
        System.out.println("Introdeixi el Password: ");
        String password = lector.nextLine();
        
        loginUsuari(login, password);
        //menu();
    }
    
    private void crearUsuari(String login, String pass){
        MsgUser msguser = new MsgUser();
        msguser.setLogin(login);
        msguser.setPass(pass);
        byte[] msg = msguser.getMessage(MsgUser.TIPUS_USER_CREATE);
        sendMessage(msg);
    }
    
    private void loginUsuari(String login, String pass){
        MsgUser msguser = new MsgUser();
        msguser.setLogin(login);
        msguser.setPass(pass);
        byte[] msg = msguser.getMessage(MsgUser.TIPUS_USER_LOGIN);
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
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String dades = in.readLine();
            if (dades.trim()!="LOGIN ERROR") {
                this.token=dades;
                System.out.println("Nou token"+this.token);
            }
            System.out.println("Resposta: " + dades);
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
    
     public void sendMessageUDP(byte[] msg){
        try {
            int port=9876;
            System.out.println(new String(msg));
            byte[] sendData = new byte[196];
            byte[] receiveData = new byte[1024]; 
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, IPAddress, port);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            System.out.println("FROM SERVER:" + modifiedSentence);
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
