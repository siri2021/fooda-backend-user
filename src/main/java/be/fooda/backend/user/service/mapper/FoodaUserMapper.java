package be.fooda.backend.user.service.mapper;

import be.fooda.backend.user.model.create.FoodaUserCreate;
import be.fooda.backend.user.model.entity.FoodaUser;
import be.fooda.backend.user.model.update.FoodaUserUpdate;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface FoodaUserMapper {

    FoodaUser fromCreateToEntity(FoodaUserCreate from);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FoodaUser fromUpdateToEntity(FoodaUserUpdate from, @MappingTarget FoodaUser to);

    FoodaUserCreate fromEntityToCreate(FoodaUser from);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FoodaUserUpdate fromEntityToUpdate(FoodaUser from, @MappingTarget FoodaUserUpdate to);
}
