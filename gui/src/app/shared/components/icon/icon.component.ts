/** @Author Justin Jantzi */

import { Component, Input } from '@angular/core';

@Component({
  selector: 'icon',
  templateUrl: 'icon.component.html',
  styleUrls: ['../components.css'],
})
export class IconComponent {
  @Input() icon: string = "";
  @Input() color: string = "";
}