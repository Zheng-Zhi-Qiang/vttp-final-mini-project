import { ComponentStore } from "@ngrx/component-store";
import { Conversation, ConversationSlice, Message } from "../models/models";
import { Injectable } from "@angular/core";

const INIT_STATE: ConversationSlice = {
    convo: undefined
}

@Injectable()
export class ConvoStore extends ComponentStore<ConversationSlice>{
    constructor() {super(INIT_STATE)}

    readonly getCurrentConvo = this.select<Conversation | undefined>(
        (slice: ConversationSlice) => {
            return slice.convo
        }
    )

    readonly setCurrentConvo = this.updater<Conversation>(
        (slice: ConversationSlice, value: Conversation) => {
            return {
                convo: value
            } as ConversationSlice
        }
    )

    readonly clearCurrentConvo = this.updater(
        (slice: ConversationSlice) => {
            return {
                convo: undefined
            } as ConversationSlice
        }
    )

    readonly setCurrentConvoId = this.updater<string>(
        (slice: ConversationSlice, value: string) => {
            return {
                convo: {
                    convo_id: value,
                    deleted: slice.convo?.deleted,
                    user_id_1: slice.convo?.user_id_1,
                    user_id_2: slice.convo?.user_id_2,
                    listing_id: slice.convo?.listing_id,
                    messages: slice.convo?.messages
                }
            } as ConversationSlice
        }
    )

    readonly addMessageToConvo = this.updater<Message>(
        (slice: ConversationSlice, value: Message) => {
            let copy = {...slice.convo}
            let messages = [...slice.convo?.messages!, value]
            console.log("same object: ",copy === slice.convo)
            console.log("same messages: ", copy!.messages === messages)
            copy!.messages = messages
            console.log("same messages after: ",copy!.messages === messages)
            return {
                convo: copy
            } as ConversationSlice
        }
    )

    readonly newConvo = this.select<boolean>(
        (slice: ConversationSlice) => {
            return slice.convo?.convo_id != undefined
        }
    )

    readonly getConvoId = this.select<string>(
        (slice: ConversationSlice) => {
            return slice.convo?.convo_id!
        }
    )
}