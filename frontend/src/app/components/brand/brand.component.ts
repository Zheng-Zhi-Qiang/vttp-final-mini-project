import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-brand',
  templateUrl: './brand.component.html',
  styleUrl: './brand.component.css'
})
export class BrandComponent {

  private router: Router = inject(Router)

  onClick(): void {
    this.router.navigate(['/'])
  }
}
