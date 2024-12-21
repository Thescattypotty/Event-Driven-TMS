export interface ProjectRequest {
    name: String;
    description: String;
    startDate?: Date;
    endDate?: Date;
    userId?: String[];
    file_id?: String[];
}
