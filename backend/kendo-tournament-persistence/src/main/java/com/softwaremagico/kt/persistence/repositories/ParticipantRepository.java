package com.softwaremagico.kt.persistence.repositories;

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

import com.softwaremagico.kt.persistence.entities.Participant;
import com.softwaremagico.kt.persistence.entities.Tournament;
import com.softwaremagico.kt.persistence.values.AchievementType;
import com.softwaremagico.kt.persistence.values.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

    List<Participant> findByIdIn(List<Integer> ids);

    List<Participant> findByOrderByLastnameAsc();

    @Query("SELECT r.participant FROM Role r WHERE r.tournament = :tournament")
    List<Participant> findByTournament(@Param("tournament") Tournament tournament);

    @Query("SELECT r.participant FROM Role r WHERE r.tournament = :tournament and r.roleType = :roleType")
    List<Participant> findByTournamentAndRoleType(@Param("tournament") Tournament tournament, @Param("roleType") RoleType roleType);

    @Query("SELECT r.participant FROM Role r WHERE r.participant IN " +
            "(SELECT rl.participant FROM Role rl WHERE rl.tournament = :tournament) " +
            "GROUP BY r.participant " +
            "HAVING COUNT(DISTINCT r.roleType) >= :differentRoleTypes")
    List<Participant> findParticipantsWithMoreRoleTypesThan(@Param("tournament") Tournament tournament, @Param("differentRoleTypes") long differentRoleTypes);

    @Query("SELECT a.participant FROM Achievement a WHERE a.participant IN :participants AND a.achievementType=:achievementType")
    List<Participant> findParticipantsWithAchievementFromList(@Param("achievementType") AchievementType achievementType, List<Participant> participants);

    @Query("SELECT r.participant FROM Role r WHERE r.tournament = :tournament AND  r.roleType = :roleType " +
            "AND NOT EXISTS " +
            "(SELECT r2.participant FROM Role r2 WHERE r.participant = r2.participant " +
            "AND r2.roleType = :roleType AND r2.tournament.createdAt < r.tournament.createdAt)")
    List<Participant> findParticipantsWithFirstRoleAs(@Param("tournament") Tournament tournament, @Param("roleType") RoleType roleType);
}
