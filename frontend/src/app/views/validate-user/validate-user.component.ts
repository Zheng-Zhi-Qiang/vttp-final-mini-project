import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '@auth0/auth0-angular';
import { User } from '../../models/models';
import { UserIdStore } from '../../stores/user-id.store';
import { Observable, firstValueFrom } from 'rxjs';
import { UserService } from '../../services/user.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-validate-user',
  templateUrl: './validate-user.component.html',
  styleUrl: './validate-user.component.css'
})
export class ValidateUserComponent implements OnInit{
  private userSvc: UserService = inject(UserService)
  private fb: FormBuilder = inject(FormBuilder)
  private router: Router = inject(Router)
  private authSvc: AuthService = inject(AuthService)
  private userIdStore: UserIdStore = inject(UserIdStore)
  private msgSvc: MessageService = inject(MessageService)

  form!: FormGroup
  userNotCreated!: boolean

  ngOnInit(): void {
    this.form = this.createForm()
    this.userSvc.userExists()
    .then((resp) => {
      console.log(resp.result)
      this.userNotCreated = !resp.result
      if (resp.result){
        this.userIdStore.setUserId(resp.user_id)
        firstValueFrom(this.userIdStore.getUserId)
        .then(value => console.log("user_id: ", value))
        this.router.navigate(['/'])
      }
    })
    .catch((err) => {
      // will just logout user for now
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while verifying your profile. Please try again later."})
      this.msgSvc.add({severity: "info", summary: "Logging Out", detail: "Logging you out..."})
      setTimeout(() => this.authSvc.logout(), 3000)
    })
  }

  createForm(): FormGroup {
    return this.fb.group({
      firstName: this.fb.control<string>('', [Validators.required]),
      lastName: this.fb.control<string>('', [Validators.required])
    })
  }

  onSubmit(): void {
    let user: User = {
      first_name: this.form.value['firstName'],
      last_name: this.form.value['lastName']
    }

    this.userSvc.createUser(user)
    .then((resp) => {
      this.userIdStore.setUserId(resp.user_id)
      firstValueFrom(this.userIdStore.getUserId)
      .then(value => console.log("user_id: ", value))
      this.router.navigate(['/'])
    })
    .catch((err) => {
      // will just logout for now
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while creating your profile. Please try again later."})
      this.msgSvc.add({severity: "info", summary: "Logging Out", detail: "Logging you out..."})
      setTimeout(() => this.authSvc.logout(), 3000)
    })
  }
}
