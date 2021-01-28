package be.fooda.backend.user.view.controller;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * -> @WebMvcTest - for testing the controller layer
 * -> @JsonTest - for testing the JSON marshalling and unmarshalling
 * -> @DataJpaTest - for testing the repository layer
 * -> @RestClientTests - for testing REST clients
 * -> @SpringBootTest - for integration tests ..
 */

@Log4j2
@ExtendWith(SpringExtension.class)
@WebMvcTest(FoodaUserController.class)
class FoodaUserControllerTest {
//
//    @Qualifier("serviceMock")
//    @Autowired
//    private FoodaUserService service;
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Test
//    void should_not_create_if_user_exists() {
//
//    }
//
//    @Test
//    void should_create_if_user_does_not_exist() {
//
//    }
//
//    @Test
//    void should_create_fooda_user() {
//    }
//
//    @Test
//    void should_create_della_user() {
//    }
//
//    @Test
//    void should_create_resta_user() {
//    }
//
//    @Test
//    void should_update_by_id() {
//    }
//
//    @Test
//    void should_update_by_example() {
//    }
//
//    @Test
//    void should_delete_by_id() {
//    }
//
//    @Test
//    void should_delete_by_example() {
//    }
//
//    @Test
//    void should_read_all() {
//    }
//
//    @Test
//    void should_read_by_id() {
//        Long givenUserId = 1L;
//        Optional<FoodaUser> expected = service.readById(givenUserId);
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        Optional<FoodaUser> actual;
//        try {
//            MvcResult mvcResult = mvc.perform(get("/user/" + givenUserId))
//                    .andDo(print())
//                    .andExpect(status().isOk())
//                    .andReturn();
//
//            actual = Optional.of(objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FoodaUser.class));
//        } catch (Exception e) {
//            throw new RuntimeException("Could not perform HttpRequest");
//        }
//
//        assertThat(actual).isPresent();
//        assertThat(actual).get().isEqualToIgnoringGivenFields(expected.get(), "password");
//    }
//
//    @Test
//    void should_read_by_example() {
//    }
//
//    @Test
//    void should_read_by_user_id() {
//    }
//
//    @Test
//    void should_exist_by_id() {
//    }
//
//    @Test
//    void should_exist_by_example() {
//    }
}