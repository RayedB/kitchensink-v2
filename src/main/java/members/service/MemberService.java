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


import members.model.Member;
import members.repository.MemberRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MemberService {
    private final MemberRepository repository;
    private static final Logger LOG = Logger.getLogger(MemberService.class);
    
    @Inject
    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public List<Member> getAllMembers() {
        return repository.findAll();
    }

    public Member getMemberById(String id) {
        return repository.findById(id).orElse(null);
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
    
}
