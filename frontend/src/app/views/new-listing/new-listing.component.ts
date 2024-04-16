import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Listing, ListingImageFile } from '../../models/models';
import { ListingService } from '../../services/listing.service';
import { MessageService } from 'primeng/api';
import { Router } from '@angular/router';
import { ListingImagesStore } from '../../stores/listing-images.store';
import { Observable, Subscription, firstValueFrom, tap } from 'rxjs';
import { UserIdStore } from '../../stores/user-id.store';

@Component({
  selector: 'app-new-listing',
  templateUrl: './new-listing.component.html',
  styleUrl: './new-listing.component.css'
})
export class NewListingComponent implements OnInit, OnDestroy {

  private fb: FormBuilder = inject(FormBuilder)
  private listingSvc: ListingService = inject(ListingService)
  private msgSvc: MessageService = inject(MessageService)
  private router: Router = inject(Router)
  private listingImgStore: ListingImagesStore = inject(ListingImagesStore)
  private userIdStore: UserIdStore = inject(UserIdStore)
  private userId!: string | undefined

  imageFiles$!: Observable<ListingImageFile[]>
  imageFiles!: ListingImageFile[]
  responsiveOptions!: any[]
  today: Date = new Date()

  listingForm!: FormGroup
  listingTypes: string[] = ["Apartment Hunting", "Apartment Found", "Apartment Owner"]
  amenities: string[] = [
    "Kitchen", "Air Conditioning", "WiFi", "Stove", "Electric Kettle", "Refridgerator",
    "Communal Toilet", "Personal Toilet", "Dishwasher", "Balcony", "Patio", "Washing Machine",
    "Gym", "Laundromat", "Parking", "Swimming Pool"
  ].sort((one, two) => (one > two ? 1 : -1))

  locationSet: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    if (Number.isNaN(control.value.lat) || Number.isNaN(control.value.lng)){
      return { locationSet : true } as ValidationErrors
    }
    return null
  }

  // dateAfterToday: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  //   if (new Date(control.value.trim()) < new Date()){
  //     return { dateAfterToday : true } as ValidationErrors
  //   }
  //   return null
  // }

  ngOnInit(): void {
    firstValueFrom(this.userIdStore.getUserId)
    .then((value) => this.userId = value)
    this.listingForm = this.createListingForm()
    this.imageFiles$ = this.listingImgStore.getAllListingImages.pipe(tap((value) => this.imageFiles = value))
    this.responsiveOptions = [
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
    ]
  }

  ngOnDestroy(): void {
    this.listingImgStore.clearListingImageStore()
  }

  createListingForm(): FormGroup {
    return this.fb.group({
      location: this.fb.control<google.maps.LatLngLiteral>({lat: NaN, lng: NaN}, [this.locationSet]),
      from: this.fb.control<string>('', [Validators.required]),
      to: this.fb.control<string>('', [Validators.required]),
      address: this.fb.control<string>(''),
      postal: this.fb.control<string>('', [Validators.pattern("[0-9]+")]),
      country: this.fb.control<string>('', [Validators.required]),
      state: this.fb.control<string>('', [Validators.required]),
      city: this.fb.control<string>('', [Validators.required]),
      name: this.fb.control<string>('', [Validators.required]),
      introduction: this.fb.control<string>('', [Validators.required]),
      description: this.fb.control<string>(''),
      amenities: this.fb.control<string[]>([]),
      type: this.fb.control<string>('', [Validators.required]),
      deposit: this.fb.control<number>(0)
    })
  }

  setLocation(location: google.maps.LatLngLiteral): void {
    this.listingForm.controls['location'].setValue(location)
  }

  processForm(): void {
    console.log(typeof(this.listingForm.value['deposit']), this.listingForm.value['deposit'])
    let listing: Listing = {
      lister_id: this.userId!,
      location: this.listingForm.value['location'],
      period: {from: this.listingForm.value['from'], to: this.listingForm.value['to']},
      country: this.listingForm.value['country'],
      state: this.listingForm.value['state'],
      city: this.listingForm.value['city'],
      name: this.listingForm.value['name'],
      introduction: this.listingForm.value['introduction'],
      type: this.listingForm.value['type'],
      address: this.listingForm.value['address'],
      postal: this.listingForm.value['postal'],
      description: this.listingForm.value['description'],
      amenities: this.listingForm.value['amenities'],
      deposit: parseFloat(this.listingForm.value['deposit'])
    }


    this.msgSvc.add({severity: "info", summary: "Processing", detail: "Creating your listing..."})
    this.listingSvc.createListing(listing)
    .then((resp) => {
      if (this.imageFiles != undefined && this.imageFiles.length > 0) { // this.imageFiles will be undefined as there is no subscription in ts and in front end if frontend did not trigger optional part of form (async subscribes to observable), hence need to check for undefined as well
        return this.listingSvc.uploadListingImages(resp.listing_id, this.imageFiles)
      }
      return resp
    })
    .then((resp) => {
      setTimeout(() => {
        this.msgSvc.clear()
        this.msgSvc.add({severity: "success", summary: "Listing Created", detail: `Listing ID: ${resp.listing_id}\nRedirecting...`})
        setTimeout(() => this.router.navigate(['/']), 3000)
      }, 1000)
    })
    .catch((err) => {
      console.log(err)
      setTimeout(() => {
        this.msgSvc.clear()
        this.msgSvc.add({severity: "error", summary: "Error", detail: "error creating listing, please try again later"})
      }, 1000)
    })
  }

  onSelect(event: any){
    let promises: Promise<ListingImageFile>[] = []
    for (var index in event.currentFiles) {
      var reader = new FileReader();
      promises.push(new Promise<ListingImageFile>((resolve, reject) => {
        let file = event.currentFiles[index] // so that async execution of the promises won't mess up the index
        reader.readAsDataURL(file)
        reader.onload = (e) => {
          resolve({file: file, imageUrl: e.target?.result})
        }
        reader.onerror = (e) => {
          reject(e)
        }
      }))
    }
    Promise.all(promises)
    .then((files) => {
      console.log(files)
      this.listingImgStore.addAllListingImages(files)
    }
    )
  }

  onRemove(event: any){
    this.listingImgStore.deleteListingImage(event.file)
  }

  onClear(){
    this.listingImgStore.clearListingImageStore()
  }
}