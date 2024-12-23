import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProjectRequest } from '../models/project-request';
import { Observable } from 'rxjs';
import { ProjectResponse } from '../models/project-response';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

    private API_URL = 'http://localhost:8222/api/v1/project';
    constructor(private http: HttpClient) { }

    getProjects(userId: String): Observable<ProjectResponse[]> {
        console.log('User ID', userId);
        return this.http.get<ProjectResponse[]>(`${this.API_URL}/user/${userId}`);
    }
    getProject(id: String): Observable<ProjectResponse> {
        return this.http.get<ProjectResponse>(`${this.API_URL}/${id}`);
    }
    createProject(project: ProjectRequest): Observable<void> {
        return this.http.post<void>(this.API_URL, project);
    }
    updateProject(id: String, project: ProjectRequest): Observable<void> {
        return this.http.put<void>(`${this.API_URL}/${id}`, project);
    }
    deleteProject(id: String) : Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }
}
