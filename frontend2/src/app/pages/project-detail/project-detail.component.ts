import { Component, EventEmitter, Input, OnInit, Output, CUSTOM_ELEMENTS_SCHEMA, Inject, PLATFORM_ID} from '@angular/core';
import {
    CdkDragDrop,
    moveItemInArray,
    transferArrayItem,
    CdkDrag,
    CdkDropList,
} from '@angular/cdk/drag-drop'; 
import { NgFor, NgIf, CommonModule } from '@angular/common';
import { ProjectService } from '../../services/project.service';
import { ActivatedRoute } from '@angular/router';
import { ProjectResponse } from '../../models/project-response';
import { TaskService } from '../../services/task.service';
import { TaskResponse } from '../../models/task-response';
import { EStatus } from '../../models/status';
import { TaskFormComponent } from '../../component/modal/task-form/task-form.component';
import { MdbModalModule, MdbModalRef, MdbModalService } from 'mdb-angular-ui-kit/modal';
import { TaskDetailsComponent } from "../../component/modal/task-details/task-details.component";
import { MdbTabsModule } from 'mdb-angular-ui-kit/tabs';
import { UserService } from '../../services/user.service';
import { UserResponse } from '../../models/user-response';
import { FileService } from '../../services/file.service';
import * as bootstrap from 'bootstrap';
import { FileResponse } from '../../models/file-response';




@Component({
    selector: 'app-project-detail',
    standalone: true,
    imports: [CdkDropList, CdkDrag, NgFor, NgIf, MdbModalModule, MdbTabsModule, CommonModule],
    templateUrl: './project-detail.component.html',
    styleUrls: ['./project-detail.component.css'],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProjectDetailComponent implements OnInit {
    id: string;
    taskResponse: TaskResponse[] = [];
    projectResponse: ProjectResponse | null = null;
    userResponse: UserResponse | null = null;
    users: UserResponse[] = [];
    files: FileResponse[] = [];

    todoTasks: TaskResponse[] = [];
    inProgressTasks: TaskResponse[] = [];
    doneTasks: TaskResponse[] = [];

    modalRef: MdbModalRef<TaskFormComponent> | null = null;

    showTaskDetails = false;
    selectedTask: TaskResponse | null = null;
    

    @Input() tabs = [
        { value: 'Tasks', label: 'tasks' },
        { value: 'details', label: 'details' },
        { value: 'files', label: 'files' },
        { value: 'members', label: 'members' },
      ];

    @Input() selectedTab = 'Tasks';
    @Output() tabChange = new EventEmitter<string>();
  
    selectTab(tab: string): void {
      this.selectedTab = tab;
      this.tabChange.emit(tab);
    }


    constructor(
        private projectService: ProjectService,
        private taskService: TaskService,
        private route: ActivatedRoute,
        private modalService: MdbModalService,
        private userService: UserService,
        private fileService: FileService,
        @Inject(PLATFORM_ID) private platformId: Object
    ) {
        this.id = this.route.snapshot.paramMap.get('id')!;
    }
    
    todo = ['Get to work', 'Pick up groceries', 'Go home', 'Fall asleep'];

    inProgress = ['Get up', 'Brush teeth', 'Take a shower', 'Check e-mail'];

    done = ['Get up', 'Brush teeth', 'Take a shower', 'Check e-mail', 'Walk dog'];

    drop(event: CdkDragDrop<TaskResponse[]>) {
        if (event.previousContainer === event.container) {
            moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
        } else {
            transferArrayItem(
                event.previousContainer.data,
                event.container.data,
                event.previousIndex,
                event.currentIndex,
            );
        }
    }

    async openTaskDetails(task: TaskResponse): Promise<void> {
  if (typeof document !== 'undefined') {
    this.selectedTask = task;

    const { Offcanvas } = await import('bootstrap');
    const offcanvasElement = document.getElementById('offcanvasRight');
    if (offcanvasElement) {
      const bsOffcanvas = new Offcanvas(offcanvasElement);
      bsOffcanvas.show();
    }
  }
}

      
    dropp(event: CdkDragDrop<string[]>) {
        if (event.previousContainer === event.container) {
            moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
        } else {
            transferArrayItem(
                event.previousContainer.data,
                event.container.data,
                event.previousIndex,
                event.currentIndex,
            );
        }
    }
    

    createTask(status: String) {
        this.modalRef = this.modalService.open(TaskFormComponent, {
            data: {
                create: true,
                projectId: this.id,
                status: EStatus[status as keyof typeof EStatus]
            }
        });
        this.modalRef.onClose.subscribe((task) => {
            if (task === undefined) {
                return;
            }
            this.taskService.createTask(task).subscribe({
                next: (response) => {
                    this.loadTasks();
                },
                error: (error) => {
                    console.log(error);
                }
            });
        });
    }

    loadProject(): void {
        this.projectService.getProject(this.id).subscribe({
            next: (response) => {
                this.projectResponse = response;
                response.users_id.forEach((userId: String) => {
                    this.userService.getUser(userId).subscribe({
                        next: (userResponse) => {
                            this.users.push(userResponse);
                        },
                        error: (error) => {
                            console.log(error);
                        }
                    });
                });
                response.file_id.forEach((fileId: String) => {
                    this.fileService.downloadFile(fileId).subscribe({
                        next: (fileResponse) => {
                            this.files.push(fileResponse);
                          },
                        error: (error) => {
                            console.log(error);
                        }
                    });
                });
            },
            error: (error) => {
                console.log(error);
            }
        });
    }


    loadTasks(){
        this.taskService.getAllTasks(this.id).subscribe({
            next: (response) => {
                this.taskResponse = response;
                this.todoTasks = this.taskResponse.filter(task => task.status === 'TO_DO');
                this.inProgressTasks = this.taskResponse.filter(task => task.status === 'IN_PROGRESS');
                this.doneTasks = this.taskResponse.filter(task => task.status === 'DONE');
            },
            error: (error) => {
                console.log(error);
            }
        })
    }


    onTaskClick(task: TaskResponse) {
        this.selectedTask = task;
        this.showTaskDetails = true;
    }

    
    ngOnInit(): void {
        this.loadProject();
        this.loadTasks();
    }
}
