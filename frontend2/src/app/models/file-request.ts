export interface FileRequest {
    fileName: String;
    contentType: String;
    size: Number;
    file: Uint8Array;
}
