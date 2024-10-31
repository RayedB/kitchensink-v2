package members.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import members.model.Member;
import members.service.MemberService;

import java.util.Map;
import java.util.HashMap;
import org.jboss.logging.Logger;

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
    public Response getMembers() {
        LOG.debug("Executing findAll query");
        return Response
            .ok(memberService.getAllMembers())
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    @POST
    public Response create(Member member) {
        LOG.info("Creating new member");
        try {
            Member created = memberService.createMember(member);
            return Response.ok(created).build();
        } catch (BadRequestException e) {
            Map<String, String> error = new HashMap<>();
            error.put("name", "Name is required");
            error.put("email", "Email is required");
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (ClientErrorException e) {
            Map<String, String> error = new HashMap<>();
            error.put("email", "Email taken");
            return Response
                .status(Response.Status.CONFLICT)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getMemberById(@PathParam("id") String id) {
        LOG.debug("Getting member by id: " + id);
        Member member = memberService.getMemberById(id);
        if (member == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response
            .ok(member)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
