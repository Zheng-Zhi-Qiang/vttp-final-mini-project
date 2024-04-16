import { ComponentStore } from "@ngrx/component-store";
import { UserId } from "../models/models";
import { Injectable } from "@angular/core";

const INIT_STATE: UserId = {
    userId: undefined
}


@Injectable()
export class UserIdStore extends ComponentStore<UserId> {
    
    constructor() {super(INIT_STATE)}

    readonly getUserId = this.select<string | undefined>(
        (slice: UserId) => {
            return slice.userId
        }
    )

    readonly setUserId = this.updater<string>(
        (slice: UserId, value: string) => {
            return {
                userId: value
            } as UserId
        }
    )

    readonly deleteUserId = this.updater(
        (slice: UserId) => {
            return {
                userId: undefined
            } as UserId
        }
    )

    readonly userLoggedIn = this.select<boolean>(
        (slice: UserId) => {
            return slice.userId != undefined;
        }
    )
}