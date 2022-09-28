package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.Tag;
import yehor.budget.repository.TagRepository;
import yehor.budget.web.converter.TagConverter;
import yehor.budget.web.dto.full.TagFullDto;
import yehor.budget.web.dto.limited.TagLimitedDto;

import java.util.List;
import java.util.Optional;

import static common.factory.TagFactory.defaultTag;
import static common.factory.TagFactory.defaultTagFullDto;
import static common.factory.TagFactory.defaultTagLimitedDto;
import static common.factory.TagFactory.secondTag;
import static common.factory.TagFactory.secondTagFullDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TagServiceTest {

    private final TagRepository tagRepositoryMock = mock(TagRepository.class);
    private final TagConverter tagConverterMock = mock(TagConverter.class);

    private final TagService tagService = new TagService(tagRepositoryMock, tagConverterMock);

    @Test
    void testGetAll() {
        TagFullDto expectedDto1 = defaultTagFullDto();
        TagFullDto expectedDto2 = secondTagFullDto();
        Tag tag1 = defaultTag();
        Tag tag2 = secondTag();

        when(tagRepositoryMock.findAll()).thenReturn(List.of(tag1, tag2));
        when(tagConverterMock.convert(tag1)).thenReturn(expectedDto1);
        when(tagConverterMock.convert(tag2)).thenReturn(expectedDto2);

        List<TagFullDto> tags = tagService.getAll();

        assertTrue(tags.contains(expectedDto1));
        assertTrue(tags.contains(expectedDto2));
    }

    @Test
    void testSave() {
        TagLimitedDto tagLimitedDto = defaultTagLimitedDto();
        Tag tag = defaultTag();

        when(tagConverterMock.convert(tagLimitedDto)).thenReturn(tag);

        tagService.save(tagLimitedDto);

        verify(tagRepositoryMock, times(1))
                .save(tag);
    }

    @Test
    void testTrySavingExistingTag() {
        TagLimitedDto expectedTagDto = defaultTagLimitedDto();
        Tag expectedTag = defaultTag();

        when(tagConverterMock.convert(expectedTagDto)).thenReturn(expectedTag);
        when(tagRepositoryMock.existsByName(expectedTag.getName())).thenReturn(true);

        try {
            tagService.save(expectedTagDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectAlreadyExistsException.class, e.getClass());
            ObjectAlreadyExistsException exception = (ObjectAlreadyExistsException) e;
            assertEquals("Tag " + expectedTag.getName() + " already exists", exception.getMessage());
            verify(tagRepositoryMock, never())
                    .save(expectedTag);
        }
    }

    @Test
    void testDeleteTag() {
        tagService.delete(1L);
        verify(tagRepositoryMock, times(1))
                .deleteById(1L);
    }

    @Test
    void testTryDeletingNotExistingTag() {
        doThrow(new EmptyResultDataAccessException(1)).when(tagRepositoryMock).deleteById(1L);

        try {
            tagService.delete(1L);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Tag with id " + 1L + " not found", exception.getMessage());
            verify(tagRepositoryMock, times(1))
                    .deleteById(1L);
        }
    }

    @Test
    void testUpdateTag() {
        TagFullDto expectedTagDto = defaultTagFullDto();
        Tag expectedTag = defaultTag();

        when(tagConverterMock.convert(expectedTagDto)).thenReturn(expectedTag);
        when(tagRepositoryMock.existsById(expectedTagDto.getId())).thenReturn(true);

        tagService.update(expectedTagDto);

        verify(tagRepositoryMock, times(1))
                .save(expectedTag);
    }

    @Test
    void testTryUpdatingNotExistingTag() {
        TagFullDto expectedTagDto = defaultTagFullDto();
        Tag expectedTag = defaultTag();

        when(tagConverterMock.convert(expectedTagDto)).thenReturn(expectedTag);
        when(tagRepositoryMock.existsById(expectedTagDto.getId())).thenReturn(false);

        try {
            tagService.update(expectedTagDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Tag with id " + 1L + " does not exist", exception.getMessage());
            verify(tagRepositoryMock, never())
                    .save(expectedTag);
        }
    }
}
