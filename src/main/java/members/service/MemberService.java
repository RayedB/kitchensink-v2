package members.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import members.model.Member;
import members.repository.LegacyRepository;
import members.repository.MemberRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MemberService {
    private final MemberRepository repository;
    private final LegacyRepository legacyRepository;
    private static final Logger LOG = Logger.getLogger(MemberService.class);
    
    @Inject
    public MemberService(MemberRepository repository, LegacyRepository legacyRepository) {
        this.repository = repository;
        this.legacyRepository = legacyRepository;
    }

    public List<Member> getAllMembers() {
        return repository.findAll();
    }

    public Member getMemberById(String id) {
        Optional<Member> member = repository.findById(id);
        if (member.isEmpty()) {
            // Try to sync from legacy system
            Optional<Member> syncedMember = syncMember(id);
            if (syncedMember.isPresent()) {
                // Store the synced member in our database
                return createMember(syncedMember.get());
            }
            return null; // Return null if not found in either system
        }
        return member.get();
    }


    public Member createMember(Member member) {
        validateMember(member);
        checkDuplicateEmail(member.getEmail());
       return repository.insert(member);
    }
    
    private void validateMember(Member member) {
       Map<String, String> violations = new HashMap<>();
    
    if (member.getName() == null || member.getName().trim().isEmpty()) {
        violations.put("name", "Name is required");
    }
    
    if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
        violations.put("email", "Email is required");
    }
    
    if (!violations.isEmpty()) {
        LOG.warn("Member validation failed: " + violations);
        try (Response response = Response
            .status(Response.Status.BAD_REQUEST)
            .entity(violations)
            .type(MediaType.APPLICATION_JSON)
            .build()) {
            throw new BadRequestException(response);
        }
    }
    }

    private void checkDuplicateEmail(String email) {
        if (repository.findByEmail(email).isPresent()) {
        LOG.warn("Duplicate email found: " + email);
        Map<String, String> error = new HashMap<>();
        error.put("email", "Email taken");
        try (Response response = Response
            .status(Response.Status.CONFLICT)
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build()) {
            throw new ClientErrorException(response);
        }
    }
    }

    private Optional<Member> syncMember(String id) {
    try {
        Optional<Member> syncedMember = legacyRepository.findMemberById(id);
        if (syncedMember.isPresent()) {
            // Store the synced member in our database
            Member createdMember = createMember(syncedMember.get());
            return Optional.of(createdMember);
        } else {
            return legacyRepository.findMemberById(id);
        }
    } catch (Exception e) {
        LOG.warn("Failed to sync member from legacy system", e);
        return Optional.empty();
    }
}
    
}
