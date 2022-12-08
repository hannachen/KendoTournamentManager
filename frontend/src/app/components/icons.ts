import {NgModule} from "@angular/core";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {MatIconRegistry} from "@angular/material/icon";


@NgModule({})
export class IconModule {

  private path: string = "../../../assets/icons";

  constructor(private domSanitizer: DomSanitizer, public matIconRegistry: MatIconRegistry) {
    this.matIconRegistry
      .addSvgIcon("men", this.setPath(`${this.path}/men.svg`))
      .addSvgIcon("shinai", this.setPath(`${this.path}/shinai.svg`))
      .addSvgIcon("team", this.setPath(`${this.path}/team.svg`))
      .addSvgIcon("fight", this.setPath(`${this.path}/fight.svg`))
      .addSvgIcon("card", this.setPath(`${this.path}/card.svg`))
      .addSvgIcon("teams-classification", this.setPath(`${this.path}/teams-classification.svg`))
      .addSvgIcon("undraw-score", this.setPath(`${this.path}/undraw-score.svg`))
      .addSvgIcon("tournament-blog", this.setPath(`${this.path}/blog.svg`))
      .addSvgIcon("ribbon", this.setPath(`${this.path}/ribbon.svg`))
      .addSvgIcon("exchange-colors", this.setPath(`${this.path}/exchange.svg`))
      .addSvgIcon("exchange-teams", this.setPath(`${this.path}/exchange-team.svg`))
      .addSvgIcon("member-order", this.setPath(`${this.path}/member-order.svg`))
      .addSvgIcon("member-order-disable", this.setPath(`${this.path}/member-order-disable.svg`))
      .addSvgIcon("competitors-classification", this.setPath(`${this.path}/competitors-classification.svg`))
      .addSvgIcon("match", this.setPath(`${this.path}/match.svg`));
  }

  private setPath(url: string): SafeResourceUrl {
    return this.domSanitizer.bypassSecurityTrustResourceUrl(url);
  }
}
