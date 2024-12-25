import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';
import { UserService } from '../../../services/user.service';
import { FileService } from '../../../services/file.service';
import { TaskRequest } from '../../../models/task-request';
import { UserResponse } from '../../../models/user-response';
import { FileRequest } from '../../../models/file-request';
import { EPriority } from '../../../models/priority';
import { EStatus } from '../../../models/status';

@Component({
    selector: 'app-task-form',
    standalone: true,
    imports: [
        FormsModule,
        NgIf,
        NgFor
    ],
    templateUrl: './task-form.component.html',
    styleUrl: './task-form.component.css'
})
export class TaskFormComponent implements OnInit{
   
    task: Partial<TaskRequest> | null = null;
    create: boolean = true;
    users: UserResponse [] = [];
    files: String[] = [];
    fileRequest: FileRequest | null = null;
    projectId: String | null = null;
    status: EStatus | null = null;

    constructor(
        public modalRef: MdbModalRef<TaskFormComponent>,
        private userService: UserService,
        private fileService: FileService
    ) {
        
    }
    onSubmit(): void{
        this.close();
    }
    close(): void{
        if(this.isTaskNotValid()){
            return;
        }
        this.modalRef.close(this.task);
    }
    isTaskNotValid(): boolean {
        return !this.task?.title || 
            !this.task?.description || 
            !this.task?.priority || 
            !this.task?.status ||
            !this.task?.userAssigned;
            !this.task?.projectId;
    }
    getUsers(): void{
        this.userService.getUsers().subscribe({
            next: (users) => {
                this.users = users;
                console.log("Users : ", users);
            },
            error: (error) => {
                console.error("Error : ", error);
            }
        });
    }



    ngOnInit(): void {
        if(this.create){
            this.task = {
                title: '',
                description: '',
                priority: EPriority.LOW,
                status: this.status ?? EStatus.TO_DO,
                userAssigned: '',
                filesIncluded: this.files
            }
        }
        if (this.task) {
            this.task.projectId = this.projectId ?? '';
        }
    }
}
