import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';
import { UserRequest } from '../../../models/user-request';
import { ERole } from '../../../models/role';
import { NgIf } from '@angular/common';

@Component({
    selector: 'app-user-form',
    standalone: true,
    imports: [
        FormsModule,
        NgIf
    ],
    templateUrl: './user-form.component.html',
    styleUrl: './user-form.component.css'
})
export class UserFormComponent implements OnInit {

    user: Partial<UserRequest> | null = null;
    create: boolean = true;

    constructor(public modalRef: MdbModalRef<UserFormComponent>) {

    }

    onSubmit() {
        this.close();
    }

    close(): void {
        if(this.isUserNotValid()) {
            return;
        }
        this.modalRef.close(this.user);
    }

    isUserNotValid(): boolean {
        return !this.user?.fullname || !this.user?.email || !this.user?.password || !this.user?.roles?.length;
    }

    ngOnInit(): void {
        if (this.create) {
            this.user = {
                fullname: '',
                email: '',
                password: '',
                roles: []
            }
        }
    }

}
