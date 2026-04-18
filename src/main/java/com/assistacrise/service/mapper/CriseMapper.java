package com.assistacrise.service.mapper;

import com.assistacrise.domain.Crise;
import com.assistacrise.domain.User;
import com.assistacrise.service.dto.CriseDTO;
import com.assistacrise.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Crise} and its DTO {@link CriseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CriseMapper extends EntityMapper<CriseDTO, Crise> {
    @Mapping(target = "declarant", source = "declarant", qualifiedByName = "userLogin")
    CriseDTO toDto(Crise s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
