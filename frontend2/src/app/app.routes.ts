import { Routes } from '@angular/router';
import { AuthComponent } from './pages/auth/auth.component';
import { UserComponent } from './pages/user/user.component';
import { ProjectComponent } from './pages/project/project.component';
import { authGuard } from './guard/auth.guard';
import { FileComponent } from './pages/file/file.component';
import { ProjectDetailComponent } from './pages/project-detail/project-detail.component';
import { TaskGeneratorComponent } from './pages/task/task-generator/task-generator.component';

export const routes: Routes = [
    {
        path: 'login',
        component: AuthComponent
    },
    {
        path: 'generator',
        component: TaskGeneratorComponent
    },
    {
        path: '',
        //canActivate: [authGuard],
        children: [
            {
                path: '',
                component: ProjectComponent
            },
            {
                path: 'users',
                component: UserComponent,
            },
            {
                path: 'files',
                component: FileComponent
            },
            
            {
                path: ':id',
                component: ProjectDetailComponent
            },
            
        ]
    }
];
