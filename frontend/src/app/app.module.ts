import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthGuard, AuthHttpInterceptor, AuthModule } from '@auth0/auth0-angular';
import { CallbackComponent } from './components/callback/callback.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { GoogleMapComponent } from './components/google-map/google-map.component';
import { GoogleMapsModule } from '@angular/google-maps';
import { SearchBarComponent } from './components/search-bar/search-bar.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SetLocationMapComponent } from './components/set-location-map/set-location-map.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { PrimengModule } from './modules/primeng/primeng.module';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { BrandComponent } from './components/brand/brand.component';
import { ListingImagesStore } from './stores/listing-images.store';
import { MessageService } from 'primeng/api';
import { MainComponent } from './views/main/main.component';
import { NewListingComponent } from './views/new-listing/new-listing.component';
import { ListingCardComponent } from './components/listing-card/listing-card.component';
import { ListingComponent } from './views/listing/listing.component';
import { ValidateUserComponent } from './views/validate-user/validate-user.component';
import { UserIdStore } from './stores/user-id.store';
import { PaymentSuccessComponent } from './views/payment-success/payment-success.component';
import { PaymentCancelledComponent } from './views/payment-cancelled/payment-cancelled.component';
import { PaymentSummaryComponent } from './views/payment-summary/payment-summary.component';
import { TransactionStore } from './stores/transaction.store';
import { generateApiRouteDefinitions } from './http-interceptor';
import { AllConversationsComponent } from './views/all-conversations/all-conversations.component';
import { ConversationComponent } from './views/conversation/conversation.component';
import { ConvoStore } from './stores/conversation.store';
import { TextMessageComponent } from './components/text-message/text-message.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { provideFirebaseApp, getApp, initializeApp } from '@angular/fire/app';
import { MessagingModule, getMessaging, provideMessaging } from '@angular/fire/messaging';
import { UserListingsComponent } from './views/user-listings/user-listings.component';
import { PeriodFilterStore } from './stores/period-filter.store';
import { BookmarksComponent } from './views/bookmarks/bookmarks.component';
import { UserTransactionsComponent } from './views/user-transactions/user-transactions.component';

@NgModule({
  declarations: [
    AppComponent,
    CallbackComponent,
    GoogleMapComponent,
    SearchBarComponent,
    MainComponent,
    SetLocationMapComponent,
    NavBarComponent,
    BrandComponent,
    NewListingComponent,
    ListingCardComponent,
    ListingComponent,
    ValidateUserComponent,
    PaymentSuccessComponent,
    PaymentCancelledComponent,
    PaymentSummaryComponent,
    AllConversationsComponent,
    ConversationComponent,
    TextMessageComponent,
    UserListingsComponent,
    BookmarksComponent,
    UserTransactionsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    GoogleMapsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    PrimengModule,
    


    // Import the module into the application, with configuration
    AuthModule.forRoot({
      domain: 'dev-tlfewg1dx6fa15n5.au.auth0.com',
      clientId: 'Q3WsvwwtQ8RD85VNvhI6zaKAolGtqD3k',
      authorizationParams: {
        redirect_uri: window.location.origin + "/callback",
        audience: 'https://buddyfinder.com',
        scope: "create:listing read:listing"
      },
      httpInterceptor: {
        allowedList: generateApiRouteDefinitions()
      }
    }),
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    }),
    provideFirebaseApp(() => initializeApp({
      apiKey: "AIzaSyBo6K3-wzeBPBuYk4Gio_2m6tZOrMLav8A",
      authDomain: "buddyfinder-416903.firebaseapp.com",
      projectId: "buddyfinder-416903",
      storageBucket: "buddyfinder-416903.appspot.com",
      messagingSenderId: "320623366748",
      appId: "1:320623366748:web:b5f86a9019781937420c99",
      measurementId: "G-12SSLSP1MW"
    })),
    provideMessaging(() => getMessaging(initializeApp({
      apiKey: "AIzaSyBo6K3-wzeBPBuYk4Gio_2m6tZOrMLav8A",
      authDomain: "buddyfinder-416903.firebaseapp.com",
      projectId: "buddyfinder-416903",
      storageBucket: "buddyfinder-416903.appspot.com",
      messagingSenderId: "320623366748",
      appId: "1:320623366748:web:b5f86a9019781937420c99",
      measurementId: "G-12SSLSP1MW"
    })))
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthHttpInterceptor, multi: true },
    MessageService,
    ListingImagesStore,
    AuthGuard,
    UserIdStore,
    TransactionStore,
    ConvoStore,
    PeriodFilterStore,
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }