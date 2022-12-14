package com.gisiona.observabilidade.produtos.api;

import com.gisiona.observabilidade.produtos.client.avaliacoes.AvaliacaoClient;
import com.gisiona.observabilidade.produtos.client.avaliacoes.AvaliacaoModel;
import com.gisiona.observabilidade.produtos.domain.Produto;
import com.gisiona.observabilidade.produtos.domain.ProdutoRepository;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Summary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
	
	private final ProdutoRepository produtos;
	private final AvaliacaoClient avaliacoes;

	private final Summary requestDuration;
	private final CollectorRegistry collectorRegistry;

	public ProdutoController(ProdutoRepository produtos, AvaliacaoClient avaliacoes, CollectorRegistry collectorRegistry) {
		this.produtos = produtos;
		this.avaliacoes = avaliacoes;
		this.collectorRegistry = collectorRegistry;
		requestDuration = Summary.build()
				.name("http_request_duration_summary_gisiona")
				.help("Time for http request")
				.quantile(0.95, 0.01)
				.register(collectorRegistry);
	}

	@GetMapping
	public List<ProdutoModel> buscarTodos() {
		Summary.Timer timer = requestDuration.startTimer();

		Long sleepDuration = Double.valueOf(Math.floor(Math.random() * 10 * 1000)).longValue();

		timer.observeDuration();

		return produtos.getAll()
				.stream()
				.map(this::converterProdutoParaModelo)
				.collect(Collectors.toList());
	}

	@GetMapping("/{produtoId}")
	public ProdutoModel buscarPorId(@PathVariable Long produtoId) {
		return produtos.getOne(produtoId)
				.map(this::converterProdutoParaModeloComAvaliacao)
				.orElseThrow(RecursoNaoEncontradoException::new);
	}
	
	private ProdutoModel converterProdutoParaModelo(Produto produto) {
		return ProdutoModel.of(produto);
	}
	
	private ProdutoModel converterProdutoParaModeloComAvaliacao(Produto produto) {
		return ProdutoModel.of(produto, buscarAvaliacaoDoProduto(produto.getId()));
	}

	private List<AvaliacaoModel> buscarAvaliacaoDoProduto(Long productId) {
		return avaliacoes.buscarTodosPorProduto(productId);
	}
}
