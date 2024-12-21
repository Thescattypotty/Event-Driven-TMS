export interface ProjectResponse {
    id: string;
    name: string;
    description: string;
    startDate: Date;
    endDate: Date;
    status: string;
    createdAt: Date;
    updatedAt: Date;
    users_id: string[];
    task_id: string[];
    file_id: string[];
}