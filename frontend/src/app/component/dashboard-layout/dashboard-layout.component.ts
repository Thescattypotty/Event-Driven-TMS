import { Component } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { FeatherIconsModule } from '../../icons/feather-icons/feather-icons.module';

@Component({
    selector: 'app-dashboard-layout',
    standalone: true,
    imports: [RouterOutlet, RouterLink, FeatherIconsModule],
    templateUrl: './dashboard-layout.component.html',
    styleUrl: './dashboard-layout.component.css'
})
export class DashboardLayoutComponent {
    constructor(private router: Router) {
    }
}
