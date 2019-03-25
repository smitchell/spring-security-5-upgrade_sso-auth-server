package com.example.service.auth.repository;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.service.auth.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@AutoConfigureRestDocs("target/snippets")
public class UserRepositoryTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  @WithMockUser
  public void createUser() throws Exception {
    User user = new User();
    user.setUsername(UUID.randomUUID().toString());
    user.setPassword(new BCryptPasswordEncoder().encode("password"));
    user.setActive(true);

    String objectString = new ObjectMapper().writeValueAsString(user);
    MvcResult result = mockMvc.perform(
        post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectString)
    )
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", containsString(
            "http://localhost:8080/users/"))) // Should be http://localhost/user/{valid-uuid}
        .andDo(document("create-user",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())))
        .andReturn();

    String userId = result.getResponse().getHeader("Location")
        .split("/")[4]; // The part of the location after the 4th slash
    Assert.assertTrue(userId.matches(User.UUID_PATTERN)); //Make sure {valid-id} matches the pattern
  }

  @Test
  @WithMockUser
  @Transactional
  public void updateUserPassword() throws Exception {
    User user = new User();
    user.setUsername(UUID.randomUUID().toString());
    user.setPassword(new BCryptPasswordEncoder().encode("password"));

    String objectString = new ObjectMapper().writeValueAsString(user);
    MvcResult result = mockMvc.perform(
        post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectString)
    )
        .andExpect(status().isCreated())
        .andReturn();
    String userId = result.getResponse().getHeader("Location").split("/")[4];

    User updated = new User();
    updated.setUsername(userId);
    updated.setPassword(new BCryptPasswordEncoder().encode("UpdatedPassword"));

    MvcResult findByIdResult = mockMvc.perform(
        patch("/users/" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(updated))
    )
        .andExpect(status().isNoContent())
        .andReturn();

  }

  @Test
  @WithMockUser
  @Transactional
  public void updateUserPasswordAlt() throws Exception {
    User user = new User();
    user.setUsername(UUID.randomUUID().toString());
    user.setPassword(new BCryptPasswordEncoder().encode("origninal"));

    String objectString = new ObjectMapper().writeValueAsString(user);
    MvcResult result = mockMvc.perform(
        post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectString)
    )
        .andExpect(status().isCreated())
        .andReturn();
    String userId = result.getResponse().getHeader("Location").split("/")[4];

    UpdatePassword updated = new UpdatePassword(
        new BCryptPasswordEncoder().encode("UpdatedPassword"));

    MvcResult findByIdResult = mockMvc.perform(
        patch("/users/" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(updated))
    )
        .andExpect(status().isNoContent())
        .andDo(document("update-password",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())))
        .andReturn();
  }

  @Test
  @WithMockUser
  @Transactional
  public void updateUserStatus() throws Exception {
    User user = new User();
    user.setUsername(UUID.randomUUID().toString());
    user.setPassword(new BCryptPasswordEncoder().encode("password"));
    user.setActive(Boolean.TRUE);

    String objectString = new ObjectMapper().writeValueAsString(user);
    MvcResult result = mockMvc.perform(
        post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectString)
    )
        .andExpect(status().isCreated())
        .andReturn();
    String userId = result.getResponse().getHeader("Location").split("/")[4];

    UpdateStatus updateStatus = new UpdateStatus(Boolean.FALSE);

    MvcResult findByIdResult = mockMvc.perform(
        patch("/users/" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(updateStatus))
    )
        .andExpect(status().isNoContent())
        .andDo(document("update-status",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())))
        .andReturn();
  }

}

@Data
@AllArgsConstructor
class UpdatePassword {

  private String password;
}

@Data
@AllArgsConstructor
class UpdateStatus {

  private Boolean isActive;
}