package br.com.fiap.produtosms.dto;

import br.com.fiap.produtosms.entities.Produto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link Produto}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProdutoDto(UUID id, String nome, String descricao, BigDecimal preco, String categoria)
        implements Serializable {

    public static ProdutoDto from(Produto produto) {
        return new ProdutoDto(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getCategoria()
        );
    }

    public static List<ProdutoDto> from(List<Produto> produtos) {
        return produtos.stream().map(ProdutoDto::from).toList();
    }

    public static ProdutoDto empty() {
        return new ProdutoDto(null, null, null, null, null);
    }

    public Produto toEntity() {
        return new Produto(null, nome, descricao, preco, categoria);
    }
}
