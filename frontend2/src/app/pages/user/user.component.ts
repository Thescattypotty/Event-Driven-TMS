import { NgFor } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MdbModalModule, MdbModalRef, MdbModalService } from 'mdb-angular-ui-kit/modal';
import { UserResponse } from '../../models/user-response';
import { UserFormComponent } from '../../component/modal/user-form/user-form.component';
import { UserService } from '../../services/user.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [
    NgFor,
    FormsModule,
    MdbModalModule
  ],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent implements OnInit {

    users: UserResponse[] = [];
    isLoading: boolean = false;
    userGetted: UserResponse | null = null;

    modalRef: MdbModalRef<UserFormComponent> | null = null;

    constructor(
        private userService: UserService,
        private modalService: MdbModalService
    ) {

    }

    addUser(){
        this.modalRef = this.modalService.open(UserFormComponent);
        this.modalRef.onClose.subscribe((user) => {
            if(user === undefined){
                return;
            }
            this.userService.createUser(user).subscribe({
                next: (response) => {
                    console.log('Response', response);
                    this.loadUsers();
                },
                error: (error) => {
                    console.log(error);
                }
            })
        });
    }
    updateUser(id: String){
        this.userService.getUser(id).subscribe({
            next: (response) => {
                this.userGetted = response;
                this.modalRef = this.modalService.open(UserFormComponent,{
                    data: {
                        create: false,
                        user: {
                            fullname: this.userGetted.fullname,
                            email: this.userGetted.email,
                            roles: this.userGetted.roles,
                            password: this.userGetted.email
                        }
                    }
                });
                this.modalRef.onClose.subscribe((user) => {
                    if(user === undefined){
                        return;
                    }
                    this.userService.updateUser(id, user).subscribe({
                        next: (response) => {
                            console.log('Response', response);
                            this.loadUsers();
                        },
                        error: (error) => {
                            console.log(error);
                        }
                    });
                });
            },
            error: (error) => {
                console.log(error);
            }
        });
    }
    deleteUser(id: String){
        this.userService.deleteUser(id).subscribe({
            next: (response) => {
                console.log('Response', response);
                this.loadUsers();
            },
            error: (error) => {
                console.log(error);
            }
        });
    }
    
    loadUsers(){
        this.userService.getUsers().pipe(
            catchError(error=> {
                console.log(error);
                return of([]);
            })
        ).subscribe({
            next: (response) => {
                this.users = response;
                this.isLoading = false;
            }
        });
    }

    ngOnInit(): void {
        this.loadUsers();   
    }
}
