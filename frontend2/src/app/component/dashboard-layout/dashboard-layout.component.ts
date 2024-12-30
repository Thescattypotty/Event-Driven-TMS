import { Component } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  templateUrl: './dashboard-layout.component.html',
  styleUrl: './dashboard-layout.component.css'
})
export class DashboardLayoutComponent {
  menuItems = [
    { icon: 'home', label: 'Home', link: '/home' },
    { icon: 'dashboard', label: 'Dashboard', link: '/dashboard' },
    { icon: 'assignment', label: 'Projects', link: '/projects' },
    { icon: 'list', label: 'Tasks', link: '/tasks' },
    { icon: 'group', label: 'Team', link: '/team' },
    { icon: 'insert_drive_file', label: 'Files', link: '/files' },
    { icon: 'settings', label: 'Settings', link: '/settings' }
  ];
    
  activeRoute: string = '';
  fullName: string | null = null;


  constructor(private authService: AuthService, private router: Router) {}

  logOut() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
  setActive(route: string) {
    this.activeRoute = route;
  }

  // Check if a route is active
  isActive(route: string): boolean {
    return this.router.url === route;
  } 

  ngOnInit(): void {
    // Subscribe to the user observable
    this.authService.user.subscribe(user => {
      if (user) {
        this.fullName = user.name; // Assuming the user object has a 'name' field
      } else {
        this.fullName = null; // In case user is logged out
      }
    });
  }

}
