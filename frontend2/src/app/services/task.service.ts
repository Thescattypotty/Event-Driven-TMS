import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TaskRequest } from '../models/task-request';
import { map, Observable } from 'rxjs';
import { TaskResponse } from '../models/task-response';

@Injectable({
    providedIn: 'root'
})
export class TaskService {

    private API_URL = 'http://localhost:8222/api/v1/task';
    constructor(
        private http: HttpClient
    ) { }

    createTask(task: TaskRequest): Observable<void>{
        return this.http.post<void>(this.API_URL, task);
    }

    getTaskById(id: String): Observable<TaskResponse>{
        return this.http.get<TaskResponse>(`${this.API_URL}/${id}`);
    }
    getAllTasks(projectId: String): Observable<TaskResponse[]>{
        return this.http.get<TaskResponse[]>(`${this.API_URL}/project/${projectId}`);
    }
    updateTask(id: String , taskRequest: TaskRequest): Observable<void>{
        return this.http.put<void>(`${this.API_URL}/${id}`, taskRequest);
    }
    deleteTask(id: String): Observable<void>{
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }

    /*
    createTaskWebSocket(task: TaskRequest): Observable<void> {
        return this.rxStompService.publish({ destination: '/task/create', body: JSON.stringify(task) }).pipe(
            map(() => void 0)
        );
    }

    updateTaskWebSocket(taskId: string, task: TaskRequest): Observable<void> {
        return this.rxStompService.publish({ destination: `/task/update/${taskId}`, body: JSON.stringify(task) }).pipe(
            map(() => void 0)
        );
    }

    deleteTaskWebSocket(taskId: string): Observable<void> {
        return this.rxStompService.publish({ destination: '/task/delete', body: taskId }).pipe(
            map(() => void 0)
        );
    }
        */
}