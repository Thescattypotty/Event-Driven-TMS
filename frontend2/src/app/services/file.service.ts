import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FileRequest } from '../models/file-request';
import { Observable } from 'rxjs';
import { FileResponse } from '../models/file-response';

@Injectable({
    providedIn: 'root'
})
export class FileService {

    private API_URL = 'http://localhost:8222/api/v1/file';
    constructor(private http: HttpClient) {}

    uploadFile(file: FileRequest): Observable<string>{
        return this.http.post(this.API_URL, file, { responseType: 'text' });
    }
    downloadFile(id: String): Observable<FileResponse>{
        return this.http.get<FileResponse>(`${this.API_URL}/${id}`);
    }
    deleteFile(id: String): Observable<void>{
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }
    isFileExisting(id: String): Observable<boolean>{
        return this.http.get<boolean>(`${this.API_URL}/verify/${id}`);
    }

}
