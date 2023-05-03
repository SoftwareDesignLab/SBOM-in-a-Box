/** @Author Justin Jantzi */

import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-accordion',
  templateUrl: './accordion.component.html',
  styleUrls: ['../components.css']
})
export class AccordionComponent {
  @Input() title: string = '';
  @Input() extra: string[] = [];
  @Input() color: string = '';
  @Input() expanded: boolean = false;
}
