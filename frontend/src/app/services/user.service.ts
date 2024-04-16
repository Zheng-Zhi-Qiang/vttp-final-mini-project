import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Listing, User } from '../models/models';
import { environment as env } from '../../environments/environment'

@Injectable({
  providedIn: 'root'
})
export class UserService {
    private client: HttpClient = inject(HttpClient)

    userExists(): Promise<any>{
        return firstValueFrom(this.client.get<any>(env.backend_url + "/api/user_check"))
      }
    
    createUser(user: User): Promise<any>{
        return firstValueFrom(this.client.post<any>(env.backend_url + "/api/user", user))
    }

    getUserDetails(userId: string): Promise<User>{
        return firstValueFrom(this.client.get<User>(env.backend_url + `/api/user/${userId}`))
    }

    updateUserMessagingToken(token: string): Promise<any>{
        return firstValueFrom(this.client.patch<any>(env.backend_url + "/api/user/messaging", {token: token}))
    }

    getAllUserFavourites(): Promise<Listing[]>{
        return firstValueFrom(this.client.get<Listing[]>(env.backend_url + "/api/user/favourites"))
    }

    saveUserFavourite(listing_id: String): Promise<any>{
        return firstValueFrom(this.client.patch<any>(env.backend_url + `/api/user/favourite/${listing_id}`, {}))
    }

    removeUserFavourite(listing_id: String): Promise<any>{
        return firstValueFrom(this.client.delete<any>(env.backend_url + `/api/user/favourite/${listing_id}`))
    }

    checkIfFavourite(listing_id: String): Promise<any>{
        return firstValueFrom(this.client.get<any>(env.backend_url + `/api/user/favourite/${listing_id}`))
    }
}