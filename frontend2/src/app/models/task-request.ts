import { EPriority } from "./priority";
import { EStatus } from "./status";

export interface TaskRequest {
    title: String;
    description: String;
    priority: EPriority;
    status: EStatus;
    userAssigned: String;
    projectId: String;
    filesIncluded: String[];
}
