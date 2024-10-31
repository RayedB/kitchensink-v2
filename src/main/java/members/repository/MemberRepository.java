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
import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.mongodb.client.model.Filters;
import java.util.Optional;

@ApplicationScoped
public class MemberRepository {
    private static final Logger LOG = Logger.getLogger(MemberRepository.class);
    private final MongoCollection<Member> collection;
    
    @Inject
    public MemberRepository(@Mongo MongoClient mongoClient,
        @ConfigProperty(name = "quarkus.mongodb.database") String databaseName) {
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
            // First try to get raw documents to verify data exists
            List<Document> rawDocs = collection.withDocumentClass(Document.class)
                .find()
                .into(new ArrayList<>());
            LOG.info("Raw documents found: " + rawDocs.size());
            rawDocs.forEach(doc -> LOG.info("Document: " + doc.toJson()));

            // Then try to map to Member objects
            List<Member> members = collection.find().into(new ArrayList<>());
            LOG.info("Members found after mapping: " + members.size());
            return members;
        } catch (Exception e) {
            LOG.error("Error executing findAll query", e);
            throw e;
        }
    }

    public Optional<Member> findByEmail(String email) {
    LOG.info("Looking for member with email: " + email);
    try {
        return Optional.ofNullable(
            collection.find(Filters.eq("email", email))
                .first()
        );
    } catch (Exception e) {
        LOG.error("Error finding member by email", e);
        throw e;
        }
    }

    public Optional<Member> findById(String id) {
    try {
        return Optional.ofNullable(
            collection.find(Filters.eq("id", id))
                .first()
        );
    } catch (Exception e) {
        LOG.error("Error finding member by id", e);
        throw e;
    }
    }
}
