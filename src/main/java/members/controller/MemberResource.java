package members.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import members.model.Member;
import members.service.MemberService;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/members")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MemberResource {
    private final MemberService memberService;
    private static final Logger LOG = Logger.getLogger(MemberResource.class);
    
    @Inject
    public MemberResource(MemberService memberService) {
        this.memberService = memberService;
    }

    @GET
    public List<Member> getMembers() {
        LOG.debug("Executing findAll query");
        return memberService.getAllMembers();
    }

        @POST
    @Path("/test")
    public void insertTest() {
        LOG.info("Inserting test document");
        memberService.insertTestDocument();
    }
}
