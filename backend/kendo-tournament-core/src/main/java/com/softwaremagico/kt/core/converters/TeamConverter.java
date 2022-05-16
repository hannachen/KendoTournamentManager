package com.softwaremagico.kt.core.converters;

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

import com.softwaremagico.kt.core.controller.models.TeamDTO;
import com.softwaremagico.kt.core.converters.models.ParticipantConverterRequest;
import com.softwaremagico.kt.core.converters.models.TeamConverterRequest;
import com.softwaremagico.kt.core.converters.models.TournamentConverterRequest;
import com.softwaremagico.kt.persistence.entities.Team;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TeamConverter extends ElementConverter<Team, TeamDTO, TeamConverterRequest> {
    private final TournamentConverter tournamentConverter;
    private final ParticipantConverter participantConverter;

    @Autowired
    public TeamConverter(TournamentConverter tournamentConverter, ParticipantConverter participantConverter) {
        this.tournamentConverter = tournamentConverter;
        this.participantConverter = participantConverter;
    }


    @Override
    public TeamDTO convert(TeamConverterRequest from) {
        final TeamDTO teamDTO = new TeamDTO();
        BeanUtils.copyProperties(from.getEntity(), teamDTO);
        teamDTO.setMembers(new ArrayList<>());
        teamDTO.setTournament(tournamentConverter.convert(
                new TournamentConverterRequest(from.getEntity().getTournament())));
        from.getEntity().getMembers().forEach(member ->
                teamDTO.getMembers().add(participantConverter.convert(new ParticipantConverterRequest(member))));
        return teamDTO;
    }

    @Override
    public Team reverse(TeamDTO to) {
        if (to == null) {
            return null;
        }
        final Team team = new Team();
        BeanUtils.copyProperties(to, team);
        team.setTournament(tournamentConverter.reverse(to.getTournament()));
        team.setMembers(new ArrayList<>());
        to.getMembers().forEach(member -> team.getMembers().add(participantConverter.reverse(member)));
        return team;
    }
}
