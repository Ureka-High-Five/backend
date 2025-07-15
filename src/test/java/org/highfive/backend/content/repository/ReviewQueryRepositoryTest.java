//package org.highfive.backend.content.repository;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.highfive.backend.common.fixture.ContentFixture;
//import org.highfive.backend.common.fixture.ReviewFixture;
//import org.highfive.backend.common.fixture.UserFixture;
//import org.highfive.backend.content.dto.response.ReviewSimpleResponseDto;
//import org.highfive.backend.content.entity.Content;
//import org.highfive.backend.global.dto.CursorPageResponse;
//import org.highfive.backend.user.entity.User;
//import org.highfive.backend.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//
//@DataJpaTest
//@Import({ReviewQueryRepositoryTest.TestConfig.class})
//public class ReviewQueryRepositoryTest {
//
//    @Autowired
//    private ReviewQueryRepositoryImpl reviewQueryRepository;
//
//    @Autowired
//    private ContentRepository contentRepository;
//
//    @Autowired
//    private ReviewRepository reviewRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private Content content;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        user = userRepository.save(UserFixture.createUser(null));
//        content = contentRepository.save(ContentFixture.createContent(null));
//    }
//
//    @Test
//    @DisplayName("리뷰가 여러 개 있을 때 첫 페이지를 요청하면 size만큼 최신순으로 반환되고 nextCursor를 준다")
//    void findReviewsByCursor_firstPage_success(){
//        //given
//        for (int i = 0; i < 5; i++) {
//            reviewRepository.save(ReviewFixture.createReview(null, user, content));
//        }
//
//        //when
//        CursorPageResponse<ReviewSimpleResponseDto> response = reviewQueryRepository.findReviewsByCursor(content.getId(),null,3, user);
//
//        //then
//        assertThat(response.items()).hasSize(3);
//        assertThat(response.nextCursor()).isNotNull();
//    }
//
//    @Test
//    @DisplayName("nextCursor를 이용해 마지막 페이지를 요청하면 남은 리뷰만 반환되고 nextCursor는 null이다")
//    void findReviewsByCursor_lastPage_success() {
//        //given
//        for (int i = 0; i < 5; i++) {
//            reviewRepository.save(ReviewFixture.createReview(null, user, content));
//        }
//
//        CursorPageResponse<ReviewSimpleResponseDto> firstPage =
//                reviewQueryRepository.findReviewsByCursor(content.getId(), null, 3,user);
//
//        //when
//        CursorPageResponse<ReviewSimpleResponseDto> secondPage =
//                reviewQueryRepository.findReviewsByCursor(content.getId(), firstPage.nextCursor(), 3,user);
//
//        //then
//        assertThat(secondPage.items()).hasSize(2);
//        assertThat(secondPage.nextCursor()).isNull();
//    }
//
//    @Test
//    @DisplayName("리뷰가 존재하지 않는 콘텐츠는 빈 리스트와 null 커서를 반환한다")
//    void findReviewsByCursor_noReview() {
//        // when
//        CursorPageResponse<ReviewSimpleResponseDto> response =
//                reviewQueryRepository.findReviewsByCursor(content.getId(), null, 3,user);
//
//        // then
//        assertThat(response.items()).isEmpty();
//        assertThat(response.nextCursor()).isNull();
//    }
//
//    @TestConfiguration
//    static class TestConfig {
//
//        @PersistenceContext
//        private EntityManager em;
//
//        @Bean
//        public JPAQueryFactory jpaQueryFactory() {
//            return new JPAQueryFactory(em);
//        }
//
//        @Bean
//        public ReviewQueryRepository reviewQueryRepository(JPAQueryFactory queryFactory) {
//            return new ReviewQueryRepositoryImpl(queryFactory);
//        }
//    }
//
//}
