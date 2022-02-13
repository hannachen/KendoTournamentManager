package com.softwaremagico.kt.core.providers;

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

import com.softwaremagico.kt.core.exceptions.TournamentNotFoundException;
import com.softwaremagico.kt.persistence.entities.Tournament;
import com.softwaremagico.kt.persistence.repositories.TournamentRepository;
import com.softwaremagico.kt.persistence.values.TournamentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentProvider {

    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentProvider(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public Tournament get(Integer id) {
        return tournamentRepository.findById(id).orElseThrow(() -> new TournamentNotFoundException(getClass(),
                "No tournament with id '" + id + "' found"));
    }

    public Tournament add(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public Tournament add(String name, Integer shiaijos, Integer teamSize, TournamentType type) {
        return tournamentRepository.save(new Tournament(name, shiaijos != null ? shiaijos : 1, teamSize != null ? teamSize : 3,
                type != null ? type : TournamentType.LEAGUE));
    }


    public Tournament update(Tournament tournament) {
        if (tournament.getId() == null) {
            throw new TournamentNotFoundException(getClass(), "Tournament with null id does not exists.");
        }
        return tournamentRepository.save(tournament);
    }

    public List<Tournament> getAll() {
        return tournamentRepository.findAll();
    }

    public long count() {
        return tournamentRepository.count();
    }


    public void delete(Tournament tournament) {
        tournamentRepository.delete(tournament);
    }

    public void delete(Integer id) {
        if (tournamentRepository.existsById(id)) {
            tournamentRepository.deleteById(id);
        } else {
            throw new TournamentNotFoundException(getClass(), "Tournament with id '" + id + "' not found");
        }
    }

}