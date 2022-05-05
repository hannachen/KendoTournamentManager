package com.softwaremagico.kt.core.controller.models;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FightDTO extends ElementDTO {
    private TeamDTO team1;
    private TeamDTO team2;
    private TournamentDTO tournament;
    private Integer shiaijo;
    private List<DuelDTO> duels = new ArrayList<>();
    private LocalDateTime finishedAt;
    private Integer level;

    public TeamDTO getTeam1() {
        return team1;
    }

    public void setTeam1(TeamDTO team1) {
        this.team1 = team1;
    }

    public TeamDTO getTeam2() {
        return team2;
    }

    public void setTeam2(TeamDTO team2) {
        this.team2 = team2;
    }

    public TournamentDTO getTournament() {
        return tournament;
    }

    public void setTournament(TournamentDTO tournament) {
        this.tournament = tournament;
    }

    public Integer getShiaijo() {
        return shiaijo;
    }

    public void setShiaijo(Integer shiaijo) {
        this.shiaijo = shiaijo;
    }

    public List<DuelDTO> getDuels() {
        return duels;
    }

    public void setDuels(List<DuelDTO> duels) {
        this.duels = duels;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FightDTO)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final FightDTO fightDTO = (FightDTO) o;
        return getTeam1().equals(fightDTO.getTeam1()) && getTeam2().equals(fightDTO.getTeam2()) && getTournament().equals(fightDTO.getTournament())
                && getShiaijo().equals(fightDTO.getShiaijo()) && Objects.equals(getDuels(), fightDTO.getDuels()) && Objects.equals(getFinishedAt(),
                fightDTO.getFinishedAt()) && getLevel().equals(fightDTO.getLevel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTeam1(), getTeam2(), getTournament(), getShiaijo(), getDuels(), getFinishedAt(), getLevel());
    }
}
