import { AfterViewInit, Component, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild, inject } from '@angular/core';
import { Observable, Subject, Subscription } from 'rxjs';
import { GeocodeService } from '../../services/geocode.service';
import { GoogleMap, MapAdvancedMarker, MapInfoWindow } from '@angular/google-maps';
import { Listing, MapBoundary } from '../../models/models';


@Component({
  selector: 'app-google-map',
  templateUrl: './google-map.component.html',
  styleUrl: './google-map.component.css'
})
export class GoogleMapComponent implements OnInit, OnDestroy, OnChanges {

  @ViewChild(GoogleMap)
  map!: GoogleMap

  @ViewChild(MapInfoWindow)
  infoWindow!: MapInfoWindow

  @Output()
  mapBoundary: Subject<MapBoundary> = new Subject()
  
  @Input()
  listings!: Listing[]

  markerPositions!: google.maps.LatLngLiteral[]
  
  geoCodeSvc: GeocodeService = inject(GeocodeService)

  center: google.maps.LatLngLiteral = {lat: 1.35, lng: 103.81}
  center$!: Observable<google.maps.LatLngLiteral>
  sub!: Subscription

  zoom = 11
  markerOptions!: google.maps.marker.AdvancedMarkerElementOptions
  mapOptions: google.maps.MapOptions = {mapTypeControl: false, streetViewControl: false}

  ngOnInit(): void {
    this.center$ = this.geoCodeSvc.searchResult.asObservable()
    this.sub = this.center$.subscribe({
      next: (value) => {
        this.map.panTo(value)
      }
    })
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.listings != undefined) {
      let loc: google.maps.LatLngLiteral[] = []
      this.listings.forEach(obj => loc.push(obj.location))
      this.markerPositions = loc
    }
  }
    

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  getNewListingMarkers(){
    // map not available in any of the life cycle stages. it initializes between the last two stages
    console.log("after map init")
    // northeast lat > southwest lat
    // northeast lng > southwest lng
    console.log(this.map.getBounds()?.getNorthEast().toJSON())
    console.log(this.map.getBounds()?.getSouthWest().toJSON())
    this.mapBoundary.next({north_east: this.map.getBounds()!.getNorthEast().toJSON(), south_west: this.map.getBounds()!.getSouthWest().toJSON()})
    this.markerOptions = {collisionBehavior: google.maps.CollisionBehavior.OPTIONAL_AND_HIDES_LOWER_PRIORITY}
  }

  openInfoWindow(marker: MapAdvancedMarker) {
    for (var listing of this.listings){
      if (listing.location.lat == marker.advancedMarker.position?.lat && listing.location.lng == marker.advancedMarker.position?.lng){
        let string = `<h3 style="margin: 0;">${listing.name}</h3>`
        this.infoWindow.openAdvancedMarkerElement(marker.advancedMarker, string)
      }
    }
  }
}
