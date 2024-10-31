package ru.hogwarts.school.mapper;

import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeToken;
import ru.hogwarts.school.dto.AvatarDTO;
import ru.hogwarts.school.model.Avatar;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class AvatarMapper {
    private static final ModelMapper mapper = new ModelMapper();

    public AvatarMapper() {
        mapper.addMappings(new PropertyMap<AvatarDTO, Avatar>() {
            @SneakyThrows
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });
    }

    public static Avatar mapFromDTO(AvatarDTO avatarDTO) {
        return mapper.map(avatarDTO, Avatar.class);
    }

    @SneakyThrows
    public static AvatarDTO mapToDTO(Avatar avatar) {
        AvatarDTO avatarDTO = mapper.map(avatar, AvatarDTO.class);
        String path = avatar.getPath();
        avatarDTO.setName(getNameFromPath(path));
        Path file = Path.of(path);
        byte[] data = Files.readAllBytes(file);
        avatarDTO.setData(data);
        return avatarDTO;
    }

    public static Collection<AvatarDTO> mapToDTO(Collection<Avatar> avatars) {
        Type collectionType = new TypeToken<Collection<AvatarDTO>>() {
        }.getType();
        Collection<AvatarDTO> mapAvatarDTOS = mapper.map(avatars, collectionType);
        mapAvatarDTOS.forEach(a -> {
            String name = getNameFromPath(a.getPath());
            a.setName(name);
        });
        return mapAvatarDTOS;
    }

    public static Collection<Avatar> mapFromDTO(Collection<AvatarDTO> avatarDTOS) {
        Type collectionType = new TypeToken<Collection<Avatar>>() {
        }.getType();
        return mapper.map(avatarDTOS, collectionType);
    }

    private static String getNameFromPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
