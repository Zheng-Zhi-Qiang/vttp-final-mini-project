import { Component, Input, OnDestroy, OnInit, inject, input } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { MenuItem } from 'primeng/api/menuitem';
import { UserIdStore } from '../../stores/user-id.store';
import { Observable, Subject, Subscription, firstValueFrom } from 'rxjs';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css'
})
export class NavBarComponent implements OnInit, OnDestroy {

  private authService: AuthService = inject(AuthService)
  private userIdStore: UserIdStore = inject(UserIdStore)

  // private userSub!: Subscription
  private authSub!: Subscription
  private itemSub!: Subscription
  private itemSubject: Subject<MenuItem[] | undefined> = new Subject<MenuItem[] | undefined>
  
  items$: Observable<MenuItem[] | undefined> = this.itemSubject.asObservable()

  items: MenuItem[] | undefined

  @Input({required: true})
  searchBar!: boolean

  @Input()
  dateSelector!: boolean

  ngOnInit(): void {
    this.itemSub = this.items$.subscribe({
      next: (value) => this.items = value
    })

    this.authSub = this.authService.isAuthenticated$.subscribe({
      next: (authenticated) => {
        console.log("nav", authenticated)
        if (authenticated) {
          this.itemSubject.next([
            {
              label: 'Log Out',
              command: () => {
                this.userIdStore.deleteUserId
                firstValueFrom(this.userIdStore.getUserId)
                .then(value => console.log("user_id: ", value))
                this.authService.logout({logoutParams: {returnTo: window.location.origin}})
              }
              //url: 'https://dev-tlfewg1dx6fa15n5.au.auth0.com/v2/logout?client_id=Q3WsvwwtQ8RD85VNvhI6zaKAolGtqD3k&returnTo=http://localhost:4200/'
            }
          ])
        }
        else {
          this.itemSubject.next([
            {
              label: 'Log In',
              command: () => this.authService.loginWithRedirect({appState: { target: '/user/validate' }})
              // url: 'https://dev-tlfewg1dx6fa15n5.au.auth0.com/authorize?client_id=Q3WsvwwtQ8RD85VNvhI6zaKAolGtqD3k&response_type=token&redirect_uri=http://localhost:4200/callback&audience=https://buddyfinder.com&scope=openid%20read:listing%20create:listing',
            }
          ])
        }
      }
    })
  }

  ngOnDestroy(): void {
    // this.userSub.unsubscribe()
    this.itemSub.unsubscribe()
    this.authSub.unsubscribe()
  }
}
