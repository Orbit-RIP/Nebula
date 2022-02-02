package cc.fyre.neutron.database;

import cc.fyre.neutron.Neutron;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;

public class MongoHandler {

    @Getter private Neutron instance;

    @Getter private MongoDatabase mongoDatabase;

    public MongoHandler(Neutron instance) {
        this.instance = instance;

        try {

            final ServerAddress serverAddress = new ServerAddress(instance.getConfig().getString("mongo.host"),instance.getConfig().getInt("mongo.port"));

            MongoClient mongoClient;

            mongoClient = new MongoClient(serverAddress);
            this.mongoDatabase = mongoClient.getDatabase(instance.getConfig().getString("mongo.database"));
        } catch (Exception ex) {
            instance.getLogger().warning("Failed to connect to mongo on " + instance.getConfig().getString("mongo.host") + ".");
            ex.printStackTrace();
        }
    }

    public MongoCollection<Document> findCollection(String name) {

        for (String collection : this.mongoDatabase.listCollectionNames().into(new ArrayList<>())) {

            if (collection.equals(name)) {
                return this.mongoDatabase.getCollection(name);
            }

        }

        return null;
    }

    public boolean doesCollectionExist(String name) {
        return this.mongoDatabase.listCollectionNames().into(new ArrayList<>()).contains(name);
    }
}
