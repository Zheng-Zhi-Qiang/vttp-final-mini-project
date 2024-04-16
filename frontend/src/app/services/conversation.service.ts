import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Conversation, Message } from '../models/models';
import { environment as env } from '../../environments/environment'


@Injectable({
  providedIn: 'root'
})
export class ConversationService {
    private client: HttpClient = inject(HttpClient)

    sendMessage(message: Message): Promise<any>{
        let headers: HttpHeaders = new HttpHeaders()
                                        .set("Content-Type", "application/json")
                                        .set("Accept", "application/json")
        return firstValueFrom(this.client.post<any>(env.backend_url + "/api/message", message, {headers: headers}))
    }

    getAllConvo(): Promise<Conversation[]> {
      return firstValueFrom(this.client.get<Conversation[]>(env.backend_url + "/api/conversations"))
    }

    getConversation(convo_id: string): Promise<Conversation> {
      return firstValueFrom(this.client.get<Conversation>(env.backend_url + `/api/conversation/${convo_id}`))
    }

    convoExists(convo: Conversation): Promise<any> {
      let params: HttpParams = new HttpParams()
                                    .set("user_id_1", convo.user_id_1)
                                    .set("user_id_2", convo.user_id_2)
                                    .set("listing_id", convo.listing_id)
      return firstValueFrom(this.client.get<any>(env.backend_url + "/api/conversation", {params: params}))
    }

    deleteConversationByConvoId(convo_id: string): Promise<any> {
      return firstValueFrom(this.client.delete<any>(env.backend_url + `/api/conversation/${convo_id}`))
    }
}