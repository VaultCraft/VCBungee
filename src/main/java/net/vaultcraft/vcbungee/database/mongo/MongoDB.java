package net.vaultcraft.vcbungee.database.mongo;

import com.mongodb.*;
import net.md_5.bungee.api.ProxyServer;
import net.vaultcraft.vcbungee.VCBungee;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * WARNING: This class has not been tested!
 *
 * @author tacticalsk8er
 */
public class MongoDB {

    private MongoClient client;


    /**
     * Creates a connection to a MongoDB server and provides easy methods to update and query that server.
     *
     * @param host IP of the MongoDB server.
     * @param port Port number of the MongoDB server.
     * @throws UnknownHostException
     */
    public MongoDB(final String host, final int port) throws UnknownHostException {
        ProxyServer.getInstance().getScheduler().schedule(VCBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                client = new MongoClient(host, port);
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    /**
     *
     * @return MongoClient
     */
    public MongoClient getClient() {
        return client;
    }

    /**
     * Use this to retrieve a DB object and modify it.
     *
     * @param dbName Name of database to access
     */
    public DB getDB(String dbName) {
        return client.getDB(dbName);
    }

    public void insert(DB db, String collectionName, DBObject dbObject) {
        DBCollection dbCollection = db.getCollection(collectionName);
        dbCollection.insert(dbObject);
    }

    public void insert(String dbName, String collectionName, DBObject dbObject) {
        DB db = getDB(dbName);
        insert(db, collectionName, dbObject);
    }

    /**
     * Inserts a BasicDBObject into a DBCollection.
     *
     * @param db             DB object to modify.
     * @param collectionName Name of "table" in SQL terms.
     * @param from       Object to update. In SQL terms updating a "row" in a "table" (collection).
     */
    public void update(DB db, String collectionName, DBObject from, DBObject to) {
        DBCollection dbCollection = db.getCollection(collectionName);
        dbCollection.update(from, to);
    }

    /**
     * Inserts a BasicDBObject into a DBCollection.
     *
     * @param dbName         The name of the database you want to modify.
     * @param collectionName Name of "table" in SQL terms.
     * @param from       Object to insert. In SQL terms updating a "row" in a "table" (collection).
     */
    public void update(String dbName, String collectionName, DBObject from, DBObject to) {
        DB db = getDB(dbName);
        update(db, collectionName, from, to);
    }

    /**
     * Queries the MongoDB server and returns a Object.
     *
     * @param db             DB object to modify.
     * @param collectionName Name of "table" in SQL terms.
     * @param primaryKey     The primary key you want to look for in the collection's documents, or "rows" in SQL terms.
     * @param ID             What the primary key's value should be.
     * @param retrieveKey    The key of the object you want to retrieve from the document found if there is one.
     * @return The object tied to the retrieveKey. If there is no document found to have the primaryKey tied to the ID, then it will return null.
     */
    public Object query(DB db, String collectionName, String primaryKey, Object ID, String retrieveKey) {
        DBCollection dbCollection = db.getCollection(collectionName);
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put(primaryKey, ID);
        DBCursor dbCursor = dbCollection.find(dbObject);
        if (dbCursor.hasNext()) {
            DBObject dbObject1 = dbCursor.next();
            return dbObject1.get(retrieveKey);
        }
        return null;
    }

    /**
     * Queries the MongoDB server and returns a Object.
     *
     * @param dbName         The name of the database you want to modify.
     * @param collectionName Name of "table" in SQL terms.
     * @param primaryKey     The primary key you want to look for in the collection's documents, or "rows" in SQL terms.
     * @param ID             What the primary key's value should be.
     * @param retrieveKey    The key of the object you want to retrieve from the document found if there is one.
     * @return The object tied to the retrieveKey. If there is no document found to have the primaryKey tied to the ID, then it will return null.
     */
    public Object query(String dbName, String collectionName, String primaryKey, Object ID, String retrieveKey) {
        DB db = getDB(dbName);
        return query(db, collectionName, primaryKey, ID, retrieveKey);
    }


    /**
     * Queries the MongoDB server and returns a DBObject.
     *
     * @param db             DB object to modify.
     * @param collectionName Name of "table" in SQL terms.
     * @param primaryKey     The primary key you want to look for in the collection's documents, or "rows" in SQL terms.
     * @param ID             What the primary key's value should be.
     * @return The DBObject that has the primaryKey tied to the ID.
     */
    public DBObject query(DB db, String collectionName, String primaryKey, Object ID) {
        DBCollection dbCollection = db.getCollection(collectionName);
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put(primaryKey, ID);
        DBCursor dbCursor = dbCollection.find(dbObject);
        if (dbCursor.hasNext()) {
            return dbCursor.next();
        }
        return null;
    }

    /**
     * Queries the MongoDB server and returns a DBObject.
     *
     * @param dbName         The name of the database you want to modify.
     * @param collectionName Name of "table" in SQL terms.
     * @param primaryKey     The primary key you want to look for in the collection's documents, or "rows" in SQL terms.
     * @param ID             What the primary key's value should be.
     * @return The DBObject that has the primaryKey tied to the ID.
     */
    public DBObject query(String dbName, String collectionName, String primaryKey, Object ID) {
        DB db = getDB(dbName);
        return query(db, collectionName, primaryKey, ID);
    }

    /**
     * Closes the connection to the MongoDB server.
     */
    public void close() {
        client.close();
    }
}
