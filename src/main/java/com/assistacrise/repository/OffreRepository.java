package com.assistacrise.repository;

import com.assistacrise.domain.Offre;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Offre entity.
 *
 * When extending this class, extend OffreRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface OffreRepository extends OffreRepositoryWithBagRelationships, JpaRepository<Offre, Long>, JpaSpecificationExecutor<Offre> {
    @Query("select offre from Offre offre where offre.citoyen.login = ?#{authentication.name}")
    List<Offre> findByCitoyenIsCurrentUser();

    default Optional<Offre> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Offre> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Offre> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select offre from Offre offre left join fetch offre.citoyen left join fetch offre.crise",
        countQuery = "select count(offre) from Offre offre"
    )
    Page<Offre> findAllWithToOneRelationships(Pageable pageable);

    @Query("select offre from Offre offre left join fetch offre.citoyen left join fetch offre.crise")
    List<Offre> findAllWithToOneRelationships();

    @Query("select offre from Offre offre left join fetch offre.citoyen left join fetch offre.crise where offre.id =:id")
    Optional<Offre> findOneWithToOneRelationships(@Param("id") Long id);
}
