import { Component, OnInit, inject } from '@angular/core';
import { PaymentService } from '../../services/payment.service';
import { UserIdStore } from '../../stores/user-id.store';
import { firstValueFrom } from 'rxjs';
import { Transaction } from '../../models/models';

@Component({
  selector: 'app-user-transactions',
  templateUrl: './user-transactions.component.html',
  styleUrl: './user-transactions.component.css'
})
export class UserTransactionsComponent implements OnInit {
  private pmtSvc: PaymentService = inject(PaymentService)
  private userIdStore: UserIdStore = inject(UserIdStore)

  userId!: String | undefined
  transactions$!: Promise<Transaction[]>

  ngOnInit(): void {
    firstValueFrom(this.userIdStore.getUserId)
    .then((value) => this.userId = value)
    
    this.transactions$ = this.pmtSvc.getUserTransactions()
  }
}
