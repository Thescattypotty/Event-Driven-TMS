import { Component, OnInit } from '@angular/core';
import { LoginRequest } from '../../../models/login-request';
import { JwtResponse } from '../../../models/jwt-response';
import { ErrorResponse } from '../../../models/error-response';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FeatherIconsModule } from '../../../icons/feather-icons/feather-icons.module';

@Component({
	selector: 'auth-login',
	standalone: true,
	imports: [
    FormsModule,
    FeatherIconsModule
],
	templateUrl: './login.component.html',
	styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{
	loginRequest: LoginRequest = { email: '', password: ''};
	jwtResponse: JwtResponse | null = null;
	errorMessage: ErrorResponse | null = null;

	constructor(
		private authService: AuthService,
		private router: Router
	){

	}
	login(): void {
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
				this.router.navigate(['']);
			},
			error: (error) => {
				this.errorMessage = error;
			}
		})
	}
	ngOnInit(): void {
		this.authService.logout();
	}
}
