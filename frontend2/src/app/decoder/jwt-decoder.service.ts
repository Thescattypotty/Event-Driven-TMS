import { Injectable } from '@angular/core';
import { UserService } from '../services/user.service';

@Injectable({
    providedIn: 'root'
})
export class JwtDecoderService {

    constructor(private userService: UserService) { }

    getUserId(): String {
        const token = localStorage.getItem('accessToken');
        if (token) {
            const payload = token.split('.')[1];
            const decoded = window.atob(payload);
            const email = JSON.parse(decoded).email;
            let userId = "";
            this.userService.getUserByEmail(email).subscribe({
                next: (response) => {
                    console.log(response.id);
                    userId = response.id.valueOf();
                },
                error: (error) => {
                    userId = "";
                }
            });
            return userId;
        } else {
            return "";
        }
    }
}
