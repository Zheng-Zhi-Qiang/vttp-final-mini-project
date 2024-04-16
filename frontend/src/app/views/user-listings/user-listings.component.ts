import { Component, OnInit, inject } from '@angular/core';
import { Listing } from '../../models/models';
import { ListingService } from '../../services/listing.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-user-listings',
  templateUrl: './user-listings.component.html',
  styleUrl: './user-listings.component.css'
})
export class UserListingsComponent implements OnInit {

  private listingSvc: ListingService = inject(ListingService)
  private msgSvc: MessageService = inject(MessageService)
  
  listings$!: Promise<Listing[]>

  ngOnInit(): void {
    this.listings$ = this.listingSvc.getListingsByUserId()
  }

  deleteListing(event: Event, listing_id: string): void{
    event.stopPropagation()
    this.listingSvc.deleteListing(listing_id)
    .then(() => this.listings$ = this.listingSvc.getListingsByUserId())
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while deleting your listing. Please try again later."})
    })
  }

  toggleListingVisibility(event: Event, listing_id: string, hidden: boolean): void {
    event.stopPropagation()
    this.listingSvc.updateListingHiddenStatus(listing_id!, !hidden)
    .then(() => this.listings$ = this.listingSvc.getListingsByUserId())
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while setting your listing visibility. Please try again later."})
    })
  }

}
