package br.com.fiap.vendasms.external_interface.jms;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ProdutoConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoConsumer.class);

    private final Tracer tracer;
    private final Propagator propagator;

    public ProdutoConsumer(Tracer tracer, Propagator propagator) {
        this.tracer = tracer;
        this.propagator = propagator;
    }

    @JmsListener(destination = "produto.queue")
    public void consume(jakarta.jms.Message jmsMessage) {
        Propagator.Getter<jakarta.jms.Message> getter = (msg, key) -> {
            try {
                return msg.getStringProperty(key.replace("-", "_"));
            } catch (JMSException e) {
                return null;
            }
        };

        Span span = propagator.extract(jmsMessage, getter)
                .name("jms.produto.consume")
                .start();

        try (var ignored = tracer.withSpan(span)) {
            String payload = ((TextMessage) jmsMessage).getText();

            String produtoId = extractId(payload);
            logger.info("Produto recebido na fila produto.queue produtoId={}", produtoId);

        } catch (Exception e) {
            span.error(e);
            logger.error("Erro ao processar mensagem de produto.queue: {}", e.getMessage(), e);
        } finally {
            span.end();
        }
    }

    private String extractId(String json) {
        if (json == null) return "unknown";
        int start = json.indexOf("\"id\"");
        if (start < 0) return "unknown";
        int colon = json.indexOf(":", start);
        int quote1 = json.indexOf("\"", colon);
        int quote2 = json.indexOf("\"", quote1 + 1);
        if (quote1 < 0 || quote2 < 0) return "unknown";
        return json.substring(quote1 + 1, quote2);
    }
}
