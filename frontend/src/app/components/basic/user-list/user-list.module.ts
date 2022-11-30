import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UserListComponent} from "./user-list.component";
import {FilterModule} from "../filter/filter.module";
import {UserCardModule} from "../../user-card/user-card.module";


@NgModule({
  declarations: [UserListComponent],
  exports: [
    UserListComponent
  ],
  imports: [
    CommonModule,
    FilterModule,
    UserCardModule,
  ]
})
export class UserListModule {
}
