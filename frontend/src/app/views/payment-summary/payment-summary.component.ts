import { Component, OnInit, inject } from '@angular/core';
import { TransactionStore } from '../../stores/transaction.store';
import { UserIdStore } from '../../stores/user-id.store';
import { Observable, firstValueFrom } from 'rxjs';
import { ListingService } from '../../services/listing.service';
import { Listing } from '../../models/models';
import { PaymentService } from '../../services/payment.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-payment-summary',
  templateUrl: './payment-summary.component.html',
  styleUrl: './payment-summary.component.css'
})
export class PaymentSummaryComponent implements OnInit {
  private transactionStore: TransactionStore = inject(TransactionStore)
  private userIdStore: UserIdStore = inject(UserIdStore)
  private listingSvc: ListingService = inject(ListingService)
  private paymentSvc: PaymentService = inject(PaymentService)
  private router: Router = inject(Router)
  private msgSvc: MessageService = inject(MessageService)

  listing$!: Promise<Listing>

  
  ngOnInit(): void {
    firstValueFrom(this.userIdStore.getUserId)
    .then((value) => {
      this.transactionStore.setCurrentTransactionPayer(value!)
      return firstValueFrom(this.transactionStore.getCurrentTransaction)
    })
    .then((value) => {
      this.listing$ = this.listingSvc.getListingById(value?.listing_id!)
    })
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while retrieving your payment summary. Please try again later."})
    })
  }

  createPaymentSession(listing: Listing){
    firstValueFrom(this.userIdStore.getUserId)
    .then((userId) => {
      localStorage.setItem("user_id", userId!)
      return firstValueFrom(this.transactionStore.getCurrentTransaction)
    })
    .then((transaction) => {
      localStorage.setItem("transaction", JSON.stringify(transaction))
      return this.paymentSvc.createPaymentSession(listing.listing_id!)
    })
    .then((resp) => {
      window.location.href = resp.location
    })
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while processing your payment request. Please try again later."})
    })
  }

  cancel(listing: Listing){
    this.transactionStore.clearCurrentTransaction
    this.router.navigate(['/listing', listing.listing_id])
  }
}
