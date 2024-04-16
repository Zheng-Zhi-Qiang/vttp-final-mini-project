import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Transaction } from '../models/models';
import { environment as env } from '../../environments/environment'

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
    private client: HttpClient = inject(HttpClient)

    createPaymentSession(listingId: string): Promise<any>{
        return firstValueFrom(this.client.get<any>(env.backend_url + `/api/payment/${listingId}`))
    }

    createTransaction(transaction: Transaction): Promise<any>{
        let headers: HttpHeaders = new HttpHeaders().set("Content-Type", "application/json").set("Accept", "application/json")
        return firstValueFrom(this.client.post<any>(env.backend_url + "/api/payment/success", transaction, {headers: headers}))
    }

    getUserTransactions(): Promise<Transaction[]>{
        return firstValueFrom(this.client.get<Transaction[]>(env.backend_url + "/api/user/transactions"))
    }
}