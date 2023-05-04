/** @Author Justin Jantzi */
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['../components.css'],
})
export class ButtonComponent {
  @Input() icon: string = "";
  @Input() backgroundColor: string = "";
}
