import { Component } from '@angular/core';
import { IconModuleModule } from '../../module/icon-module/icon-module.module';
import { RouterLink } from '@angular/router';

@Component({
	selector: 'app-header',
	standalone: true,
	imports: [
		IconModuleModule,
		RouterLink
	],
	templateUrl: './header.component.html',
	styleUrl: './header.component.css'
})
export class HeaderComponent{
}
