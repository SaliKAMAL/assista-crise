package com.assistacrise.service.mapper;

import com.assistacrise.domain.Demande;
import com.assistacrise.domain.MessageChat;
import com.assistacrise.domain.User;
import com.assistacrise.service.dto.DemandeDTO;
import com.assistacrise.service.dto.MessageChatDTO;
import com.assistacrise.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MessageChat} and its DTO {@link MessageChatDTO}.
 */
@Mapper(componentModel = "spring")
public interface MessageChatMapper extends EntityMapper<MessageChatDTO, MessageChat> {
    @Mapping(target = "auteur", source = "auteur", qualifiedByName = "userLogin")
    @Mapping(target = "demande", source = "demande", qualifiedByName = "demandeTitre")
    MessageChatDTO toDto(MessageChat s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("demandeTitre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titre", source = "titre")
    DemandeDTO toDtoDemandeTitre(Demande demande);
}
