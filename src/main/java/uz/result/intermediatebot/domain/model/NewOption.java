package uz.result.intermediatebot.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.result.intermediatebot.domain.dto.NewOptionCreateDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "new_option")
public class NewOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 500)
    String titleUz;

    @Column(length = 500)
    String titleRu;

    @Column(length = 500)
    String titleEn;

    @Column(length = 5000)
    String bodyUz;

    @Column(length = 5000)
    String bodyRu;

    @Column(length = 5000)
    String bodyEn;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    Photo photo;

    Integer orderNum;

    @ManyToOne
    @JsonIgnore
    Newness newness;

    public NewOption(NewOptionCreateDto createDto) {
        if (createDto == null) {
            return;
        }
        if (createDto.getTitle() != null) {
            this.titleUz = createDto.getTitle().getUz();
            this.titleRu = createDto.getTitle().getRu();
            this.titleEn = createDto.getTitle().getEn();
        }
        if (createDto.getBody() != null) {
            this.bodyUz = createDto.getBody().getUz();
            this.bodyRu = createDto.getBody().getRu();
            this.bodyEn = createDto.getBody().getEn();
        }
        this.orderNum = createDto.getOrderNum();
    }
}
