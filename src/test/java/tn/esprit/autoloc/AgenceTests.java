package tn.esprit.autoloc;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;

@SpringBootTest
@EnableJpaRepositories(considerNestedRepositories = true)
public class AgenceTests {

    // =========================================================
    // REPOSITORIES
    // =========================================================

    // Repository basique
    @Autowired
    private AgenceRepository basicAgenceRepository;

    // Repository complet
    @Autowired
    private IAgenceRepository fullAgenceRepository;

    @Autowired
    private VehiculeRepository vehiculeRepository;

    @Autowired
    private EntityManager entityManager;

    // Repository Mock demandé par l'atelier
    @Repository
    interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
    }

    // =========================================================
    // METHODES UTILITAIRES
    // =========================================================

    private Agence creerAgence() {

        Agence agence = new Agence();

        agence.setNom("Agence Test");
        agence.setAdresse("10 Avenue Habib Bourguiba");
        agence.setVille("Tunis");
        agence.setTelephone("71000000");
        agence.setEmail(
                "contact_" + System.currentTimeMillis()
                        + "@agencetunis.tn"
        );

        return agence;
    }

    private Vehicule creerVehicule() {

        Vehicule vehicule = new Vehicule();

        vehicule.setImmatriculation(
                UUID.randomUUID().toString().substring(0, 15)
        );

        vehicule.setMarque("Peugeot");
        vehicule.setModele("208");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("80.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);

        return vehicule;
    }

    // =========================================================
    // QUESTIONS 4, 5 ET 6
    // =========================================================

    private void addAgence(
            CrudRepository<Agence, Long> repository) {

        Agence agence = new Agence();

        agence.setAdresse("1 Rue Hedi");
        agence.setNom("Agence ariana");
        agence.setTelephone("71585874");
        agence.setVille("Tunis");

        // Question 5 :
        // conversion de System.currentTimeMillis() en int
        // pour éviter les doublons
        int ms = (int) System.currentTimeMillis();

        agence.setEmail(
                "ariana_" + ms + "@autoloc.tn"
        );

        // -----------------------------------------------------
        // VEHICULE 1
        // -----------------------------------------------------

        Vehicule vehicule1 = new Vehicule();

        vehicule1.setCategorie(CategorieVehicule.SUV);
        vehicule1.setImmatriculation(
                "785414TU96" + ms
        );
        vehicule1.setMarque("Isuzu");
        vehicule1.setModele("DMax");
        vehicule1.setStatut(
                StatutVehicule.EN_MAINTENANCE
        );
        vehicule1.setTarifJournalier(
                new BigDecimal("100.00")
        );
        vehicule1.setAgence(agence);

        // -----------------------------------------------------
        // VEHICULE 2
        // -----------------------------------------------------

        Vehicule vehicule2 = new Vehicule();

        vehicule2.setCategorie(
                CategorieVehicule.UTILITAIRE
        );
        vehicule2.setImmatriculation(
                "785414TU95" + ms
        );
        vehicule2.setMarque("Toyota");
        vehicule2.setModele("Yaris");
        vehicule2.setStatut(
                StatutVehicule.DISPONIBLE
        );
        vehicule2.setTarifJournalier(
                new BigDecimal("80.00")
        );
        vehicule2.setAgence(agence);

        // -----------------------------------------------------
        // ASSOCIATION AGENCE <-> VEHICULES
        // -----------------------------------------------------

        agence.getVehicules().add(vehicule1);
        agence.getVehicules().add(vehicule2);

        // Sauvegarde avec le repository reçu en paramètre
        Agence saved = repository.save(agence);

        // -----------------------------------------------------
        // VERIFICATIONS
        // -----------------------------------------------------

        assertNotNull(saved.getIdAgence());

        assertEquals(
                2,
                saved.getVehicules().size()
        );

        assertTrue(
                repository
                        .findById(saved.getIdAgence())
                        .isPresent()
        );
    }

    // =========================================================
    // QUESTION 7 : BASIC ADD
    // =========================================================

    @Test
    void basicAddAgence() {

        addAgence(
                basicAgenceRepository
        );
    }

    // =========================================================
    // QUESTION 7 : FULL ADD
    // =========================================================

    @Test
    void fullAddAgence() {

        addAgence(
                fullAgenceRepository
        );
    }

    // =========================================================
    // TEST PRECEDENT 1
    // Un véhicule peut exister sans agence
    // =========================================================

    @Test
    void vehiculeSansAgence() {

        Vehicule vehicule = creerVehicule();

        vehicule.setAgence(null);

        Vehicule saved =
                vehiculeRepository.save(vehicule);

        assertNotNull(
                saved.getIdVehicule()
        );

        assertNull(
                vehiculeRepository
                        .findById(
                                saved.getIdVehicule()
                        )
                        .get()
                        .getAgence()
        );
    }

    // =========================================================
    // TEST PRECEDENT 2
    // CascadeType.PERSIST
    // =========================================================

    @Test
    void ajoutVehiculeEnregistreAgence() {

        Agence agence = creerAgence();

        Vehicule vehicule = creerVehicule();

        vehicule.setAgence(agence);

        Vehicule saved =
                vehiculeRepository.save(vehicule);

        assertNotNull(
                saved.getIdVehicule()
        );

        assertNotNull(
                agence.getIdAgence()
        );

        assertTrue(
                basicAgenceRepository.existsById(
                        agence.getIdAgence()
                )
        );
    }

    // =========================================================
    // TEST PRECEDENT 3
    // FetchType.EAGER
    // =========================================================

    @Test
    void chargementAgenceChargeVehicules() {

        Agence agence = creerAgence();

        Vehicule v1 = creerVehicule();

        v1.setAgence(agence);

        agence.getVehicules().add(v1);

        vehiculeRepository.save(v1);

        entityManager.flush();

        entityManager.clear();

        Agence chargee =
                basicAgenceRepository
                        .findById(
                                agence.getIdAgence()
                        )
                        .orElseThrow();

        assertEquals(
                1,
                chargee.getVehicules().size()
        );
    }

    // =========================================================
    // QUESTION 10 :
    // LOAD AGENCE
    // =========================================================

    private void loadAgence(
            CrudRepository<Agence, Long> repository,
            String typeRepository) {

        StringBuilder sb = new StringBuilder();

        sb.append("Dépôt utilisé : ")
                .append(typeRepository)
                .append("\n");

        Iterable<Agence> agences =
                repository.findAll();

        for (Agence agence : agences) {

            sb.append("Agence ")
                    .append(agence.getIdAgence())
                    .append(" : ")
                    .append(agence.getNom())
                    .append("\n");

            sb.append("  Nombre de vehicules : ")
                    .append(
                            agence.getVehicules().size()
                    )
                    .append("\n");

            for (Vehicule vehicule :
                    agence.getVehicules()) {

                sb.append("  - ")
                        .append(
                                vehicule.getIdVehicule()
                        )
                        .append(" / ")
                        .append(
                                vehicule.getImmatriculation()
                        )
                        .append("\n");
            }
        }

        Assertions.fail(
                sb.toString()
        );
    }

    // =========================================================
    // QUESTION 10 : BASIC LOAD
    // =========================================================

    @Test
    void basicLoadAgence() {

        loadAgence(
                basicAgenceRepository,
                "basic (AgenceRepository / CrudRepository)"
        );
    }

    // =========================================================
    // QUESTION 10 : FULL LOAD
    // =========================================================

    @Test
    void fullLoadAgence() {

        loadAgence(
                fullAgenceRepository,
                "full (IAgenceRepository / JpaRepository)"
        );
    }

    // =========================================================
    // QUESTIONS 11 → 14
    // AGENCES TRIEES PAR ID DECROISSANT
    // =========================================================

    @Test
    void loadSortedAgences() {

        Iterable<Agence> agences =
                fullAgenceRepository.findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "idAgence"
                        )
                );

        StringBuilder sb = new StringBuilder();

        sb.append(
                "Dépôt utilisé : full (IAgenceRepository / JpaRepository)\n"
        );

        sb.append(
                "Agences triées par id décroissant :\n"
        );

        for (Agence agence : agences) {

            // Informations de l'agence uniquement
            // Aucun détail sur les véhicules
            sb.append("Agence ")
                    .append(agence.getIdAgence())
                    .append(" : ")
                    .append(agence.getNom())
                    .append(" | Ville : ")
                    .append(agence.getVille())
                    .append(" | Adresse : ")
                    .append(agence.getAdresse())
                    .append("\n");
        }

        // Affichage volontaire avec AssertionFailedError
        Assertions.fail(
                sb.toString()
        );
    }

    // =========================================================
    // QUESTIONS 15 → 18
    // PAGINATION PAR LOT DE 2
    // =========================================================

    @Test
    void loadPagedAgences() {

        int pageNumber = 0;
        int pageSize = 2;

        StringBuilder sb = new StringBuilder();

        // Première page pour connaître le nombre total de pages
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.DESC, "idAgence")
        );

        Page<Agence> page =
                fullAgenceRepository.findAll(pageable);

        sb.append("Total pages : ")
                .append(page.getTotalPages())
                .append("\n");

        // Parcourir toutes les pages
        while (pageNumber < page.getTotalPages()) {

            sb.append("--- Page en cours : ")
                    .append(pageNumber)
                    .append("\n");

            // Afficher les agences de la page
            for (Agence agence : page.getContent()) {

                sb.append(agence.getIdAgence())
                        .append(" | ")
                        .append(agence.getNom())
                        .append("\n");
            }

            // Passer à la page suivante
            pageNumber++;

            if (pageNumber < page.getTotalPages()) {

                pageable = PageRequest.of(
                        pageNumber,
                        pageSize,
                        Sort.by(
                                Sort.Direction.DESC,
                                "idAgence"
                        )
                );

                page = fullAgenceRepository.findAll(pageable);
            }
        }

        // Affichage dans AssertionError
        Assertions.fail(sb.toString());
    }

}