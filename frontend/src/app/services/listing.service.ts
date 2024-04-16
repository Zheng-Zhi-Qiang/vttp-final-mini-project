import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Listing, ListingImageFile, MapBoundary, SubsetListings, User } from '../models/models';
import { environment as env } from '../../environments/environment'

@Injectable({
  providedIn: 'root'
})
export class ListingService {

  private client: HttpClient = inject(HttpClient)

  createListing(listing: Listing): Promise<any> {
    let headers: HttpHeaders = new HttpHeaders().set("Content-Type", "application/json").set("Accept", "application/json")
    return firstValueFrom(this.client.post<any>(env.backend_url + "/api/listing", listing, {headers: headers}))
  }

  uploadListingImages(listing_id: string, imageFiles: ListingImageFile[]): Promise<any> {
      let headers: HttpHeaders = new HttpHeaders().set("Accept", "application/json")
      let formData: FormData = new FormData()
      formData.set("listingId", listing_id)
      imageFiles.forEach((file) => formData.append("files", file.file))
      return firstValueFrom(this.client.post<any>(env.backend_url + "/api/listing/images", formData, {headers: headers}))
  }

  getListingsByMapBoundary(boundary: MapBoundary, limit: number, offset: number): Promise<SubsetListings> {
    let params: HttpParams = new HttpParams()
                                .set("neLat", boundary.north_east!.lat)
                                .set("swLat", boundary.south_west!.lat)
                                .set("neLng", boundary.north_east!.lng)
                                .set("swLng", boundary.south_west!.lng)
                                .set("limit", limit)
                                .set("offset", offset)
    return firstValueFrom(this.client.get<SubsetListings>(env.backend_url + "/api/listings", {params: params}))
  }

  getListingsByMapBoundaryAndPeriod(boundary: MapBoundary, period: Date[], limit: number, offset: number): Promise<SubsetListings> {
    if (!(period.length < 2) && period[1] == null){
      period.pop()
    }
    let params: HttpParams = new HttpParams()
                                .set("neLat", boundary.north_east!.lat)
                                .set("swLat", boundary.south_west!.lat)
                                .set("neLng", boundary.north_east!.lng)
                                .set("swLng", boundary.south_west!.lng)
                                .set("period", JSON.stringify(period))
                                .set("limit", limit)
                                .set("offset", offset)
    return firstValueFrom(this.client.get<SubsetListings>(env.backend_url + "/api/listings", {params: params}))
  }

  getListingById(listing_id: string, user_id?: string): Promise<Listing>{
    console.log("here")
    let params: HttpParams = new HttpParams().set("userId", user_id == undefined ? "nil" : user_id)
    console.log(params)
    return firstValueFrom(this.client.get<Listing>(env.backend_url + `/api/listing/${listing_id}`, {params: params}))
  }

  getListingsByUserId(): Promise<Listing[]>{
    return firstValueFrom(this.client.get<Listing[]>(env.backend_url + "/api/user/listings"))
  }

  deleteListing(listing_id: string): Promise<any>{
    return firstValueFrom(this.client.delete<any>(env.backend_url + `/api/listing/${listing_id}`))
  }

  updateListingHiddenStatus(listing_id: string, status: boolean): Promise<any> {
    return firstValueFrom(this.client.patch<any>(env.backend_url + `/api/listing/${listing_id}`, {status: status}))
  }
}