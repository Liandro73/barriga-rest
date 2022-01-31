package br.com.liandro.rest.testes.refact;

import br.com.liandro.core.BaseTest;
import br.com.liandro.core.Movimentacao;
import br.com.liandro.utils.DataUtils;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

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
    public void deveInserirMovimentacaoComSucesso() {

        Movimentacao movimentacao = getMovimentacaoValida();

        given()
                .body(movimentacao)
            .when()
                .post("/transacoes")
            .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveValidarCamposObrigatoriosMovimentacao() {
        given()
                .body("{}")
            .when()
                .post("/transacoes")
            .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                ))
        ;
    }

    @Test
    public void naoDeveCadastrarMovimentacaoFutura() {
        Movimentacao movimentacao = getMovimentacaoValida();
        movimentacao.setData_transacao(DataUtils.getDataDiferencaDias(7));

        given()
                .body(movimentacao)
            .when()
                .post("/transacoes")
            .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void naoDeveRemoverContaComMovimentacao() {

        Integer CONTA_ID = getIdContaPeloNome("Conta com movimentacao");

        given()
                .pathParam("id", CONTA_ID)
            .when()
                .delete("/contas/{id}")
            .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"))
        ;
    }

    @Test
    public void deveRemoverMovimentacao() {
        Integer MOVIMENTACAO_ID = getIdMovimentacaoPelaDescricao("Movimentacao para exclusao");

        given()
                .pathParam("id", MOVIMENTACAO_ID)
                .when()
                .delete("/transacoes/{id}")
                .then()
                .statusCode(204)
        ;
    }

    public Integer getIdContaPeloNome(String nome) {
        return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
    }

    public Integer getIdMovimentacaoPelaDescricao(String descricao) {
        return RestAssured.get("/transacoes?descricao=" + descricao).then().extract().path("id[0]");
    }

    private Movimentacao getMovimentacaoValida() {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
//        movimentacao.setUsuario_id(0);
        movimentacao.setDescricao("Descricao da movimentacao");
        movimentacao.setEnvolvido("Envolvido na movimentacao");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao(DataUtils.getDataDiferencaDias(-1));
        movimentacao.setData_pagamento(DataUtils.getDataDiferencaDias(30));
        movimentacao.setValor(100f);
        movimentacao.setStatus(true);

        return movimentacao;
    }

}
