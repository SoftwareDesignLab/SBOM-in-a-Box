/** @Author Justin Jantzi */

import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-side-panel',
  templateUrl: './side-panel.component.html',
  styleUrls: ['./side-panel.component.css'],
})
export class SidePanelComponent {
  collapsed: boolean = false;
  @Input() tabText: string = '';
}
