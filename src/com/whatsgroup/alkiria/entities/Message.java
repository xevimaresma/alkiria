/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.entities;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.sql.Time;
import org.bson.types.ObjectId;


/**
 *
 * @author PC
 */
public class Message extends AlkiriaDataBaseObject{
    private String remitent;
    private String destinatari;
    private String missatge;
    private int horaEnviament;
    private int horaLliurament;  
    ObjectId _id;

    public Message(){
    
    }
    
    public String getRemitent() {
        return remitent;
    }
    public void setRemitent(String remitent) {
        this.remitent=remitent;
        put("remitent",remitent);
    }
    
    public String getDestinatari() {
        return destinatari;
    }
    public void setDestinatari(String destinatari) {
        this.destinatari=destinatari;
        put("destinatari",destinatari);
    }

    public String getMissatge() {
        return missatge;
    }
    public void setMissatge(String missatge) {
        this.missatge=missatge;
        put("missatge",missatge);
    }
    
    public int getHoraLliurament() {
        return horaLliurament;        
    }
    public void setHoraLliurament(int horaLliurament) {
        this.horaLliurament=horaLliurament;
        put("horaLliurament",horaLliurament);
    }

    public int getHoraEnviament() {
        return horaEnviament;
    }
    public void setHoraEnviament(int horaEnviament) {
        this.horaEnviament=horaEnviament;
        put("horaEnviament",horaEnviament);
    }

    public void loadFromDBObject(BasicDBObject obj){
        this.remitent=obj.getString("remitent");
        this.destinatari=obj.getString("destinatari");
        this.missatge=obj.getString("missatge");
        if (obj.containsField("horaLliurament")) {
            this.horaLliurament = obj.getInt("horaLliurament");
        } else { this.horaLliurament=0; }
        this.horaEnviament = obj.getInt("horaEnviament");        
        this._id=new ObjectId(obj.getString("_id"));
    }
    public String getId() {
        return this._id.toString();
    }

    @Override
    public String getTableName() {
        return "messages";
    }
       
    public String toString(){
        return "Remitent: " + this.remitent + " Destinatari: " + this.destinatari + " Missatge: "+this.missatge+" Hora enviament: "+horaEnviament+" Hora Lliurament: "+horaLliurament;
    }
}
