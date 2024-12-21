import { ERole } from "./role";

export interface UserRequest {
    fullname: String;
    email: String;
    password: String;
    roles: ERole[];
}