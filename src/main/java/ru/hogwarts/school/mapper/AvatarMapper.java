package ru.hogwarts.school.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import ru.hogwarts.school.dto.AvatarDTO;
import ru.hogwarts.school.model.Avatar;

public class AvatarMapper {
    private static final ModelMapper mapper = new ModelMapper();

    public AvatarMapper() {
        mapper.addMappings(new PropertyMap<AvatarDTO, Avatar>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });
    }

    public static Avatar mapFromDTO(AvatarDTO avatarDTO) {
        return mapper.map(avatarDTO, Avatar.class);
    }

    public static AvatarDTO mapToDTO(Avatar avatar) {
        return mapper.map(avatar, AvatarDTO.class);
    }
}
