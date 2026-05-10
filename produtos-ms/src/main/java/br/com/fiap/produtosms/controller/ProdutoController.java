package br.com.fiap.produtosms.controller;

import br.com.fiap.produtosms.dto.ProdutoDto;
import br.com.fiap.produtosms.service.ProdutoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
@RequestMapping("/produtos")
public class ProdutoController extends CommonController {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String index(Model model) {
        logger.info("Listando todos os produtos");
        model.addAttribute("produtos", ProdutoDto.from(produtoService.findAll()));
        return "produtos";
    }

    @GetMapping("/detalhe/{id}")
    public String detalhe(@PathVariable("id") UUID id, Model model) {
        logger.info("Buscando produto id={}", id);
        ProdutoDto produtoDto;
        try {
            produtoDto = ProdutoDto.from(produtoService.findById(id));
        } catch (NoSuchElementException e) {
            logger.warn("Produto não encontrado, exibindo formulário vazio id={}", id);
            produtoDto = ProdutoDto.empty();
        }
        model.addAttribute("produto", produtoDto);
        return "detalhe-produto";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ProdutoDto produto) {
        logger.info("Salvando produto id={}", produto.id());
        produtoService.saveOrUpdate(produto.toEntity());
        logger.info("Produto salvo com sucesso id={}", produto.id());
        return "redirect:/produtos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") UUID id) {
        logger.info("Excluindo produto id={}", id);
        produtoService.deleteById(id);
        return "redirect:/produtos";
    }
}
