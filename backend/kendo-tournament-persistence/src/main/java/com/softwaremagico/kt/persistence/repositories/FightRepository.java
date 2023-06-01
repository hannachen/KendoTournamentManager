package com.softwaremagico.kt.persistence.repositories;

/*-
 * #%L
 * Kendo Tournament Manager (Persistence)
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

import com.softwaremagico.kt.persistence.entities.Fight;
import com.softwaremagico.kt.persistence.entities.Participant;
import com.softwaremagico.kt.persistence.entities.Tournament;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface FightRepository extends JpaRepository<Fight, Integer> {

    List<Fight> findByTournament(Tournament tournament);

    List<Fight> findByTournamentAndLevel(Tournament tournament, Integer level);

    List<Fight> findByTournamentAndFinishedAtIsNotNull(Tournament tournament);

    @Query("SELECT DISTINCT f FROM Fight f LEFT JOIN f.duels d WHERE d.competitor1 IN :participants or d.competitor2 IN :participants")
    List<Fight> findByParticipantIn(@Param("participants") Collection<Participant> participants);

    long countByTournamentAndFinishedAtIsNull(Tournament tournament);

    long countByTournament(Tournament tournament);

    Optional<Fight> findFirstByTournamentAndFinishedAtIsNullOrderByCreatedAtAsc(Tournament tournament);

    long deleteByTournament(Tournament tournament);

    Optional<Fight> findFirstByTournamentOrderByLevelDesc(Tournament tournament);

    @Query("SELECT COUNT(f) FROM Fight f WHERE f.tournament=:tournament AND NOT EXISTS " +
            "(SELECT f1 FROM Fight f1 INNER JOIN f1.duels fd ON fd.finished=:finished WHERE f1.id=f.id)")
    long countByTournamentAndFinishedNot(@Param("tournament") Tournament tournament, @Param("finished") Boolean finished);
}
