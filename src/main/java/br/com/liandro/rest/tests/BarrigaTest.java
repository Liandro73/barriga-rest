package br.com.liandro.rest.tests;

import br.com.liandro.core.BaseTest;
import br.com.liandro.core.Movimentacao;
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
                .body("{\"nome\": \"conta teste nova\"}")
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
    public void naoDeveInserirContaComMesmoNome() {
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

    @Test
    public void deveInserirMovimentacaoComSucesso() {

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(1050150);
//        movimentacao.setUsuario_id(0);
        movimentacao.setDescricao("Descricao da movimentacao");
        movimentacao.setEnvolvido("Envolvido na movimentacao");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao("01/01/2019");
        movimentacao.setData_pagamento("10/10/2021");
        movimentacao.setValor(100f);
        movimentacao.setStatus(true);

        given()
                .header("Authorization", "JWT " + TOKEN)
                .body(movimentacao)
            .when()
                .post("/transacoes")
            .then()
                .statusCode(201)
        ;
    }

}
