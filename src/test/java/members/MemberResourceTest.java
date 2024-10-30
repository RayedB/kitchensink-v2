package members;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import members.model.Member;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class MemberResourceTest {

    private static final String MEMBERS_ENDPOINT = "/api/members";
    
    @Test
    public void testListAllMembers() {
        given()
            .when()
            .get(MEMBERS_ENDPOINT)
            .then()
            .statusCode(200)
            .body("$.size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testGetMemberById() {
        // First create a member to test with
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@test.com");
        member.setPhoneNumber("1234567890");

        // Create member and get ID from response
        Response createResponse = given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(member)
            .when()
            .post(MEMBERS_ENDPOINT)
            .then()
            .statusCode(200)
            .extract().response();

        Long memberId = createResponse.jsonPath().getLong("id");

        // Test getting the member by ID
        given()
            .when()
            .get(MEMBERS_ENDPOINT + "/" + memberId)
            .then()
            .statusCode(200)
            .body("name", equalTo("John Doe"))
            .body("email", equalTo("john@test.com"));
    }

    @Test
    public void testGetNonExistentMember() {
        given()
            .when()
            .get(MEMBERS_ENDPOINT + "/999999")
            .then()
            .statusCode(404);
    }

    @Test
    public void testCreateMember() {
        Member member = new Member();
        member.setName("Jane Doe");
        member.setEmail("jane@test.com");
        member.setPhoneNumber("0987654321");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(member)
            .when()
            .post(MEMBERS_ENDPOINT)
            .then()
            .statusCode(200);
    }

    @Test
    public void testCreateInvalidMember() {
        Member member = new Member();
        // Missing required fields

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(member)
            .when()
            .post(MEMBERS_ENDPOINT)
            .then()
            .statusCode(400)
            .body("name", notNullValue())  // Expect validation error message
            .body("email", notNullValue()); // Expect validation error message
    }

    @Test
    public void testCreateDuplicateEmail() {
        Member member1 = new Member();
        member1.setName("Original User");
        member1.setEmail("duplicate@test.com");
        member1.setPhoneNumber("1234567890");

        // Create first member
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(member1)
            .when()
            .post(MEMBERS_ENDPOINT)
            .then()
            .statusCode(200);

        // Try to create second member with same email
        Member member2 = new Member();
        member2.setName("Duplicate User");
        member2.setEmail("duplicate@test.com");
        member2.setPhoneNumber("0987654321");

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(member2)
            .when()
            .post(MEMBERS_ENDPOINT)
            .then()
            .statusCode(409)  // Conflict
            .body("email", equalTo("Email taken"));
    }
}