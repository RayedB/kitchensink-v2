package config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jboss.logging.Logger;
import io.github.cdimascio.dotenv.Dotenv;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@ApplicationScoped
public class MongoConfig {
    private static final Logger LOG = Logger.getLogger(MongoConfig.class);
    
    @Produces
    @ApplicationScoped
    @Mongo
    public MongoClient produceMongoClient() {
        // Load at the start of your application
        Dotenv dotenv = Dotenv.load();

        // Then use it to get environment variables
        String connectionString = dotenv.get("MONGODB_URI");

        // Configure codec registry
        CodecRegistry pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        // Build client settings
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .codecRegistry(pojoCodecRegistry)
            .build();

        LOG.info("Creating MongoDB client with POJO codec registry");
        return MongoClients.create(settings);
    }
}
 