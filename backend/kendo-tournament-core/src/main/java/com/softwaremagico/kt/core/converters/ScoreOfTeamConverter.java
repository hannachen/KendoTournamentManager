package com.softwaremagico.kt.core.converters;

/*-
 * #%L
 * Kendo Tournament Manager (Core)
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

import com.softwaremagico.kt.core.controller.models.ScoreOfTeamDTO;
import com.softwaremagico.kt.core.converters.models.DuelConverterRequest;
import com.softwaremagico.kt.core.converters.models.FightConverterRequest;
import com.softwaremagico.kt.core.converters.models.ScoreOfTeamConverterRequest;
import com.softwaremagico.kt.core.converters.models.TeamConverterRequest;
import com.softwaremagico.kt.core.score.ScoreOfTeam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component

public class ScoreOfTeamConverter extends ElementConverter<ScoreOfTeam, ScoreOfTeamDTO, ScoreOfTeamConverterRequest> {

    private final TeamConverter teamConverter;

    private final FightConverter fightConverter;

    private final DuelConverter duelConverter;

    public ScoreOfTeamConverter(TeamConverter teamConverter, FightConverter fightConverter, DuelConverter duelConverter) {
        this.teamConverter = teamConverter;
        this.fightConverter = fightConverter;
        this.duelConverter = duelConverter;
    }

    @Override
    protected ScoreOfTeamDTO convertElement(ScoreOfTeamConverterRequest from) {
        final ScoreOfTeamDTO scoreOfTeamDTO = new ScoreOfTeamDTO();
        BeanUtils.copyProperties(from.getEntity(), scoreOfTeamDTO, ConverterUtils.getNullPropertyNames(from.getEntity()));
        scoreOfTeamDTO.setTeam(teamConverter.convert(new TeamConverterRequest(from.getEntity().getTeam())));
        scoreOfTeamDTO.setFights(fightConverter.convertAll(from.getEntity().getFights().stream()
                .map(FightConverterRequest::new).collect(Collectors.toList())));
        scoreOfTeamDTO.setUnties(duelConverter.convertAll(from.getEntity().getUnties().stream()
                .map(DuelConverterRequest::new).collect(Collectors.toList())));
        return scoreOfTeamDTO;
    }

    @Override
    public ScoreOfTeam reverse(ScoreOfTeamDTO to) {
        if (to == null) {
            return null;
        }
        final ScoreOfTeam scoreOfTeam = new ScoreOfTeam();
        BeanUtils.copyProperties(to, scoreOfTeam, ConverterUtils.getNullPropertyNames(to));
        scoreOfTeam.setTeam(teamConverter.reverse(to.getTeam()));
        scoreOfTeam.setFights(new ArrayList<>());
        to.getFights().forEach(fight -> scoreOfTeam.getFights().add(fightConverter.reverse(fight)));
        scoreOfTeam.setUnties(new ArrayList<>());
        to.getUnties().forEach(duel -> scoreOfTeam.getUnties().add(duelConverter.reverse(duel)));
        return scoreOfTeam;
    }
}
