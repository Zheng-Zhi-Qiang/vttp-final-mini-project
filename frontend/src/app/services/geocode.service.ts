import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Subject, firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GeocodeService {

  private client: HttpClient = inject(HttpClient)

  url: string = "https://maps.googleapis.com/maps/api/geocode/json"

  searchResult: Subject<google.maps.LatLngLiteral> = new Subject<google.maps.LatLngLiteral>()

  getGeoCode(address: string): void {
    let params: HttpParams = new HttpParams().set("address", address).set("key", "AIzaSyC387YaS5z4OXQ9Ln0nrq1DvT3Ww2Zte_I")
    console.log("test")
    firstValueFrom(this.client.get<any>(this.url, {params: params}))
    .then((resp) => {
      console.log()
      this.searchResult.next(resp.results[0].geometry.location)
    })
  }
}
