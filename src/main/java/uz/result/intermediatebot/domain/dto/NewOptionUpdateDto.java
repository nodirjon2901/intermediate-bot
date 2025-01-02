package uz.result.intermediatebot.domain.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.result.intermediatebot.domain.model.Photo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewOptionUpdateDto {

    Long id;

    LanguageDto title;

    LanguageDto body;

    Photo photo;

    Integer orderNum;

}
