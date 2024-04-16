import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { UserIdStore } from '../../stores/user-id.store';

@Component({
  selector: 'app-payment-cancelled',
  templateUrl: './payment-cancelled.component.html',
  styleUrl: './payment-cancelled.component.css'
})
export class PaymentCancelledComponent implements OnInit {

  private router: Router = inject(Router)
  private userIdStore: UserIdStore = inject(UserIdStore)

  ngOnInit(): void {
    let transaction = JSON.parse(localStorage.getItem("transaction")!)
    this.userIdStore.setUserId(localStorage.getItem("user_id")!)
    localStorage.clear()
    this.router.navigate(['/listing', transaction.listing_id])
  }
}
