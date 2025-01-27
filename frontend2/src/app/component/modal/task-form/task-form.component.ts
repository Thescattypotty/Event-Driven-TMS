import { NgFor, NgIf } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';
import { UserService } from '../../../services/user.service';
import { FileService } from '../../../services/file.service';
import { TaskRequest } from '../../../models/task-request';
import { UserResponse } from '../../../models/user-response';
import { FileRequest } from '../../../models/file-request';
import { EPriority } from '../../../models/priority';
import { EStatus } from '../../../models/status';
import { catchError } from 'rxjs';

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
export class TaskFormComponent implements OnInit {

    task: Partial<TaskRequest> | null = null;
    create: boolean = true;
    users: UserResponse[] = [];
    @Input() usersId: String[] = [];
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
    onSubmit(): void {
        this.close();
    }
    close(): void {
        if (this.isTaskNotValid()) {
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
    getUsers(): void {
        this.userService.getUsers().subscribe({
          next: (users) => {
            this.users = users.filter(user => this.usersId.includes(user.id));
            console.log("Users : ", this.users);
          },
          error: (error) => {
            console.error("Error : ", error);
          }
        });
      }

    uploadFile(event: any): void {
        const file: File = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = () => {
                const base64String = this.arrayBufferToBase64(new Uint8Array(reader.result as ArrayBuffer));
                this.fileRequest = {
                    fileName: file.name,
                    contentType: file.type,
                    size: file.size,
                    file: base64String
                };
                console.log("File Request : ", this.fileRequest);
                this.fileService.uploadFile(this.fileRequest).pipe(
                    catchError((error) => {
                        console.error(error);
                        return [];
                    })
                ).subscribe({
                    next: (response) => {
                        this.files.push(response);
                        this.task?.filesIncluded?.push(response);
                    },
                    error: (error) => {
                        console.log(error);
                    }
                }
                );
            };
            reader.readAsArrayBuffer(file);
        }
    }
    private arrayBufferToBase64(buffer: Uint8Array): string {
        let binary = '';
        const len = buffer.byteLength;
        for (let i = 0; i < len; i++) {
            binary += String.fromCharCode(buffer[i]);
        }
        return btoa(binary);
    }

    removeFile(id: String): void {
        this.fileService.deleteFile(id).subscribe({
            next: (response) => {
                this.files = this.files.filter((file) => file !== id);
                this.task?.filesIncluded?.filter((file) => file !== id);
            },
            error: (error) => {
                console.log(error);
            }
        });
    }



    ngOnInit(): void {
        if (this.create) {
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
        this.getUsers();
    }
}
