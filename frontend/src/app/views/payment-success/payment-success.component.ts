import { Component, OnInit, inject } from '@angular/core';
import { PaymentService } from '../../services/payment.service';
import { UserIdStore } from '../../stores/user-id.store';
import { Observable } from 'rxjs';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-payment-success',
  templateUrl: './payment-success.component.html',
  styleUrl: './payment-success.component.css'
})
export class PaymentSuccessComponent implements OnInit {
  private paymentSvc: PaymentService = inject(PaymentService)
  private userIdStore: UserIdStore = inject(UserIdStore)
  private msgSvc: MessageService = inject(MessageService)

  ngOnInit(): void {
    let transaction = JSON.parse(localStorage.getItem("transaction")!)
    this.userIdStore.setUserId(localStorage.getItem("user_id")!)
    this.paymentSvc.createTransaction(transaction!)
    .then(() => {
      localStorage.clear()
    })
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered. Please contact us."})
    })
  }
}
