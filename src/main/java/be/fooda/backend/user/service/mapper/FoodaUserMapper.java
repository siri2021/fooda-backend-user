package be.fooda.backend.user.service.mapper;

import be.fooda.backend.user.model.create.UserCreate;
import be.fooda.backend.user.model.entity.UserEntity;
import be.fooda.backend.user.model.update.UserUpdate;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface FoodaUserMapper {

    UserEntity toEntity(UserCreate from);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserEntity toEntity(UserUpdate from, @MappingTarget UserEntity to);

    UserCreate toDTO(UserEntity from);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserUpdate toDTO(UserEntity from, @MappingTarget UserUpdate to);
}
