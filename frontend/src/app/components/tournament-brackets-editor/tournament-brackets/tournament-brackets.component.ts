import {Component, Input, OnInit} from '@angular/core';
import {Group} from "../../../models/group";
import {GroupsUpdatedService} from "./groups-updated.service";

@Component({
  selector: 'app-tournament-brackets',
  templateUrl: './tournament-brackets.component.html',
  styleUrls: ['./tournament-brackets.component.scss']
})
export class TournamentBracketsComponent implements OnInit {

  static readonly GROUP_HIGH: number = 100;
  static readonly GROUP_WIDTH: number = 300;
  static readonly GROUP_SEPARATION: number = 150;
  static readonly LEVEL_SEPARATION: number = 100;

  @Input()
  totalTeams: number;

  relations: Map<number, { src: number, dest: number }[]>;

  groupsByLevel: Map<number, Group[]> = new Map();


  constructor(private groupsUpdatedService: GroupsUpdatedService) {
  }

  ngOnInit(): void {
    this.groupsUpdatedService.areGroupsUpdated.subscribe((_groups: Group[]): void => {
      this.groupsByLevel = TournamentBracketsComponent.convert(_groups);
    });
    this.groupsUpdatedService.areRelationsUpdated.subscribe((_relations: Map<number, {
      src: number,
      dest: number
    }[]>): void => {
      this.relations = _relations;
    });
  }

  private static convert(groups: Group[]): Map<number, Group[]> {
    const groupsByLevel: Map<number, Group[]> = new Map();
    if (groups) {
      for (const group of groups) {
        if (group.level !== undefined) {
          if (!groupsByLevel.get(group.level)) {
            groupsByLevel.set(group.level, []);
          }
          groupsByLevel.get(group.level)?.push(group);
        }
      }
    }
    return groupsByLevel;
  }

  getGroupHigh(level: number, index: number): number {
    if (this.groupsByLevel && this.groupsByLevel!.get(level) && this.groupsByLevel.get(level)![index]) {
      const estimatedTeams: number = Math.ceil(this.totalTeams / this.groupsByLevel.get(0)!.length);
      const teams: number = this.groupsByLevel.get(level)![index].teams.length;

      if (level == 0 && estimatedTeams > 1) {
        return estimatedTeams * 90;
      } else if (teams && teams > 1) {
        return teams * 90;
      }
    }
    return TournamentBracketsComponent.GROUP_HIGH;
  }

  getGroupTopSeparation(level: number, group: number, groupsByLevel: Map<number, Group[]>): number {
    if (level == 0) {
      return group * (TournamentBracketsComponent.GROUP_SEPARATION + this.getGroupHigh(level, group));
    }
    if (groupsByLevel && groupsByLevel.get(0) && groupsByLevel.get(level)) {
      const maxHeight: number = groupsByLevel.get(0)!.length * (this.getGroupHigh(0, group) + TournamentBracketsComponent.GROUP_SEPARATION);
      const portion: number = (maxHeight / groupsByLevel.get(level)!.length);
      return (portion * (group + 1)) - portion / 2 - this.getGroupHigh(level, group) / 2 - TournamentBracketsComponent.GROUP_SEPARATION / 2
    }
    return 0;
  }

  getGroupLeftSeparation(level: number, group: number): number {
    return (TournamentBracketsComponent.GROUP_WIDTH + TournamentBracketsComponent.LEVEL_SEPARATION) * level;
  }

  getArrowX1Coordinate(level: number, group: number): number {
    return TournamentBracketsComponent.GROUP_WIDTH * (level + 1) + TournamentBracketsComponent.LEVEL_SEPARATION * level + 5;
  }

  getArrowY1Coordinate(level: number, group: number): number {
    return this.getGroupTopSeparation(level, group, this.groupsByLevel) + this.getGroupHigh(level, group) / 2;
  }

  getArrowX2Coordinate(column: number, group: number): number {
    return TournamentBracketsComponent.GROUP_WIDTH * column + TournamentBracketsComponent.LEVEL_SEPARATION * column + 5;
  }

  getArrowY2Coordinate(column: number, sourceGroupIndex: number, destinationGroupIndex: number): number {
    let correction: number = 15;
    if (sourceGroupIndex % 2 === 0) {
      correction = -correction;
    }
    return this.getGroupTopSeparation(column, destinationGroupIndex, this.groupsByLevel) + this.getGroupHigh(column, sourceGroupIndex) / 2 + correction;
  }

}
