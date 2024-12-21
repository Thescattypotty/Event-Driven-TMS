import { Component, OnInit } from '@angular/core';
import { LoginRequest } from '../../models/login-request';
import { JwtResponse } from '../../models/jwt-response';
import { ErrorResponse } from '../../models/error-response';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css'
})
export class AuthComponent implements OnInit{

    loginRequest: LoginRequest = { email: '', password: '' };
    jwtResponse: JwtResponse | null = null;
    errorResponse: ErrorResponse | null = null;

    constructor(private authService: AuthService, private router: Router) { }

    login(): void{
        this.authService.login(this.loginRequest).subscribe({
            next: (response) => {
                this.jwtResponse = response;
                if (this.jwtResponse.accessToken && this.jwtResponse.refreshToken) {
                    this.authService.setToken(
                        this.jwtResponse.accessToken.valueOf(),
                        this.jwtResponse.refreshToken.valueOf()
                    );
                } else {
                    console.error('accessToken or refreshToken is missing!');
                }
            },
            error: (error) => {
                this.errorResponse = error;
            }
        })
    }
    ngOnInit(): void {
        this.authService.logout();
    }
}
