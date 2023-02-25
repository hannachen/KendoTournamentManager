package com.softwaremagico.kt.persistence.values;

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

public enum AchievementType {

    BILLY_THE_KID,

    LETHAL_WEAPON,

    TERMINATOR,

    JUGGERNAUT,

    THE_KING,

    LOOKS_GOOD_FROM_FAR_AWAY_BUT,

    I_LOVE_THE_FLAGS,

    THE_TOWER,

    THE_CASTLE,

    ENTRENCHED,

    A_LITTLE_OF_EVERYTHING,

    BONE_BREAKER;


    public static AchievementType getType(String name) {
        for (final AchievementType type : AchievementType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
