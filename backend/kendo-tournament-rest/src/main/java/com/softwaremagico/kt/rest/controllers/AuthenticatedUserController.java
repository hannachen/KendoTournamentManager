package com.softwaremagico.kt.rest.controllers;

/*-
 * #%L
 * Kendo Tournament Manager (Rest)
 * %%
 * Copyright (C) 2021 - 2023 Softwaremagico
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

import com.softwaremagico.kt.core.exceptions.DuplicatedUserException;
import com.softwaremagico.kt.core.providers.AuthenticatedUserProvider;
import com.softwaremagico.kt.logger.KendoTournamentLogger;
import com.softwaremagico.kt.persistence.entities.AuthenticatedUser;
import com.softwaremagico.kt.rest.exceptions.BadRequestException;
import com.softwaremagico.kt.rest.exceptions.InvalidPasswordException;
import com.softwaremagico.kt.rest.exceptions.UserNotFoundException;
import com.softwaremagico.kt.rest.security.dto.CreateUserRequest;
import com.softwaremagico.kt.security.AvailableRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
public class AuthenticatedUserController {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    public AuthenticatedUserController(AuthenticatedUserProvider authenticatedUserProvider) {
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public AuthenticatedUser createUser(String creator, CreateUserRequest createUserRequest) {
        return createUser(creator, createUserRequest.getUsername(), createUserRequest.getName(), createUserRequest.getLastname(),
                createUserRequest.getPassword(), createUserRequest.getRoles() != null ?
                        createUserRequest.getRoles().toArray(new String[0]) : null);
    }

    public AuthenticatedUser createUser(String creator, String username, String firstName, String lastName, String password, String... roles) {
        try {
            try {
                return authenticatedUserProvider.save(username, firstName, lastName, password, roles);
            } finally {
                KendoTournamentLogger.info(this.getClass(), "User '{}' created by '{}'.", username, creator);
            }
        } catch (DuplicatedUserException e) {
            throw new BadRequestException(this.getClass(), "Username exists!");
        }
    }

    public AuthenticatedUser createUser(String creator, String username, String firstName, String lastName, String password, AvailableRole... roles) {
        final String[] roleTags = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            roleTags[i] = roles[i].name().replaceAll(AvailableRole.ROLE_PREFIX, "").toLowerCase();
        }
        return createUser(creator, username, firstName, lastName, password, roleTags);
    }

    public void updatePassword(String username, String oldPassword, String newPassword) {
        final AuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "User with username '" + username + "' does not exists"));

        //Check old password.
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException(this.getClass(), "Provided password is incorrect!");
        }

        //Update new password.
        user.setPassword(newPassword);
        authenticatedUserProvider.save(user);
        KendoTournamentLogger.info(this.getClass(), "Password updated correctly by '{}'!", username);
    }

    public AuthenticatedUser updateUser(String updater, CreateUserRequest createUserRequest) {
        final AuthenticatedUser user = authenticatedUserProvider.findByUsername(createUserRequest.getUsername()).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "User with username '" + createUserRequest.getUsername() + "' does not exists"));
        user.setName(createUserRequest.getName() != null ? createUserRequest.getName().replaceAll("[\n\r\t]", "_") : "");
        user.setLastname(createUserRequest.getLastname() != null ? createUserRequest.getLastname().replaceAll("[\n\r\t]", "_") : "");
        if (!Objects.equals(user.getUsername(), updater)) {
            user.setRoles(createUserRequest.getRoles());
        }
        KendoTournamentLogger.debug(this.getClass(), "Updating user '{}' by '{}' with roles '{}'.", user.getUsername(),
                updater, user.getRoles());
        try {
            return authenticatedUserProvider.save(user);
        } finally {
            KendoTournamentLogger.info(this.getClass(), "User '{}' updated by '{}' with roles '{}'.", user.getUsername(),
                    updater, user.getRoles());
        }
    }

    public void deleteUser(String actioner, String username) {
        final AuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "User with username '" + username + "' does not exists"));
        //Ensure that at least, one user remain.
        if (authenticatedUserProvider.count() > 1) {
            authenticatedUserProvider.delete(user);
            KendoTournamentLogger.info(this.getClass(), "User '{}' deleted by '{}'.", username, actioner);
        }
    }

    public Set<String> getRoles(String username) {
        final AuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "User with username '" + username + "' does not exists"));
        return user.getRoles();
    }

    public List<AuthenticatedUser> findAll() {
        return authenticatedUserProvider.findAll();
    }

    public void delete(AuthenticatedUser authenticatedUser) {
        authenticatedUserProvider.delete(authenticatedUser);
    }

    public long countUsers() {
        return authenticatedUserProvider.count();
    }
}
