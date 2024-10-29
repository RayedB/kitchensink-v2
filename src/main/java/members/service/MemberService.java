package members.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

import members.model.Member;
import members.repository.MemberRepository;

@ApplicationScoped
public class MemberService {
    private final MemberRepository repository;
    
    @Inject
    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public List<Member> getAllMembers() {
        return repository.findAll();
    }

    public void insertTestDocument() {
       Member member = new Member();
       member.setName("Test User");
       member.setEmail("testuser@example.com");
       member.setPhoneNumber("123-456-7890");
       repository.insert(member);
    }
    
    // Business logic methods will go here
}
