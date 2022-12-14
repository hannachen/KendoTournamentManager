package com.softwaremagico.kt.core.controller;

/*-
 * #%L
 * Kendo Tournament Manager (Core)
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

import com.softwaremagico.kt.core.controller.models.*;
import com.softwaremagico.kt.core.converters.*;
import com.softwaremagico.kt.core.converters.models.GroupConverterRequest;
import com.softwaremagico.kt.core.converters.models.TournamentConverterRequest;
import com.softwaremagico.kt.core.exceptions.GroupNotFoundException;
import com.softwaremagico.kt.core.exceptions.TeamNotFoundException;
import com.softwaremagico.kt.core.exceptions.TournamentInvalidException;
import com.softwaremagico.kt.core.exceptions.TournamentNotFoundException;
import com.softwaremagico.kt.core.providers.*;
import com.softwaremagico.kt.logger.ExceptionType;
import com.softwaremagico.kt.persistence.entities.Group;
import com.softwaremagico.kt.persistence.repositories.GroupRepository;
import com.softwaremagico.kt.persistence.values.TournamentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GroupController extends BasicInsertableController<Group, GroupDTO, GroupRepository, GroupProvider, GroupConverterRequest, GroupConverter> {
    private final TournamentConverter tournamentConverter;
    private final TournamentProvider tournamentProvider;
    private final FightProvider fightProvider;
    private final FightConverter fightConverter;

    private final DuelProvider duelProvider;

    private final DuelConverter duelConverter;

    private final TeamConverter teamConverter;

    private final TeamProvider teamProvider;

    @Autowired
    public GroupController(GroupProvider provider, GroupConverter converter, TournamentConverter tournamentConverter,
                           TournamentProvider tournamentProvider, FightProvider fightProvider, FightConverter fightConverter,
                           DuelProvider duelProvider, DuelConverter duelConverter, TeamConverter teamConverter, TeamProvider teamProvider) {
        super(provider, converter);
        this.tournamentConverter = tournamentConverter;
        this.tournamentProvider = tournamentProvider;
        this.fightProvider = fightProvider;
        this.fightConverter = fightConverter;
        this.duelProvider = duelProvider;
        this.duelConverter = duelConverter;
        this.teamConverter = teamConverter;
        this.teamProvider = teamProvider;
    }

    @Override
    protected GroupConverterRequest createConverterRequest(Group group) {
        return new GroupConverterRequest(group);
    }

    public List<GroupDTO> getFromTournament(Integer tournamentId) {
        return get(tournamentConverter.convert(new TournamentConverterRequest(tournamentProvider.get(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(getClass(), "No tournament found with id '" + tournamentId + "',",
                        ExceptionType.INFO)))));
    }

    public List<GroupDTO> get(TournamentDTO tournament) {
        final List<GroupDTO> groups = converter.convertAll(provider.getGroups(tournamentConverter.reverse(tournament))
                .stream().map(this::createConverterRequest).collect(Collectors.toList()));
        groups.sort(Comparator.comparing(GroupDTO::getLevel).thenComparing(GroupDTO::getIndex));
        return groups;
    }

    @Transactional
    public GroupDTO update(GroupDTO groupDTO, String username) {
        validate(groupDTO);
        final GroupDTO oldGroupDTO = get(groupDTO.getId());
        final List<FightDTO> fights = new ArrayList<>(oldGroupDTO.getFights());
        oldGroupDTO.getFights().clear();
        fightProvider.delete(fightConverter.reverseAll(fights));

        final List<DuelDTO> unties = new ArrayList<>(oldGroupDTO.getUnties());
        oldGroupDTO.getUnties().clear();
        duelProvider.delete(duelConverter.reverseAll(unties));

        //Remove all fights and duels from the group. Will be added on the update.
        converter.convert(createConverterRequest(provider.save(converter.reverse(oldGroupDTO))));

        groupDTO.setUpdatedBy(username);
        return create(groupDTO, null);
    }

    public GroupDTO addTeams(Integer groupId, List<TeamDTO> teams, String username) {
        return converter.convert(createConverterRequest(provider.addTeams(groupId, teamConverter.reverseAll(teams), username)));
    }

    public GroupDTO deleteTeams(Integer groupId, List<TeamDTO> teams, String username) {
        return converter.convert(createConverterRequest(provider.deleteTeams(groupId, teamConverter.reverseAll(teams), username)));
    }

    public GroupDTO setTeams(Integer groupId, List<TeamDTO> teams, String username) {
        GroupDTO groupDTO = get(groupId);

        final List<FightDTO> fights = new ArrayList<>(groupDTO.getFights());
        groupDTO.getFights().clear();
        fightProvider.delete(fightConverter.reverseAll(fights));

        final List<DuelDTO> unties = new ArrayList<>(groupDTO.getUnties());
        groupDTO.getUnties().clear();
        duelProvider.delete(duelConverter.reverseAll(unties));

        groupDTO.getTeams().clear();
        groupDTO = converter.convert(createConverterRequest(provider.save(converter.reverse(groupDTO))));
        groupDTO.setTeams(teams);
        groupDTO.setUpdatedBy(username);
        return converter.convert(createConverterRequest(provider.save(converter.reverse(groupDTO))));
    }

    public GroupDTO setTeams(List<TeamDTO> teams, String username) {
        if (teams.isEmpty()) {
            throw new TeamNotFoundException(this.getClass(), "No teams found!");
        }
        GroupDTO groupDTO = get(teams.get(0).getTournament()).stream().findAny().orElseThrow(() ->
                new GroupNotFoundException(this.getClass(), "No groups found!"));
        if (groupDTO.getTournament().getType().equals(TournamentType.CHAMPIONSHIP) ||
                groupDTO.getTournament().getType().equals(TournamentType.TREE) ||
                groupDTO.getTournament().getType().equals(TournamentType.CUSTOM_CHAMPIONSHIP)) {
            throw new TournamentInvalidException(this.getClass(), "On tournaments with type '" +
                    groupDTO.getTournament().getType() + "' group must be selected.");
        }
        final List<FightDTO> fights = groupDTO.getFights();
        groupDTO.getFights().clear();
        fightProvider.delete(fightConverter.reverseAll(fights));
        groupDTO.getTeams().clear();
        groupDTO = converter.convert(createConverterRequest(provider.save(converter.reverse(groupDTO))));
        groupDTO.setTeams(teams);
        groupDTO.setUpdatedBy(username);
        return converter.convert(createConverterRequest(provider.save(converter.reverse(groupDTO))));
    }

    public GroupDTO addUnties(Integer groupId, List<DuelDTO> duelDTOS, String username) {
        final GroupDTO groupDTO = get(groupId);
        groupDTO.getUnties().addAll(duelDTOS);
        return converter.convert(createConverterRequest(provider.save(converter.reverse(groupDTO))));
    }

    public long count(TournamentDTO tournament) {
        return provider.count(tournamentConverter.reverse(tournament));
    }

}
