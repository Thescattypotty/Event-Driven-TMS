import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Edit, Delete, Github, Trash, Trash2, Copy, Inbox, Settings, Archive, Book, Users, Star, RefreshCcw, RefreshCw, ChevronUp, ChevronDown, MoreVertical, AlignLeft, Search, Bell, Clock, Calendar, Plus, CreditCard, CheckSquare, Tag } from 'angular-feather/icons';
import { FeatherModule } from 'angular-feather';

const icons = {
    Edit,
    Delete,
    Github,
    Trash,
    Trash2,
    Copy,
    Inbox,
    Settings,
    Archive,
    Book,
    Users,
    Star,
    RefreshCw,
    ChevronUp,
    ChevronDown,
    MoreVertical,
    RefreshCcw,
    AlignLeft,
    Search,
    Bell,
    Clock,
    Calendar,
    Plus,
    CreditCard,
    CheckSquare,
    Tag
}


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    FeatherModule.pick(icons)
  ],
  exports:[
    FeatherModule
  ]
})
export class FeatherIconsModule { }
