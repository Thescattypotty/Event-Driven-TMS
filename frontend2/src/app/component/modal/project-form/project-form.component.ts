import { NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';
import { ProjectRequest } from '../../../models/project-request';
import { UserService } from '../../../services/user.service';
import { UserResponse } from '../../../models/user-response';
import { FileService } from '../../../services/file.service';

@Component({
  selector: 'app-project-form',
  standalone: true,
  imports: [
    FormsModule,
    NgIf
  ],
  templateUrl: './project-form.component.html',
  styleUrl: './project-form.component.css'
})
export class ProjectFormComponent implements OnInit{

    project: Partial<ProjectRequest> | null = null;
    users: UserResponse[]  = [];
    create: boolean = true;
    files: String[] = [];

    constructor(
        public modalRef: MdbModalRef<ProjectFormComponent>,
        private userService: UserService,
        private fileService: FileService
    ) {

    }

    onSubmit(): void{
        this.close();
    }
    close(): void {
        if(this.isProjectNotValid()){
            return;
        }
        this.modalRef.close(this.project);
    }
    isProjectNotValid(): boolean {
        return !this.project?.name || 
            !this.project?.description || 
            !this.project?.startDate || 
            !this.project?.endDate;
    }
    getUsers(): void{
        this.userService.getUsers().subscribe({
            next: (users) => {
                this.users = users;
                console.log("Users : ", users);
            },
            error: (error) => {
                console.error(error);
            }
        });
    }
    uploadFile(event: any): void{
        const file: File = event.target.files[0];
        if(file){
            
        }
    }

    ngOnInit(): void {
        if(this.create){
            this.project = {
                name: '',
                description: '',
                startDate: new Date(),
                endDate: new Date(),
                userId: [],
                file_id: []
            };
        }
        this.getUsers();
    }
}
