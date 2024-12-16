import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/auth/login/login.component';
import { authGuard } from './guards/auth.guard';
import { TeamMembersComponent } from './pages/team-members/team-members.component';
import { UserListComponent } from './pages/user/user-list/user-list.component';

export const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        canActivate: [authGuard]
    },
    {
        path: 'login',
        component: LoginComponent
    },
    {
        path: 'team',
        component: TeamMembersComponent
    },
    {
        path: 'user',
        component: UserListComponent
    }
    
];
