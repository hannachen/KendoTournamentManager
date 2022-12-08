package com.softwaremagico.kt.persistence.entities;

/*-
 * #%L
 * Kendo Tournament Manager (Persistence)
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

import com.softwaremagico.kt.persistence.encryption.IntegerCryptoConverter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "tournament_groups")
public class Group extends Element {

    @ManyToOne
    @JoinColumn(name = "tournament", nullable = false)
    private Tournament tournament;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "teams_by_group", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "team_id"))
    @OrderColumn(name = "group_index")
    private List<Team> teams;

    @Column(name = "shiaijo", nullable = false)
    @Convert(converter = IntegerCryptoConverter.class)
    private Integer shiaijo = 0;

    @Column(name = "group_level", nullable = false)
    @Convert(converter = IntegerCryptoConverter.class)
    private Integer level = 0;

    @Column(name = "group_index", nullable = false)
    @Convert(converter = IntegerCryptoConverter.class)
    private Integer index = 0;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinTable(name = "fights_by_group", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "fight_id"))
    @OrderColumn(name = "group_index")
    private List<Fight> fights;

    @Column(name = "number_of_winners", nullable = false)
    @Convert(converter = IntegerCryptoConverter.class)
    private int numberOfWinners = 1;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "unties", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "duel_id"))
    private List<Duel> unties = new ArrayList<>();

    public Group() {
        super();
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public Integer getShiaijo() {
        return shiaijo;
    }

    public void setShiaijo(Integer shiaijo) {
        this.shiaijo = shiaijo;
    }

    public List<Fight> getFights() {
        return fights;
    }

    public void setFights(List<Fight> fights) {
        this.fights = fights;
    }

    public void removeTeams() {
        teams.clear();
    }

    public void removeFights() {
        fights.clear();
    }

    public boolean isFightOfGroup(Fight fight) {
        return fights.contains(fight);
    }

    public boolean areFightsOverOrNull() {
        if (teams.size() < 2) {
            return true;
        }
        return areFightsOverOrNull(getFights());
    }

    /**
     * If the fightManager are over or fightManager are not needed.
     *
     * @param fights the fights.
     * @return
     */
    public static boolean areFightsOverOrNull(List<Fight> fights) {
        if (fights.size() > 0) {
            for (final Fight fight : fights) {
                if (!fight.isOver()) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public int getNumberOfWinners() {
        return numberOfWinners;
    }

    public void setNumberOfWinners(int numberOfWinners) {
        this.numberOfWinners = numberOfWinners;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void createUntieDuel(Participant competitor1, Participant competitor2, String createdBy) {
        final Duel untie = new Duel(competitor1, competitor2, tournament, createdBy);
        untie.setTotalDuration(tournament.getDuelsDuration());
        untie.setType(DuelType.UNDRAW);
        unties.add(untie);
    }

    public List<Duel> getUnties() {
        return unties;
    }

    public void setUnties(List<Duel> unties) {
        this.unties = unties;
    }

    @Override
    public String toString() {
        return "Group{" +
                "tournament=" + tournament +
                ", shiaijo=" + shiaijo +
                ", level=" + level +
                ", index=" + index +
                '}';
    }
}

