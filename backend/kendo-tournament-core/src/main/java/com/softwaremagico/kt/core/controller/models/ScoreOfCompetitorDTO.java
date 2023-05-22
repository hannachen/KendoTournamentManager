package com.softwaremagico.kt.core.controller.models;

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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.softwaremagico.kt.utils.NameUtils;

import java.util.List;
import java.util.Objects;

public class ScoreOfCompetitorDTO {

    private ParticipantDTO competitor;
    @JsonIgnore
    protected List<FightDTO> fights;
    @JsonIgnore
    private List<DuelDTO> unties;
    private Integer wonDuels = null;
    private Integer drawDuels = null;
    private Integer untieDuels = null;
    private Integer hits = null;
    private Integer untieHits = null;
    private Integer duelsDone = null;
    private Integer wonFights = null;
    private Integer drawFights = null;
    @JsonIgnore
    private boolean countNotOver = false;

    public ScoreOfCompetitorDTO() {

    }

    public ScoreOfCompetitorDTO(ParticipantDTO competitor, List<FightDTO> fights, List<DuelDTO> unties, boolean countNotOver) {
        this.competitor = competitor;
        this.fights = fights;
        this.unties = unties;
        this.countNotOver = countNotOver;
        update();
    }

    public void setCompetitor(ParticipantDTO competitor) {
        this.competitor = competitor;
    }

    public List<FightDTO> getFights() {
        return fights;
    }

    public void setFights(List<FightDTO> fights) {
        this.fights = fights;
    }

    public List<DuelDTO> getUnties() {
        return unties;
    }

    public void setUnties(List<DuelDTO> unties) {
        this.unties = unties;
    }

    public void update() {
        wonFights = null;
        drawFights = null;
        wonDuels = null;
        drawDuels = null;
        hits = null;
        setDuelsWon();
        setDuelsDraw();
        setDuelsDone();
        setFightsWon();
        setFightsDraw();
        setUntieDuels();
        setUntieHits();
        setHits();
    }

    public ParticipantDTO getCompetitor() {
        return competitor;
    }

    public void setDuelsDone() {
        duelsDone = 0;
        fights.forEach(fight -> {
            if (fight.isOver() || countNotOver) {
                duelsDone += fight.getDuels(competitor).size();
            }
        });
    }

    public void setDuelsWon() {
        wonDuels = 0;
        fights.forEach(fight -> {
            if (fight.isOver() || countNotOver) {
                wonDuels += fight.getDuelsWon(competitor);
            }
        });
    }

    public void setFightsWon() {
        wonFights = 0;
        for (final FightDTO fight : fights) {
            if (fight.isOver() || countNotOver) {
                if (fight.isWon(competitor)) {
                    wonFights++;
                }
            }
        }
    }

    public void setFightsDraw() {
        drawFights = 0;
        for (final FightDTO fight : fights) {
            if (fight.isOver() || countNotOver) {
                if (fight.getWinner() == null && (fight.getTeam1().isMember(competitor)
                        || fight.getTeam2().isMember(competitor))) {
                    drawFights++;
                }
            }
        }
    }

    public void setDuelsDraw() {
        drawDuels = 0;
        for (final FightDTO fight : fights) {
            if (fight.isOver() || countNotOver) {
                drawDuels += fight.getDrawDuels(competitor);
            }
        }
    }

    public void setHits() {
        hits = 0;
        for (final FightDTO fight : fights) {
            hits += fight.getScore(competitor);
        }
    }

    public void setUntieDuels() {
        untieDuels = 0;
        unties.forEach(duel -> {
            if (Objects.equals(duel.getCompetitor1(), competitor) && duel.getWinner() == -1) {
                untieDuels++;
            } else if (Objects.equals(duel.getCompetitor2(), competitor) && duel.getWinner() == 1) {
                untieDuels++;
            }
        });
    }

    public void setUntieHits() {
        untieHits = 0;
        unties.forEach(duel -> {
            if (Objects.equals(duel.getCompetitor1(), competitor)) {
                untieHits += duel.getCompetitor1ScoreValue();
            } else if (Objects.equals(duel.getCompetitor2(), competitor)) {
                untieHits += duel.getCompetitor2ScoreValue();
            }
        });
    }

    public Integer getWonDuels() {
        return wonDuels;
    }

    public Integer getDrawDuels() {
        return drawDuels;
    }

    public Integer getHits() {
        return hits;
    }

    public Integer getDuelsDone() {
        return duelsDone;
    }

    public Integer getWonFights() {
        return wonFights;
    }

    public Integer getDrawFights() {
        return drawFights;
    }

    public Integer getUntieDuels() {
        return untieDuels;
    }

    public Integer getUntieHits() {
        return untieHits;
    }

    public void setWonDuels(Integer wonDuels) {
        this.wonDuels = wonDuels;
    }

    public void setDrawDuels(Integer drawDuels) {
        this.drawDuels = drawDuels;
    }

    public void setUntieDuels(Integer untieDuels) {
        this.untieDuels = untieDuels;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public void setUntieHits(Integer untieHits) {
        this.untieHits = untieHits;
    }

    public void setDuelsDone(Integer duelsDone) {
        this.duelsDone = duelsDone;
    }

    public void setWonFights(Integer wonFights) {
        this.wonFights = wonFights;
    }

    public void setDrawFights(Integer drawFights) {
        this.drawFights = drawFights;
    }

    public boolean isCountNotOver() {
        return countNotOver;
    }

    public void setCountNotOver(boolean countNotOver) {
        this.countNotOver = countNotOver;
    }

    @Override
    public String toString() {
        return "{" + NameUtils.getLastnameName(competitor) + " D:" + getWonDuels() + "/" + getDrawDuels() + ", H:" + getHits() + "}";
    }

}
