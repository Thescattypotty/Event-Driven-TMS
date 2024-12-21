import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProjectRequest } from '../models/project-request';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

    private API_URL = 'http://localhost:8222/api/v1/project';
    constructor(private http: HttpClient) { }

    getProjects(userId: String) {
        return this.http.get(`${this.API_URL}/user/${userId}`);
    }
    getProject(id: String) {
        return this.http.get(`${this.API_URL}/${id}`);
    }
    createProject(project: ProjectRequest) {
        return this.http.post(this.API_URL, project);
    }
    updateProject(id: String, project: ProjectRequest) {
        return this.http.put(`${this.API_URL}/${id}`, project);
    }
    deleteProject(id: String) {
        return this.http.delete(`${this.API_URL}/${id}`);
    }
}
