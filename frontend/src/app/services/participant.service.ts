import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {EnvironmentService} from "../environment.service";
import {catchError, tap} from 'rxjs/operators';
import {Observable} from "rxjs";
import {Participant} from "../models/participant";
import {AuthenticatedUserService} from "./authenticated-user.service";
import {MessageService} from "./message.service";
import {LoggerService} from "./logger.service";

@Injectable({
  providedIn: 'root'
})
export class ParticipantService {

  private baseUrl = this.environmentService.getBackendUrl() + '/participants';

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + this.authenticatedUserService.getJwtValue()
    })
  };

  constructor(private http: HttpClient, private environmentService: EnvironmentService, private messageService: MessageService,
              private loggerService: LoggerService, public authenticatedUserService: AuthenticatedUserService) {
  }

  getAll(): Observable<Participant[]> {
    const url: string = `${this.baseUrl}`;
    return this.http.get<Participant[]>(url, this.httpOptions)
      .pipe(
        tap(_ => this.loggerService.info(`fetched all Participants`)),
        catchError(this.messageService.handleError<Participant[]>(`gets all`))
      );
  }

  get(id: number): Observable<Participant> {
    const url: string = `${this.baseUrl}/${id}`;
    return this.http.get<Participant>(url, this.httpOptions)
      .pipe(
        tap(_ => this.loggerService.info(`fetched participant id=${id}`)),
        catchError(this.messageService.handleError<Participant>(`get id=${id}`))
      );
  }

  deleteById(id: number) {
    const url: string = `${this.baseUrl}/${id}`;
    this.http.delete(url, this.httpOptions)
      .pipe(
        tap(_ => this.loggerService.info(`deleting participant id=${id}`)),
        catchError(this.messageService.handleError<Participant>(`delete id=${id}`))
      );
  }

  delete(participant: Participant): Observable<Participant> {
    const url: string = `${this.baseUrl}/delete`;
    return this.http.post<Participant>(url, participant, this.httpOptions)
      .pipe(
        tap(_ => this.loggerService.info(`deleting participant ${participant}`)),
        catchError(this.messageService.handleError<Participant>(`delete ${participant}`))
      );
  }

  add(participant: Participant): Observable<Participant> {
    const url: string = `${this.baseUrl}`;
    return this.http.post<Participant>(url, participant, this.httpOptions)
      .pipe(
        tap((newParticipant: Participant) => this.loggerService.info(`adding participant ${newParticipant}`)),
        catchError(this.messageService.handleError<Participant>(`adding ${participant}`))
      );
  }


  update(participant: Participant): Observable<Participant> {
    const url: string = `${this.baseUrl}`;
    return this.http.put<Participant>(url, participant, this.httpOptions)
      .pipe(
        tap((updatedParticipant: Participant) => this.loggerService.info(`updating participant ${updatedParticipant}`)),
        catchError(this.messageService.handleError<Participant>(`updating ${participant}`))
      );
  }
}
