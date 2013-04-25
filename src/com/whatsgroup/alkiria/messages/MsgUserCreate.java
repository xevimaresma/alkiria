/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.messages;

import java.nio.ByteBuffer;

/**
 *
 * @author XeviPortatil
 */
public class MsgUserCreate {
    public static final int TIPUS_USER_CREATE = 1;
    private String login;
    private String pass;
    
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    
    public byte[] getMessage(){
        byte[] loginchars = new byte[64];
        byte[] passchars = new byte[64];
        byte[] valors = new byte[132];
        
        loginchars = login.getBytes();
        passchars = pass.getBytes();
        
        ByteBuffer buffer = ByteBuffer.wrap(valors);
        buffer.putInt(TIPUS_USER_CREATE);
        buffer.put(loginchars);
        buffer.put(passchars);
        return buffer.array();
    }
}
