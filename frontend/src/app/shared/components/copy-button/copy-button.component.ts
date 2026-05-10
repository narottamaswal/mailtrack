import { Component, Input, signal } from '@angular/core';

@Component({
  selector: 'app-copy-btn',
  standalone: true,
  template: `
    <button type="button" class="btn btn-sm" (click)="copy()" style="font-family:monospace;font-size:11px">
      {{ copied() ? '✓ Copied' : label }}
    </button>
  `
})
export class CopyBtnComponent {
  @Input() text  = '';
  @Input() label = 'Copy';
  copied = signal(false);
  copy() {
    navigator.clipboard.writeText(this.text).then(() => {
      this.copied.set(true);
      setTimeout(() => this.copied.set(false), 2000);
    });
  }
}
