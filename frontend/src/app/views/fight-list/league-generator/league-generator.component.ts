import {Component, Inject, OnInit, Optional} from '@angular/core';
import {Action} from "../../../action";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Tournament} from "../../../models/tournament";
import {TeamListData} from "../../../components/basic/team-list/team-list-data";
import {TeamService} from "../../../services/team.service";
import {CdkDragDrop, transferArrayItem} from "@angular/cdk/drag-drop";
import {Team} from "../../../models/team";
import {RbacBasedComponent} from "../../../components/RbacBasedComponent";
import {RbacService} from "../../../services/rbac/rbac.service";
import {TournamentType} from "../../../models/tournament-type";
import {TournamentService} from "../../../services/tournament.service";
import {FormControl} from "@angular/forms";
import {TournamentExtendedPropertiesService} from "../../../services/tournament-extended-properties.service";
import {TournamentExtraPropertyKey} from "../../../models/tournament-extra-property-key";
import {TournamentExtendedProperty} from "../../../models/tournament-extended-property.model";
import {MessageService} from "../../../services/message.service";

@Component({
  selector: 'app-league-generator',
  templateUrl: './league-generator.component.html',
  styleUrls: ['./league-generator.component.scss']
})
export class LeagueGeneratorComponent extends RbacBasedComponent implements OnInit {

  teamListData: TeamListData = new TeamListData();
  title: string;
  action: Action;
  actionName: string;
  teamsOrder: Team[] = [];
  canHaveDuplicated: boolean;
  tournament: Tournament;
  avoidDuplicates = new FormControl('', []);

  constructor(public dialogRef: MatDialogRef<LeagueGeneratorComponent>,
              private teamService: TeamService, rbacService: RbacService, private tournamentService: TournamentService,
              private tournamentExtendedPropertiesService: TournamentExtendedPropertiesService,
              private messageService: MessageService,
              @Optional() @Inject(MAT_DIALOG_DATA) public data: { title: string, action: Action, tournament: Tournament }) {
    super(rbacService);
    this.title = data.title;
    this.action = data.action;
    this.actionName = Action[data.action];
    this.tournament = data.tournament;
    this.canHaveDuplicated = TournamentType.canHaveDuplicates(this.tournament.type);
  }

  ngOnInit(): void {
    this.tournamentExtendedPropertiesService.getByTournamentAndKey(this.tournament, TournamentExtraPropertyKey.MAXIMIZE_FIGHTS)
      .subscribe(_tournamentProperty => {
        if (_tournamentProperty) {
          this.avoidDuplicates.setValue(_tournamentProperty.value.toLowerCase() !== "true");
        } else {
          this.avoidDuplicates.setValue(false);
        }
      });
    this.teamService.getFromTournament(this.tournament).subscribe(teams => {
      if (teams) {
        teams.sort(function (a, b) {
          return a.name.localeCompare(b.name);
        });
      }
      this.teamListData.teams = teams;
      this.teamListData.filteredTeams = teams;
    });
    this.avoidDuplicates.valueChanges.subscribe(avoidDuplicates => {
      const tournamentProperty: TournamentExtendedProperty = new TournamentExtendedProperty();
      tournamentProperty.tournament = this.tournament;
      tournamentProperty.value = !avoidDuplicates + "";
      tournamentProperty.property = TournamentExtraPropertyKey.MAXIMIZE_FIGHTS;
      this.tournamentExtendedPropertiesService.update(tournamentProperty).subscribe(() => {
        this.messageService.infoMessage('infoTournamentUpdated');
      });
    });
  }

  acceptAction() {
    this.dialogRef.close({data: this.teamsOrder, action: this.action});
  }

  cancelDialog() {
    this.dialogRef.close({action: Action.Cancel});
  }

  private transferCard(event: CdkDragDrop<Team[], any>): Team {
    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex,
    );
    return event.container.data[event.currentIndex];
  }

  removeTeam(event: CdkDragDrop<Team[], any>) {
    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex,
    );
    // const team: Team = event.container.data[event.currentIndex];
    this.teamListData.filteredTeams.sort((a, b) => a.name.localeCompare(b.name));
    this.teamListData.teams.sort((a, b) => a.name.localeCompare(b.name));
  }

  dropTeam(event: CdkDragDrop<Team[], any>) {
    const team: Team = this.transferCard(event);
    if (this.teamListData.teams.includes(team)) {
      this.teamListData.teams.splice(this.teamListData.teams.indexOf(team), 1);
    }
    if (this.teamListData.filteredTeams.includes(team)) {
      this.teamListData.filteredTeams.splice(this.teamListData.filteredTeams.indexOf(team), 1);
    }
  }

  sortedTeams() {
    this.teamsOrder.push(...this.teamListData.teams);
    this.teamsOrder.sort(function (a, b) {
      return a.name.localeCompare(b.name);
    });
    this.teamListData.filteredTeams.splice(0, this.teamListData.filteredTeams.length);
    this.teamListData.teams.splice(0, this.teamListData.teams.length);
  }

  randomTeams() {
    this.teamListData.teams.push(...this.teamsOrder);
    this.teamsOrder = [];
    while (this.teamListData.teams.length > 0) {
      const team: Team = this.getRandomTeam(this.teamListData.teams);
      this.teamsOrder.push(team);
    }
  }

  getRandomTeam(teams: Team[]): Team {
    const selected: number = Math.floor(Math.random() * teams.length);
    const team: Team = teams[selected];
    teams.splice(selected, 1);
    return team;
  }

}
