import { Component, inject } from '@angular/core';
import { Listing, MapBoundary, SubsetListings } from '../../models/models';
import { ListingService } from '../../services/listing.service';
import { Router } from '@angular/router';
import { PeriodFilterStore } from '../../stores/period-filter.store';
import { firstValueFrom } from 'rxjs';
import { MessageService } from 'primeng/api';


@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent {
  private listingSvc: ListingService = inject(ListingService)
  private router: Router = inject(Router)
  private periodFilterStore: PeriodFilterStore = inject(PeriodFilterStore)
  private msgSvc: MessageService = inject(MessageService)

  // listingLocations!: google.maps.LatLngLiteral[]
  listings!: Listing[]
  defaultImage: string[] = ['https://www.freeiconspng.com/thumbs/no-image-icon/no-image-icon-6.png']
  boundary!: MapBoundary
  
  totalRecords!: number
  totalPages!: number
  first: number = 0
  page: number = 1
  rows: number = 1


  getListingsByMapBoundary(boundary: MapBoundary){
    this.boundary = boundary
    firstValueFrom(this.periodFilterStore.getPeriodFilter)
    .then((period) => this.listingSvc.getListingsByMapBoundaryAndPeriod(boundary, period, this.rows * 3 + this.first * 3, this.first * 3))
    .then((resp) => {
      this.assignListingValues(resp)
    })
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered. Please try again later."})
    })
  }

  onPageChange(event: any) {
    console.log(event)
    this.page = event.page + 1
    this.first = event.first
    this.rows = event.rows
    firstValueFrom(this.periodFilterStore.getPeriodFilter)
    .then((period) => this.listingSvc.getListingsByMapBoundaryAndPeriod(this.boundary, period, this.rows * 3 + this.first * 3, this.first * 3))
    .then((resp) => {
      this.assignListingValues(resp)
    })
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered. Please try again later."})
    })
  }

  navigate(){
    this.router.navigate(['listing'])
  }

  private assignListingValues(resp: SubsetListings){
    this.listings = resp.listings // since the html execution of the listingsSubset.listing is async, must create a separate variable prevent listings from being an undefined value
    // let loc: google.maps.LatLngLiteral[] = []
    // resp.listings.forEach(obj => loc.push(obj.location))
    // this.listingLocations = loc
    this.totalRecords = resp.total_records
    this.totalPages = Math.ceil(resp.total_records / 3)
  }

  
}