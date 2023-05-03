/** @Author Tina DiLorenzo */

import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.css'],
})
export class DialogComponent {
  @Output() clicked: EventEmitter<boolean> = new EventEmitter<boolean>;
  @Input() icon: string = '';
  @Input() buttonText: string = '';

  onClick() {
    this.clicked.emit(true);
  }
}
