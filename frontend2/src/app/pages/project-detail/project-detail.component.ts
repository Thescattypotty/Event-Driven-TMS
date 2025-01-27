import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  CUSTOM_ELEMENTS_SCHEMA,
  Inject,
  PLATFORM_ID,
} from '@angular/core';
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
import {
  MdbModalModule,
  MdbModalRef,
  MdbModalService,
} from 'mdb-angular-ui-kit/modal';
import { TaskDetailsComponent } from '../../component/modal/task-details/task-details.component';
import { MdbTabsModule } from 'mdb-angular-ui-kit/tabs';
import { UserService } from '../../services/user.service';
import { UserResponse } from '../../models/user-response';
import { FileService } from '../../services/file.service';
import { FileResponse } from '../../models/file-response';
import { TaskRequest } from '../../models/task-request';
import { ProjectRequest } from '../../models/project-request';
import { EPriority } from '../../models/priority';
import { FormsModule} from '@angular/forms';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [
    CdkDropList,
    CdkDrag,
    NgFor,
    NgIf,
    MdbModalModule,
    MdbTabsModule,
    CommonModule,
    FormsModule
  ],
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.css'],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ProjectDetailComponent implements OnInit {
  id: string;
  taskResponse: TaskResponse[] = [];
  projectResponse: ProjectResponse | null = null;
  userResponse: UserResponse | null = null;
  users: UserResponse[] = [];
  projectFiles: FileResponse[] = [];
  taskFiles: FileResponse[] = [];
  projectDescription: string = '';
  backlogTasks: TaskResponse[] = [];
  apiKey: string = 'AIzaSyBqP2CIJrp45Pl1HmhMThcny_AbW3bZuRQ';
  generatedText: string = '';
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
  isGenerating?: false;

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
      moveItemInArray(
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    } else {
      // Transfer item between containers
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );

      const movedTask: TaskResponse = event.container.data[event.currentIndex];
      const newStatus: EStatus = this.getStatusFromContainerId(
        event.container.id
      );

      if (!newStatus) {
        console.error('Error: Invalid container ID:', event.container.id);
        return;
      }

      const taskRequest: TaskRequest = {
        ...movedTask,
        status: newStatus,
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
        },
      });
    }
  }

  // Helper function to map container IDs to backend statuses
  private getStatusFromContainerId(containerId: string): EStatus {
    const statusMap: { [key: string]: EStatus } = {
      todoList: EStatus.TO_DO,
      inProgressList: EStatus.IN_PROGRESS,
      doneList: EStatus.DONE,
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
        status: EStatus[status as keyof typeof EStatus],
        usersId: this.projectResponse?.users_id || []
      },
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
        },
      });
    });
  }
  updateTask(task: TaskResponse): void {
    const taskRequest: TaskRequest = {
      // Map TaskResponse to TaskRequest
      title: task.title,
      description: task.description,
      priority: task.priority,
      status: task.status,
      userAssigned: task.userAssigned,
      projectId: task.projectId,
      filesIncluded: task.filesIncluded,
    };
  
    this.taskService.updateTask(task.id, taskRequest).subscribe({
      next: () => {
        this.loadTasks();
      },
      error: (error) => {
        console.log(error);
      }
    });
  }
  
  deleteTask(taskId: String): void {
    this.taskService.deleteTask(taskId).subscribe({
      next: () => {
        this.loadTasks();
      },
      error: (error) => {
        console.log(error);
      }
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
            },
          });
        });
        response.file_id.forEach((fileId: String) => {
          this.fileService.downloadFile(fileId).subscribe({
            next: (fileResponse) => {
              this.projectFiles.push(fileResponse);
            },
            error: (error) => {
              console.log(error);
            },
          });
        });
      },
      error: (error) => {
        console.log(error);
      },
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
            },
          });

          // Fetch files for each task
          task.files = [];
          task.filesIncluded.forEach((fileId: String) => {
            this.fileService.downloadFile(fileId).subscribe({
              next: (fileResponse) => {
                task.files!.push(fileResponse);
              },
              error: (error) => {
                console.log(error);
              },
            });
          });
        });

        // Filter tasks based on their status
        this.todoTasks = this.taskResponse.filter(
          (task) => task.status === 'TO_DO'
        );
        this.inProgressTasks = this.taskResponse.filter(
          (task) => task.status === 'IN_PROGRESS'
        );
        this.doneTasks = this.taskResponse.filter(
          (task) => task.status === 'DONE'
        );
      },
      error: (error) => {
        console.log('Error fetching tasks:', error);
      },
    });
  }
  downloadFile(fileId: string) {
    this.fileService.downloadFile(fileId).subscribe({
      next: (fileResponse) => {
        const byteCharacters = atob(fileResponse.file.toString());
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const blob = new Blob([new Uint8Array(byteNumbers)], {
          type: fileResponse.contentType.toString(),
        });
        const url = window.URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.href = url;
        link.download = fileResponse.fileName.toString();
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error(err),
    });
  }

async generateTasks() {
        if (!this.projectDescription.trim()) {
            alert("Project description is required!");
            return;
        }

        const prompt = `
            You are a project management AI assistant. Your task is to generate a list of actionable 5 tasks based on the given project description: "${this.projectDescription}". 
            Each task should be specific, measurable, and directly related to the project.

            Project Description: "${this.projectDescription}"

            Instructions:
            - Read the project description carefully.
            - Output tasks from highest to lowest priority.
            - Generate all tasks needed.
            - Each task should start with an action verb.
            - Tasks should be concise but descriptive.
            - Ensure tasks cover different aspects of the project.
            - the discription of the task should be short and resumed in 1 sentence.

            Output only valid JSON in the format:
            {
                "task1": "First task description",
                "task2": "Second task description",
                ...
            }
        `;

        try {
            // Fetch tasks using Google Generative AI API
            const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${this.apiKey}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                  contents: [{
                      parts: [{ text: prompt }]
                  }]
              })
            });

            
    const data = await response.json();
    console.log('API response:', data); 
            if (response.ok && data.candidates?.length > 0) {
              const generatedText = data.candidates[0].content.parts[0].text;
              console.log('Generated text:', this.generatedText);
                // Extract JSON from code block
                const jsonMatch = generatedText.match(/```json\n([\s\S]*?)\n```/);
                if (jsonMatch && jsonMatch[1]) {
                  const tasksJson = jsonMatch[1];
                  console.log('Extracted JSON:', tasksJson);
                  const tasks = JSON.parse(tasksJson);
                console.log('Parsed tasks:', tasks);  


                this.backlogTasks = Object.keys(tasks).map((key) => ({
                    id: '', // Assign a default or generate a unique ID
                    title: key,
                    description: tasks[key],
                    priority: EPriority.MEDIUM,
                    status: EStatus.TO_DO,
                    userAssigned: '',
                    projectId: this.id,
                    filesIncluded: [],
                    dueDate: new Date(), // Assign a default date
                    historyOfTask: [], // Assign an empty array or appropriate value
                    createdAt: new Date(), // Assign the current date
                    updatedAt: new Date() // Assign the current date
                }));
              } else {
                throw new Error('Failed to extract JSON from generated text');
            }
            } else {
                throw new Error(data.error?.message || 'Failed to generate tasks');
            }
        } catch (error) {
            console.error(error);
            alert("Failed to generate tasks.");
        }
    }

    acceptTask(task: TaskResponse) {
    this.taskService.createTask(task).subscribe({
        next: () => {
            this.todoTasks.push(task);  // Add accepted task to To Do list
            this.backlogTasks = this.backlogTasks.filter(t => t !== task);  // Remove task from Backlog
            alert(`Task "${task.title}" has been accepted and created.`);
            this.loadTasks(); 
        },
        error: (err) => {
            console.error(err);
            alert("Failed to create task.");
        }
    });
}

    declineTask(task: TaskRequest) {
        this.backlogTasks = this.backlogTasks.filter(t => t !== task);
        alert(`Task "${task.title}" has been declined.`);
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
