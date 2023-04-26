package com.softwaremagico.kt.utils;

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
import org.springframework.stereotype.Service;
import org.testng.Assert;

@Service
public class ClubFactory {
    private static final String CLUB_NAME = "Name";
    private static final String CLUB_COUNTRY = "Country";
    private static final String CLUB_CITY = "City";
    private static final String CLUB_ADDRESS = "Address";
    private static final String CLUB_EMAIL = "email";
    private static final String CLUB_PHONE = "phone";
    private static final String CLUB_WEB = "web";
    private static final String CLUB_REPRESENTATIVE = "representative";

    public Club createDefaultClub() {
        Club club = new Club();
        club.setCity(CLUB_CITY);
        club.setCountry(CLUB_COUNTRY);
        club.setPhone(CLUB_PHONE);
        club.setAddress(CLUB_ADDRESS);
        club.setEmail(CLUB_EMAIL);
        club.setName(CLUB_NAME);
        club.setWeb(CLUB_WEB);
        club.setRepresentative(CLUB_REPRESENTATIVE);

        return club;
    }

    public void checkDefaultUser(Club club) {
        Assert.assertEquals(club.getName(), CLUB_NAME);
        Assert.assertEquals(club.getCountry(), CLUB_COUNTRY);
        Assert.assertEquals(club.getCity(), CLUB_CITY);
        Assert.assertEquals(club.getAddress(), CLUB_ADDRESS);
        Assert.assertEquals(club.getEmail(), CLUB_EMAIL);
        Assert.assertEquals(club.getPhone(), CLUB_PHONE);
        Assert.assertEquals(club.getWeb(), CLUB_WEB);
        Assert.assertEquals(club.getRepresentativeId(), CLUB_REPRESENTATIVE);
    }
}
