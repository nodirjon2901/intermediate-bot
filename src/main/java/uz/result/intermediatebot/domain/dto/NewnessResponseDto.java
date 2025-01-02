package uz.result.intermediatebot.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.result.intermediatebot.domain.model.Newness;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewnessResponseDto {

    Long id;

    String slug;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    Date createdDate;

    boolean active;

    List<NewOptionResponseDto> optionList;

    public NewnessResponseDto(Newness newness) {
        this.id = newness.getId();
        this.slug = newness.getSlug();
        this.createdDate = newness.getCreatedDate();
        this.active = newness.isActive();
        this.optionList = newness.getOptionList().stream().map(NewOptionResponseDto::new).collect(Collectors.toList());
    }

}

