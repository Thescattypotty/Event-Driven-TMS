import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectRequest } from '../models/project-request';
import { ProjectResponse } from '../models/project-response';

@Injectable({
    providedIn: 'root'
})
export class ProjectService {
    private API_URL = 'http://localhost:8222/api/v1/projects';

    constructor(private http: HttpClient) { }

    getProjects(): Observable<ProjectResponse[]> {
        return this.http.get<ProjectResponse[]>(`${this.API_URL}`);
    }

    getProjectById(id: string): Observable<ProjectResponse> {
        return this.http.get<ProjectResponse>(`${this.API_URL}/${id}`);
    }

    createProject(projectRequest: ProjectRequest): Observable<void> {
        return this.http.post<void>(`${this.API_URL}`, projectRequest);
    }

    updateProject(id: string, projectRequest: ProjectRequest): Observable<void> {
        return this.http.post<void>(`${this.API_URL}/${id}`, projectRequest);
    }

    deleteProject(id: string): Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }
}