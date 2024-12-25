import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FileRequest } from '../models/file-request';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class FileService {

    private API_URL = 'http://localhost:8222/api/v1/file';
    constructor(private http: HttpClient) {

    }

    uploadFile(file: FileRequest): Observable<String>{
        return this.http.post<String>(this.API_URL, file);
    }
    
    downloadFile(id: String): Observable<String>{
        return this.http.get<String>(`${this.API_URL}/${id}`);
    }
    deleteFile(id: String): Observable<void>{
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }
    isFileExisting(id: String): Observable<boolean>{
        return this.http.get<boolean>(`${this.API_URL}/verify/${id}`);
    }

}
