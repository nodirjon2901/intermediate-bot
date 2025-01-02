package uz.result.intermediatebot.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.result.intermediatebot.domain.model.Photo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDto {

    private Long id;

    private String url;

    public PhotoDto(Photo photo) {
        if (photo == null) {
            return;
        }
        this.id = photo.getId();
        this.url = photo.getHttpUrl();
    }

}
