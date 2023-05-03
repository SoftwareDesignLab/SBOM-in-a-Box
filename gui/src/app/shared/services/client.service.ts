/** @Author Justin Jantzi */

import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private readonly SERVER_URL: string = 'http://localhost:8080/plugfest/';
  private loggedIn: boolean = false;
  public showLogin: boolean = false;

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': 'http://localhost:4200'
    }),
    params: new HttpParams()
  };

  constructor(private http: HttpClient) { 
  }

  get(path: string, params: HttpParams = new HttpParams()) {
    this.httpOptions.params = params;
    return this.http.get(this.SERVER_URL + path, this.httpOptions);
  }

  post(path: string, params: HttpParams = new HttpParams()) {
    this.httpOptions.params = params;
    return this.http.post(this.SERVER_URL + path, this.httpOptions.params);
  }
}
