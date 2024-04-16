import { ComponentStore } from "@ngrx/component-store";
import { Transaction, TransactionSlice } from "../models/models";
import { Injectable } from "@angular/core";

const INIT_STATE: TransactionSlice = {
    transaction: undefined
}


@Injectable()
export class TransactionStore extends ComponentStore<TransactionSlice> {
    
    constructor() {super(INIT_STATE)}

    readonly getCurrentTransaction = this.select<Transaction | undefined>(
        (slice: TransactionSlice) => {
            return slice.transaction
        }
    )

    readonly setCurrentTransaction = this.updater<Transaction>(
        (slice: TransactionSlice, value: Transaction) => {
            return {
                transaction: value
            } as TransactionSlice
        }
    )

    readonly clearCurrentTransaction = this.updater(
        (slice: TransactionSlice) => {
            return {
                transaction: undefined
            } as TransactionSlice
        }
    )

    readonly setCurrentTransactionPayer = this.updater<string>(
        (slice: TransactionSlice, value: string) => {
            return {
                transaction: {
                    listing_id: slice.transaction?.listing_id,
                    payer: value,
                    payee: slice.transaction?.payee,
                    amount: slice.transaction?.amount,
                }
            } as TransactionSlice
        }
    )

    readonly ongoingTransaction = this.select<boolean>(
        (slice: TransactionSlice) => {
            return slice.transaction != undefined
        }
    )
}