export interface ProjectRequest {
    id?: string;
    name: string;
    description?: string;
    startDate?: Date;
    endDate?: Date;
    status?: ProjectStatus;
    userId?: string[];
    file_id?: string[];
}

export enum ProjectStatus {
    Pending = 'Pending',
    InProgress = 'InProgress',
    Completed = 'Completed',
    Cancelled = 'Cancelled'
}