/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whatsgroup.alkiria.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.whatsgroup.alkiria.entities.AlkiriaDataBaseObject;
import com.whatsgroup.alkiria.entities.User;
import com.whatsgroup.alkiria.loginserver.AlkiriaLoginServer;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.types.ObjectId;

/**
 *
 * @author XeviPortatil
 */
public class DataBase {
    private MongoClient mc = null;
    private DB db = null;
    

    public DataBase(){
        try {
            //mc = new MongoClient("alkiria.xevimr.eu");
            mc = new MongoClient("134.0.8.6");
            db = mc.getDB("test");
            //boolean auth = db.authenticate("admin", "admin".toCharArray());
            
            //Set<String> colls = db.getCollectionNames();
            
            //for (String s : colls){
            //    System.out.println("Col: "+s);
            //}
            
            //DBCollection usertbl = db.getCollection("users");
            
            //User user = new User("xevimaresma2@gmail.com","proves",null,"estat");
            //usertbl.save(user);
            
            //DBCursor cursor = usertbl.find();
            //while(cursor.hasNext()){
                //DBObject dades = cursor.next();
                //System.out.println("Email: "+dades.get("mail"));
            //}
            
            //System.out.println("Autenticat: "+auth);
        } catch (UnknownHostException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    public boolean save(AlkiriaDataBaseObject object){
        DBCollection tbl = db.getCollection(object.getTableName());
        tbl.save(object);
        return true;        
    }
    
    public DBObject find(AlkiriaDataBaseObject object){
        DBCollection tbl = db.getCollection(object.getTableName());
        return tbl.findOne(object);
    }
    
    public DBObject findById(AlkiriaDataBaseObject object, String idString){        
        DBCollection tbl = db.getCollection(object.getTableName());
        DBObject searchById = new BasicDBObject("_id", new ObjectId(idString));        
        return tbl.findOne(searchById);
    }
    
    public DBCursor findAll(AlkiriaDataBaseObject object){
        DBCollection tbl = db.getCollection(object.getTableName());        
        return tbl.find(object);
    }
    
    public void updateMsg(BasicDBObject object,BasicDBObject object2) {
        DBCollection tbl = db.getCollection("messages");        
        WriteResult prova=tbl.update(object,object2);        
    }
    
    public DBCursor findMessages(String destinatari) {
        DBCollection tbl = db.getCollection("messages");        
        DBCursor cursor = tbl.find(new BasicDBObject().append("destinatari",destinatari.trim()));	        
	return cursor;
    }
    
}
