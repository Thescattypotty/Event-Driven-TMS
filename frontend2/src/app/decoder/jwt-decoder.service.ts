import { Injectable } from '@angular/core';
import { UserService } from '../services/user.service';
import { catchError, map, Observable, of } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class JwtDecoderService {

    constructor(private userService: UserService) { }

    decodeToken(token: string): any {
        try {
          const payload = token.split('.')[1];
          const decoded = window.atob(payload);
          return JSON.parse(decoded);
        } catch (error) {
          console.error('Error decoding token:', error);
          return null;
        }
      }

      getUserId(): Observable<string> {
        const token = localStorage.getItem('accessToken');
        if (!token) {
          console.error('No token found in localStorage');
          return of('');
        }
    
        const decoded = this.decodeToken(token);
        if (!decoded || !decoded.email) {
          console.error('Invalid token or missing email in token');
          return of('');
        }
    
        return this.userService.getUserByEmail(decoded.email).pipe(
          map(response => {
            console.log('User ID retrieved:', response.id);
            return response.id.toString();
          }),
          catchError(error => {
            console.error('Error fetching user by email:', error);
            return of('');
          })
        );
      }
}
