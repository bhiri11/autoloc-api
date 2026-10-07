package tn.esprit.autoloc;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;

@SpringBootTest
@EnableJpaRepositories(considerNestedRepositories = true) // <-- Autorise le scan des interfaces internes
@Transactional
public class AgenceTests {

    @Autowired
    private AgenceRepository agenceRepository;

    @Autowired
    private VehiculeRepository vehiculeRepository;

    @Autowired
    private EntityManager entityManager;

    @Repository
    interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
    }

    private Agence creerAgence() {
        Agence agence = new Agence();
        agence.setNom("Agence Test");
        agence.setAdresse("10 Avenue Habib Bourguiba");
        agence.setVille("Tunis");
        agence.setTelephone("71000000");
        agence.setEmail("contact_" + System.currentTimeMillis() + "@agencetunis.tn");
        return agence;
    }

    private Vehicule creerVehicule() {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(UUID.randomUUID().toString().substring(0, 15));
        vehicule.setMarque("Peugeot");
        vehicule.setModele("208");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("80.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        return vehicule;
    }

    // Question 6, 7 & 8 : Insertion de l'agence et de ses deux véhicules (données exactes de l'atelier)
    @Test
    void addAgence() {
        Agence agence = new Agence();
        agence.setAdresse("1 Rue Hedi");
        agence.setNom("Agence ariana");
        agence.setTelephone("71585874");
        agence.setVille("Tunis");
        agence.setEmail("ariana_" + System.currentTimeMillis() + "@autoloc.tn");

        Vehicule vehicule1 = new Vehicule();
        vehicule1.setCategorie(CategorieVehicule.SUV);
        vehicule1.setImmatriculation("785414TU96");
        vehicule1.setMarque("Isuzu");
        vehicule1.setModele("DMax");
        vehicule1.setStatut(StatutVehicule.EN_MAINTENANCE);
        vehicule1.setTarifJournalier(new BigDecimal("100.00"));
        vehicule1.setAgence(agence);

        Vehicule vehicule2 = new Vehicule();
        vehicule2.setCategorie(CategorieVehicule.UTILITAIRE);
        vehicule2.setImmatriculation("785414TU95");
        vehicule2.setMarque("Toyota");
        vehicule2.setModele("Yaris");
        vehicule2.setStatut(StatutVehicule.DISPONIBLE);
        vehicule2.setTarifJournalier(new BigDecimal("80.00"));
        vehicule2.setAgence(agence);

        agence.getVehicules().add(vehicule1);
        agence.getVehicules().add(vehicule2);

        Agence saved = agenceRepository.save(agence);

        assertNotNull(saved.getIdAgence());
        assertEquals(2, saved.getVehicules().size());
        assertTrue(agenceRepository.findById(saved.getIdAgence()).isPresent());
    }

    // Contrainte 1 : un véhicule peut exister sans agence
    @Test
    void vehiculeSansAgence() {
        Vehicule vehicule = creerVehicule();
        vehicule.setAgence(null);

        Vehicule saved = vehiculeRepository.save(vehicule);

        assertNotNull(saved.getIdVehicule());
        assertNull(vehiculeRepository.findById(saved.getIdVehicule()).get().getAgence());
    }

    // Contrainte 2 : l'ajout d'un véhicule enregistre son agence (cascade PERSIST)
    @Test
    void ajoutVehiculeEnregistreAgence() {
        Agence agence = creerAgence();
        Vehicule vehicule = creerVehicule();
        vehicule.setAgence(agence);

        Vehicule saved = vehiculeRepository.save(vehicule);

        assertNotNull(saved.getIdVehicule());
        assertNotNull(agence.getIdAgence());
        assertTrue(agenceRepository.existsById(agence.getIdAgence()));
    }

    // Contrainte 3 : le chargement d'une agence charge ses véhicules (EAGER)
    @Test
    void chargementAgenceChargeVehicules() {
        Agence agence = creerAgence();
        Vehicule v1 = creerVehicule();

        v1.setAgence(agence);
        agence.getVehicules().add(v1);

        vehiculeRepository.save(v1);

        entityManager.flush();
        entityManager.clear();

        Agence chargee = agenceRepository.findById(agence.getIdAgence()).orElseThrow();

        assertEquals(1, chargee.getVehicules().size());
    }

    // Questions 11 à 15 : Affichage structuré du chargement d'agence
// Questions 11 à 15 : Affichage structuré du chargement d'agence
    @Test
    void loadAgence() {
        //  Utiliser l'instance 'agenceRepository' (avec 'a' minuscule) et non la classe/interface
        Iterable<Agence> agences = agenceRepository.findAll();
        StringBuilder sb = new StringBuilder("\n");

        for (Agence agence : agences) {
            sb.append(agence.getIdAgence()).append(" ").append(agence.getNom()).append("\n");
            sb.append("Vehicules Count: ").append(agence.getVehicules().size()).append("\n");

            for (Vehicule vehicule : agence.getVehicules()) {
                sb.append("=== ")
                        .append(vehicule.getIdVehicule())
                        .append(" ")
                        .append(vehicule.getImmatriculation())
                        .append("\n");
            }
        }

System.out.println(sb.toString());    }
}