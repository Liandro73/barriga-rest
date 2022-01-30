package br.com.liandro.rest.tests;

import br.com.liandro.core.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BarrigaTest extends BaseTest {

    private String TOKEN;

    @Before
    public void login() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "wagner@aquino");
        login.put("senha", "123456");

        TOKEN = given()
                .body(login)
            .when()
                .post("/signin")
            .then()
                .statusCode(200)
                .extract().path("token")
        ;
    }

    @Test
    public void naoDeveAcessarApiSemToken() {
        given()
            .when()
                .get("/contas")
            .then()
                .statusCode(401)
        ;
    }

    @Test
    public void deveIncluirContaComSucesso() {
        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{\"nome\": \"conta teste\"}")
            .when()
                .post("/contas")
            .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveAlterarContaComSucesso() {
        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{\"nome\": \"conta teste alterada\"}")
            .when()
                .put("/contas/1050150")
            .then()
                .statusCode(200)
                .body("nome", is("conta teste alterada"))
        ;
    }

    @Test
    public void naoDeveInserirContaComMesmoNOmejkj() {
        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{\"nome\": \"conta teste alterada\"}")
            .when()
                .post("/contas")
            .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

}
