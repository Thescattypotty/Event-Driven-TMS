import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { FooterComponent } from './component/footer/footer.component';
import { HeaderComponent } from './component/header/header.component';
import { SidebarComponent } from './component/sidebar/sidebar.component';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs';
import { AuthService } from './services/auth.service';

@Component({
	selector: 'app-root',
	standalone: true,
	imports: [
		CommonModule,
		RouterOutlet,
		FooterComponent,
		HeaderComponent,
		SidebarComponent,
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
