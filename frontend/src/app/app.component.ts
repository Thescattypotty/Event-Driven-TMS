import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { DashboardLayoutComponent } from "./component/dashboard-layout/dashboard-layout.component";

@Component({
	selector: 'app-root',
	standalone: true,
	imports: [
    CommonModule,
    RouterOutlet,
    DashboardLayoutComponent
],
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css'],
})
export class AppComponent {
	title = 'Event Driven TMS';

	constructor(private router: Router, private authService: AuthService) { }

	get isAuthenticated(): boolean{
		return this.authService.isLoggedIn();
	}
	get isDashboardRoute(): boolean{
		return !this.router.url.startsWith('/login');
	}
	get isLoginRoute(): boolean{
		return this.router.url.startsWith('/login');
	}
}
