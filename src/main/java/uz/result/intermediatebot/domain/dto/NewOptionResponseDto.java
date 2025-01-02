package uz.result.intermediatebot.domain.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.result.intermediatebot.domain.model.NewOption;
import uz.result.intermediatebot.domain.model.Photo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewOptionResponseDto {

    Long id;

    LanguageDto title;

    LanguageDto body;

    Photo photo;

    Integer orderNum;

    public NewOptionResponseDto(NewOption option) {
        this.id = option.getId();
        this.photo = option.getPhoto();
        this.orderNum = option.getOrderNum();
        this.title = new LanguageDto(option.getTitleUz(), option.getTitleRu(), option.getTitleEn());
        this.body = new LanguageDto(option.getBodyUz(), option.getBodyRu(), option.getBodyEn());
    }

}
