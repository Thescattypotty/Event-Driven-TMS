export interface ProjectRequest {
    id: string;
    name: string;
    description: string;
    startDate: Date;
    endDate: Date;
    status: ProjectStatus;
}

export enum ProjectStatus {
    Pending = 'Pending',
    InProgress = 'InProgress',
    Completed = 'Completed',
    Cancelled = 'Cancelled'
}