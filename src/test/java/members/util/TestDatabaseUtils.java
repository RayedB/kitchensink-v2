package members.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import config.Mongo;
import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TestDatabaseUtils {
    private static final Logger LOG = Logger.getLogger(TestDatabaseUtils.class);
    private final MongoClient mongoClient;
    private final String databaseName;

    @Inject
    public TestDatabaseUtils(
        @Mongo MongoClient mongoClient,
        @ConfigProperty(name = "quarkus.mongodb.database", defaultValue = "test") String databaseName
    ) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    public void clearCollection(String collectionName) {
        LOG.info("Clearing collection: " + collectionName);
        MongoCollection<Document> collection = mongoClient
            .getDatabase(databaseName)
            .getCollection(collectionName);
        collection.deleteMany(new Document());
    }
}