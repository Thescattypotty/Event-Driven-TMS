import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserResponse } from '../models/user-response';
import { UserRequest } from '../models/user-request';

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private API_URL = 'http://localhost:8222/api/v1/user';
    constructor(private http: HttpClient) { }

    getUser(id: String): Observable<UserResponse>{
        return this.http.get<UserResponse>(`${this.API_URL}/${id}`);
    }
    getUsers(): Observable<UserResponse[]>{
        return this.http.get<UserResponse[]>(`${this.API_URL}`);
    }

    createUser(userRequest: UserRequest): Observable<void>{
        return this.http.post<void>(`${this.API_URL}`, userRequest);
    }
    updateUser(id: String , userRequest : UserRequest): Observable<void>{
        return this.http.put<void>(`${this.API_URL}/${id}`, userRequest);
    }
    
    deleteUser(id: String): Observable<void>{
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }
}
