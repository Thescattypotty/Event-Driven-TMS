import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { DashboardLayoutComponent } from './component/dashboard-layout/dashboard-layout.component';
import { NgIf } from '@angular/common';
import { AuthService } from './services/auth.service';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [RouterOutlet, DashboardLayoutComponent, NgIf],
    templateUrl: './app.component.html',
    styleUrl: './app.component.css'
})
export class AppComponent {
    title = 'frontend2';
    constructor(private authService: AuthService, private router: Router){

    }

    get isAuthenticated(): boolean{
        return this.authService.isLoggedIn();
    }
    
    get isLoginRoute(): boolean{
        return this.router.url === '/login';
    }
}
