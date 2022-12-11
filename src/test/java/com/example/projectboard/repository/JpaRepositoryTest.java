package com.example.projectboard.repository;

import com.example.projectboard.config.JpaConfig;
import com.example.projectboard.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JPA 연결 테스트")
@Import(JpaConfig.class)    // 내가 만든 JpaConfig.java 를 Test 는 인식하지 못하므로 import 해주어야함.
@DataJpaTest
class JpaRepositoryTest {

    //@DataJpaTest Annotation 에 autowired 에 대한 로직이 있어서 별도로 써주지 않아도 됨.
    private final ArticleRepository articleRepository;
    private final ArticleCommentsRepository articleCommentsRepository;

    public JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentsRepository articleCommentsRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentsRepository = articleCommentsRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        //단순 CRUD Test

        // Given

        // When
        List<Article> articles = articleRepository.findAll();

        // Then
        assertThat(articles)
                .isNotNull()
                .hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        //단순 CRUD Test

        // Given
        long previousCount = articleRepository.count();
        // When
        Article savedArticle = articleRepository.save(Article.of("new article", "new content", "#spring"));

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        //단순 CRUD Test
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updatedHashtag = "#springboot";
        article.setHashtag(updatedHashtag);

        // When
        Article savedArticle = articleRepository.saveAndFlush(article);

        // Then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        //단순 CRUD Test
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        // 게시글 삭제 시 댓글도 같이 삭제 되는지 보려고 함. cascade 처리 되도록 도메인 설계 되었기 때문임.
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentsRepository.count();
        // 랜덤으로 댓글 생성 해놨기 때문에 해당 게시글의 댓글 count 를 현재 모르기 때문에 size 로 구함.
        int deletedCommentsSize = article.getArticleComments().size();

        // When
        articleRepository.delete(article);

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentsRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }
}