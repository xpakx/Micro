package io.github.xpakx.micro2.administration;

import io.github.xpakx.micro2.administration.dto.RoleRequest;
import io.github.xpakx.micro2.security.JwtTokenUtils;
import io.github.xpakx.micro2.user.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdministrationControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;
    private Long adminId;

    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenUtils jwtTokenUtil;
    @Autowired
    UserService userService;
    @Autowired
    UserRoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        UserAccount user = new UserAccount();
        user.setUsername("user1");
        user.setPassword("password");
        user.setRoles(new HashSet<>());
        user = userRepository.save(user);
        this.userId = user.getId();
        UserRole adminRole = new UserRole();
        adminRole.setAuthority("ROLE_ADMIN");
        UserAccount admin = new UserAccount();
        admin.setUsername("admin");
        admin.setPassword("password");
        Set<UserRole> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        admin.setRoles(adminRoles);
        admin = userRepository.save(admin);
        this.adminId = admin.getId();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private String tokenFor(String username) {
        return jwtTokenUtil.generateToken(userService.loadUserByUsername(username));
    }

    @Test
    void shouldRespondWith401ToAddRoleIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .post(baseUrl + "/administration/users/{username}/roles", "user1")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith403ToAddRoleIfUserIsNotAdmin() {
        RoleRequest request = getRoleRequest("ROLE_TEST");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/administration/users/{username}/roles", "user1")
        .then()
                .statusCode(FORBIDDEN.value());
    }

    private RoleRequest getRoleRequest(String role) {
        RoleRequest request = new RoleRequest();
        request.setName(role);
        return request;
    }

    @Test
    void shouldAddRole() {
        RoleRequest request = getRoleRequest("ROLE_ADMIN");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("admin"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/administration/users/{username}/roles", "user1")
        .then()
                .statusCode(OK.value())
                .body("roles.authority", hasItem("ROLE_ADMIN"));
    }

    @Test
    void shouldCreateNewRole() {
        RoleRequest request = getRoleRequest("ROLE_TEST");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("admin"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/administration/users/{username}/roles", "user1")
        .then()
                .statusCode(OK.value())
                .body("roles.authority", hasItem("ROLE_TEST"));
        int rolesInDb = roleRepository.findAll().size();
        assertEquals(2, rolesInDb);
    }

    @Test
    void shouldRespondWith401ToDeleteRoleIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .delete(baseUrl + "/administration/users/{username}/roles", "user1")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith403ToDeleteRoleIfUserIsNotAdmin() {
        RoleRequest request = getRoleRequest("ROLE_TEST");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .delete(baseUrl + "/administration/users/{username}/roles", "user1")
        .then()
                .statusCode(FORBIDDEN.value());
    }

    @Test
    void shouldDeleteRole() {
        RoleRequest request = getRoleRequest("ROLE_TEST");
        addRoleToUser("user1", "ROLE_TEST");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("admin"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .delete(baseUrl + "/administration/users/{username}/roles", "user1")
        .then()
                .statusCode(OK.value());

        Optional<UserAccount> user = userRepository.findByUsername("user1");
        assertTrue(user.isPresent());
        assertThat(user.get().getRoles(), not(hasItem(hasProperty("authority", equalTo("ROLE_TEST")))));
    }

    private void addRoleToUser(String username, String roleName) {
        UserRole role = new UserRole();
        role.setAuthority(roleName);
        UserAccount user = userRepository.findByUsername(username).get();
        Set<UserRole> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
    }
}