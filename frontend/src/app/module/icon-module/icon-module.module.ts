import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatherModule } from 'angular-feather';
import { ExternalLink, Eye, EyeOff, HelpCircle } from 'angular-feather/icons';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    FeatherModule.pick({ HelpCircle, EyeOff, Eye, ExternalLink})
  ],
  exports: [
    FeatherModule
  ]
})
export class IconModuleModule { }
