package com.assistacrise.repository;

import com.assistacrise.domain.Crise;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Crise entity.
 */
@Repository
public interface CriseRepository extends JpaRepository<Crise, Long>, JpaSpecificationExecutor<Crise> {
    @Query("select crise from Crise crise where crise.declarant.login = ?#{authentication.name}")
    List<Crise> findByDeclarantIsCurrentUser();

    default Optional<Crise> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Crise> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Crise> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select crise from Crise crise left join fetch crise.declarant", countQuery = "select count(crise) from Crise crise")
    Page<Crise> findAllWithToOneRelationships(Pageable pageable);

    @Query("select crise from Crise crise left join fetch crise.declarant")
    List<Crise> findAllWithToOneRelationships();

    @Query("select crise from Crise crise left join fetch crise.declarant where crise.id =:id")
    Optional<Crise> findOneWithToOneRelationships(@Param("id") Long id);
}
