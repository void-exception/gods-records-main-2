package com.God.sRecords.regestration.controller;

import com.God.sRecords.one.regestration.controller.AuthViewController;
import com.God.sRecords.one.regestration.model.User;
import com.God.sRecords.one.regestration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthViewControllerTest {

    private static final String VALID_JSON =
            "{\"email\":\"test@mail.com\",\"name\":\"Иван\",\"password\":\"Password1\"}";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthViewController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void register_createsUserWithHashedPassword() throws Exception {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password1")).thenReturn("hashed");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isCreated());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("test@mail.com");
        assertThat(captor.getValue().getPassword()).isEqualTo("hashed");
    }

    @Test
    void register_returns409_whenEmailTaken() throws Exception {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isConflict());

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_returns400_whenPasswordIsWeak() throws Exception {
        String weak = "{\"email\":\"test@mail.com\",\"name\":\"Иван\",\"password\":\"alllowercase\"}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(weak))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any());
    }
}