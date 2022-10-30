package com.gisiona.observabilidade.produtos.domain;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {
	
	void save(Produto produto);
	Optional<Produto> getOne(Long id);
	List<Produto> getAll();
	
}
