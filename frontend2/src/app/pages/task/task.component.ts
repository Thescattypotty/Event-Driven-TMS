import { Component, OnInit } from '@angular/core';
import {
    CdkDragDrop,
    moveItemInArray,
    transferArrayItem,
    CdkDrag,
    CdkDropList,
} from '@angular/cdk/drag-drop'; 
import { NgFor, NgIf } from '@angular/common';
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


@Component({
    selector: 'app-project-detail',
    standalone: true,
    imports: [CdkDropList, CdkDrag, NgFor, NgIf, MdbModalModule, TaskDetailsComponent, MdbTabsModule],
    templateUrl: './task.component.html',
    styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {
    id: string;

    taskResponse: TaskResponse[] = [];
    projectResponse: ProjectResponse | null = null;

    todoTasks: TaskResponse[] = [];
    inProgressTasks: TaskResponse[] = [];
    doneTasks: TaskResponse[] = [];

    modalRef: MdbModalRef<TaskFormComponent> | null = null;

    showTaskDetails = false;
    selectedTask: TaskResponse | null = null;

    selectedTab = 'tasks';

    constructor(
        private projectService: ProjectService,
        private taskService: TaskService,
        private route: ActivatedRoute,
        private modalService: MdbModalService
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
            if(task === undefined){
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

    loadProject(){
        this.projectService.getProject(this.id).subscribe({
            next: (response) => {
                this.projectResponse = response;
            },
            error: (error) => {
                console.log(error);
            }
        })
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
    onTabChange(tab: string): void {
        this.selectedTab = tab;
      }

    ngOnInit(): void {
        this.loadProject();
        this.loadTasks();
    }
}
