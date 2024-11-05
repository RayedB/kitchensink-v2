package members.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import members.model.Member;
import org.jboss.logging.Logger;

import java.util.Optional;

@ApplicationScoped
public class LegacyRepository {
    private static final Logger LOG = Logger.getLogger(LegacyRepository.class);
    private static final String LEGACY_API_URL = "http://localhost:8080/kitchensink/rest/members";
    private final Client client;
    private final WebTarget target;

    public LegacyRepository() {
        this.client = ClientBuilder.newClient();
        this.target = client.target(LEGACY_API_URL);
    }

    public Optional<Member> findMemberById(String id) {
        try {
            LOG.info("Attempting to retrieve member from legacy system: " + id);
            Member member = target
                .path("/{id}")
                .resolveTemplate("id", id)
                .request(MediaType.APPLICATION_JSON)
                .get(Member.class);
            LOG.info("Successfully retrieved member from legacy system: " + id);
            return Optional.ofNullable(member);
            
        } catch (Exception e) {
            LOG.error("Failed to fetch member from legacy system: " + id, e);
            return Optional.empty();
        }
    }
}
