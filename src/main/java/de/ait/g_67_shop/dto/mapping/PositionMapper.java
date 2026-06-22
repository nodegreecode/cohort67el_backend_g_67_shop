package de.ait.g_67_shop.dto.mapping;

import de.ait.g_67_shop.domain.Position;
import de.ait.g_67_shop.dto.position.PositionDto;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface PositionMapper {

    PositionDto mapEntityToDto(Position entity);

    Set<PositionDto> mapEntityToDto(Set<Position> positions);
}
