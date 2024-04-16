import { inject } from "@angular/core"
import { CanActivateFn, Router } from "@angular/router"
import { firstValueFrom } from "rxjs"
import { UserIdStore } from "../stores/user-id.store"
import { UserService } from "../services/user.service"
import { TransactionStore } from "../stores/transaction.store"
import { ConvoStore } from "../stores/conversation.store"
import { Conversation, Transaction } from "../models/models"
import { AuthService } from "@auth0/auth0-angular"

export const profileCreated: CanActivateFn = async (_route, _state) => {
    const userSvc: UserService = inject(UserService)
    const router: Router = inject(Router)
    const userIdStore: UserIdStore = inject(UserIdStore)
    let userExists!: boolean
    let userId!: string

    await userSvc.userExists()
    .then((resp) => {
        console.log(resp)
      userExists = resp.result
      if (userExists) {
        userId = resp.user_id
      }
    })
    .catch((err) => {
      // will just return home for now
      console.log(err)
    })

    console.log("profileCreated:", userExists)

    if (userExists){
        console.log("profileCreated")
        userIdStore.setUserId(userId)
        firstValueFrom(userIdStore.getUserId)
        .then(value => console.log("user_id: ", value))
        return true
    }
    else {
        // userIdStore.deleteUserId
        return router.parseUrl("/user/validate")
    }
}

export const profileCreatedForMain: CanActivateFn = async (_route, _state) => {
  const userSvc: UserService = inject(UserService)
  const router: Router = inject(Router)
  const userIdStore: UserIdStore = inject(UserIdStore)
  const auth0Svc: AuthService = inject(AuthService)
  let userExists!: boolean
  let userId!: string
  let authenticated !:boolean

  await firstValueFrom(auth0Svc.isAuthenticated$)
  .then((value) => authenticated = value)

  if (authenticated) {
    await userSvc.userExists()
    .then((resp) => {
        console.log(resp)
      userExists = resp.result
      if (userExists) {
        userId = resp.user_id
      }
    })
    .catch((err) => {
      // will just return home for now
      console.log(err)
    })
  
    console.log("profileCreated:", userExists)
  
    if (userExists){
        console.log("profileCreated")
        userIdStore.setUserId(userId)
        firstValueFrom(userIdStore.getUserId)
        .then(value => console.log("user_id: ", value))
        return true
    }
    else {
        // userIdStore.deleteUserId
        return router.parseUrl("/user/validate")
    }
  }
  return true
}

export const transactionStarted: CanActivateFn = async (_route, _state) => {
  const transactionStore: TransactionStore = inject(TransactionStore)
  const userIdStore: UserIdStore = inject(UserIdStore)
  const router: Router = inject(Router)
  let ongoingTransaction!: boolean
  let transaction!: Transaction | undefined
  let userId!: string | undefined
  await firstValueFrom(transactionStore.ongoingTransaction)
  .then((value) => {
    ongoingTransaction = value
    return firstValueFrom(transactionStore.getCurrentTransaction)
  })
  .then((value) => {
    transaction = value
    return firstValueFrom(userIdStore.getUserId)
  })
  .then((value) => userId = value)
 

  if (ongoingTransaction) {
    if (transaction?.payee == userId) {
      return router.parseUrl("/")
    }
    return true
  }

  return router.parseUrl("/")
}

export const appStateBeforePaymentRedirectStored: CanActivateFn = (_route, _state) => {
  const router: Router = inject(Router)
  if (localStorage.getItem("transaction") == null || localStorage.getItem("user_id") == null){
    return router.parseUrl("/")
  }
  return true
}

export const convoStateAvailable: CanActivateFn = async (_route, _state) => {
  const router: Router = inject(Router)
  const convoStore: ConvoStore = inject(ConvoStore)
  var convo!: Conversation | undefined
  await firstValueFrom(convoStore.getCurrentConvo)
  .then((value) => convo = value)
  if (convo == undefined) {
    return router.parseUrl('/conversations')
  }
  return true;
}
