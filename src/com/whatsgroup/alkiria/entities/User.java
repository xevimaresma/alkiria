/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.entities;

import com.mongodb.BasicDBObject;
import java.sql.Time;

/**
 *
 * @author XeviPortatil
 */
public class User extends AlkiriaDataBaseObject{
    public User(){
    
    }
    public User(String mail, String pass, Time ultimaConexio, String frase){
        put("mail",mail);
        put("pass",pass);
        put("ultimaconexio",ultimaConexio);
        put("frase",frase);
    }

    @Override
    public String getTableName() {
        return "users";
    }
    
    public String toString(){
        return "User: " + getString("mail") + " Pass: " + getString("pass");
    }
}
