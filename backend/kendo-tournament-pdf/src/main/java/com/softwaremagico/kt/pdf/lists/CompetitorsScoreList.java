package com.softwaremagico.kt.pdf.lists;

/*-
 * #%L
 * Kendo Tournament Manager (PDF)
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


import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.softwaremagico.kt.core.controller.models.TournamentDTO;
import com.softwaremagico.kt.core.score.ScoreOfCompetitor;
import com.softwaremagico.kt.pdf.BaseColor;
import com.softwaremagico.kt.pdf.ParentList;
import com.softwaremagico.kt.pdf.PdfTheme;
import com.softwaremagico.kt.utils.NameUtils;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

/**
 * Creates a sheet with the competitors ranking depending on the performance on the tournament.
 */
public class CompetitorsScoreList extends ParentList {
    private final List<ScoreOfCompetitor> competitorTopTen;
    private final TournamentDTO tournament;
    private final MessageSource messageSource;
    private final Locale locale;

    public CompetitorsScoreList(MessageSource messageSource, Locale locale, TournamentDTO tournament, List<ScoreOfCompetitor> competitorTopTen) {
        this.tournament = tournament;
        this.competitorTopTen = competitorTopTen;
        this.messageSource = messageSource;
        this.locale = locale;
    }

    @Override
    public void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
                                BaseFont font, int fontSize) {
        PdfPCell cell;

        // Tournament name.
        if (tournament != null) {
            cell = new PdfPCell(new Paragraph(tournament.getName(), new Font(font, fontSize)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        } else {
            // List name.
            cell = new PdfPCell(new Paragraph(
                    messageSource.getMessage("classification.competitors.title", null, locale), new Font(font, fontSize)));
        }

        cell.setColspan(getTableWidths().length);
        cell.setBorderWidth(HEADER_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.addCell(cell);
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
                               BaseFont font, int fontSize) {

        mainTable.addCell(getCell(messageSource.getMessage("classification.competitors.competitor.name", null, locale),
                PdfTheme.getBasicFont(), 0, Element.ALIGN_CENTER, Font.BOLD));
        mainTable.addCell(getCell(messageSource.getMessage("classification.competitors.duels.won", null, locale),
                PdfTheme.getBasicFont(), 0, Element.ALIGN_CENTER, Font.BOLD));
        mainTable.addCell(getCell(messageSource.getMessage("classification.competitors.hits", null, locale),
                PdfTheme.getBasicFont(), 0, Element.ALIGN_CENTER, Font.BOLD));
        mainTable.addCell(getCell(messageSource.getMessage("classification.competitors.fights", null, locale),
                PdfTheme.getBasicFont(), 0, Element.ALIGN_CENTER, Font.BOLD));

        for (final ScoreOfCompetitor scoreOfCompetitor : competitorTopTen) {
            mainTable.addCell(getCell(NameUtils.getLastnameName(scoreOfCompetitor.getCompetitor()), PdfTheme.getHandwrittenFont(), 1, Element.ALIGN_CENTER));
            mainTable.addCell(getCell(scoreOfCompetitor.getWonDuels() + (scoreOfCompetitor.getUntieDuels() > 0 ? "*" : "") + "/"
                    + scoreOfCompetitor.getDrawDuels(), PdfTheme.getHandwrittenFont(), 1, Element.ALIGN_CENTER));
            mainTable.addCell(getCell("" + scoreOfCompetitor.getHits() + (scoreOfCompetitor.getUntieHits() > 0 ? "*" : ""),
                    PdfTheme.getHandwrittenFont(), 1, Element.ALIGN_CENTER));
            mainTable.addCell(getCell("" + scoreOfCompetitor.getDuelsDone(), PdfTheme.getHandwrittenFont(), 1, Element.ALIGN_CENTER));
        }
    }

    @Override
    public void createFooterRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer,
                                BaseFont font, int fontSize) {
        mainTable.addCell(getEmptyRow());
    }

    @Override
    public float[] getTableWidths() {
        return new float[]{0.50f, 0.20f, 0.20f, 0.20f};
    }

    @Override
    public void setTableProperties(PdfPTable mainTable) {
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.getDefaultCell().setBorder(TABLE_BORDER);
        mainTable.getDefaultCell().setBorderColor(BaseColor.BLACK);
        mainTable.setWidthPercentage(100);
    }

    @Override
    protected void addDocumentWriterEvents(PdfWriter writer) {
        // No background.
    }

    @Override
    protected Rectangle getPageSize() {
        return PageSize.A4;
    }
}
