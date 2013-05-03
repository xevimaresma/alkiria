/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.entities;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.sql.Time;

/**
 *
 * @author XeviPortatil
 */
public class User extends AlkiriaDataBaseObject{
    private String token;
    private String mail;
    private String pass;
    private Time ultimaConexio;
    private String frase;

    public User(){
    
    }
    
    public String getToken() {
        if(this.token == "")
            this.token = getString("_id");
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
        put("token",token);
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
        put("mail",mail);
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
        put("pass",pass);
    }

    public Time getUltimaConexio() {
        return ultimaConexio;
    }

    public void setUltimaConexio(Time ultimaConexio) {
        this.ultimaConexio = ultimaConexio;
        put("ultimaconexio",ultimaConexio);
    }

    public String getFrase() {
        return frase;
    }

    public void setFrase(String frase) {
        this.frase = frase;
        put("frase",frase);
    }
    
    public void loadFromDBObject(BasicDBObject obj){
        this.token = obj.getString("_id");
        this.mail = obj.getString("mail");
        this.pass = obj.getString("pass");
    }

    @Override
    public String getTableName() {
        return "users";
    }
    
    public String toString(){
        return "User: " + this.mail + " Pass: " + this.pass + " Token: " +this.token;
    }
}
