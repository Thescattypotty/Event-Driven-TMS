import { Component, EventEmitter, Input, OnInit, Output, CUSTOM_ELEMENTS_SCHEMA, Inject, PLATFORM_ID } from '@angular/core';
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
import { FileResponse } from '../../models/file-response';
import { TaskRequest } from '../../models/task-request';




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

    drop(event: CdkDragDrop<TaskResponse[]>) {
        if (event.previousContainer === event.container) {
          // Move item within the same container
          moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
        } else {
          // Transfer item between containers
          transferArrayItem(
            event.previousContainer.data,
            event.container.data,
            event.previousIndex,
            event.currentIndex
          );
      
          const movedTask: TaskResponse = event.container.data[event.currentIndex];
          const newStatus: EStatus = this.getStatusFromContainerId(event.container.id);
      
          if (!newStatus) {
            console.error('Error: Invalid container ID:', event.container.id);
            return;
          }
      
          const taskRequest: TaskRequest = {
            ...movedTask,
            status: newStatus
          };
      
          // Update task status in backend
          this.taskService.updateTask(movedTask.id, taskRequest).subscribe({
            next: () => {
              console.log('Task status updated successfully:', newStatus);
      
              // Update the moved task's status in the local state
              movedTask.status = newStatus;
            },
            error: (err) => {
              console.error('Error updating task status:', err);
      
              // Rollback the UI changes in case of an error
              transferArrayItem(
                event.container.data,
                event.previousContainer.data,
                event.currentIndex,
                event.previousIndex
              );
            }
          });
        }
      }
      
      
      // Helper function to map container IDs to backend statuses
      private getStatusFromContainerId(containerId: string): EStatus {
        const statusMap: { [key: string]: EStatus } = {
          todoList: EStatus.TO_DO,
          inProgressList: EStatus.IN_PROGRESS,
          doneList: EStatus.DONE
        };
        return statusMap[containerId];
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


    loadTasks() {
        this.taskService.getAllTasks(this.id).subscribe({
          next: (response) => {
            this.taskResponse = response;
      
            // Fetch user name for each task
            this.taskResponse.forEach((task) => {
              this.userService.getUser(task.userAssigned).subscribe({
                next: (userResponse) => {
                  task.userAssigned = userResponse.fullname; // Assuming 'name' is the user's name
                },
                error: (error) => {
                  console.log('Error fetching user:', error);
                }
              });
            });
      
            // Filter tasks based on their status
            this.todoTasks = this.taskResponse.filter(task => task.status === 'TO_DO');
            this.inProgressTasks = this.taskResponse.filter(task => task.status === 'IN_PROGRESS');
            this.doneTasks = this.taskResponse.filter(task => task.status === 'DONE');
          },
          error: (error) => {
            console.log('Error fetching tasks:', error);
          }
        });
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
