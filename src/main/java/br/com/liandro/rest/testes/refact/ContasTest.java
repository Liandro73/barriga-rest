package br.com.liandro.rest.testes.refact;

import br.com.liandro.core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ContasTest extends BaseTest {

    @BeforeClass
    public static void login() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "1@1.gmail.com");
        login.put("senha", "123456");

        String TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token")
                ;

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);

        RestAssured.get("/reset").then().statusCode(200);
    }

    @Test
    public void deveIncluirContaComSucesso() {
        given()
                .body("{\"nome\": \"nova conta\"}")
            .when()
                .post("/contas")
            .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveAlterarContaComSucesso() {

        Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");

        given()
                .body("{\"nome\": \"conta alterada\"}")
                .pathParam("id", CONTA_ID)
            .when()
                .put("/contas/{id}")
            .then()
                .statusCode(200)
                .body("nome", is("conta alterada"))
        ;
    }

    @Test
    public void naoDeveInserirContaComMesmoNome() {
        given()
                .body("{\"nome\": \"Conta mesmo nome\"}")
            .when()
                .post("/contas")
            .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    public Integer getIdContaPeloNome(String nome) {
        return RestAssured.get("/contas?name=" + nome).then().extract().path("id[0]");
    }

}
