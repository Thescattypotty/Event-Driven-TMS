import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';
import { ProjectRequest } from '../../../models/project-request';
import { UserService } from '../../../services/user.service';
import { UserResponse } from '../../../models/user-response';
import { FileService } from '../../../services/file.service';
import { FileRequest } from '../../../models/file-request';
import { catchError } from 'rxjs';

@Component({
  selector: 'app-project-form',
  standalone: true,
  imports: [FormsModule, NgIf, NgFor],
  templateUrl: './project-form.component.html',
  styleUrl: './project-form.component.css',
})
export class ProjectFormComponent implements OnInit {
  project: Partial<ProjectRequest> | null = null;
  users: UserResponse[] = [];
  create: boolean = true;
  files: String[] = [];
  fileRequest: FileRequest | null = null;

  constructor(
    public modalRef: MdbModalRef<ProjectFormComponent>,
    private userService: UserService,
    private fileService: FileService
  ) {}

  onSubmit(): void {
    this.close();
  }
  close(): void {
    if (this.isProjectNotValid()) {
      return;
    }
    this.modalRef.close(this.project);
  }
  isProjectNotValid(): boolean {
    return (
      !this.project?.name ||
      !this.project?.description ||
      !this.project?.startDate ||
      !this.project?.endDate
    );
  }
  getUsers(): void {
    this.userService.getUsers().subscribe({
      next: (users) => {
        this.users = users;
        console.log('Users : ', users);
      },
      error: (error) => {
        console.error(error);
      },
    });
  }
  uploadFile(event: any): void {
    const files: FileList = event.target.files;
    if (!files || files.length === 0) {
      return;
    }

    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      if (file) {
        const reader = new FileReader();
        reader.onload = () => {
          const base64String = this.arrayBufferToBase64(
            new Uint8Array(reader.result as ArrayBuffer)
          );
          this.fileRequest = {
            fileName: file.name,
            contentType: file.type,
            size: file.size,
            file: base64String,
          };
          console.log('File Request : ', this.fileRequest);
          this.fileService
            .uploadFile(this.fileRequest)
            .pipe(
              catchError((error) => {
                console.error(error);
                return [];
              })
            )
            .subscribe({
              next: (response) => {
                this.files.push(response);
                this.project?.file_id?.push(response);
              },
              error: (error) => {
                console.log(error);
              },
            });
        };
        reader.readAsArrayBuffer(file);
      }
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
        this.project?.file_id?.filter((file) => file !== id);
      },
      error: (error) => {
        console.log(error);
      },
    });
  }

  ngOnInit(): void {
    if (this.create) {
      this.project = {
        name: '',
        description: '',
        startDate: new Date(),
        endDate: new Date(),
        userId: [],
        file_id: this.files,
      };
      //this.project.userId?.push(this.jwtDecoderService.getUserId());
    }
    this.getUsers();
  }
}
