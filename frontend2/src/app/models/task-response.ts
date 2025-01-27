import { FileResponse } from "./file-response";
import { EPriority } from "./priority";
import { EStatus } from "./status";

export interface TaskResponse {
    id: String;
    title: String;
    description: String;
    dueDate: Date;
    priority: EPriority;
    status: EStatus;
    historyOfTask: String[];
    filesIncluded: String[];
    files?: FileResponse[];
    userAssigned: String;
    projectId: String;
    createdAt: Date;
    updatedAt: Date;
}
