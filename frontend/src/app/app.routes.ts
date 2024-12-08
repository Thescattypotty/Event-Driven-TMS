import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/auth/login/login.component';
import { authGuard } from './guards/auth.guard';
import { SidebarComponent } from './component/sidebar/sidebar.component';

export const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        ///canActivate: [authGuard]
    },
    {
        path: 'login',
        component: LoginComponent
    },
    {
        path: 'sidebar',
        component: SidebarComponent
    }
];
