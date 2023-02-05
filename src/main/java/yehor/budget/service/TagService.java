package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.Tag;
import yehor.budget.repository.TagRepository;
import yehor.budget.web.converter.TagConverter;
import yehor.budget.web.dto.full.TagFullDto;
import yehor.budget.web.dto.limited.TagLimitedDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagConverter tagConverter;

    public List<TagFullDto> getAll() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(tagConverter::convert)
                .toList();
    }

    public void save(TagLimitedDto tagDto) {
        Tag tag = tagConverter.convert(tagDto);
        validateNotExists(tag);
        tagRepository.save(tag);
        log.info("Saved: {}", tag);
    }

    public void delete(Long id) {
        try {
            tagRepository.deleteById(id);
            log.info("Tag with id {} is deleted", id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Tag with id " + id + " not found");
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Cannot delete tag with dependent expenses");
        }
    }

    @Transactional
    public void update(TagFullDto tagDto) {
        validateExists(tagDto.getId());
        Tag tag = tagConverter.convert(tagDto);
        tagRepository.save(tag);
        log.info("Updated: {}", tag);
    }

    private void validateNotExists(Tag tag) {
        if (tagRepository.existsByName(tag.getName())) {
            throw new ObjectAlreadyExistsException("Tag " + tag.getName() + " already exists");
        }
    }

    private void validateExists(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ObjectNotFoundException("Tag with id " + id + " does not exist");
        }
    }
}
