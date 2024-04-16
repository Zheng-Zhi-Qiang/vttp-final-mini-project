import { Injectable } from "@angular/core";
import { ComponentStore } from "@ngrx/component-store";
import { ListingImageFile, ListingImages } from "../models/models";

const INIT_STATE: ListingImages = {
    images: []
}

@Injectable()
export class ListingImagesStore extends ComponentStore<ListingImages>{
    constructor() {super(INIT_STATE)}

    readonly getAllListingImages = this.select<ListingImageFile[]>(
        (slice: ListingImages) => {
          return slice.images
        }
      )

    readonly addAllListingImages = this.updater<ListingImageFile[]>(
        (slice: ListingImages, value: ListingImageFile[]) => {
            return {
                images: value
            } as ListingImages
        }
    )

    readonly clearListingImageStore = this.updater(
        (slice: ListingImages) => {
            return {
                images: []
            } as ListingImages
        }
    )

    readonly deleteListingImage = this.updater<File>(
        (slice: ListingImages, value: File) => {
            for (let i = 0; i < slice.images.length; i++){
                if (slice.images[i].file.name == value.name){
                    slice.images.splice(i, 1)
                    break
                }
            }
            return {
                images: [...slice.images]
            } as ListingImages
        }

    )
}