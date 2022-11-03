import {Injectable} from '@angular/core';
import {RbacActivity} from "./rbac.activity";
import {UserService} from "../user.service";
import {UserRoles} from "./user-roles";

@Injectable({
  providedIn: 'root'
})
export class RbacService {

  private roles: UserRoles[];
  activities: RbacActivity[] = [];

  constructor(private userService: UserService) {
    this.userService.getRoles().subscribe(_roles => {
      this.setRoles(_roles);
    });
  }

  public getRoles() {
    this.userService.getRoles().subscribe(_roles => {
      this.setRoles(_roles);
    });
  }

  public setRoles(roles: UserRoles[]): void {
    this.roles = roles;
    this.activities = this.getActivities(roles);
  }

  public isAllowed(activity: RbacActivity): boolean {
    if (!this.activities) {
      return false;
    }
    return this.activities.includes(activity);
  }

  private getActivities(roles: string[]): RbacActivity[] {
    let activities: RbacActivity[] = this.getGuestActivities();
    for (const role of roles) {
      switch (role) {
        case 'admin':
          activities = activities.concat(this.getAdminActivities());
          break;
        case 'editor':
          activities = activities.concat(this.getEditorActivities());
          break;
        case 'viewer':
          activities = activities.concat(this.getViewerActivities());
          break;
      }
    }
    return activities;
  }

  private removeActivity(activities: RbacActivity[], activityToRemove: RbacActivity): void {
    const index = activities.indexOf(activityToRemove, 0);
    if (index > -1) {
      activities.splice(index, 1);
    }
  }

  private getAdminActivities(): RbacActivity[] {
    return RbacActivity.toArray();
  }

  private getEditorActivities(): RbacActivity[] {
    const adminActivities = this.getAdminActivities();
    //Remove user management activities,
    this.removeActivity(adminActivities, RbacActivity.READ_ALL_USERS);
    this.removeActivity(adminActivities, RbacActivity.READ_ONE_USER);
    this.removeActivity(adminActivities, RbacActivity.CREATE_USER);
    this.removeActivity(adminActivities, RbacActivity.EDIT_USER);
    this.removeActivity(adminActivities, RbacActivity.DELETE_USER);
    return adminActivities;
  }

  private getViewerActivities(): RbacActivity[] {
    return [RbacActivity.READ_ALL_TOURNAMENTS,
      RbacActivity.READ_ONE_TOURNAMENT,
      RbacActivity.READ_ALL_TEAMS,
      RbacActivity.READ_ONE_TEAM,
      RbacActivity.READ_ALL_FIGHTS,
      RbacActivity.READ_ONE_FIGHT,
      RbacActivity.READ_ALL_DUELS,
      RbacActivity.READ_ONE_DUEL,
      RbacActivity.READ_ALL_RANKINGS,
      RbacActivity.READ_ONE_RANKING,
      RbacActivity.CHANGE_PASSWORD,
      RbacActivity.CAN_LOGOUT,
      RbacActivity.CHANGE_LANGUAGE,];
  }

  private getGuestActivities(): RbacActivity[] {
    return [];
  }

}
