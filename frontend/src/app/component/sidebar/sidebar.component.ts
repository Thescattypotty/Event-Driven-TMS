import { Component } from '@angular/core';
import { IconModuleModule } from '../../module/icon-module/icon-module.module';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [IconModuleModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {

}