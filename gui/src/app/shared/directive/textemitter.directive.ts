/** @Author Tina DiLorenzo */

import { Directive, ElementRef, Output, EventEmitter, HostListener } from '@angular/core';

@Directive({
  selector: '[appTextEmitter]'
})
export class TextEmitterDirective {
  @Output() textOut = new EventEmitter<Text>();

  @HostListener('click') onMouseEnter() {
    const text = this.el.nativeElement.innerText;
    this.textOut.emit(text);
  }


  constructor(private el: ElementRef) {
 }
}
