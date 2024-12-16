import { ERole } from "./role";

export interface UserResponse{
    id: String;
    fullname: String;
    email: String;
    roles: ERole[];
}