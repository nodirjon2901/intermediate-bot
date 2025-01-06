package uz.result.intermediatebot.domain.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.result.intermediatebot.domain.model.NewOption;
import uz.result.intermediatebot.domain.model.Photo;
import uz.result.intermediatebot.exception.LanguageNotSupportedException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewOptionMapper {

    Long id;

    String title;

    String body;

    Photo photo;

    Integer orderNum;

    public NewOptionMapper(NewOption option, String lang) {
        this.id = option.getId();
        this.photo = option.getPhoto();
        this.orderNum = option.getOrderNum();
        switch (lang.toLowerCase()) {
            case "uz": {
                this.title = option.getTitleUz();
                this.body = option.getBodyUz();
                break;
            }
            case "en": {
                this.title = option.getTitleEn();
                this.body = option.getBodyEn();
                break;
            }
            case "ru": {
                this.title = option.getTitleRu();
                this.body = option.getBodyRu();
                break;
            }
            default:
                throw new LanguageNotSupportedException("Language is not supported: " + lang);
        }
    }

//    public static NewOption updateDtoToEntity(NewOptionUpdateDto updateDto) {
//        if (updateDto == null) {
//            return null;
//        }
//        NewOption option = new NewOption();
//        option.setId(updateDto.getId());
//        option.setPhoto(updateDto.getPhoto());
//        option.setOrderNum(updateDto.getOrderNum());
//        if (updateDto.getTitle() != null) {
//            option.setTitleUz(updateDto.getTitle().getUz());
//            option.setTitleEn(updateDto.getTitle().getEn());
//            option.setTitleRu(updateDto.getTitle().getRu());
//        }
//        if (updateDto.getBody() != null) {
//            option.setBodyUz(updateDto.getBody().getUz());
//            option.setBodyEn(updateDto.getBody().getEn());
//            option.setBodyRu(updateDto.getBody().getRu());
//        }
//        return option;
//    }

}
