import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { UserResponse } from '../../../models/user-response';
import { NgFor, NgIf } from '@angular/common';
import { ERole } from '../../../models/role';
import { FormsModule } from '@angular/forms';
import { FeatherIconsModule } from '../../../icons/feather-icons/feather-icons.module';
import { UserRequest } from '../../../models/user-request';

@Component({
    selector: 'app-user-list',
    standalone: true,
    imports: [NgFor, FormsModule, FeatherIconsModule,NgIf],
    templateUrl: './user-list.component.html',
    styleUrl: './user-list.component.css'
})
export class UserListComponent implements OnInit {

    userResponse : UserResponse[] = [];
    roles: ERole[] = [ERole.ROLE_USER, ERole.ROLE_ADMIN];
    userCreationRequest: UserRequest = {
        fullname: '',
        email: '',
        password: '',
        roles: [ERole.ROLE_USER]
    };
    userUpdateRequest!: UserRequest;
    isUpdateMode: boolean = false;
    constructor(private userService: UserService){

    }
    openCreateModal() {
        this.isUpdateMode = false;
        this.userCreationRequest = { fullname: '', email: '', password: '', roles: [] }; // Reset form
      }
    
      openUpdateModal(user: UserResponse) {
        this.isUpdateMode = true;
        this.getUser(user.id);
        this.userCreationRequest = {
          fullname: user.fullname,
          email: user.email,
          password: '', // Leave blank for security
          roles: user.roles,
        };
      }
    createUser(){
        console.log("User Creation :");
        this.userService.createUser(this.userCreationRequest).subscribe({
            next: (response) => {
                console.log("User Created Successfully");
                this.refreshUsers();
            },
            error: (error) => {
                console.log(error);
            }
        });
    }
    getUser(id: String){
        this.userService.getUser(id).subscribe({
            next: (response) => {
                console.log("User Getted Sucessfully");
                this.userUpdateRequest.fullname = response.fullname;
                this.userUpdateRequest.email = response.email;
                this.userUpdateRequest.password = response.email;
                this.userUpdateRequest.roles = response.roles;
            },
            error: (error) => {
                console.log(error);
            }
        });
    }
    updateUser(id: String){
        this.userService.updateUser(id, this.userUpdateRequest).subscribe({
            next: (response) => {
                console.log("User with ID : " + id + " Updated Successfully .");
                this.refreshUsers();
            },
            error: (error) => {
                console.log(error);
            }
        });
    }

    deleteUser(id: String){
        console.log("User deletion with ID : " + id);
        this.userService.deleteUser(id).subscribe({
            next: (response) => {
                console.log("User with ID : " + id + " Deleted Successfully .");
                this.refreshUsers();
            },
            error: (error) => {
                console.log(error);
            }
        });
    }
    
    refreshUsers(){
        this.userService.getUsers().subscribe({
            next: (response) => {
                console.log(response);
                this.userResponse = response;
            },
            error: (error) => {
                console.log(error);
            }
        });
    }

    ngOnInit(): void {
        this.refreshUsers();
    }

}
