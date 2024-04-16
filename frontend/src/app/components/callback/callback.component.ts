import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@auth0/auth0-angular';

@Component({
  selector: 'app-callback',
  templateUrl: './callback.component.html',
  styleUrl: './callback.component.css'
})
export class CallbackComponent implements OnInit{
  private auth: AuthService = inject(AuthService)
  private router: Router = inject(Router)

  ngOnInit(): void {
    // this.router.navigate(['/'])
  }
}
