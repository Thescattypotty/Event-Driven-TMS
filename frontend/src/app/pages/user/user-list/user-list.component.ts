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
    imports: [NgFor,NgIf, FormsModule, FeatherIconsModule],
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
    id: String | null = null;

    constructor(private userService: UserService){ }
    createUser(){
        console.log("User Creation :" + " Started");
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
        console.log("Get User with ID: " + id + " Started");
        this.userService.getUser(id).subscribe({
            next: (response) => {
                console.log("User Getted Sucessfully");
                this.userCreationRequest.fullname = response.fullname;
                this.userCreationRequest.email = response.email;
                this.userCreationRequest.password = response.email;
                this.userCreationRequest.roles = response.roles;
                this.id = response.id;
            },
            error: (error) => {
                console.log(error);
            }
        });
    }
    updateUser(id: String){
        console.log("User Update with ID :" + id + " Started");
        console.log("User Updated Sucessfully with ID : " + id);
        this.userService.updateUser(id, this.userCreationRequest).subscribe({
            next: (response) => {
                console.log("User with ID : " + id + " Updated Successfully .");
                this.id = null;
                this.refreshUsers();
            },
            error: (error) => {
                console.log(error);
            }
        });
    }

    deleteUser(id: String){
        console.log("User Delete with ID :" + id + " Started");
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
