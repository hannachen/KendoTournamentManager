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
import com.softwaremagico.kt.persistence.encryption.StringCryptoConverter;
import com.softwaremagico.kt.persistence.encryption.TournamentTypeCryptoConverter;
import com.softwaremagico.kt.persistence.values.TournamentType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "tournaments")
public class Tournament extends Element {

    @Column(name = "name")
    @Convert(converter = StringCryptoConverter.class)
    private String name;

    @Column(name = "shiaijos")
    @Convert(converter = IntegerCryptoConverter.class)
    private Integer shiaijos;

    @Column(name = "team_size")
    @Convert(converter = IntegerCryptoConverter.class)
    private Integer teamSize;

    @Column(name = "tournament_type")
    @Enumerated(EnumType.STRING)
    @Convert(converter = TournamentTypeCryptoConverter.class)
    private TournamentType type;

    @OneToOne(fetch = FetchType.EAGER)
    private TournamentScore tournamentScore;

    public Tournament() {
        super();
    }

    public Tournament(String name, int shiaijos, int teamSize, TournamentType type) {
        this();
        setName(name);
        setShiaijos(shiaijos);
        setTeamSize(teamSize);
        setType(type);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
    }

    public TournamentType getType() {
        return type;
    }

    public void setType(TournamentType type) {
        this.type = type;
    }

    public Integer getShiaijos() {
        return shiaijos;
    }

    public void setShiaijos(Integer shiaijos) {
        this.shiaijos = shiaijos;
    }

    public TournamentScore getTournamentScore() {
        return tournamentScore;
    }

    public void setTournamentScore(TournamentScore tournamentScore) {
        this.tournamentScore = tournamentScore;
    }
}
