package com.gisiona.observabilidade.produtos.client.avaliacoes;

import java.util.List;

public interface AvaliacaoClient {

	List<AvaliacaoModel> buscarTodosPorProduto(Long productId);
}
