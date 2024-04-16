import { Component, Input, OnDestroy, OnInit, Output, inject } from '@angular/core';
import { GeocodeService } from '../../services/geocode.service';
import { Observable, Subject, Subscription } from 'rxjs';

@Component({
  selector: 'app-set-location-map',
  templateUrl: './set-location-map.component.html',
  styleUrl: './set-location-map.component.css'
})
export class SetLocationMapComponent implements OnInit, OnDestroy{

  geoCodeSvc: GeocodeService = inject(GeocodeService)

  center!: google.maps.LatLngLiteral
  center$!: Observable<google.maps.LatLngLiteral>
  sub!: Subscription

  @Output()
  setLocation: Subject<google.maps.LatLngLiteral> = new Subject<google.maps.LatLngLiteral>

  zoom = 11
  markerPositions: google.maps.LatLngLiteral[] = []
  mapOptions: google.maps.MapOptions = {center: {lat: 1.35, lng: 103.81}, mapTypeControl: false, streetViewControl: false}

  ngOnInit(): void {
    this.center$ = this.geoCodeSvc.searchResult.asObservable()
    this.sub = this.center$.subscribe({
      next: (value) => {
        this.center = value
      }
    })
  }
  
  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  addMarker(event: google.maps.MapMouseEvent) {
    // not sure why assigning collision behaviour here won't get error
    this.markerPositions.pop()
    this.markerPositions.push(event.latLng!.toJSON())
    this.setLocation.next(event.latLng!.toJSON())
  }
}
