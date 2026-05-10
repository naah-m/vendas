package br.com.fiap.produtosms.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "reference_id", nullable = false)
    private String referenceId;

    @Column(name = "class", nullable = false, columnDefinition = "TEXT")
    private String type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String destination;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;

    public OutboxEvent() {
    }

    public OutboxEvent(String referenceId, String type, String destination, String payload) {
        this.referenceId = referenceId;
        this.type = type;
        this.destination = destination;
        this.payload = payload;
        this.criadoEm = LocalDateTime.now();
        this.status = Status.PENDENTE;
    }

    public void marcarComoEnviado() {
        this.status = Status.ENVIADO;
        this.enviadoEm = LocalDateTime.now();
    }

    public String getDestination() {
        return destination;
    }

    public String getPayload() {
        return payload;
    }

    public UUID getId() {
        return id;
    }

    public enum Status {
        PENDENTE,
        ENVIADO
    }
}
