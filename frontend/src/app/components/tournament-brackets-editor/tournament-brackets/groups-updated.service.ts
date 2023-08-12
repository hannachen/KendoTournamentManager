import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {Group} from "../../../models/group";

@Injectable({
  providedIn: 'root'
})
export class GroupsUpdatedService {

  public areGroupsUpdated: BehaviorSubject<Group[]> = new BehaviorSubject<Group[]>([]);

  public areRelationsUpdated: BehaviorSubject<Map<number, {
    src: number,
    dest: number
  }[]>> = new BehaviorSubject<Map<number, { src: number, dest: number }[]>>(new Map());

  public areTotalTeamsNumberUpdated: BehaviorSubject<number> = new BehaviorSubject<number>(0);
}
