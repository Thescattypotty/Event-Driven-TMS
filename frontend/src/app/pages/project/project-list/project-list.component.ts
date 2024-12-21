import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FeatherIconsModule } from '../../../icons/feather-icons/feather-icons.module';
import { ProjectResponse } from '../../../models/project-response';
import { ProjectRequest } from '../../../models/project-request';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [NgFor, FormsModule, FeatherIconsModule, NgIf],
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.css']
})
export class ProjectListComponent implements OnInit {
  projectService: any;
  projectResponse: ProjectResponse[] = [
    { id: '1', name: 'Project Alpha', startDate: new Date('2023-01-01'), endDate: new Date('2023-06-01'), description: 'Description Alpha', status: 'active' },
    { id: '2', name: 'Project Beta', startDate: new Date('2023-02-01'), endDate: new Date('2023-07-01'), description: 'Description Beta', status: 'active' },
    { id: '3', name: 'Project Gamma', startDate: new Date('2023-03-01'), endDate: new Date('2023-08-01'), description: 'Description Gamma', status: 'active' }
  ];
  ngOnInit(): void {
    this.refreshProjects();
  }

  refreshProjects() {
    this.projectService.getProjects().subscribe({
      next: (response: ProjectResponse[]) => {
        console.log(response);
        this.projectResponse = response;
      },
      error: (error: any) => {
        console.log(error);
      }
    });
  }
  updateProject(projectId: string, project: ProjectRequest) {
    this.projectService.updateProject(projectId, project).subscribe({
      next: (response: ProjectResponse) => {
        console.log(response);
        this.refreshProjects();
      },
      error: (error: any) => {
        console.log(error);
      }
    });
  }

  deleteProject(projectId: string) {
    this.projectService.deleteProject(projectId).subscribe({
      next: (response: any) => {
        console.log(response);
        this.refreshProjects();
      },
      error: (error: any) => {
        console.log(error);
      }
    });
  }
  createProject(project: ProjectRequest) {
    this.projectService.createProject(project).subscribe({
      next: (response: ProjectResponse) => {
        console.log(response);
        this.refreshProjects();
      },
      error: (error: any) => {
        console.log(error);
      }
    });
  }

  getProject(projectId: string) {
    this.projectService.getProject(projectId).subscribe({
      next: (response: ProjectResponse) => {
        console.log(response);
      },
      error: (error: any) => {
        console.log(error);
      }
    });
  }
}