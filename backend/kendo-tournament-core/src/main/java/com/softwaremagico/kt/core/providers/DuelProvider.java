package com.softwaremagico.kt.core.providers;

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

import com.softwaremagico.kt.persistence.entities.Duel;
import com.softwaremagico.kt.persistence.entities.Participant;
import com.softwaremagico.kt.persistence.entities.Tournament;
import com.softwaremagico.kt.persistence.repositories.DuelRepository;
import com.softwaremagico.kt.persistence.values.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class DuelProvider extends CrudProvider<Duel, Integer, DuelRepository> {
    private static final int CACHE_EXPIRATION_TIME = 10 * 60 * 1000;

    @Autowired
    public DuelProvider(DuelRepository duelRepository) {
        super(duelRepository);
    }

    public long delete(Tournament tournament) {
        return getRepository().deleteByTournament(tournament);
    }

    public long count(Tournament tournament) {
        return getRepository().countByTournament(tournament);
    }

    public List<Duel> get(Participant participant) {
        return getRepository().findByParticipant(participant);
    }

    public List<Duel> get(Tournament tournament) {
        return getRepository().findByTournament(tournament);
    }

    public List<Duel> getUnties(Collection<Participant> participants) {
        return getRepository().findUntiesByParticipantIn(participants);
    }

    public List<Duel> getUnties() {
        return getRepository().findAllUnties();
    }

    @Cacheable(value = "duels-duration-average", key = "'average'")
    public Long getDurationAverage() {
        final Long duration = getRepository().getDurationAverage();
        return duration != null ? duration : -1;
    }

    public Long getDurationAverage(Tournament tournament) {
        return getRepository().getDurationAverage(tournament);
    }

    public Duel getFirstDuel(Tournament tournament) {
        return getRepository().findFirstByTournamentOrderByStartedAtAsc(tournament);
    }

    public Duel getLastDuel(Tournament tournament) {
        return getRepository().findFirstByTournamentOrderByFinishedAtDesc(tournament);
    }

    public Long countScore(Tournament tournament, Score score) {
        return getRepository().countScore(tournament, Collections.singletonList(score));
    }

    public Set<Duel> findByOnlyScore(Tournament tournament, Score score) {
        final List<Score> forbiddenScores = new ArrayList<>(Arrays.asList(Score.values()));
        forbiddenScores.remove(score);
        forbiddenScores.remove(Score.EMPTY);
        return getRepository().findByOnlyScore(tournament, forbiddenScores);
    }

    public Set<Duel> findByScorePerformedInLessThan(Tournament tournament, int maxSeconds) {
        return getRepository().findByScoreOnTimeLess(tournament, maxSeconds);
    }

    public List<Duel> findByScoreDuration(Tournament tournament, int scoreMaxDuration) {
        return getRepository().findByTournamentAndCompetitor1ScoreTimeLessThanEqualOrCompetitor2ScoreTimeLessThanEqual(
                tournament, scoreMaxDuration, scoreMaxDuration);
    }

    public long countFaults(Tournament tournament) {
        final Long faults = getRepository().countFaultsByTournament(tournament, true);
        final Long hansokus = getRepository().countScore(tournament, Collections.singletonList(Score.HANSOKU));
        return (faults != null ? faults : 0) + (hansokus != null ? hansokus : 0) * 2;
    }

    public long countScoreFromCompetitor(Participant participant) {
        return getRepository().countLeftScoreFromCompetitor(participant) + getRepository().countRightScoreFromCompetitor(participant);
    }

    public long countScoreAgainstCompetitor(Participant participant) {
        return getRepository().countLeftScoreAgainstCompetitor(participant) + getRepository().countRightScoreAgainstCompetitor(participant);
    }

    @CacheEvict(allEntries = true, value = {"duels-duration-average"})
    @Scheduled(fixedDelay = CACHE_EXPIRATION_TIME)
    public void reportCacheEvict() {
        //Only for handling Spring cache.
    }

}
