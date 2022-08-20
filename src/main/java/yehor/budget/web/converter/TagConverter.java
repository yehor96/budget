package yehor.budget.web.converter;

import org.springframework.stereotype.Component;
import yehor.budget.entity.Tag;
import yehor.budget.web.dto.full.TagFullDto;
import yehor.budget.web.dto.limited.TagLimitedDto;

@Component
public class TagConverter {
    public TagFullDto convert(Tag tag) {
        return TagFullDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    public Tag convert(TagLimitedDto tagDto) {
        return Tag.builder()
                .name(tagDto.getName())
                .build();
    }

    public Tag convert(TagFullDto tagDto) {
        return Tag.builder()
                .id(tagDto.getId())
                .name(tagDto.getName())
                .build();
    }
}
