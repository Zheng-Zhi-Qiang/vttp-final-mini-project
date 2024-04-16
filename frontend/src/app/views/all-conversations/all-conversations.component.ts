import { Component, OnInit, inject } from '@angular/core';
import { ConversationService } from '../../services/conversation.service';
import { Conversation } from '../../models/models';
import { ConvoStore } from '../../stores/conversation.store';
import { Router } from '@angular/router';
import { UserIdStore } from '../../stores/user-id.store';
import { Observable } from 'rxjs';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-all-conversations',
  templateUrl: './all-conversations.component.html',
  styleUrl: './all-conversations.component.css'
})
export class AllConversationsComponent implements OnInit {
  private convoSvc: ConversationService = inject(ConversationService)
  private convoStore: ConvoStore = inject(ConvoStore)
  private router: Router = inject(Router)
  private msgSvc: MessageService = inject(MessageService)
  convos$!: Promise<any>

  ngOnInit(): void {
    this.convos$ = this.convoSvc.getAllConvo()
  }
  
  onSelect(convo: Conversation){
    this.convoStore.setCurrentConvo(convo)
    this.router.navigate(['/conversation'])
  }

  deleteConvo(event: Event, convo_id: string){
    event.stopPropagation()
    this.convoSvc.deleteConversationByConvoId(convo_id)
    .then(() => this.convos$ = this.convoSvc.getAllConvo())
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while deleting conversation. Please try again later."})
    })
  }

  goTolisting(event: Event, listing_id: string){
    event?.stopPropagation()
    this.router.navigate(['/listing', listing_id])
  }
}
