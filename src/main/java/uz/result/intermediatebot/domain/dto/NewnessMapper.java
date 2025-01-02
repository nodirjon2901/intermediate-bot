package uz.result.intermediatebot.domain.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.result.intermediatebot.domain.model.Newness;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewnessMapper {

    Long id;

    String slug;

    Date createdDate;

    boolean active;

    List<NewOptionMapper> optionList;

    public NewnessMapper(Newness newness, String lang) {
        this.id = newness.getId();
        this.slug = newness.getSlug();
        this.createdDate = newness.getCreatedDate();
        this.active = newness.isActive();
        this.optionList = newness.getOptionList().stream().map(
                option -> new NewOptionMapper(option, lang)
        ).collect(Collectors.toList());
    }

    public static Newness updateDtoToEntity(NewnessUpdateDto updateDTO) {
        if (updateDTO == null) {
            return null;
        }
        Newness newness = new Newness();
        newness.setId(updateDTO.getId());
        newness.setActive(updateDTO.isActive());
        if (updateDTO.getOptionList() != null && !updateDTO.getOptionList().isEmpty()) {
            newness.setOptionList(
                    updateDTO.getOptionList().stream().map(
                            NewOptionMapper::updateDtoToEntity
                    ).collect(Collectors.toList())
            );
        }
        return newness;
    }

}
