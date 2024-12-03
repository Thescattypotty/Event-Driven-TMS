import { Component } from '@angular/core';
import { IconModuleModule } from '../../module/icon-module/icon-module.module';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [IconModuleModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {

}
