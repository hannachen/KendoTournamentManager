import {Tournament} from "./tournament";
import {TournamentExtraPropertyKey} from "./tournament-extra-property-key";
import {Element} from "./element";

export class TournamentExtendedProperty extends Element {
  public tournament: Tournament;
  public property: TournamentExtraPropertyKey;
  public value: string;

  public static override copy(source: TournamentExtendedProperty, target: TournamentExtendedProperty): void {
    Element.copy(source, target);
    if (source.tournament !== undefined) {
      target.tournament = Tournament.clone(source.tournament);
    }
    target.property = source.property;
    target.value = source.value;
  }

  public static clone(data: TournamentExtendedProperty): TournamentExtendedProperty {
    const instance: TournamentExtendedProperty = new TournamentExtendedProperty();
    this.copy(data, instance);
    return instance;
  }

}
