package com.softwaremagico.kt.core.tournaments;

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

import com.softwaremagico.kt.core.controller.RankingController;
import com.softwaremagico.kt.core.controller.models.TeamDTO;
import com.softwaremagico.kt.core.converters.GroupConverter;
import com.softwaremagico.kt.core.converters.TeamConverter;
import com.softwaremagico.kt.core.converters.models.GroupConverterRequest;
import com.softwaremagico.kt.core.managers.KingOfTheMountainFightManager;
import com.softwaremagico.kt.core.managers.TeamsOrder;
import com.softwaremagico.kt.core.providers.*;
import com.softwaremagico.kt.persistence.entities.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class KingOfTheMountainHandler extends LeagueHandler {

    private final KingOfTheMountainFightManager kingOfTheMountainFightManager;
    private final FightProvider fightProvider;
    private final GroupProvider groupProvider;
    private final TeamProvider teamProvider;
    private final RankingController rankingController;
    private final GroupConverter groupConverter;
    private final TeamConverter teamConverter;

    private final TournamentProvider tournamentProvider;
    private final TournamentExtraPropertyProvider tournamentExtraPropertyProvider;

    public KingOfTheMountainHandler(KingOfTheMountainFightManager kingOfTheMountainFightManager, FightProvider fightProvider,
                                    GroupProvider groupProvider, TeamProvider teamProvider, GroupConverter groupConverter,
                                    RankingController rankingController, TeamConverter teamConverter, TournamentProvider tournamentProvider,
                                    TournamentExtraPropertyProvider tournamentExtraPropertyProvider) {
        super(groupProvider, teamProvider, groupConverter, rankingController);
        this.kingOfTheMountainFightManager = kingOfTheMountainFightManager;
        this.fightProvider = fightProvider;
        this.groupProvider = groupProvider;
        this.teamProvider = teamProvider;
        this.rankingController = rankingController;
        this.groupConverter = groupConverter;
        this.teamConverter = teamConverter;
        this.tournamentProvider = tournamentProvider;
        this.tournamentExtraPropertyProvider = tournamentExtraPropertyProvider;
    }

    @Override
    public List<Fight> createFights(Tournament tournament, TeamsOrder teamsOrder, String createdBy) {
        return createFights(tournament, teamsOrder, getNextLevel(tournament), createdBy);
    }

    private int getNextLevel(Tournament tournament) {
        //Each group on a different level, to ensure that the last group winner is the king of the mountain and the winner of the league.
        return (int) groupProvider.count(tournament);
    }

    @Override
    public List<Fight> createFights(Tournament tournament, TeamsOrder teamsOrder, Integer level, String createdBy) {
        //Create fights from first group.
        final List<Fight> fights = fightProvider.saveAll(kingOfTheMountainFightManager.createFights(tournament,
                getGroup(tournament).getTeams().subList(0, 2), level, createdBy));
        final Group group = getGroup(tournament);
        group.setFights(fights);
        groupProvider.save(group);
        return fights;
    }

    @Override
    public List<Fight> createNextFights(Tournament tournament, String createdBy) {
        //Generates next group.
        final int level = getNextLevel(tournament);
        final Group group = addGroup(tournament, getGroupTeams(tournament, level), level, 0);
        final List<Fight> fights = fightProvider.saveAll(kingOfTheMountainFightManager.createFights(tournament, group.getTeams(),
                level, createdBy));
        group.setFights(fights);
        groupProvider.save(group);
        return fights;
    }

    private List<Team> getGroupTeams(Tournament tournament, int level) {
        final List<Team> existingTeams = teamProvider.getAll(tournament);
        final List<Team> teams = new ArrayList<>();
        final List<Group> groups = groupProvider.getGroupsByLevel(tournament, level - 1);
        //Repository OrderByIndex not working well...
        groups.sort(Comparator.comparing(Group::getLevel).thenComparing(Group::getIndex));
        final Group lastGroup = !groups.isEmpty() ? groups.get(groups.size() - 1) : null;
        final Map<Integer, List<TeamDTO>> ranking = rankingController.getTeamsByPosition(groupConverter.convert(new GroupConverterRequest(lastGroup)));
        //Previous winner with no draw
        if (lastGroup != null && ranking.get(0) != null && ranking.get(0).size() == 1) {
            final Team previousWinner = teamConverter.reverse(ranking.get(0).get(0));
            final Team previousLooser = teamConverter.reverse(ranking.get(1).get(0));
            //Next team on the list. Looser is the other team on the previous group.
            teams.add(getNextTeam(existingTeams, Collections.singletonList(previousWinner), Collections.singletonList(previousLooser), tournament));
            //Add winner on the same color
            teams.add(lastGroup.getTeams().indexOf(previousWinner), previousWinner);
        } else {
            //Depending on the configuration.
            TournamentExtraProperty extraProperty = tournamentExtraPropertyProvider.getByTournamentAndProperty(tournament,
                    TournamentExtraPropertyKey.KING_DRAW_RESOLUTION);
            if (extraProperty == null) {
                extraProperty = tournamentExtraPropertyProvider.save(new TournamentExtraProperty(tournament,
                        TournamentExtraPropertyKey.KING_DRAW_RESOLUTION, DrawResolution.BOTH_ELIMINATED.name()));
            }

            final DrawResolution drawResolution = DrawResolution.getFromTag(extraProperty.getValue());
            final Group previousLastGroup = level > 1 ? groupProvider.getGroupsByLevel(tournament, level - 2).get(0) : null;
            switch (drawResolution) {
                case BOTH_ELIMINATED:
                    bothEliminated(existingTeams, teams, teamConverter.reverseAll(ranking.get(0)), tournament);
                    break;
                case OLDEST_ELIMINATED:
                    if (previousLastGroup == null) {
                        bothEliminated(existingTeams, teams, teamConverter.reverseAll(ranking.get(0)), tournament);
                    } else {
                        final List<Team> previousLastGroupTeams = previousLastGroup.getTeams();
                        if (lastGroup != null) {
                            previousLastGroupTeams.retainAll(lastGroup.getTeams());
                        }
                        oldestEliminated(existingTeams, teams, teamConverter.reverseAll(ranking.get(0)), previousLastGroupTeams, tournament, lastGroup);
                    }
                    break;
                case NEWEST_ELIMINATED:
                    if (previousLastGroup == null) {
                        bothEliminated(existingTeams, teams, teamConverter.reverseAll(ranking.get(0)), tournament);
                    } else {
                        final List<Team> previousLastGroupTeams = previousLastGroup.getTeams();
                        if (lastGroup != null) {
                            previousLastGroupTeams.retainAll(lastGroup.getTeams());
                        }
                        newestEliminated(existingTeams, teams, teamConverter.reverseAll(ranking.get(0)), previousLastGroupTeams, tournament, lastGroup);
                    }
                    break;
            }
        }
        return teams;
    }

    private void bothEliminated(final List<Team> existingTeams, final List<Team> nextTeams, List<Team> previousWinners, Tournament tournament) {
        //A draw!
        final Team firstTeam = getNextTeam(existingTeams, previousWinners, new ArrayList<>(), tournament);
        nextTeams.add(firstTeam);
        //Avoid to select again the same team.
        previousWinners.add(firstTeam);
        nextTeams.add(getNextTeam(existingTeams, previousWinners, new ArrayList<>(), tournament));
    }

    private void oldestEliminated(final List<Team> existingTeams, final List<Team> nextTeams, List<Team> previousWinners, List<Team> previousLastGroupWinners,
                                  Tournament tournament, Group lastGroup) {
        // Add a new team to the fight.
        final Team firstTeam = getNextTeam(existingTeams, previousWinners, new ArrayList<>(), tournament);
        nextTeams.add(firstTeam);
        //Remove the winner that has been on the previous group.
        previousWinners.removeAll(previousLastGroupWinners);
        //Include the newest winner on the same position.
        if (lastGroup != null) {
            nextTeams.add(lastGroup.getTeams().indexOf(previousWinners.get(0)), previousWinners.get(0));
        }
    }

    private void newestEliminated(final List<Team> existingTeams, final List<Team> nextTeams, List<Team> previousWinners, List<Team> previousLastGroupWinners,
                                  Tournament tournament, Group lastGroup) {
        // Add a new team to the fight.
        final Team firstTeam = getNextTeam(existingTeams, previousWinners, new ArrayList<>(), tournament);
        nextTeams.add(firstTeam);
        //Remove the winner that has been on the previous group.
        previousWinners.retainAll(previousLastGroupWinners);
        //Include the newest winner on the same position.
        if (lastGroup != null) {
            nextTeams.add(lastGroup.getTeams().indexOf(previousWinners.get(0)), previousWinners.get(0));
        }
    }

    private Team getNextTeam(List<Team> teams, List<Team> winners, List<Team> loosers, Tournament tournament) {
        final AtomicInteger kingIndex = new AtomicInteger(0);
        TournamentExtraProperty extraProperty = tournamentExtraPropertyProvider.getByTournamentAndProperty(tournament,
                TournamentExtraPropertyKey.KING_INDEX);
        if (extraProperty == null) {
            extraProperty = tournamentExtraPropertyProvider.save(new TournamentExtraProperty(tournament,
                    TournamentExtraPropertyKey.KING_INDEX, "1"));
        }
        try {
            kingIndex.addAndGet(Integer.parseInt(extraProperty.getValue()));
        } catch (NumberFormatException | NullPointerException e) {
            kingIndex.set(1);
        }
        kingIndex.getAndIncrement();
        // Avoid to repeat a winner.
        for (final Team winner : winners) {
            if (teams.indexOf(winner) == kingIndex.get() % teams.size()) {
                kingIndex.getAndIncrement();
            }
        }
        // Avoid to repeat a looser.
        for (final Team looser : loosers) {
            if (teams.indexOf(looser) == kingIndex.get() % teams.size()) {
                kingIndex.getAndIncrement();
            }
        }

        // Get next team and save index.
        final Team nextTeam = teams.get(kingIndex.get() % teams.size());
        extraProperty.setValue(kingIndex.get() + "");
        tournamentExtraPropertyProvider.save(extraProperty);
        return nextTeam;
    }

    @Override
    public List<Group> getGroups(Tournament tournament) {
        return groupProvider.getGroups(tournament);
    }

    @Override
    public Group addGroup(Tournament tournament, Group group) {
        return groupProvider.addGroup(tournament, group);
    }
}
