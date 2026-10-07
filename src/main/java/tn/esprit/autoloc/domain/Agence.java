package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    private String nom;

    private String ville;

    private String adresse;

    private String telephone;

    private String email;

    /*
     * Relation Agence -> Vehicule
     *
     * Une agence possède plusieurs véhicules.
     *
     * EAGER : le chargement d'une agence charge également
     * ses véhicules.
     *
     * PERSIST : les véhicules sont persistés avec l'agence.
     */
    @OneToMany(
            mappedBy = "agence",
            fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST
    )
    private Set<Vehicule> vehicules = new HashSet<>();

    /*
     * Relation Agence -> Employe
     *
     * Le chargement d'une agence ne charge PAS
     * automatiquement ses employés.
     *
     * La suppression d'une agence ne supprime PAS
     * ses employés.
     */
    @OneToMany(
            mappedBy = "agence",
            fetch = FetchType.LAZY
    )
    private Set<Employe> employes = new HashSet<>();
}
