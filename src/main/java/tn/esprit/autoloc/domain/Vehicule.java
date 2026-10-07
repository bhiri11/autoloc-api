package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    @NotNull
    @Column(nullable = false)
    private String immatriculation;

    @NotNull
    @Column(nullable = false)
    private String marque;

    @NotNull
    @Column(nullable = false)
    private String modele;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutVehicule statut;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieVehicule categorie;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifJournalier;

    /*
     * Relation Vehicule -> Agence
     *
     * Plusieurs véhicules peuvent appartenir
     * à une même agence.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            optional = true
    )
    @JoinColumn(name = "id_agence", nullable = true)
    private Agence agence;

    /*
     * Relation Vehicule -> Reservation
     *
     * Le chargement d'un véhicule ne charge PAS
     * ses réservations.
     *
     * La suppression d'un véhicule supprime
     * ses réservations.
     */
    @OneToMany(
            mappedBy = "vehicule",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    private List<Reservation> reservations;

    /*
     * Relation Vehicule -> Maintenance
     *
     * Un véhicule peut avoir plusieurs maintenances.
     */
    @OneToMany(
            mappedBy = "vehicule",
            fetch = FetchType.LAZY
    )
    private List<Maintenance> maintenances;

    /*
     * Relation Vehicule <-> Equipement
     *
     * Un véhicule peut avoir plusieurs équipements
     * et un équipement peut être associé à plusieurs véhicules.
     *
     * Pas de cascade :
     * les opérations sur Vehicule et Equipement
     * restent indépendantes.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "vehicule_equipement",
            joinColumns = @JoinColumn(name = "id_vehicule"),
            inverseJoinColumns = @JoinColumn(name = "id_equipement")
    )
    private Set<Equipement> equipements = new HashSet<>();
}