import {Component, Inject, OnInit, Optional} from '@angular/core';
import {Tournament} from "../../models/tournament";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RankingService} from "../../services/ranking.service";
import {ScoreOfCompetitor} from "../../models/score-of-competitor";
import {TranslateService} from "@ngx-translate/core";
import {RbacBasedComponent} from "../RbacBasedComponent";
import {RbacService} from "../../services/rbac/rbac.service";
import {Participant} from "../../models/participant";
import {DOCUMENT} from "@angular/common";

@Component({
  selector: 'app-competitors-ranking',
  templateUrl: './competitors-ranking.component.html',
  styleUrls: ['./competitors-ranking.component.scss']
})
export class CompetitorsRankingComponent extends RbacBasedComponent implements OnInit {

  competitorsScore: ScoreOfCompetitor[];
  tournament: Tournament | undefined;
  competitor: Participant | undefined;
  showIndex: boolean | undefined;

  constructor(public dialogRef: MatDialogRef<CompetitorsRankingComponent>,
              @Inject(DOCUMENT) document: Document,
              @Optional() @Inject(MAT_DIALOG_DATA) public data: {
                tournament: Tournament | undefined,
                competitor: Participant | undefined,
                showIndex: boolean | undefined,
              },
              private rankingService: RankingService, public translateService: TranslateService, rbacService: RbacService) {
    super(rbacService);
    this.tournament = data.tournament;
    this.competitor = data.competitor;
    this.showIndex = data.showIndex;
  }

  ngOnInit(): void {
    if (this.tournament?.id) {
      this.rankingService.getCompetitorsScoreRankingByTournament(this.tournament.id).subscribe((competitorsScore: ScoreOfCompetitor[]) => {
        this.competitorsScore = competitorsScore;
      });
    } else {
      this.rankingService.getCompetitorsGlobalScoreRanking(undefined).subscribe((competitorsScore: ScoreOfCompetitor[]) => {
        this.competitorsScore = competitorsScore;
        //Timeout to scroll after the component is drawn.
        setTimeout((): void => {
          this.scrollToScore(document.getElementById(this.competitor?.id + ""));
        }, 500);
      });
    }
  }

  closeDialog() {
    this.dialogRef.close();
  }

  downloadPDF() {
    if (this.tournament?.id) {
      this.rankingService.getCompetitorsScoreRankingByTournamentAsPdf(this.tournament.id).subscribe((pdf: Blob): void => {
        const blob: Blob = new Blob([pdf], {type: 'application/pdf'});
        const downloadURL: string = window.URL.createObjectURL(blob);

        const anchor: HTMLAnchorElement = document.createElement("a");
        anchor.download = "Competitors Ranking - " + this.tournament!.name + ".pdf";
        anchor.href = downloadURL;
        anchor.click();
      });
    } else {
      this.rankingService.getCompetitorsGlobalScoreRankingAsPdf(undefined).subscribe((pdf: Blob): void => {
        const blob: Blob = new Blob([pdf], {type: 'application/pdf'});
        const downloadURL: string = window.URL.createObjectURL(blob);

        const anchor: HTMLAnchorElement = document.createElement("a");
        anchor.download = "Competitors Ranking.pdf";
        anchor.href = downloadURL;
        anchor.click();
      });
    }
  }

  scrollToScore(row: HTMLElement | null): void {
    if (row) {
      row.scrollIntoView({behavior: 'smooth'});
    }
  }
}
