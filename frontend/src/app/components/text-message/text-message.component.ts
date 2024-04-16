import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-text-message',
  templateUrl: './text-message.component.html',
  styleUrl: './text-message.component.css'
})
export class TextMessageComponent {
  @Input()
  isUser!: boolean

  @Input()
  text!: string
}
