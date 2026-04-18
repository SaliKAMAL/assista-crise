package com.assistacrise.service.mapper;

import com.assistacrise.domain.Crise;
import com.assistacrise.domain.Information;
import com.assistacrise.domain.User;
import com.assistacrise.service.dto.CriseDTO;
import com.assistacrise.service.dto.InformationDTO;
import com.assistacrise.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Information} and its DTO {@link InformationDTO}.
 */
@Mapper(componentModel = "spring")
public interface InformationMapper extends EntityMapper<InformationDTO, Information> {
    @Mapping(target = "auteur", source = "auteur", qualifiedByName = "userLogin")
    @Mapping(target = "crise", source = "crise", qualifiedByName = "criseTitre")
    InformationDTO toDto(Information s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("criseTitre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titre", source = "titre")
    CriseDTO toDtoCriseTitre(Crise crise);
}
