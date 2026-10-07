package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "contrat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrat;

    private LocalDate dateSignature;

    private BigDecimal montantTotal;

    private boolean valide;

    // Relation Contrat -> Paiement
    @OneToMany(
            mappedBy = "contrat",
            fetch = FetchType.EAGER
    )
    private List<Paiement> paiements;

    // Relation Contrat -> Reservation
    @OneToOne(
            mappedBy = "contrat",
            fetch = FetchType.LAZY
    )
    private Reservation reservation;
}
