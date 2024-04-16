import { ComponentStore } from "@ngrx/component-store";
import { PeriodSlice } from "../models/models";
import { Injectable } from "@angular/core";

const INIT_STATE: PeriodSlice = {
    period: [new Date()]
}


@Injectable()
export class PeriodFilterStore extends ComponentStore<PeriodSlice> {
    constructor(){super(INIT_STATE)}
    
    readonly getPeriodFilter = this.select<Date[]>(
        (slice: PeriodSlice) => {
            return slice.period
        }
    )

    readonly setPeriodFilter = this.updater<Date[]>(
        (slice: PeriodSlice, value: Date[]) => {
            return {
                period: value
            } as PeriodSlice
        }
    )
}