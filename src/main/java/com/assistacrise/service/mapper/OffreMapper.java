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
 * Mapper for the entity {@link Offre} and its DTO {@link OffreDTO}.
 */
@Mapper(componentModel = "spring")
public interface OffreMapper extends EntityMapper<OffreDTO, Offre> {
    @Mapping(target = "citoyen", source = "citoyen", qualifiedByName = "userLogin")
    @Mapping(target = "crise", source = "crise", qualifiedByName = "criseTitre")
    @Mapping(target = "demandes", source = "demandes", qualifiedByName = "demandeTitreSet")
    OffreDTO toDto(Offre s);

    @Mapping(target = "removeDemande", ignore = true)
    Offre toEntity(OffreDTO offreDTO);

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

    @Named("demandeTitre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titre", source = "titre")
    DemandeDTO toDtoDemandeTitre(Demande demande);

    @Named("demandeTitreSet")
    default Set<DemandeDTO> toDtoDemandeTitreSet(Set<Demande> demande) {
        return demande.stream().map(this::toDtoDemandeTitre).collect(Collectors.toSet());
    }
}
