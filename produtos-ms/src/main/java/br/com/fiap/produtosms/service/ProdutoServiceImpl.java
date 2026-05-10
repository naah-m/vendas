package br.com.fiap.produtosms.service;

import br.com.fiap.produtosms.entities.OutboxEvent;
import br.com.fiap.produtosms.entities.Produto;
import br.com.fiap.produtosms.repositories.OutboxEventRepository;
import br.com.fiap.produtosms.repositories.ProdutoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
class ProdutoServiceImpl implements ProdutoService {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoServiceImpl.class);

    private static final String TIPO_PRODUTO = "PRODUTO";
    private static final String QUEUE = "produto.queue";

    private final ProdutoRepository produtoRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final Counter produtosSalvosCounter;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository,
                              OutboxEventRepository outboxEventRepository,
                              ObjectMapper objectMapper,
                              MeterRegistry meterRegistry) {
        this.produtoRepository = produtoRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.produtosSalvosCounter = Counter.builder("produtos.salvos")
                .description("Total de produtos salvos ou atualizados")
                .register(meterRegistry);
    }

    @Override
    public Produto findById(UUID id) {
        logger.debug("Buscando produto id={}", id);
        return this.produtoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado id={}", id);
                    return new NoSuchElementException("Produto não encontrado: " + id);
                });
    }

    @Override
    public List<Produto> findAll() {
        logger.debug("Listando todos os produtos");
        return this.produtoRepository.findAll();
    }

    @Override
    @Transactional
    public void saveOrUpdate(Produto produto) {
        logger.info("Salvando produto id={}", produto.getId());
        final Produto saved = this.produtoRepository.save(produto);
        produtosSalvosCounter.increment();

        try {
            final String payload = objectMapper.writeValueAsString(
                    Map.of("id", saved.getId().toString())
            );
            this.outboxEventRepository.save(
                    new OutboxEvent(saved.getId().toString(), TIPO_PRODUTO, QUEUE, payload)
            );
            logger.info("Evento outbox criado produtoId={} queue={}", saved.getId(), QUEUE);
        } catch (JsonProcessingException e) {
            logger.error("Erro ao serializar produto para outbox produtoId={}", saved.getId(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        logger.info("Excluindo produto id={}", id);
        this.produtoRepository.deleteById(id);
        logger.info("Produto excluído com sucesso id={}", id);
    }
}
