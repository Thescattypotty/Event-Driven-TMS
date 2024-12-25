import { Component, OnInit } from '@angular/core';
import {
    CdkDragDrop,
    moveItemInArray,
    transferArrayItem,
    CdkDrag,
    CdkDropList,
} from '@angular/cdk/drag-drop'; 
import { NgFor } from '@angular/common';
import { ProjectService } from '../../services/project.service';
import { ActivatedRoute } from '@angular/router';
import { ProjectResponse } from '../../models/project-response';
import { TaskService } from '../../services/task.service';
import { TaskResponse } from '../../models/task-response';

@Component({
    selector: 'app-project-detail',
    standalone: true,
    imports: [CdkDropList, CdkDrag, NgFor],
    templateUrl: './project-detail.component.html',
    styleUrls: ['./project-detail.component.css']
})
export class ProjectDetailComponent implements OnInit{
    id: string;

    taskResponse: TaskResponse[] = [];
    projectResponse: ProjectResponse | null = null;

    todoTasks: TaskResponse[] = [];
    inProgressTasks: TaskResponse[] = [];
    doneTasks: TaskResponse[] = [];

    constructor(private projectService: ProjectService, private taskService: TaskService, private route: ActivatedRoute) {
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

    ngOnInit(): void {
        this.loadProject();
        this.loadTasks();
    }
}
