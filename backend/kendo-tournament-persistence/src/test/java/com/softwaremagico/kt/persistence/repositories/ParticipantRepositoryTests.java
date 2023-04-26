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

import com.softwaremagico.kt.persistence.entities.Club;
import com.softwaremagico.kt.persistence.entities.Participant;
import com.softwaremagico.kt.utils.ClubFactory;
import com.softwaremagico.kt.utils.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest
@Test(groups = {"userRepository"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ParticipantRepositoryTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubFactory clubFactory;

    @BeforeClass
    public void createDefaultStructure() {

    }

    @Test
    public void addUser() throws Exception {
        Club club = clubFactory.createDefaultClub();
        club = clubRepository.save(club);
        Participant participant = userFactory.createDefaultUser(club);
        Assert.assertEquals(participantRepository.count(), 0);
        participant = participantRepository.save(participant);
        Assert.assertEquals(participantRepository.count(), 1);
        userFactory.checkDefaultUser(participantRepository.findById(participant.getId()).orElseThrow(() -> new Exception("Invalid user")));
    }
}
