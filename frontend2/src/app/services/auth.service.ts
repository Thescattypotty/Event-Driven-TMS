import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginRequest } from '../models/login-request';
import { JwtResponse } from '../models/jwt-response';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private API_URL = 'http://localhost:8222/api/v1/auth';
    private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
    public isAuthenticated = this.isAuthenticatedSubject.asObservable();
    private userSubject = new BehaviorSubject<any>(null); // Holds user data
    public user = this.userSubject.asObservable();

    constructor(private http: HttpClient) {
        this.checkToken();
    }
    login(loginRequest: LoginRequest): Observable<JwtResponse> {
        return this.http.post<JwtResponse>(`${this.API_URL}/login`, loginRequest);
    }

    setToken(accessToken: string, refreshToken: string) {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        this.isAuthenticatedSubject.next(true);
    }

    isLoggedIn(): boolean {
        return this.isLocalStorageAvailable() && !!localStorage.getItem('accessToken');
    }

    logout() {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        this.isAuthenticatedSubject.next(false);
    }

    private checkToken() {
        if (this.isLocalStorageAvailable()) {
            const token = localStorage.getItem('accessToken');
            this.isAuthenticatedSubject.next(!!token);
            return;
        }
        this.isAuthenticatedSubject.next(false);
    }

    private isLocalStorageAvailable(): boolean {
        try {
            return typeof localStorage !== 'undefined';
        } catch (e) {
            return false;
        }
    }

    private getUserDetails() {
        if (this.isLoggedIn()) {
            const accessToken = localStorage.getItem('accessToken');
            this.http.get<any>(`${this.API_URL}/user-details`, {
                headers: { 'Authorization': `Bearer ${accessToken}` }
            }).subscribe(
                (user) => {
                    this.userSubject.next(user); // Set user data
                },
                (error) => {
                    console.error('Error fetching user details:', error);
                }
            );
        }
    }
}
