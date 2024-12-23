import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ProjectResponse } from '../../models/project-response';
import { MdbModalModule, MdbModalRef, MdbModalService } from 'mdb-angular-ui-kit/modal';
import { ProjectFormComponent } from '../../component/modal/project-form/project-form.component';
import { ProjectService } from '../../services/project.service';
import { JwtDecoderService } from '../../decoder/jwt-decoder.service';
import { catchError, of } from 'rxjs';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';

@Component({
    selector: 'app-project',
    standalone: true,
    imports: [
        NgIf,
        NgFor,
        MdbModalModule
    ],
    templateUrl: './project.component.html',
    styleUrl: './project.component.css'
})
export class ProjectComponent implements OnInit {
    projects: ProjectResponse[] = [];
    isLoading: boolean = false;
    projectGetted: ProjectResponse | null = null;

    modalRef: MdbModalRef<ProjectFormComponent> | null = null;
    constructor(
        private projectService: ProjectService,
        private modalService: MdbModalService,
        private jwtDecoderService: JwtDecoderService,
        private router: Router,
        private userService: UserService
    ) {

    }
    createProject(){
        this.modalRef = this.modalService.open(ProjectFormComponent);
        this.modalRef.onClose.subscribe((project) => {
            if(project === undefined){
                return;
            }
            this.projectService.createProject(project).subscribe({
                next: (response) => {
                    console.log('Response', response);
                    this.loadProjects();
                },
                error: (error) => {
                    console.log(error);
                }
            })
        });
    }
    
    viewProject(id: String){
        this.router.navigate(['/project', id]);
    }

    updateProject(id: String){
        this.projectService.getProject(id).subscribe({
            next: (response) => {
                this.projectGetted = response;
                this.modalRef = this.modalService.open(ProjectFormComponent,{
                    data: {
                        create: false,
                        project: this.projectGetted
                    }
                });
                this.modalRef.onClose.subscribe((project) => {
                    if(project === undefined){
                        return;
                    }
                    this.projectService.updateProject(id, project).subscribe({
                        next: (response) => {
                            console.log('Response', response);
                            this.loadProjects();
                        },error: (error) => {
                            console.log(error);
                        }
                    });
                })
            },error: (error) => {
                console.log(error);
            }
        });
    }

    deleteProject(id: String){
        this.projectService.deleteProject(id).subscribe({
            next:(response) => {
                this.loadProjects();
            },
            error: (error) => {
                console.log(error);
            }
        });
    }

    loadProjects(){

        const token = localStorage.getItem('accessToken');
        if (token) {
            const payload = token.split('.')[1];
            const decoded = window.atob(payload);
            const email = JSON.parse(decoded).email;
            let userId = "";
            this.userService.getUserByEmail(email).subscribe({
                next: (response) => {
                    console.log(response.id);
                    userId = response.id.valueOf();

                    this.projectService.getProjects(userId).pipe(
                        catchError((error) => {
                            console.log(error);
                            return of([]);
                        })
                    ).subscribe({
                        next: (response) => {
                            console.log(response);
                            this.projects = response;
                        },
                        error: (error) => {
                            console.log(error);
                        }
                    });
                },
                error: (error) => {
                    userId = "";
                }
            });
        }


        //let id = this.jwtDecoderService.getUserId();
        
    }

    ngOnInit() { 
        this.loadProjects();
        console.log("id : " + this.jwtDecoderService?.getUserId());
    }

}
