package uz.result.intermediatebot.domain.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewnessUpdateDto {

    Long id;

    boolean active;

    List<NewOptionUpdateDto> optionList;

}
