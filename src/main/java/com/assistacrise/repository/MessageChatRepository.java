package com.assistacrise.repository;

import com.assistacrise.domain.MessageChat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MessageChat entity.
 */
@Repository
public interface MessageChatRepository extends JpaRepository<MessageChat, Long> {
    @Query("select messageChat from MessageChat messageChat where messageChat.auteur.login = ?#{authentication.name}")
    List<MessageChat> findByAuteurIsCurrentUser();

    default Optional<MessageChat> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MessageChat> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MessageChat> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select messageChat from MessageChat messageChat left join fetch messageChat.auteur left join fetch messageChat.demande",
        countQuery = "select count(messageChat) from MessageChat messageChat"
    )
    Page<MessageChat> findAllWithToOneRelationships(Pageable pageable);

    @Query("select messageChat from MessageChat messageChat left join fetch messageChat.auteur left join fetch messageChat.demande")
    List<MessageChat> findAllWithToOneRelationships();

    @Query(
        "select messageChat from MessageChat messageChat left join fetch messageChat.auteur left join fetch messageChat.demande where messageChat.id =:id"
    )
    Optional<MessageChat> findOneWithToOneRelationships(@Param("id") Long id);
}
