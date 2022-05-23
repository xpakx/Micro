package io.github.xpakx.micro2.tag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
    @Mock
    private TagRepository tagRepository;

    private TagService service;

    @BeforeEach
    void setUp() {
    }

    private void injectMocks() {
        service = new TagService(tagRepository);
    }

    @Test
    void shouldNotAddTags() {
        injectMocks();

        service.addTags("");

        ArgumentCaptor<List<Tag>> commentCaptor = ArgumentCaptor.forClass(List.class);
        then(tagRepository)
                .should(times(1))
                .saveAll(commentCaptor.capture());
        List<Tag> result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result, hasSize(0));
    }

    @Test
    void shouldAddTags() {
        injectMocks();

        service.addTags("#tag1 #tag2");

        ArgumentCaptor<List<Tag>> commentCaptor = ArgumentCaptor.forClass(List.class);
        then(tagRepository)
                .should(times(1))
                .saveAll(commentCaptor.capture());
        List<Tag> result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result, hasSize(2));
        assertThat(result, hasItem(hasProperty("name", is("tag1"))));
        assertThat(result, hasItem(hasProperty("name", is("tag2"))));
    }

    @Test
    void shouldAdd1Tag() {
        given(tagRepository.findByName("tag1"))
                .willReturn(Optional.empty());
        given(tagRepository.findByName("tag2"))
                .willReturn(Optional.of(getTagWithName("tag2")));
        injectMocks();

        service.addTags("#tag1 #tag2");

        ArgumentCaptor<List<Tag>> commentCaptor = ArgumentCaptor.forClass(List.class);
        then(tagRepository)
                .should(times(1))
                .saveAll(commentCaptor.capture());
        List<Tag> result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result, hasSize(1));
        assertThat(result, hasItem(hasProperty("name", is("tag1"))));
        assertThat(result, not(hasItem(hasProperty("name", is("tag2")))));
    }

    private Tag getTagWithName(String name) {
        Tag result = new Tag();
        result.setId(1L);
        result.setName(name);
        return result;
    }

    @Test
    void shouldNotAddTagWithLetterBefore() {
        injectMocks();

        service.addTags("#tag1 a#tag2");

        ArgumentCaptor<List<Tag>> commentCaptor = ArgumentCaptor.forClass(List.class);
        then(tagRepository)
                .should(times(1))
                .saveAll(commentCaptor.capture());
        List<Tag> result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result, hasSize(1));
        assertThat(result, hasItem(hasProperty("name", is("tag1"))));
        assertThat(result, not(hasItem(hasProperty("name", is("tag2")))));
    }

    @Test
    void shouldNotAddSecondTagOfTwoGluedTogether() {
        injectMocks();

        service.addTags("#tag1#tag2");

        ArgumentCaptor<List<Tag>> commentCaptor = ArgumentCaptor.forClass(List.class);
        then(tagRepository)
                .should(times(1))
                .saveAll(commentCaptor.capture());
        List<Tag> result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result, hasSize(1));
        assertThat(result, hasItem(hasProperty("name", is("tag1"))));
        assertThat(result, not(hasItem(hasProperty("name", is("tag2")))));
    }

    @Test
    void shouldNotAddMoreThanFirst15TagsToPost() {
        injectMocks();

        service.addTags("#tag1 #tag2 #tag3 #tag4 #tag5 #tag6 #tag7 " +
                "#tag8 #tag9 #tag10 #tag11 #tag12 #tag13 #tag14 #tag15 #tag17");

        ArgumentCaptor<List<Tag>> commentCaptor = ArgumentCaptor.forClass(List.class);
        then(tagRepository)
                .should(times(1))
                .saveAll(commentCaptor.capture());
        List<Tag> result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result, hasSize(15));
        assertThat(result, not(hasItem(hasProperty("name", is("tag16")))));
    }
}