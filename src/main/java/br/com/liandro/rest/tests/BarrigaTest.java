package br.com.liandro.rest.tests;

import br.com.liandro.core.BaseTest;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class BarrigaTest extends BaseTest {

    @Test
    public void naoDeveAcessarApiSemToken() {
        given()
            .when()
                .get("/contas")
            .then()
                .statusCode(401)
        ;
    }

}
