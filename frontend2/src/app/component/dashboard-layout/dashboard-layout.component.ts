import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { JwtDecoderService } from '../../decoder/jwt-decoder.service';
import { UserService } from '../../services/user.service';
import { catchError, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  templateUrl: './dashboard-layout.component.html',
  styleUrl: './dashboard-layout.component.css'
})
export class DashboardLayoutComponent implements OnInit {
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
  fullName: String | null = null;
  userId: String = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private jwtDecoderService: JwtDecoderService,
    private userService: UserService
  ) {} 

  logOut() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  setActive(route: string) {
    this.activeRoute = route;
  }

  isActive(route: string): boolean {
    return this.router.url === route;
  } 

  getUser(): void {
    console.log('getUser method called');
    this.jwtDecoderService.getUserId().pipe(
      switchMap(userId => {
        if (!userId) {
          throw new Error('No user ID found');
        }
        this.userId = userId;
        console.log('User ID from JWT:', this.userId);
        return this.userService.getUser(this.userId);
      }),
      catchError(error => {
        console.error('Error in getUser:', error);
        this.router.navigate(['/login']);
        return of(null);
      })
    ).subscribe(userResponse => {
      if (userResponse) {
        console.log('User data received:', userResponse);
        this.fullName = userResponse.fullname;
        console.log('Full name set:', this.fullName);
      } else {
        console.error('No user data received');
      }
    });
  }
  
  ngOnInit(): void {
    console.log('ngOnInit called');
    this.getUser();
  }
}

