package com.assistacrise.service.mapper;

import com.assistacrise.domain.Crise;
import com.assistacrise.domain.Demande;
import com.assistacrise.domain.Offre;
import com.assistacrise.domain.User;
import com.assistacrise.service.dto.CriseDTO;
import com.assistacrise.service.dto.DemandeDTO;
import com.assistacrise.service.dto.OffreDTO;
import com.assistacrise.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Demande} and its DTO {@link DemandeDTO}.
 */
@Mapper(componentModel = "spring")
public interface DemandeMapper extends EntityMapper<DemandeDTO, Demande> {
    @Mapping(target = "sinistre", source = "sinistre", qualifiedByName = "userLogin")
    @Mapping(target = "crise", source = "crise", qualifiedByName = "criseTitre")
    @Mapping(target = "offres", source = "offres", qualifiedByName = "offreIdSet")
    DemandeDTO toDto(Demande s);

    @Mapping(target = "offres", ignore = true)
    @Mapping(target = "removeOffre", ignore = true)
    Demande toEntity(DemandeDTO demandeDTO);

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

    @Named("offreId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OffreDTO toDtoOffreId(Offre offre);

    @Named("offreIdSet")
    default Set<OffreDTO> toDtoOffreIdSet(Set<Offre> offre) {
        return offre.stream().map(this::toDtoOffreId).collect(Collectors.toSet());
    }
}
