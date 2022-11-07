import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatSliderModule} from '@angular/material/slider';
import {MatButtonModule} from '@angular/material/button';
import {MatListModule} from '@angular/material/list';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatIconModule} from '@angular/material/icon';
import {MatToolbarModule} from '@angular/material/toolbar';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {FlexLayoutModule} from '@angular/flex-layout';
import {TranslateLoader, TranslateModule, TranslateService} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {ClubListComponent} from './views/club-list/club-list.component';
import {MatTableModule} from "@angular/material/table";
import {MatPaginatorIntl, MatPaginatorModule} from "@angular/material/paginator";
import {MatMenuModule} from "@angular/material/menu";
import {ClubDialogBoxComponent} from './views/club-list/club-dialog-box/club-dialog-box.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatDialogModule} from "@angular/material/dialog";
import {MatSortModule} from '@angular/material/sort';
import {MatInputModule} from "@angular/material/input";
import {LoginComponent} from "./views/login/login.component";
import {CookieService} from 'ngx-cookie-service';
import {MatSelectModule} from "@angular/material/select";
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatCardModule} from "@angular/material/card";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {BasicTableModule} from "./components/basic/basic-table/basic-table.module";
import {ParticipantListComponent} from './views/participant-list/participant-list.component';
import {
  ParticipantDialogBoxComponent
} from './views/participant-list/participant-dialog-box/participant-dialog-box.component';
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {TournamentListComponent} from './views/tournament-list/tournament-list.component';
import {
  TournamentDialogBoxComponent
} from './views/tournament-list/tournament-dialog-box/tournament-dialog-box.component';
import {UserListComponent} from './components/basic/user-list/user-list.component';
import {TournamentRolesComponent} from './views/tournament-list/tournament-roles/tournament-roles.component';
import {DragDropModule} from "@angular/cdk/drag-drop";
import {TournamentTeamsComponent} from './views/tournament-list/tournament-teams/tournament-teams.component';
import {IconModule} from "./components/icons";
import {FightListComponent} from './views/fight-list/fight-list.component';
import {FightComponent} from './components/fight/fight.component';
import {DuelComponent} from './components/fight/duel/duel.component';
import {UserScoreComponent} from './components/fight/duel/user-score/user-score.component';
import {ScoreComponent} from './components/fight/duel/user-score/score/score.component';
import {UserNameComponent} from './components/fight/duel/user-score/user-name/user-name.component';
import {FaultComponent} from './components/fight/duel/user-score/fault/fault.component';
import {DrawComponent} from './components/fight/duel/draw/draw.component';
import {FightDialogBoxComponent} from './views/fight-list/fight-dialog-box/fight-dialog-box.component';
import {UserCardComponent} from './components/user-card/user-card.component';
import {TeamCardComponent} from './components/team-card/team-card.component';
import {registerLocaleData} from "@angular/common";
import localeES from "@angular/common/locales/es";
import localeCAT from "@angular/common/locales/ca-ES-VALENCIA";
import localeIT from "@angular/common/locales/it";
import localeDE from "@angular/common/locales/de";
import localeNL from "@angular/common/locales/nds-NL";
import {TeamListComponent} from './components/basic/team-list/team-list.component';
import {LeagueGeneratorComponent} from './views/fight-list/league-generator/league-generator.component';
import {ConfirmationDialogComponent} from './components/basic/confirmation-dialog/confirmation-dialog.component';
import {TeamRankingComponent} from './views/fight-list/team-ranking/team-ranking.component';
import {CompetitorsRankingComponent} from './views/fight-list/competitors-ranking/competitors-ranking.component';
import {TimerComponent} from './components/timer/timer.component';
import {UndrawTeamsComponent} from './views/fight-list/undraw-teams/undraw-teams.component';
import {MemberSelectorComponent} from './components/basic/member-selector/member-selector.component';
import {UntieFightComponent} from './components/untie-fight/untie-fight.component';
import {MatTabsModule} from "@angular/material/tabs";
import {MatSpinnerOverlayComponent} from "./components/mat-spinner-overlay/mat-spinner-overlay.component";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {PaginatorI18n} from "./components/basic/basic-table/paginator-i18n";
import {AuthenticatedUserListComponent} from './views/authenticated-user-list/authenticated-user-list.component';
import {
  AuthenticatedUserDialogBoxComponent
} from './views/authenticated-user-list/authenticated-user-dialog-box/authenticated-user-dialog-box.component';
import {PasswordsComponent} from './views/passwords/passwords.component';
import { RbacPipe } from './pipes/rbac.pipe';
import {MatTooltipModule} from "@angular/material/tooltip";


registerLocaleData(localeES, "es");
registerLocaleData(localeIT, "it");
registerLocaleData(localeCAT, "ca");
registerLocaleData(localeDE, "de");
registerLocaleData(localeNL, "nl");

@NgModule({
  declarations: [
    AppComponent,
    ClubListComponent,
    ClubDialogBoxComponent,
    LoginComponent,
    ParticipantListComponent,
    ParticipantDialogBoxComponent,
    TournamentListComponent,
    TournamentDialogBoxComponent,
    UserListComponent,
    TournamentRolesComponent,
    TournamentTeamsComponent,
    FightListComponent,
    FightComponent,
    DuelComponent,
    UserScoreComponent,
    ScoreComponent,
    UserNameComponent,
    FaultComponent,
    DrawComponent,
    FightDialogBoxComponent,
    UserCardComponent,
    TeamCardComponent,
    TeamListComponent,
    LeagueGeneratorComponent,
    ConfirmationDialogComponent,
    TeamRankingComponent,
    CompetitorsRankingComponent,
    TimerComponent,
    UndrawTeamsComponent,
    MemberSelectorComponent,
    UntieFightComponent,
    MatSpinnerOverlayComponent,
    AuthenticatedUserListComponent,
    AuthenticatedUserDialogBoxComponent,
    PasswordsComponent,
    RbacPipe
  ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        FlexLayoutModule,
        HttpClientModule,
        MatToolbarModule,
        MatSidenavModule,
        MatListModule,
        MatButtonModule,
        MatIconModule,
        MatSliderModule,
        AppRoutingModule,
        MatTableModule,
        MatPaginatorModule,
        MatMenuModule,
        FormsModule,
        MatFormFieldModule,
        MatDialogModule,
        MatSortModule,
        MatInputModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: httpTranslateLoader,
                deps: [HttpClient]
            }
        }),
        MatSelectModule,
        MatSnackBarModule,
        MatCardModule,
        FormsModule,
        ReactiveFormsModule,
        MatExpansionModule,
        MatCheckboxModule,
        BasicTableModule,
        MatAutocompleteModule,
        DragDropModule,
        IconModule,
        MatTabsModule,
        MatProgressSpinnerModule,
        MatTooltipModule
    ],
  providers: [CookieService, {
    provide: MatPaginatorIntl,
    useFactory: (translate: TranslateService) => {
      const service = new PaginatorI18n();
      service.injectTranslateService(translate);
      return service;
    },
    deps: [TranslateService]
  }],
  bootstrap: [AppComponent]
})
export class AppModule {
}

export function httpTranslateLoader(http: HttpClient) {
  return new TranslateHttpLoader(http);
}
