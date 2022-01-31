package br.com.liandro.rest.testes.refact;

import br.com.liandro.core.BaseTest;
import br.com.liandro.core.Movimentacao;
import br.com.liandro.utils.DataUtils;
import io.restassured.RestAssured;
import org.junit.Test;

import static br.com.liandro.utils.BarrigaUtils.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

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

}
