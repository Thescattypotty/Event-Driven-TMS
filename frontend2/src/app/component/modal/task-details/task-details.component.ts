import { Component, Input } from '@angular/core';
import { TaskResponse } from '../../../models/task-response';

@Component({
  selector: 'app-task-details',
  standalone: true,
  imports: [],
  templateUrl: './task-details.component.html',
  styleUrl: './task-details.component.css'
})
export class TaskDetailsComponent {
  @Input() task!: TaskResponse;
}
