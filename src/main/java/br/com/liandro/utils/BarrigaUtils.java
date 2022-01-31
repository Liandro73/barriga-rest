package br.com.liandro.utils;

import br.com.liandro.core.Movimentacao;
import io.restassured.RestAssured;

public class BarrigaUtils {

    public static Integer getIdContaPeloNome(String nome) {
        return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
    }

    public static Integer getIdMovimentacaoPelaDescricao(String descricao) {
        return RestAssured.get("/transacoes?descricao=" + descricao).then().extract().path("id[0]");
    }

    public static Movimentacao getMovimentacaoValida() {
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
