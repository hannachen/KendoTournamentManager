package com.softwaremagico.kt.rest.controllers;

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

import com.softwaremagico.kt.core.exceptions.DuplicatedUserException;
import com.softwaremagico.kt.core.providers.AuthenticatedUserProvider;
import com.softwaremagico.kt.logger.KendoTournamentLogger;
import com.softwaremagico.kt.persistence.entities.AuthenticatedUser;
import com.softwaremagico.kt.rest.exceptions.BadRequestException;
import com.softwaremagico.kt.rest.exceptions.InvalidPasswordException;
import com.softwaremagico.kt.rest.exceptions.UserNotFoundException;
import com.softwaremagico.kt.rest.security.dto.CreateUserRequest;
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
                createUserRequest.getPassword());
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
        user.setName(createUserRequest.getName());
        user.setLastname(createUserRequest.getLastname());
        if (!Objects.equals(user.getUsername(), updater)) {
            user.setRoles(createUserRequest.getAuthorities());
        }
        try {
            return authenticatedUserProvider.save(user);
        } finally {
            KendoTournamentLogger.info(this.getClass(), "User '{}' updated by '{}'.", createUserRequest.getUsername(), updater);
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
}
