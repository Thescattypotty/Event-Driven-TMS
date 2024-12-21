import { Component } from '@angular/core';
import { ProjectResponse } from '../../../models/project-response';
import { ProjectService } from '../../../services/project.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css'
})
export class ProjectListComponent {

    projects: ProjectResponse[] = [];
    
    constructor(private projectService: ProjectService, private router: Router){}
}
