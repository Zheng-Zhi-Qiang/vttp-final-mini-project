export interface Period {
    from: Date
    to: Date
}

export interface Listing {
    lister_id: string
    listing_id?: string
    location: google.maps.LatLngLiteral
    period: Period
    address?: string
    postal?: string
    country: string
    state: string
    city: string
    name: string
    introduction: string
    description?: string
    amenities?: string[]
    type: string
    deposit?: number
    images?: string[]
    deleted?: boolean
    hidden?: boolean
    filled?: boolean
}

export interface MapBoundary {
    north_east?: google.maps.LatLngLiteral
    south_west?: google.maps.LatLngLiteral
}

export interface ListingImageFile {
    file: File
    imageUrl: any
}

export interface ListingImages {
    images: ListingImageFile[]
}

export interface SubsetListings {
    listings: Listing[]
    total_records: number
}

export interface User {
    user_id?: string
    first_name: string
    last_name: string
    verified?: boolean
}

export interface UserId {
    userId: string | undefined
}

export interface Message {

}

export interface Transaction {
    transaction_id?: number
    listing_id: string
    payer?: string
    payee: string
    amount: number
    date?: Date
}

export interface TransactionSlice {
    transaction: Transaction | undefined
}

export interface Message {
    convo_id?: string
    listing_id?: string
    sender: string
    receiver: string
    message: string
    date: Date
}

export interface Conversation {
    convo_id?: string
    messages: Message[]
    user_id_1: string
    user_id_2: string
    listing_id: string
    deleted?: boolean
}

export interface ConversationSlice {
    convo: Conversation | undefined
}

export interface PeriodSlice {
    period: Date[]
}