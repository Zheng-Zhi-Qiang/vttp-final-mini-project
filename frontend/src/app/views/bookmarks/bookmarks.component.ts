import { Component, OnInit, inject } from '@angular/core';
import { UserService } from '../../services/user.service';
import { Listing } from '../../models/models';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-bookmarks',
  templateUrl: './bookmarks.component.html',
  styleUrl: './bookmarks.component.css'
})
export class BookmarksComponent implements OnInit {
  private userSvc: UserService = inject(UserService)
  private msgSvc: MessageService = inject(MessageService)
  defaultImage: string[] = ['https://www.freeiconspng.com/thumbs/no-image-icon/no-image-icon-6.png']

  listings!: Listing[]

  ngOnInit(): void {
    this.userSvc.getAllUserFavourites()
    .then((value) => this.listings = value)
    .catch((err) => {
      console.log(err)
      this.msgSvc.add({severity: "error", summary: "Error", detail: "Error encountered while retrieving your bookmarks. Please try again later."})
    })
  }
}
