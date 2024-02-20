import {NgModule} from '@angular/core';
import {CommonModule} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";
import {TranslateModule} from "@ngx-translate/core";
import {BasicTableModule} from "../../components/basic/basic-table/basic-table.module";
import {MatTooltipModule} from "@angular/material/tooltip";
import {RbacModule} from "../../pipes/rbac-pipe/rbac.module";
import {MatSpinnerOverlayModule} from "../../components/mat-spinner-overlay/mat-spinner-overlay.module";
import {MatButtonModule} from "@angular/material/button";
import {ParticipantListComponent} from "./participant-list.component";
import {ParticipantRoutingModule} from "./participant-routing.module";

@NgModule({
  declarations: [ParticipantListComponent],
  exports: [ParticipantListComponent],
  imports: [
    ParticipantRoutingModule,
    CommonModule,
    MatIconModule,
    TranslateModule,
    BasicTableModule,
    MatTooltipModule,
    MatButtonModule,
    RbacModule,
    MatSpinnerOverlayModule
  ]
})
export class ParticipantListModule {
}
