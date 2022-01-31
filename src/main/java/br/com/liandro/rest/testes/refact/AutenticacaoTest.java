package br.com.liandro.rest.testes.refact;

import br.com.liandro.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class AutenticacaoTest extends BaseTest {

    @Test
    public void naoDeveAcessarApiSemToken() {
        FilterableRequestSpecification frs = (FilterableRequestSpecification) RestAssured.requestSpecification;
        frs.removeHeader("Authorization");

        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401)
        ;
    }

}
