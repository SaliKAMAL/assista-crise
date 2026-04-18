package com.assistacrise.repository;

import com.assistacrise.domain.Offre;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface OffreRepositoryWithBagRelationships {
    Optional<Offre> fetchBagRelationships(Optional<Offre> offre);

    List<Offre> fetchBagRelationships(List<Offre> offres);

    Page<Offre> fetchBagRelationships(Page<Offre> offres);
}
