import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CallbackComponent } from './components/callback/callback.component';
import { MainComponent } from './views/main/main.component';
import { NewListingComponent } from './views/new-listing/new-listing.component';
import { ListingComponent } from './views/listing/listing.component';
import { AuthGuard } from '@auth0/auth0-angular';
import { ValidateUserComponent } from './views/validate-user/validate-user.component';
import { appStateBeforePaymentRedirectStored, convoStateAvailable, profileCreated, profileCreatedForMain, transactionStarted } from './guards/guards';
import { PaymentSuccessComponent } from './views/payment-success/payment-success.component';
import { PaymentCancelledComponent } from './views/payment-cancelled/payment-cancelled.component';
import { PaymentSummaryComponent } from './views/payment-summary/payment-summary.component';
import { AllConversationsComponent } from './views/all-conversations/all-conversations.component';
import { ConversationComponent } from './views/conversation/conversation.component';
import { TextMessageComponent } from './components/text-message/text-message.component';
import { UserListingsComponent } from './views/user-listings/user-listings.component';
import { BookmarksComponent } from './views/bookmarks/bookmarks.component';
import { UserTransactionsComponent } from './views/user-transactions/user-transactions.component';

const routes: Routes = [
  {path: "", component: MainComponent, canActivate: [profileCreatedForMain]},
  {path: "message", component: TextMessageComponent},
  {path: "callback", component: CallbackComponent},
  {path: "user/validate", component: ValidateUserComponent, canActivate: [AuthGuard]},
  {path: "listing", component: NewListingComponent, canActivate: [AuthGuard, profileCreated]},
  {path: "listing/:listingId", component: ListingComponent, canActivate: [profileCreatedForMain]},
  {path: "payment/success", component: PaymentSuccessComponent, canActivate: [AuthGuard, profileCreated, appStateBeforePaymentRedirectStored]},
  {path: "payment/summary", component: PaymentSummaryComponent, canActivate: [AuthGuard, profileCreated, transactionStarted]},
  {path: "payment/cancelled", component: PaymentCancelledComponent, canActivate: [AuthGuard, profileCreated, appStateBeforePaymentRedirectStored]},
  {path: "conversations", component: AllConversationsComponent, canActivate: [AuthGuard, profileCreated]},
  {path: "conversation", component: ConversationComponent, canActivate: [AuthGuard, profileCreated, convoStateAvailable]},
  {path: "user/listings", component: UserListingsComponent, canActivate: [AuthGuard, profileCreated]},
  {path: "user/bookmarks", component: BookmarksComponent, canActivate: [AuthGuard, profileCreated]},
  {path: "user/transactions", component: UserTransactionsComponent, canActivate: [AuthGuard, profileCreated]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
