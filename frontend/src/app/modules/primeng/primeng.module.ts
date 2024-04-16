import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InputTextModule } from 'primeng/inputtext';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button'
import { MenuModule } from 'primeng/menu';
import { CalendarModule } from 'primeng/calendar';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { FloatLabelModule } from 'primeng/floatlabel';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { MultiSelectModule } from 'primeng/multiselect';
import { ToastModule } from 'primeng/toast';
import { FileUploadModule } from 'primeng/fileupload';
import { GalleriaModule } from 'primeng/galleria';
import { ToolbarModule } from 'primeng/toolbar';
import { PaginatorModule } from 'primeng/paginator';
import { ChipModule } from 'primeng/chip';
import { DividerModule } from 'primeng/divider';
import { CardModule } from './card/card.component';
import { PanelModule } from 'primeng/panel';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TooltipModule } from 'primeng/tooltip';

const PRIMENG = [
  InputTextModule,
  InputGroupModule,
  InputGroupAddonModule,
  IconFieldModule,
  InputIconModule,
  MenubarModule,
  ButtonModule,
  MenuModule,
  CalendarModule,
  InputTextareaModule,
  FloatLabelModule,
  InputNumberModule,
  DropdownModule,
  MultiSelectModule,
  ToastModule,
  FileUploadModule,
  GalleriaModule,
  ToolbarModule,
  PaginatorModule,
  ChipModule,
  DividerModule,
  CardModule, 
  PanelModule,
  ProgressSpinnerModule,
  TooltipModule,
]

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    PRIMENG
  ],
  exports: [
    PRIMENG
  ]
})
export class PrimengModule { }
