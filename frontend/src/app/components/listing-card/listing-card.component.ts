import { Component, Input, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-listing-card',
  templateUrl: './listing-card.component.html',
  styleUrl: './listing-card.component.css'
})
export class ListingCardComponent {

  router: Router = inject(Router)

  @Input({required: true})
  images!: string[] | undefined

  @Input({required: true})
  name!: string
  
  @Input({required: true})
  period!: string

  @Input({required: true})
  type!: string

  @Input({required: true})
  listingId!: string | undefined

  // navigate = (this.listingId, this.router) => {
  //   return router.navigate(['/listing', listingId])
  // }

  navigate(): (id: string, router: Router) => void {
    let listingId = this.listingId
    let router = this.router
    return (): void => {
      console.log(listingId)
      router.navigate(['listing', listingId])
    }
  }
}
