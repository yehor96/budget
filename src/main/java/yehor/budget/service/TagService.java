package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.Tag;
import yehor.budget.repository.TagRepository;
import yehor.budget.web.converter.TagConverter;
import yehor.budget.web.dto.full.TagFullDto;
import yehor.budget.web.dto.limited.TagLimitedDto;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private static final Logger LOG = LogManager.getLogger(TagService.class);

    private final TagConverter tagConverter;
    private final TagRepository tagRepository;

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
        LOG.info("{} is saved", tag);
    }

    public void delete(Long id) {
        try {
            tagRepository.deleteById(id);
            LOG.info("Tag with id {} is deleted", id);
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
        LOG.info("{} is updated", tag);
    }

    private void validateNotExists(Tag tag) {
        tagRepository.findByName(tag.getName())
                .ifPresent(e -> {
                    throw new ObjectAlreadyExistsException("Tag " + tag.getName() + " already exists");
                });
    }

    private void validateExists(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ObjectNotFoundException("Tag with id " + id + " does not exist");
        }
    }
}
