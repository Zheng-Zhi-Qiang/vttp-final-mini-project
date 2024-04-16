import { Component, Input, OnInit, Output, inject } from '@angular/core';
import { GeocodeService } from '../../services/geocode.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { PeriodFilterStore } from '../../stores/period-filter.store';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent implements OnInit{
  private geoSvc: GeocodeService = inject(GeocodeService)
  private fb: FormBuilder = inject(FormBuilder)
  private periodFilterStore: PeriodFilterStore = inject(PeriodFilterStore)

  form!: FormGroup
  today: Date = new Date();

  @Input({required: true})
  dateSelector!: boolean

  @Input()
  buttonText!: boolean

  @Input()
  buttonSeverity!: string

  @Input()
  buttonRounded!: boolean

  @Input()
  buttonOutlined!: boolean

  ngOnInit(): void {
    this.form = this.dateSelector ? this.createFullForm() : this.createPartialForm()
  }

  createFullForm(): FormGroup {
    return this.fb.group({
      address: this.fb.control<string>('', [Validators.required]),
      period: this.fb.control<string>('', [Validators.required])
    })
  }

  createPartialForm(): FormGroup {
    return this.fb.group({
      address: this.fb.control<string>('', [Validators.required])
    })
  }

  searchGeocode(): void {
    this.geoSvc.getGeoCode(this.form.value['address'])
  }

  searchGeocodeAndPeriod(): void {
    console.log(this.form.value['period'])
    this.periodFilterStore.setPeriodFilter(this.form.value['period'])
    this.geoSvc.getGeoCode(this.form.value['address'])
  }
}
