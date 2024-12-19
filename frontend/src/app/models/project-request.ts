export interface ProjectRequest {
    name: String;
    description?: String;
    startDate?: String;
    endDate?: String;
    userId?: String[];
    file_id?: String[];
}