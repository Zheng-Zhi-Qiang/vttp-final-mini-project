import { Component, OnInit, inject } from '@angular/core';
import { ListingService } from '../../services/listing.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Conversation, Listing } from '../../models/models';
import { UserIdStore } from '../../stores/user-id.store';
import { TransactionStore } from '../../stores/transaction.store';
import { Observable, firstValueFrom, from, tap } from 'rxjs';
import { ConvoStore } from '../../stores/conversation.store';
import { ConversationService } from '../../services/conversation.service';
import { AuthService } from '@auth0/auth0-angular';
import { UserService } from '../../services/user.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-listing',
  templateUrl: './listing.component.html',
  styleUrl: './listing.component.css'
})
export class ListingComponent implements OnInit{
  private listingSvc: ListingService = inject(ListingService)
  private activateRoute: ActivatedRoute = inject(ActivatedRoute)
  private router: Router = inject(Router)
  private transactionStore: TransactionStore = inject(TransactionStore)
  private convoStore: ConvoStore = inject(ConvoStore)
  private userIdStore: UserIdStore = inject(UserIdStore)
  private convoSvc: ConversationService = inject(ConversationService)
  private authSvc: AuthService = inject(AuthService)
  private userSvc: UserService = inject(UserService)
  private msgSvc: MessageService = inject(MessageService)

  userId!: string | undefined
  listing$!: Observable<Listing>
  images!: string[] | undefined
  bookmark: string = 'pi-bookmark'

  responsiveOptions: any[] = [
    {
        breakpoint: '1024px',
        numVisible: 5
    },
    {
        breakpoint: '768px',
        numVisible: 3
    },
    {
        breakpoint: '560px',
        numVisible: 1
    }
  ];

  ngOnInit(): void {
    let listingId: string = this.activateRoute.snapshot.params['listingId']
    firstValueFrom(this.userIdStore.getUserId)
    .then((value) => {
      this.userId = value
      this.listing$ = from(this.listingSvc.getListingById(listingId, value)).pipe(tap((listing) => {
        if (value != undefined){
          this.userSvc.checkIfFavourite(listing.listing_id!)
          .then((resp) => {
            this.bookmark = resp.result ? 'pi-bookmark-fill' : 'pi-bookmark'
          })
          .catch((err) => {
            console.log(err)
            this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while checking the listing."})
          })
        }
      }))
    })
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while retrieving the listing. Please try again later."})
    })
  }

  goToPaymentSummary(listing: Listing){
    this.transactionStore.setCurrentTransaction({
      listing_id: listing?.listing_id!,
      payee: listing?.lister_id!,
      amount: listing?.deposit!
    })
    this.router.navigate(['/payment/summary'])
  }

  message(listing: Listing){
    this.loggedIn()
    let convo: Conversation = {
      user_id_1: this.userId!,
      user_id_2: listing.lister_id,
      listing_id: listing.listing_id!,
      messages: []
    }

    this.convoSvc.convoExists(convo)
    .then((resp) => {
      if (resp.result){
        this.convoStore.setCurrentConvo(resp.convo)
      } else {
        this.convoStore.setCurrentConvo(convo)
      }
      this.router.navigate(['/conversation'])
    })
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while starting your conversation. Please try again later."})
    })
  }

  loggedIn(){
    firstValueFrom(this.authSvc.isAuthenticated$)
    .then((value) => {
      if (!value) {
        this.authSvc.loginWithRedirect({appState: { target: '/user/validate' }})
      }
    })
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered. Please try again later."})
    })
  }

  toggleBookmark(listing_id: string) {
    this.loggedIn()
    if (this.bookmark == 'pi-bookmark'){
      this.userSvc.saveUserFavourite(listing_id)
      .then(() => this.bookmark = 'pi-bookmark-fill')
      .catch((err) => {
        console.log(err)
        this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while bookmarking the listing. Please try again later."})
      })
    } else {
      this.userSvc.removeUserFavourite(listing_id)
      .then(() => this.bookmark = 'pi-bookmark')
      .catch((err) => {
        console.log(err)
        this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while removing bookmark. Please try again later."})
      })
    }
  }
}
