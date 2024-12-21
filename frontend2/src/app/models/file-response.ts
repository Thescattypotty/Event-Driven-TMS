export interface FileResponse {
    fileId: String;
    fileName: String;
    contentType: String;
    size: Number;
    file: Uint8Array;
    uploadedDate: Date;
}
