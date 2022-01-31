package br.com.liandro.rest.testes.refact;

import br.com.liandro.core.BaseTest;
import io.restassured.RestAssured;
import org.junit.Test;

import static br.com.liandro.utils.BarrigaUtils.getIdContaPeloNome;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ContasTest extends BaseTest {

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

}
