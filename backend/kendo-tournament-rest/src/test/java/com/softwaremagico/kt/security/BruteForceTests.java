package com.softwaremagico.kt.security;

/*-
 * #%L
 * Kendo Tournament Manager (Rest)
 * %%
 * Copyright (C) 2021 - 2022 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwaremagico.kt.persistence.entities.AuthenticatedUser;
import com.softwaremagico.kt.rest.controllers.AuthenticatedUserController;
import com.softwaremagico.kt.rest.security.dto.AuthRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@Test(groups = "bruteForce")
public class BruteForceTests extends AbstractTestNGSpringContextTests {
    private final static String USER_NAME = "user";
    private final static String USER_FULL_NAME = "Test User";
    private final static String USER_PASSWORD = "password";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticatedUserController authenticatedUserController;

    private <T> String toJson(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private <T> T fromJson(String payload, Class<T> clazz) throws IOException {
        return objectMapper.readValue(payload, clazz);
    }

    @BeforeClass
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeMethod
    public void clearDatabase() {
        authenticatedUserController.findAll().forEach(authenticatedUser -> {
            authenticatedUserController.delete(authenticatedUser);
        });
    }

    @Test
    public void testLoginBlocked() throws Exception {
        AuthenticatedUser authenticatedUser = authenticatedUserController.createUser(USER_NAME, USER_FULL_NAME, USER_PASSWORD);

        AuthRequest request = new AuthRequest();
        request.setUsername(authenticatedUser.getUsername());
        request.setPassword("invalidPassword");

        // 3 attempts to block the user.
        this.mockMvc
                .perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().doesNotExist(HttpHeaders.AUTHORIZATION))
                .andReturn();

        this.mockMvc
                .perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().doesNotExist(HttpHeaders.AUTHORIZATION))
                .andReturn();

        this.mockMvc
                .perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().doesNotExist(HttpHeaders.AUTHORIZATION))
                .andReturn();

        //Correct password, user blocked.
        request.setPassword(USER_PASSWORD);
        this.mockMvc
                .perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().doesNotExist(HttpHeaders.AUTHORIZATION))
                .andReturn();
    }
}
