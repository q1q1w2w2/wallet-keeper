package com.project.wallet_keeper.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.wallet_keeper.domain.Role;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.user.SignupDto;
import com.project.wallet_keeper.dto.user.UserProfileUpdateDto;
import com.project.wallet_keeper.repository.UserRepository;
import com.project.wallet_keeper.security.jwt.TokenProvider;
import com.project.wallet_keeper.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
//@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TokenProvider tokenProvider;

    SignupDto createSignupDto() {
        return new SignupDto("email@email.com", "password", "사용자01", LocalDate.of(2000, 1, 1));
    }

    User createUser() {
        return User.builder()
                .email("email@email.com")
                .password("password")
                .nickname("사용자01")
                .role(Role.ROLE_USER)
                .birth(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    @DisplayName("회원가입")
    void signUp() throws Exception {
        // given
        User user = createUser();
        SignupDto signupDto = createSignupDto();

        given(userService.signUp(any())).willReturn(user);

        // when
        ResultActions result = mockMvc.perform(post("/api/users")
                .contentType(APPLICATION_JSON) // request의 Content-Tpye
                .content(objectMapper.writeValueAsString(signupDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value(user.getEmail()))
                .andExpect(jsonPath("$.data.nickname").value(user.getNickname()));
    }

    @Test
    @DisplayName("현재 로그인한 사용자 정보")
    void getCurrentUser() throws Exception {
        // given
        User user = createUser();
        given(userService.getCurrentUser()).willReturn(user);

        // when
        ResultActions result = mockMvc.perform(get("/api/users/me")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(user.getEmail()))
                .andExpect(jsonPath("$.data.nickname").value(user.getNickname()));
    }

    @Test
    @DisplayName("현재 로그인한 사용자 정보 수정")
    void updateCurrentUser() throws Exception {
        // given
        User currentUser = createUser();
        UserProfileUpdateDto updateDto = new UserProfileUpdateDto("changeNickname", LocalDate.of(2000, 1, 1));

        given(userService.getCurrentUser()).willReturn(currentUser);
        given(userService.updateUser(eq(currentUser), any(UserProfileUpdateDto.class))).willReturn(currentUser);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/me")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(currentUser.getEmail()))
                .andExpect(jsonPath("$.data.nickname").value(currentUser.getNickname()));
    }

    @Test
    void deleteCurrentUser() {
    }

    @Test
    void updatePassword() {
    }

    @Test
    void resetPassword() {
    }
}