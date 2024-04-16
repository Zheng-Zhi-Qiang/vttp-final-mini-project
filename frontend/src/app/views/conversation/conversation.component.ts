import { AfterViewChecked, ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { ConvoStore } from '../../stores/conversation.store';
import { ConversationService } from '../../services/conversation.service';
import { Observable, firstValueFrom, tap } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Conversation, Listing, Message, User } from '../../models/models';
import { UserIdStore } from '../../stores/user-id.store';
import { UserService } from '../../services/user.service';
import { ListingService } from '../../services/listing.service';
import { Messaging, getToken, onMessage } from '@angular/fire/messaging';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-conversation',
  templateUrl: './conversation.component.html',
  styleUrl: './conversation.component.css'
})
export class ConversationComponent implements OnInit, AfterViewChecked, OnDestroy {

  private messaging: Messaging = inject(Messaging)
  private timeoutInterval = 60000
  
  private msgSvc: MessageService = inject(MessageService)
  private convoStore: ConvoStore = inject(ConvoStore)
  private convoSvc: ConversationService = inject(ConversationService)
  private userIdStore: UserIdStore = inject(UserIdStore)
  private userSvc: UserService = inject(UserService)
  private listingSvc: ListingService = inject(ListingService)
  private fb: FormBuilder = inject(FormBuilder)
  private convo!: Conversation | undefined

  @ViewChild('messages')
  private msgDiv!: ElementRef

  userId!: string
  convo$!: Observable<Conversation | undefined>

  listing$!: Promise<Listing>
  otherUserProfile$!: Promise<User>
  msgForm!: FormGroup
  private createInterval = (convo_id: string, timeout: number) => {
    return setInterval(() => this.pollConvo(convo_id), timeout)
  }
  private polling!: any

  ngOnInit(): void {
    Notification.requestPermission().then((permission) => {
      if (permission === 'granted') {
        // Get the current FCM token
        navigator.serviceWorker.register('firebase-messaging-sw.js', { type: 'module', scope: '___' }).
        then(serviceWorkerRegistration =>
          getToken(this.messaging, {serviceWorkerRegistration, vapidKey: "BMLmEjt9ANAvWva90mKAx9uuNPd-SLNWOMbHilVLo5gP7kYufbyQnKr9Dcf46152mpbvtseMIE5xO3V09lR5VMU"})
          .then((token) => {
              return this.userSvc.updateUserMessagingToken(token)
          })
          .catch((error) => console.log('Token error', error))
        )

        this.listen()
      }
    })


    firstValueFrom(this.convoStore.getCurrentConvo)
    .then((value) => {
      this.convo = value
      this.convo$ = this.convoStore.getCurrentConvo.pipe(tap((value) => this.convo = value))
      return this.convoSvc.convoExists(value!)
    })
    .then((resp) => {
      if (resp.result){
        this.convoStore.setCurrentConvo(resp.convo)
        // this.polling = setTimeout(() => {this.pollConvo(resp.convo.convo_id)}, this.timeoutInterval) // trigger polling only if convo is existing // polling continues even after page exits, clear timeout will stop future timeout calls but does not stop current timeout called
        this.polling = this.createInterval(resp.convo.convo_id, this.timeoutInterval)
      }
      return firstValueFrom(this.userIdStore.getUserId)
    })
    .then((value) => {
      this.userId = value!
      this.otherUserProfile$ = this.userId == this.convo!.user_id_1 ? this.userSvc.getUserDetails(this.convo!.user_id_2) : this.userSvc.getUserDetails(this.convo!.user_id_1)
      this.listing$ = this.listingSvc.getListingById(this.convo!.listing_id)
    })
    // .then((resp) => {
    //   this.otherUserProfile = resp // same error occuring here
    //   this.listing$ = this.listingSvc.getListingById(this.convo!.listing_id)
    // })
    // .then((resp) => this.listing = resp) // an error occurs here in parsing the template as due to listing not being available yet. changing to promise removes the error
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while retrieving your conversation. Please try again later."})
    })

    this.msgForm = this.createForm()
  }

  ngAfterViewChecked(): void {
    this.scrollTop() // not sure why this causes a ExpressionChangedAfterItHasBeenCheckedError after polling even though works fine
  }

  ngOnDestroy(): void {
    clearInterval(this.polling) //clears timer
  }

  createForm(): FormGroup {
    return this.fb.group({
      message: this.fb.control<string>('', [Validators.required])
    })
  }

  sendMessage(){
    let message: Message = {
      sender: this.userId,
      receiver: this.convo?.user_id_1 == this.userId ? this.convo?.user_id_2 : this.convo?.user_id_1!,
      message: this.msgForm.value['message'],
      date: new Date()
    }

    this.convoStore.addMessageToConvo(message)
    this.msgForm.reset()

    if (this.convo?.convo_id == undefined){
      message.listing_id = this.convo?.listing_id
    } else {
      message.convo_id = this.convo?.convo_id
    }

    this.convoSvc.sendMessage(message)
    .then((resp) => {
      if (this.convo?.convo_id == undefined){
        this.convoStore.setCurrentConvoId(resp.convo_id) 
        // this.polling = setTimeout(() => {this.pollConvo(resp.convo_id)}, this.timeoutInterval) // check if convo is new, if yes then trigger polling
        this.polling = this.createInterval(resp.convo_id, this.timeoutInterval)
      }
    })
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while sending your message. Please try again later."})
    })
  }


  pollConvo(convo_id: string){
    console.log("poll:", convo_id)
    this.convoSvc.getConversation(convo_id)
    .then((convo) => {
      this.convoStore.setCurrentConvo(convo)
    })
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while updating your conversation."})
    })
    // setTimeout(() => this.pollConvo(convo_id), this.timeoutInterval)
  }

  listen(){
    // Listen for messages from FCM
    onMessage(this.messaging, (payload) => {
        let message: Message = JSON.parse(payload.notification?.body!)
        if (this.convo?.convo_id == message.convo_id) {
          this.convoStore.addMessageToConvo(message)
        }
      }
      // {
      // next: (payload) => {
      //   console.log('Message', payload);
      //   let message: Message = JSON.parse(payload.notification?.body!)
      //   console.log(this.convo?.messages)
      //   if (this.convo?.convo_id == message.convo_id) {
      //     this.convoStore.addMessageToConvo(message)
      //   }
      //   // firstValueFrom(this.convoStore.getConvoId)
      //   // .then(value => {
      //   //   console.log("convo_store_id: ", value)
      //   //   if (value == message.convo_id) {
      //   //     this.convoStore.addMessageToConvo(message) // TODO: this change is not being pushed into the observable
      //   //     console.log("message added")
      //   //   }
      //   //   console.log(this.convo?.messages)
      //   // })
      // },
      // error: (error) => console.log('Message error', error),
      // complete: () => console.log('Done listening to messages')
      // }
    )
  }

  scrollTop(): void {
    this.msgDiv.nativeElement.scrollTop = this.msgDiv.nativeElement.scrollHeight
  }
}
