export interface ProjectResponse {
    id: String;
    name: String;
    description: String;
    startDate: Date;
    endDate: Date;
    createdAt: Date;
    updatedAt: Date;
    users_id: String[];
    task_id: String[];
    file_id: String[];
}