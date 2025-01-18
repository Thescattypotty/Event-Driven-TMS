import { Component, OnInit } from '@angular/core';
import { TaskRequest } from '../../../models/task-request';
import { TaskService } from '../../../services/task.service';
import { EPriority } from '../../../models/priority';
import { EStatus } from '../../../models/status';
import { FormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import {
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem,
  CdkDrag,
  CdkDropList,
} from '@angular/cdk/drag-drop';
@Component({
  standalone: true,
    selector: 'app-task-generator',
    imports: [FormsModule, NgFor, NgIf, CdkDropList, CdkDrag],
    templateUrl: './task-generator.component.html',
    styleUrls: ['./task-generator.component.css']
})
export class TaskGeneratorComponent {
    projectDescription: string = '';
    backlogTasks: TaskRequest[] = [];
    apiKey: string = 'AIzaSyBqP2CIJrp45Pl1HmhMThcny_AbW3bZuRQ';
    generatedText: string = ''; // Add this property


    constructor(private taskService: TaskService) {}

    async generateTasks() {
        if (!this.projectDescription.trim()) {
            alert("Project description is required!");
            return;
        }

        const prompt = `
            You are a project management AI assistant. Your task is to generate a list of actionable tasks based on the given project description: "${this.projectDescription}". 
            Each task should be specific, measurable, and directly related to the project.

            Project Description: "${this.projectDescription}"

            Instructions:
            - Read the project description carefully.
            - Output tasks from highest to lowest priority.
            - Generate all tasks needed.
            - Each task should start with an action verb.
            - Tasks should be concise but descriptive.
            - Ensure tasks cover different aspects of the project.

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
                    title: key,
                    description: tasks[key],
                    priority: EPriority.MEDIUM,
                    status: EStatus.TO_DO,
                    userAssigned: '',
                    projectId: 'exampleProjectId',
                    filesIncluded: []
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

    acceptTask(task: TaskRequest) {
        this.taskService.createTask(task).subscribe({
            next: () => {
                this.backlogTasks = this.backlogTasks.filter(t => t !== task);
                alert(`Task "${task.title}" has been accepted and created.`);
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
}
