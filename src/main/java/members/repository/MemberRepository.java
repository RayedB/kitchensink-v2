package members.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import config.Mongo;
import members.model.Member;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MemberRepository {
    private static final Logger LOG = Logger.getLogger(MemberRepository.class);
    private final MongoCollection<Member> collection;
    
    @Inject
    public MemberRepository(@Mongo MongoClient mongoClient) {
        String databaseName = "kitchensink";
        String collectionName = "members";
        
        LOG.info("Initializing MemberRepository");
        LOG.info("Database name: " + databaseName);
        LOG.info("Collection name: " + collectionName);
        
        this.collection = mongoClient
            .getDatabase(databaseName)
            .getCollection(collectionName, Member.class);
    }

    public Member insert(Member member) {
        LOG.info("Attempting to insert member: " + member);
        try {
            collection.insertOne(member);
            LOG.info("Successfully inserted member with ID: " + member.getId());
            return member;
        } catch (Exception e) {
            LOG.error("Error inserting member", e);
            throw e;
        }
    }

    public List<Member> findAll() {
        LOG.info("Executing findAll query");
        try {
            List<Member> members = collection.find().into(new ArrayList<>());
            LOG.info("Found " + members.size() + " members");
            members.forEach(member -> LOG.info("Member: " + member));
            return members;
        } catch (Exception e) {
            LOG.error("Error in findAll", e);
            throw e;
        }
    }
}
